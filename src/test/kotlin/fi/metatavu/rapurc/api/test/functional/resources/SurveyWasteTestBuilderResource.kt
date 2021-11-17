package fi.metatavu.rapurc.api.test.functional.resources

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.rapurc.api.client.apis.SurveyWastesApi
import fi.metatavu.rapurc.api.client.infrastructure.ApiClient
import fi.metatavu.rapurc.api.client.infrastructure.ClientException
import fi.metatavu.rapurc.api.client.models.Metadata
import fi.metatavu.rapurc.api.client.models.Waste
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.impl.ApiTestBuilderResource
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import java.util.*

/**
 * Test builder resource for SurveyWastesApi
 */
class SurveyWasteTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<Waste, ApiClient?>(testBuilder, apiClient) {

    private val wasteSurveyMap = mutableMapOf<UUID, UUID>()

    override fun getApi(): SurveyWastesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return SurveyWastesApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Creates new waste object
     *
     * @param surveyId survey id it belongs to
     * @param wasteMaterialId waste material id
     * @param usageId usage id
     * @return created waste
     */
    fun create(surveyId: UUID, wasteMaterialId: UUID, usageId: UUID): Waste {
        val waste = Waste(
            wasteMaterialId = wasteMaterialId,
            usageId = usageId,
            amount = 10.0,
            metadata = Metadata()
        )
        val result = api.createSurveyWaste(surveyId, waste)
        wasteSurveyMap[result.id!!] = surveyId
        return addClosable(result)
    }

    /**
     * Creates new waste object
     *
     * @param surveyId survey id it belongs to
     * @param waste waste
     * @return created waste
     */
    fun create(surveyId: UUID, waste: Waste): Waste {
        val result = api.createSurveyWaste(surveyId, waste)
        wasteSurveyMap[result.id!!] = surveyId
        return addClosable(result)
    }

    /**
     * Finds Waste
     *
     * @param surveyId survey id
     * @param wasteId waste Id
     * @return found Waste
     */
    fun findWaste(surveyId: UUID, wasteId: UUID): Waste {
        return api.findSurveyWaste(surveyId, wasteId)
    }

    /**
     * Lists all Waste entries for a survey
     *
     * @param surveyId survey id
     * @return Waste list
     */
    fun list(surveyId: UUID): Array<Waste> {
        return api.listSurveyWastes(surveyId)
    }

    /**
     * Updates waste
     *
     * @param surveyId survey id
     * @param wasteId waste id
     * @param waste new waste data
     * @return updated waste
     */
    fun update(surveyId: UUID, wasteId: UUID, waste: Waste): Waste {
        return api.updateSurveyWaste(surveyId, wasteId, waste)
    }

    /**
     * Deletes waste from the API
     *
     * @param surveyId survey id
     * @param wasteId waste id to delete
     */
    fun delete(surveyId: UUID, wasteId: UUID) {
        api.deleteSurveyWaste(surveyId, wasteId)
        removeCloseable { closable: Any? ->
            if (closable !is Waste) {
                return@removeCloseable false
            }
            closable.id == wasteId
        }
    }

    /**
     * Asserts the amount of waste records for a survey
     *
     * @param expected expected status
     * @param surveyId survey id
     */
    fun assertCount(expected: Int, surveyId: UUID) {
        assertEquals(
            expected,
            api.listSurveyWastes(
                surveyId
            ).size
        )
    }

    /**
     * Asserts that finding owner information fails with the status
     *
     * @param expectedStatus expected status
     * @param surveyId survey id
     * @param wasteId waste id
     */
    fun assertFindFailStatus(expectedStatus: Int, surveyId: UUID?, wasteId: UUID?) {
        try {
            api.findSurveyWaste(surveyId!!, wasteId!!)
            fail(String.format("Expected find to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts create status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param surveyId survey id
     * @param waste waste
     */
    fun assertCreateFailStatus(expectedStatus: Int, surveyId: UUID?, waste: Waste) {
        try {
            create(surveyId!!, waste)
            fail(String.format("Expected create to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts update status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param surveyId survey id
     * @param wasteId waste id
     * @param waste waste
     */
    fun assertUpdateFailStatus(expectedStatus: Int, surveyId: UUID, wasteId: UUID, waste: Waste) {
        try {
            update(surveyId, wasteId, waste)
            fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts delete status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param surveyId survey id
     * @param wasteId waste to delete
     */
    fun assertDeleteFailStatus(expectedStatus: Int, surveyId: UUID, wasteId: UUID) {
        try {
            api.deleteSurveyWaste(surveyId, wasteId)
            fail(String.format("Expected delete to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts list status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param surveyId survey ids
     */
    fun assertListFailStatus(expectedStatus: Int, surveyId: UUID?) {
        try {
            api.listSurveyWastes(
                surveyId!!
            )
            fail(String.format("Expected list to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    override fun clean(waste: Waste) {
        val surveyId = wasteSurveyMap[waste.id!!]
        api.deleteSurveyWaste(surveyId!!, waste.id)
    }

}
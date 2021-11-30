package fi.metatavu.rapurc.api.test.functional.resources

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.rapurc.api.client.apis.SurveyHazardousWastesApi
import fi.metatavu.rapurc.api.client.infrastructure.ApiClient
import fi.metatavu.rapurc.api.client.infrastructure.ClientException
import fi.metatavu.rapurc.api.client.models.Metadata
import fi.metatavu.rapurc.api.client.models.HazardousWaste
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.impl.ApiTestBuilderResource
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import java.util.*

/**
 * Test builder resource for SurveyHazardousWastesApi
 */
class SurveyHazardousWasteTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<HazardousWaste, ApiClient?>(testBuilder, apiClient) {

    private val hazardousWasteSurveyMap = mutableMapOf<UUID, UUID>()

    override fun getApi(): SurveyHazardousWastesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return SurveyHazardousWastesApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Creates new hazardous waste object
     *
     * @param surveyId survey id it belongs to
     * @param hazardousMaterialId waste hazardous material id
     * @param wasteSpecifierId waste specifier id
     * @return created hazardous waste
     */
    fun create(surveyId: UUID, hazardousMaterialId: UUID, wasteSpecifierId: UUID): HazardousWaste {
        val hazardousWaste = HazardousWaste(
            hazardousMaterialId = hazardousMaterialId,
            wasteSpecifierId = wasteSpecifierId,
            amount = 10.0,
            metadata = Metadata()
        )

        val result = api.createSurveyHazardousWaste(surveyId, hazardousWaste)
        hazardousWasteSurveyMap[result.id!!] = surveyId
        return addClosable(result)
    }

    /**
     * Creates new hazardous waste
     *
     * @param surveyId survey id it belongs to
     * @param waste hazardous waste
     * @return created hazardous waste
     */
    fun create(surveyId: UUID, waste: HazardousWaste): HazardousWaste {
        val result = api.createSurveyHazardousWaste(surveyId, waste)
        hazardousWasteSurveyMap[result.id!!] = surveyId
        return addClosable(result)
    }

    /**
     * Finds hazardous Waste
     *
     * @param surveyId survey id
     * @param wasteId hazardous waste Id
     * @return found hazardous Waste
     */
    fun findWaste(surveyId: UUID, wasteId: UUID): HazardousWaste {
        return api.findSurveyHazardousWaste(surveyId, wasteId)
    }

    /**
     * Lists all hazardous Waste entries for a survey
     *
     * @param surveyId survey id
     * @return hazardous Waste list
     */
    fun list(surveyId: UUID): Array<HazardousWaste> {
        return api.listSurveyHazardousWastes(surveyId)
    }

    /**
     * Updates hazardous waste
     *
     * @param surveyId survey id
     * @param hazardousWasteId hazardous waste id
     * @param hazardousWaste new hazardous waste data
     * @return updated waste
     */
    fun update(surveyId: UUID, hazardousWasteId: UUID, hazardousWaste: HazardousWaste): HazardousWaste {
        return api.updateSurveyHazardousWaste(surveyId, hazardousWasteId, hazardousWaste)
    }

    /**
     * Deletes hazardous waste from the API
     *
     * @param surveyId survey id
     * @param hazardousWasteId hazardous waste to delete
     */
    fun delete(surveyId: UUID, hazardousWasteId: UUID) {
        api.deleteSurveyHazardousWaste(surveyId, hazardousWasteId)
        removeCloseable { closable: Any? ->
            if (closable !is HazardousWaste) {
                return@removeCloseable false
            }
            closable.id == hazardousWasteId
        }
    }

    /**
     * Asserts the amount of hazardous waste records for a survey
     *
     * @param expected expected status
     * @param surveyId survey id
     */
    fun assertCount(expected: Int, surveyId: UUID) {
        assertEquals(
            expected,
            api.listSurveyHazardousWastes(
                surveyId
            ).size
        )
    }

    /**
     * Asserts that finding owner information fails with the status
     *
     * @param expectedStatus expected status
     * @param surveyId survey id
     * @param hazardousWasteId hazardous waste id
     */
    fun assertFindFailStatus(expectedStatus: Int, surveyId: UUID?, hazardousWasteId: UUID?) {
        try {
            api.findSurveyHazardousWaste(surveyId!!, hazardousWasteId!!)
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
     * @param hazardousWaste hazardous waste
     */
    fun assertCreateFailStatus(expectedStatus: Int, surveyId: UUID?, hazardousWaste: HazardousWaste) {
        try {
            create(surveyId!!, hazardousWaste)
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
     * @param hazardousWasteId hazardous waste id
     * @param waste hazardous waste
     */
    fun assertUpdateFailStatus(expectedStatus: Int, surveyId: UUID, hazardousWasteId: UUID, waste: HazardousWaste) {
        try {
            update(surveyId, hazardousWasteId, waste)
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
            api.deleteSurveyHazardousWaste(surveyId, wasteId)
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
            api.listSurveyHazardousWastes(
                surveyId!!
            )
            fail(String.format("Expected list to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    override fun clean(hazardousWaste: HazardousWaste) {
        val surveyId = hazardousWasteSurveyMap[hazardousWaste.id!!]
        api.deleteSurveyHazardousWaste(surveyId!!, hazardousWaste.id)
    }

    /**
     * Marks hazardous waste as deleted and removes it from closables
     *
     * @param hazardousWaste hazardous waste
     */
    fun markAsDeleted(hazardousWaste: HazardousWaste) {
        removeCloseable { closable: Any? ->
            if (closable !is HazardousWaste) {
                return@removeCloseable false
            }
            closable.id == hazardousWaste.id
        }
    }



}
package fi.metatavu.rapurc.api.test.functional.resources

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.rapurc.api.client.apis.SurveyorsApi
import fi.metatavu.rapurc.api.client.infrastructure.ApiClient
import fi.metatavu.rapurc.api.client.infrastructure.ClientException
import fi.metatavu.rapurc.api.client.models.*
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.impl.ApiTestBuilderResource
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import java.util.*

/**
 * Test builder resource for Surveyors API
 */
class SurveyorTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<Surveyor, ApiClient?>(testBuilder, apiClient) {

    override fun getApi(): SurveyorsApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return SurveyorsApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Creates new surveyor object
     *
     * @param surveyId survey id it belongs to
     * @return created surveyor
     */
    fun createWithDefaultValues(surveyId: UUID): Surveyor? {
        val surveyor = Surveyor(
            firstName = "First",
            lastName = "Last",
            company = "Company",
            phone = "044123456789"
        )
        val result = api.createSurveyor(surveyId = surveyId, surveyor = surveyor)
        return addClosable(result)
    }

    /**
     * Creates new surveyor object
     *
     * @param surveyId survey id it belongs to
     * @param surveyor surveyor information
     * @return created surveyor
     */
    fun create(surveyId: UUID, surveyor: Surveyor): Surveyor? {
        val result = api.createSurveyor(surveyId, surveyor)
        return addClosable(result)
    }

    /**
     * Finds surveyor
     *
     * @param surveyId survey id
     * @param surveyorId surveyor id
     * @return found surveyor
     */
    fun findSurveyor(surveyId: UUID, surveyorId: UUID): Surveyor {
        return api.findSurveyor(surveyId, surveyorId)
    }

    /**
     * Lists all surveyor entries for a survey
     *
     * @param surveyId survey id
     * @return surveyors list
     */
    fun list(surveyId: UUID): Array<Surveyor> {
        return api.listSurveyors(surveyId)
    }

    /**
     * Updates surveyor
     *
     * @param surveyId survey id
     * @param surveyorId surveyor id
     * @param surveyor new surveyor data
     * @return updated surveyor
     */
    fun update(surveyId: UUID, surveyorId: UUID, surveyor: Surveyor): Surveyor {
        return api.updateSurveyor(surveyId, surveyorId, surveyor)
    }

    /**
     * Deletes surveyor information from the API
     *
     * @param surveyor surveyor information to delete
     */
    fun delete(surveyor: Surveyor) {
        api.deleteSurveyor(surveyor.surveyId!!, surveyor.id!!)
        removeCloseable { closable: Any? ->
            if (closable !is Surveyor) {
                return@removeCloseable false
            }
            closable.id == surveyor.id
        }
    }

    /**
     * Asserts the amount of surveyor records for a survey
     *
     * @param expected expected status
     * @param surveyId survey id
     */
    fun assertCount(expected: Int, surveyId: UUID) {
        assertEquals(
            expected,
            api.listSurveyors(
                surveyId
            ).size
        )
    }

    /**
     * Asserts that finding surveyor fails with the status
     *
     * @param expectedStatus expected status
     * @param surveyId survey id
     * @param surveyorId surveyor id
     */
    fun assertFindFailStatus(expectedStatus: Int, surveyId: UUID?, surveyorId: UUID?) {
        try {
            api.findSurveyor(surveyId!!, surveyorId!!)
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
     * @param surveyor surveyor information
     */
    fun assertCreateFailStatus(expectedStatus: Int, surveyId: UUID?, surveyor: Surveyor) {
        try {
            create(surveyId!!, surveyor)
            fail(String.format("Expected create to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts update status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param surveyor surveyor information
     */
    fun assertUpdateFailStatus(expectedStatus: Int, surveyor: Surveyor) {
        try {
            update(surveyor.surveyId!!, surveyor.id!!, surveyor)
            fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts delete status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param surveyor surveyor to delete
     */
    fun assertDeleteFailStatus(expectedStatus: Int, surveyor: Surveyor) {
        try {
            api.deleteSurveyor(surveyor.surveyId!!, surveyor.id!!)
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
            api.listSurveyors(
                surveyId!!
            )
            fail(String.format("Expected list to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    override fun clean(surveyor: Surveyor) {
        api.deleteSurveyor(surveyor.surveyId!!, surveyor.id!!)
    }

    /**
     * Removes the surveyor info from the closables list
     *
     * @param surveyor surveyor info
     */
    fun markAsDeleted(surveyor: Surveyor) {
        removeCloseable { closable: Any? ->
            if (closable !is Surveyor) {
                return@removeCloseable false
            }
            closable.id == surveyor.id
        }
    }
}
package fi.metatavu.rapurc.api.test.functional.resources

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.rapurc.api.client.apis.SurveyReusablesApi
import fi.metatavu.rapurc.api.client.infrastructure.ApiClient
import fi.metatavu.rapurc.api.client.infrastructure.ClientException
import fi.metatavu.rapurc.api.model.Reusable
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.impl.ApiTestBuilderResource
import org.junit.Assert
import java.util.*

/**
 * Test builder resource for Reusable API
 */
class SurveyReusableTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<fi.metatavu.rapurc.api.client.models.Reusable, ApiClient?>(testBuilder, apiClient) {

    private val reusableSurveyMap = mutableMapOf<fi.metatavu.rapurc.api.client.models.Reusable, UUID>()

    override fun getApi(): SurveyReusablesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return SurveyReusablesApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Creates new reusable object
     *
     * @param surveyId survey id it belongs to
     * @param reusable reusable
     * @return created OwnerInformation
     */
    fun create(surveyId: UUID, reusable: fi.metatavu.rapurc.api.client.models.Reusable): fi.metatavu.rapurc.api.client.models.Reusable? {
        val result = api.createSurveyReusable(surveyId, reusable)
        reusableSurveyMap[result] = surveyId
        return addClosable(result)
    }

    /**
     * Finds reusable
     *
     * @param surveyId survey id
     * @param reusableId reusable id
     * @return found reusable
     */
    fun findOwner(surveyId: UUID, reusableId: UUID): fi.metatavu.rapurc.api.client.models.Reusable {
        return api.findSurveyReusable(surveyId, reusableId)
    }

    /**
     * Lists all Reusable entries for a survey
     *
     * @param surveyId survey id
     * @return Reusable list
     */
    fun list(surveyId: UUID): Array<fi.metatavu.rapurc.api.client.models.Reusable> {
        return api.listSurveyReusables(surveyId)
    }

    /**
     * Updates owner information
     *
     * @param surveyId survey id
     * @param reusableId reusable id
     * @param reusable new reusable data
     * @return updated owner information
     */
    fun update(surveyId: UUID, reusableId: UUID, reusable: fi.metatavu.rapurc.api.client.models.Reusable): fi.metatavu.rapurc.api.client.models.Reusable {
        return api.updateSurveyReusable(surveyId, reusableId, reusable)
    }

    /**
     * Deletes reusable from the API
     *
     * @param reusable reusable to delete
     */
    fun delete(surveyId: UUID, reusable: Reusable) {
        api.deleteSurveyReusable(surveyId, reusable.id!!)

        val foundEntry = reusableSurveyMap.filter { it.key.id == reusable.id }
        foundEntry.forEach { (reusable, uuid) ->
            reusableSurveyMap.remove(reusable, uuid)
        }

        removeCloseable { closable: Any? ->
            if (closable !is Reusable) {
                return@removeCloseable false
            }
            closable.id == reusable.id
        }
    }

    /**
     * Asserts the amount of reusable records for a survey
     *
     * @param expected expected status
     * @param surveyId survey id
     */
    fun assertCount(expected: Int, surveyId: UUID) {
        Assert.assertEquals(
            expected,
            api.listSurveyReusables(
                surveyId
            ).size
        )
    }

    /**
     * Asserts that finding reusable fails with the status
     *
     * @param expectedStatus expected status
     * @param surveyId survey id
     * @param reusableId reusable id
     */
    fun assertFindFailStatus(expectedStatus: Int, surveyId: UUID?, reusableId: UUID?) {
        try {
            api.findSurveyReusable(surveyId!!, reusableId!!)
            Assert.fail(String.format("Expected find to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts create status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param surveyId survey id
     * @param reusable reusable
     */
    fun assertCreateFailStatus(expectedStatus: Int, surveyId: UUID?, reusable: fi.metatavu.rapurc.api.client.models.Reusable) {
        try {
            create(surveyId!!, reusable)
            Assert.fail(String.format("Expected create to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts update status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param surveyId survey id
     * @param reusableId reusable id
     * @param reusable reusable
     */
    fun assertUpdateFailStatus(expectedStatus: Int, surveyId: UUID, reusableId: UUID, reusable: fi.metatavu.rapurc.api.client.models.Reusable) {
        try {
            update(surveyId, reusableId, reusable)
            Assert.fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts delete status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param reusable reusable to delete
     */
    fun assertDeleteFailStatus(expectedStatus: Int, surveyId: UUID, reusable: Reusable) {
        try {
            api.deleteSurveyReusable(surveyId, reusable.id!!)
            Assert.fail(String.format("Expected delete to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
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
            api.listSurveyReusables(
                surveyId!!
            )
            Assert.fail(String.format("Expected list to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    override fun clean(reusable: fi.metatavu.rapurc.api.client.models.Reusable) {
        api.deleteSurveyReusable(reusableSurveyMap[reusable]!!, reusable.id!!)
    }

}
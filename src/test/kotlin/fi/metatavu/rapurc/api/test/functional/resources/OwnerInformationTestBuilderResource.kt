package fi.metatavu.rapurc.api.test.functional.resources

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.rapurc.api.client.apis.OwnersApi
import fi.metatavu.rapurc.api.client.infrastructure.ApiClient
import fi.metatavu.rapurc.api.client.infrastructure.ClientException
import fi.metatavu.rapurc.api.client.models.OwnerInformation
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.impl.ApiTestBuilderResource
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import java.util.*

/**
 * Test builder resource for Owners API
 */
class OwnerInformationTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<OwnerInformation, ApiClient?>(testBuilder, apiClient) {

    override fun getApi(): OwnersApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return OwnersApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Creates new owner information object
     *
     * @param surveyId survey id it belongs to
     * @param ownerInfo owner information
     * @return created OwnerInformation
     */
    fun create(surveyId: UUID, ownerInfo: OwnerInformation): OwnerInformation? {
        val result = api.createOwnerInformation(surveyId, ownerInfo)
        return addClosable(result)
    }

    /**
     * Finds owner information
     *
     * @param surveyId survey id
     * @param ownerId owner id
     * @return found OwnerInformation
     */
    fun findOwner(surveyId: UUID, ownerId: UUID): OwnerInformation {
        return api.findOwnerInformation(surveyId, ownerId)
    }

    /**
     * Lists all owner information entries for a survey
     *
     * @param surveyId survey id
     * @return owner information list
     */
    fun list(surveyId: UUID): Array<OwnerInformation> {
        return api.listOwnerInformation(surveyId)
    }

    /**
     * Updates owner information
     *
     * @param surveyId survey id
     * @param ownerId owner id
     * @param ownerInformation new owner information data
     * @return updated owner information
     */
    fun update(surveyId: UUID, ownerId: UUID, ownerInformation: OwnerInformation): OwnerInformation {
        return api.updateOwnerInformation(surveyId, ownerId, ownerInformation)
    }

    /**
     * Deletes owner information from the API
     *
     * @param ownerInfo owner information to delete
     */
    fun delete(ownerInfo: OwnerInformation) {
        api.deleteOwnerInformation(ownerInfo.surveyId, ownerInfo.id!!)
        removeCloseable { closable: Any? ->
            if (closable !is OwnerInformation) {
                return@removeCloseable false
            }
            closable.id == ownerInfo.id
        }
    }

    /**
     * Asserts the amount of owner information records for a survey
     *
     * @param expected expected status
     * @param surveyId survey id
     */
    fun assertCount(expected: Int, surveyId: UUID) {
        assertEquals(
            expected,
            api.listOwnerInformation(
                surveyId
            ).size
        )
    }

    /**
     * Asserts that finding owner information fails with the status
     *
     * @param expectedStatus expected status
     * @param surveyId survey id
     * @param ownerId owner id
     */
    fun assertFindFailStatus(expectedStatus: Int, surveyId: UUID?, ownerId: UUID?) {
        try {
            api.findOwnerInformation(surveyId!!, ownerId!!)
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
     * @param ownerInfo owner information
     */
    fun assertCreateFailStatus(expectedStatus: Int, surveyId: UUID?, ownerInfo: OwnerInformation) {
        try {
            create(surveyId!!, ownerInfo)
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
     * @param ownerId owner id
     * @param ownerInfo owner information
     */
    fun assertUpdateFailStatus(expectedStatus: Int, surveyId: UUID, ownerId: UUID, ownerInfo: OwnerInformation) {
        try {
            update(surveyId, ownerId, ownerInfo)
            fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts delete status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param owner owner information to delete
     */
    fun assertDeleteFailStatus(expectedStatus: Int, owner: OwnerInformation) {
        try {
            api.deleteOwnerInformation(owner.surveyId, owner.id!!)
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
            api.listOwnerInformation(
                surveyId!!
            )
            fail(String.format("Expected list to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    override fun clean(ownerInfo: OwnerInformation) {
        api.deleteOwnerInformation(ownerInfo.surveyId, ownerInfo.id!!)
    }

    /**
     * Removes the owner info from the closables list
     *
     * @param ownerInfo owner info
     */
    fun markAsDeleted(ownerInfo: OwnerInformation) {
        removeCloseable { closable: Any? ->
            if (closable !is OwnerInformation) {
                return@removeCloseable false
            }
            closable.id == ownerInfo.id
        }
    }

}
package fi.metatavu.rapurc.api.test.functional.resources

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.rapurc.api.client.apis.UsagesApi
import fi.metatavu.rapurc.api.client.infrastructure.ApiClient
import fi.metatavu.rapurc.api.client.infrastructure.ClientException
import fi.metatavu.rapurc.api.client.models.Metadata
import fi.metatavu.rapurc.api.client.models.Usage
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.impl.ApiTestBuilderResource
import org.junit.Assert
import org.junit.Assert.fail
import java.util.*

/**
 * Test resource for Waste Material API
 */
class UsageTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<Usage, ApiClient?>(testBuilder, apiClient) {

    override fun clean(usage: Usage) {
        api.deleteUsage(usage.id!!)
    }

    override fun getApi(): UsagesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return UsagesApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Creates new usage object
     *
     * @return created usage
     */
    fun create(): Usage {
        return addClosable(api.createUsage(Usage(name = "default_usage", metadata = Metadata())))
    }

    /**
     * Creates new usage object
     *
     * @param usage new usage
     * @return created usage
     */
    fun create(usage: Usage): Usage {
        return addClosable(api.createUsage(usage))
    }

    /**
     * Finds usage
     *
     * @param id usage id
     * @return found usage
     */
    fun find(id: UUID): Usage {
        return api.findUsage(id)
    }

    /**
     * Lists all usages
     *
     * @return usages
     */
    fun list(): Array<Usage> {
        return api.listUsages()
    }

    /**
     * Updates usage
     *
     * @param usageId id
     * @param usage new usage
     * @return updated usage
     */
    fun update(usageId: UUID, usage: Usage): Usage {
        return api.updateUsage(usageId, usage)
    }

    /**
     * Deletes usage
     *
     * @param usageId usage id to delete
     */
    fun delete(usageId: UUID) {
        api.deleteUsage(usageId)
        removeCloseable { closable: Any? ->
            if (closable !is Usage) {
                return@removeCloseable false
            }
            closable.id == usageId
        }
    }

    /**
     * Asserts the amount of usage records for a survey
     *
     * @param expected expected status
     */
    fun assertCount(expected: Int) {
        Assert.assertEquals(
            expected,
            api.listUsages().size
        )
    }

    /**
     * Asserts that finding waste material fails with the status
     *
     * @param expectedStatus expected status
     * @param usageId id
     */
    fun assertFindFailStatus(expectedStatus: Int, usageId: UUID) {
        try {
            api.findUsage(usageId)
            fail(String.format("Expected find to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts create status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param usage usage id
     */
    fun assertCreateFailStatus(expectedStatus: Int, usage: Usage) {
        try {
            api.createUsage(usage)
            fail(String.format("Expected create to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts update status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param usageId usage id
     * @param usage usage
     */
    fun assertUpdateFailStatus(expectedStatus: Int, usageId: UUID, usage: Usage) {
        try {
            update(usageId, usage)
            fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts delete status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param usageId usage id to delete
     */
    fun assertDeleteFailStatus(expectedStatus: Int, usageId: UUID) {
        try {
            api.deleteUsage(usageId)
            fail(String.format("Expected delete to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }
}
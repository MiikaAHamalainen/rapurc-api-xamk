package fi.metatavu.rapurc.api.test.functional.resources

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.rapurc.api.client.apis.WasteCategoryApi
import fi.metatavu.rapurc.api.client.infrastructure.ApiClient
import fi.metatavu.rapurc.api.client.infrastructure.ClientException
import fi.metatavu.rapurc.api.client.models.Metadata
import fi.metatavu.rapurc.api.client.models.WasteCategory
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.impl.ApiTestBuilderResource
import org.junit.Assert
import java.util.*

/**
 * Test resource for Waste Category API
 */
class WasteCategoryTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<WasteCategory, ApiClient?>(testBuilder, apiClient) {

    val wasteCategory = WasteCategory(
        name = "metal waste",
        ewcCode = "123C",
        metadata = Metadata()
    )

    override fun clean(wasteCategory: WasteCategory) {
        api.deleteWasteCategory(wasteCategory.id!!)
    }

    override fun getApi(): WasteCategoryApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return WasteCategoryApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Creates default waste category
     *
     * @return new waste category
     */
    fun createDefault(): WasteCategory {
        return create(wasteCategory)
    }

    /**
     * Creates new waste category object
     *
     * @param wasteCategory new data
     * @return created waste category
     */
    fun create(wasteCategory: WasteCategory): WasteCategory {
        return addClosable(api.createWasteCategory(wasteCategory))
    }

    /**
     * Finds waste category
     *
     * @param id waste category id
     * @return found waste category
     */
    fun find(id: UUID): WasteCategory {
        return api.findWasteCategory(id)
    }

    /**
     * Lists all waste category
     *
     * @return waste category list
     */
    fun list(): Array<WasteCategory> {
        return api.listWasteCategories()
    }

    /**
     * Updates waste category
     *
     * @param wasteCategoryId id
     * @param wasteCategory new data
     * @return updated waste category
     */
    fun update(wasteCategoryId: UUID, wasteCategory: WasteCategory): WasteCategory {
        return api.updateWasteCategory(wasteCategoryId, wasteCategory)
    }

    /**
     * Deletes waste category
     *
     * @param id waste category to delete
     */
    fun delete(id: UUID) {
        api.deleteWasteCategory(id)
        removeCloseable { closable: Any? ->
            if (closable !is WasteCategory) {
                return@removeCloseable false
            }
            closable.id == id
        }
    }

    /**
     * Asserts the amount of waste category records for a survey
     *
     * @param expected expected status
     */
    fun assertCount(expected: Int) {
        Assert.assertEquals(
            expected,
            api.listWasteCategories().size
        )
    }

    /**
     * Asserts that finding waste category fails with the status
     *
     * @param expectedStatus expected status
     * @param ud id
     */
    fun assertFindFailStatus(expectedStatus: Int, ud: UUID) {
        try {
            api.findWasteCategory(ud)
            Assert.fail(String.format("Expected find to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts create status fails with given status code
     *
     * @param expectedStatus expected status code
     */
    fun assertCreateFailStatus(expectedStatus: Int) {
        try {
            create(wasteCategory)
            Assert.fail(String.format("Expected create to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts update status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param wasteCategoryId id
     * @param wasteCategory waste category information
     */
    fun assertUpdateFailStatus(expectedStatus: Int, wasteCategoryId: UUID, wasteCategory: WasteCategory) {
        try {
            update(wasteCategoryId, wasteCategory)
            Assert.fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts delete status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param wasteCategoryId waste category to delete
     */
    fun assertDeleteFailStatus(expectedStatus: Int, wasteCategoryId: UUID) {
        try {
            api.deleteWasteCategory(wasteCategoryId)
            Assert.fail(String.format("Expected delete to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }
}
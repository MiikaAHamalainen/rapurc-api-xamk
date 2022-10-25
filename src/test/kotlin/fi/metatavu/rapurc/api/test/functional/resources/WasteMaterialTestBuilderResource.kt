package fi.metatavu.rapurc.api.test.functional.resources

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.rapurc.api.client.apis.WasteMaterialApi
import fi.metatavu.rapurc.api.client.infrastructure.ApiClient
import fi.metatavu.rapurc.api.client.infrastructure.ClientException
import fi.metatavu.rapurc.api.client.models.Metadata
import fi.metatavu.rapurc.api.client.models.WasteMaterial
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.impl.ApiTestBuilderResource
import org.junit.Assert
import java.util.*

/**
 * Test resource for Waste Material API
 */
class WasteMaterialTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<WasteMaterial, ApiClient?>(testBuilder, apiClient) {

    override fun clean(wasteMaterial: WasteMaterial) {
        api.deleteWasteMaterial(wasteMaterial.id!!)
    }

    override fun getApi(): WasteMaterialApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return WasteMaterialApi(testBuilder.settings.apiBasePath)
    }

    val wasteMaterial = WasteMaterial(
        name = "brick",
        ewcSpecificationCode = "111",
        metadata = Metadata(),
        wasteCategoryId = UUID.randomUUID()
    )

    /**
     * Creates default waste material with selected category
     *
     * @param wasteCategoryId waste category id
     * @return created waste material
     */
    fun create(wasteCategoryId: UUID): WasteMaterial {
        return create(
            wasteMaterial.copy(
                wasteCategoryId = wasteCategoryId
            )
        )
    }

    /**
     * Creates new waste material object
     *
     * @param wasteMaterial new data
     * @return created waste material
     */
    fun create(wasteMaterial: WasteMaterial): WasteMaterial {
        return addClosable(api.createWasteMaterial(wasteMaterial))
    }

    /**
     * Finds waste material
     *
     * @param id waste material id
     * @return found waste material
     */
    fun find(id: UUID): WasteMaterial {
        return api.findWasteMaterial(id)
    }

    /**
     * Lists all waste materials
     *
     * @return waste material list
     */
    fun list(): Array<WasteMaterial> {
        return api.listWasteMaterials()
    }

    /**
     * Updates waste material
     *
     * @param wasteMaterialId id
     * @param wasteMaterial new data
     * @return updated building
     */
    fun update(wasteMaterialId: UUID, wasteMaterial: WasteMaterial): WasteMaterial {
        return api.updateWasteMaterial(wasteMaterialId, wasteMaterial)
    }

    /**
     * Deletes waste material
     *
     * @param wasteMaterialId waste material to delete
     */
    fun delete(wasteMaterialId: UUID) {
        api.deleteWasteMaterial(wasteMaterialId)
        removeCloseable { closable: Any? ->
            if (closable !is WasteMaterial) {
                return@removeCloseable false
            }
            closable.id == wasteMaterialId
        }
    }

    /**
     * Asserts the amount of waste material records for a survey
     *
     * @param expected expected status
     */
    fun assertCount(expected: Int) {
        Assert.assertEquals(
            expected,
            api.listWasteMaterials().size
        )
    }

    /**
     * Asserts that finding waste material fails with the status
     *
     * @param expectedStatus expected status
     * @param wasteMaterialId id
     */
    fun assertFindFailStatus(expectedStatus: Int, wasteMaterialId: UUID) {
        try {
            api.findWasteMaterial(wasteMaterialId)
            Assert.fail(String.format("Expected find to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts create status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param wasteCategoryId waste category id
     */
    fun assertCreateFailStatus(expectedStatus: Int, wasteCategoryId: UUID) {
        try {
            create(wasteCategoryId)
            Assert.fail(String.format("Expected create to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts update status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param wasteMaterialId survey id
     * @param wasteMaterial waste material information
     */
    fun assertUpdateFailStatus(expectedStatus: Int, wasteMaterialId: UUID, wasteMaterial: WasteMaterial) {
        try {
            update(wasteMaterialId, wasteMaterial)
            Assert.fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts delete status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param wasteMaterialId waste material to delete
     */
    fun assertDeleteFailStatus(expectedStatus: Int, wasteMaterialId: UUID) {
        try {
            api.deleteWasteMaterial(wasteMaterialId)
            Assert.fail(String.format("Expected delete to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }
}
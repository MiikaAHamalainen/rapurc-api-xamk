package fi.metatavu.rapurc.api.test.functional.resources

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.rapurc.api.client.apis.ReusableMaterialApi
import fi.metatavu.rapurc.api.client.infrastructure.ApiClient
import fi.metatavu.rapurc.api.client.infrastructure.ClientException
import fi.metatavu.rapurc.api.client.models.ReusableMaterial
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.impl.ApiTestBuilderResource
import org.junit.Assert
import java.util.*

/**
 * Test builder resource for ReusableMaterials API
 */
class ReusableMaterialTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<ReusableMaterial, ApiClient?>(testBuilder, apiClient) {

    override fun getApi(): ReusableMaterialApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return ReusableMaterialApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Creates new reusable Material object
     *
     * @param reusableMaterial reusable Material
     * @return created ReusableMaterial
     */
    fun create(reusableMaterial: ReusableMaterial): ReusableMaterial {
        val result = api.createReusableMaterial(reusableMaterial)
        return addClosable(result)
    }

    /**
     * Finds reusable Material
     *
     * @param materialId material id
     * @return found OwnerInformation
     */
    fun findMaterial(materialId: UUID): ReusableMaterial {
        return api.findReusableMaterial(materialId)
    }

    /**
     * Lists all reusable Material entries for a survey
     *
     * @return reusable material list
     */
    fun list(): Array<ReusableMaterial> {
        return api.listReusableMaterials()
    }

    /**
     * Updates reusable material
     *
     * @param materialId survey id
     * @param reusableMaterial reusable material
     * @return updated reusable material
     */
    fun update(materialId: UUID, reusableMaterial: ReusableMaterial): ReusableMaterial {
        return api.updateReusableMaterial(materialId, reusableMaterial)
    }

    /**
     * Deletes reusable material from the API
     *
     * @param reusableMaterial reusable material to delete
     */
    fun delete(reusableMaterial: ReusableMaterial) {
        api.deleteReusableMaterial(reusableMaterial.id!!)
        removeCloseable { closable: Any? ->
            if (closable !is ReusableMaterial) {
                return@removeCloseable false
            }
            closable.id == reusableMaterial.id
        }
    }

    /**
     * Asserts the amount of reusable material records for a survey
     *
     * @param expected expected status
     */
    fun assertCount(expected: Int) {
        Assert.assertEquals(
            expected,
            api.listReusableMaterials().size
        )
    }

    /**
     * Asserts that finding reusable material fails with the status
     *
     * @param expectedStatus expected status
     * @param materialId material id
     */
    fun assertFindFailStatus(expectedStatus: Int, materialId: UUID) {
        try {
            api.findReusableMaterial(materialId)
            Assert.fail(String.format("Expected find to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts create status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param reusableMaterial reusable Material
     */
    fun assertCreateFailStatus(expectedStatus: Int, reusableMaterial: ReusableMaterial) {
        try {
            create(reusableMaterial)
            Assert.fail(String.format("Expected create to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts update status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param materialId material id
     * @param reusableMaterial reusable material
     */
    fun assertUpdateFailStatus(expectedStatus: Int, materialId: UUID, reusableMaterial: ReusableMaterial) {
        try {
            update(materialId, reusableMaterial)
            Assert.fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts delete status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param reusableMaterial reusable material to delete
     */
    fun assertDeleteFailStatus(expectedStatus: Int, reusableMaterial: ReusableMaterial) {
        try {
            api.deleteReusableMaterial(reusableMaterial.id!!)
            Assert.fail(String.format("Expected delete to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts list status fails with given status code
     *
     * @param expectedStatus expected status code
     */
    fun assertListFailStatus(expectedStatus: Int) {
        try {
            api.listReusableMaterials()
            Assert.fail(String.format("Expected list to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    override fun clean(reusableMaterial: ReusableMaterial) {
        api.deleteReusableMaterial(reusableMaterial.id!!)
    }
}
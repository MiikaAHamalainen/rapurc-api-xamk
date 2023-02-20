package fi.metatavu.rapurc.api.test.functional.resources

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.rapurc.api.client.apis.HazardousMaterialApi
import fi.metatavu.rapurc.api.client.infrastructure.ApiClient
import fi.metatavu.rapurc.api.client.infrastructure.ClientException
import fi.metatavu.rapurc.api.client.models.HazardousMaterial
import fi.metatavu.rapurc.api.client.models.LocalizedValue
import fi.metatavu.rapurc.api.client.models.Metadata
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.impl.ApiTestBuilderResource
import org.junit.Assert
import java.util.*

/**
 * Test resource for Hazardous Material API
 */
class HazardousMaterialTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<HazardousMaterial, ApiClient?>(testBuilder, apiClient) {

    override fun clean(hazardousMaterial: HazardousMaterial) {
        api.deleteHazardousMaterial(hazardousMaterial.id!!)
    }

    override fun getApi(): HazardousMaterialApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return HazardousMaterialApi(testBuilder.settings.apiBasePath)
    }

    val defaultHazardousMaterial = HazardousMaterial(
        localizedNames = arrayOf(
            LocalizedValue("en", "asbest")
        ),
        ewcSpecificationCode = "111",
        metadata = Metadata(),
        wasteCategoryId = UUID.randomUUID()
    )

    /**
     * Creates default hazardous material with selected category
     *
     * @param wasteCategoryId waste category id
     * @return created hazardous material
     */
    fun create(wasteCategoryId: UUID): HazardousMaterial {
        return create(
            defaultHazardousMaterial.copy(
                wasteCategoryId = wasteCategoryId
            )
        )
    }

    /**
     * Creates new hazardous material object
     *
     * @param hazardousMaterial new data
     * @return created hazardous material
     */
    fun create(hazardousMaterial: HazardousMaterial): HazardousMaterial {
        return addClosable(api.createHazardousMaterial(hazardousMaterial))
    }

    /**
     * Finds hazardous material
     *
     * @param id hazardous material id
     * @return found hazardous material
     */
    fun find(id: UUID): HazardousMaterial {
        return api.findHazardousMaterial(id)
    }

    /**
     * Lists all hazardous materials
     *
     * @return hazardous material list
     */
    fun list(): Array<HazardousMaterial> {
        return api.listHazardousMaterials()
    }

    /**
     * Updates hazardous material
     *
     * @param materialId id
     * @param hazardousMaterial new data
     * @return updated hazardous material
     */
    fun update(materialId: UUID, hazardousMaterial: HazardousMaterial): HazardousMaterial {
        return api.updateHazardousMaterial(materialId, hazardousMaterial)
    }

    /**
     * Deletes hazardous material
     *
     * @param materialId hazardous material to delete
     */
    fun delete(materialId: UUID) {
        api.deleteHazardousMaterial(materialId)
        removeCloseable { closable: Any? ->
            if (closable !is HazardousMaterial) {
                return@removeCloseable false
            }
            closable.id == materialId
        }
    }

    /**
     * Asserts the amount of hazardous material records for a survey
     *
     * @param expected expected status
     */
    fun assertCount(expected: Int) {
        Assert.assertEquals(
            expected,
            api.listHazardousMaterials().size
        )
    }

    /**
     * Asserts that finding hazardous material fails with the status
     *
     * @param expectedStatus expected status
     * @param materialId id
     */
    fun assertFindFailStatus(expectedStatus: Int, materialId: UUID) {
        try {
            api.findHazardousMaterial(materialId)
            Assert.fail(String.format("Expected find to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts create status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param categoryId hazardous material category id
     */
    fun assertCreateFailStatus(expectedStatus: Int, categoryId: UUID) {
        try {
            create(categoryId)
            Assert.fail(String.format("Expected create to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts update status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param hazMaterialId hazardous material id
     * @param hazardousMaterial hazardous material information
     */
    fun assertUpdateFailStatus(expectedStatus: Int, hazMaterialId: UUID, hazardousMaterial: HazardousMaterial) {
        try {
            update(hazMaterialId, hazardousMaterial)
            Assert.fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts delete status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param hazardousMaterialId hazardous material to delete
     */
    fun assertDeleteFailStatus(expectedStatus: Int, hazardousMaterialId: UUID) {
        try {
            api.deleteHazardousMaterial(hazardousMaterialId)
            Assert.fail(String.format("Expected delete to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }
}
package fi.metatavu.rapurc.api.test.functional.resources

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.rapurc.api.client.apis.BuildingTypesApi
import fi.metatavu.rapurc.api.client.apis.BuildingsApi
import fi.metatavu.rapurc.api.client.infrastructure.ApiClient
import fi.metatavu.rapurc.api.client.infrastructure.ClientException
import fi.metatavu.rapurc.api.client.models.*
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.impl.ApiTestBuilderResource
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import java.util.*

/**
 * Test builder resource for Building Types API
 */
class BuildingTypeTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<BuildingType, ApiClient?>(testBuilder, apiClient) {

    val buildingType = BuildingType(
        name = "stndard building type",
        code = "code",
        metadata = Metadata()
    )

    override fun getApi(): BuildingTypesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return BuildingTypesApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Creates new building type object
     *
     * @return created building type
     */
    fun create(): BuildingType? {
        return addClosable(api.createBuildingType(buildingType))
    }


    /**
     * Finds building type
     *
     * @param buildingTypeId building type id
     * @return found building type
     */
    fun findBuilding(buildingTypeId: UUID): BuildingType {
        return api.findBuildingType(buildingTypeId)
    }

    /**
     * Lists all building type entries for a survey
     *
     * @return building types list
     */
    fun list(): Array<BuildingType> {
        return api.listBuildingTypes()
    }

    /**
     * Updates building type
     *
     * @param buildingTypeId building type id
     * @param buildingType new building type data
     * @return updated building
     */
    fun update(buildingTypeId: UUID, buildingType: BuildingType): BuildingType {
        return api.updateBuildingType(buildingTypeId, buildingType)
    }

    /**
     * Deletes building type from the API
     *
     * @param buildingType building type to delete
     */
    fun delete(buildingType: BuildingType) {
        api.deleteBuildingType(buildingType.id!!)
        removeCloseable { closable: Any? ->
            if (closable !is BuildingType) {
                return@removeCloseable false
            }
            closable.id == buildingType.id
        }
    }

    /**
     * Asserts the amount of building type records
     *
     * @param expected expected status
     */
    fun assertCount(expected: Int) {
        assertEquals(
            expected,
            api.listBuildingTypes().size
        )
    }

    /**
     * Asserts that finding building type fails with the status
     *
     * @param expectedStatus expected status
     * @param buildingTypeId building type id
     */
    fun assertFindFailStatus(expectedStatus: Int, buildingTypeId: UUID?) {
        try {
            api.findBuildingType(buildingTypeId!!)
            fail(String.format("Expected find to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts create building type fails with given status code
     *
     * @param expectedStatus expected status code
     */
    fun assertCreateFailStatus(expectedStatus: Int) {
        try {
            create()
            fail(String.format("Expected create to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }


    /**
     * Asserts update building type fails with given status code
     *
     * @param expectedStatus expected status code
     * @param buildingTypeId building type id
     * @param buildingType building type
     */
    fun assertUpdateFailStatus(expectedStatus: Int, buildingTypeId: UUID, buildingType: BuildingType) {
        try {
            update(buildingTypeId, buildingType)
            fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts delete building type fails with given status code
     *
     * @param expectedStatus expected status code
     * @param buildingType building type to delete
     */
    fun assertDeleteFailStatus(expectedStatus: Int, buildingType: BuildingType) {
        try {
            api.deleteBuildingType(buildingType.id!!)
            fail(String.format("Expected delete to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts list building type fails with given status code
     *
     * @param expectedStatus expected status code
     */
    fun assertListFailStatus(expectedStatus: Int) {
        try {
            api.listBuildingTypes()
            fail(String.format("Expected list to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    override fun clean(buildingType: BuildingType) {
        api.deleteBuildingType(buildingType.id!!)
    }

    /**
     * Removes the building type info from the closables list
     *
     * @param buildingType building type info
     */
    fun markAsDeleted(buildingType: BuildingType) {
        removeCloseable { closable: Any? ->
            if (closable !is BuildingType) {
                return@removeCloseable false
            }
            closable.id == buildingType.id
        }
    }
}
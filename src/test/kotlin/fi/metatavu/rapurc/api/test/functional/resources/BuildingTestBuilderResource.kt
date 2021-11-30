package fi.metatavu.rapurc.api.test.functional.resources

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
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
 * Test builder resource for Buildings API
 */
class BuildingTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<Building, ApiClient?>(testBuilder, apiClient) {

    val buildingData = Building(
        surveyId = UUID.randomUUID(),
        propertyId = "property id 1",
        buildingId = "building id 1",
        floors = 5,
        basements = 1,
        facadeMaterial = "red bricks",
        otherStructures = arrayOf(
            OtherStructure(
                name = "b bike house",
                description = "bike house"
            ),
            OtherStructure(
                name = "a bike house",
                description = "bike house"
            )
        ),
        address = Address(
            city = "mikkeli",
            postCode = "1222",
            streetAddress = "qqq"
        ),
        metadata = Metadata()
    )

    override fun getApi(): BuildingsApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return BuildingsApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Creates new building object
     *
     * @param surveyId survey id it belongs to
     * @param building building information
     * @return created building
     */
    fun create(surveyId: UUID, building: Building): Building? {
        val result = api.createBuilding(surveyId, building)
        return addClosable(result)
    }

    /**
     * Creates new building object from default
     *
     * @param surveyId survey id it belongs to
     * @param buildingSurveyId building survey id
     * @return created building
     */
    fun create(surveyId: UUID, buildingSurveyId: UUID): Building? {
        val result = api.createBuilding(surveyId, buildingData.copy(surveyId = buildingSurveyId))
        return addClosable(result)
    }

    /**
     * Finds building
     *
     * @param surveyId survey id
     * @param buildingId building id
     * @return found building
     */
    fun findBuilding(surveyId: UUID, buildingId: UUID): Building {
        return api.findBuilding(surveyId, buildingId)
    }

    /**
     * Lists all building entries for a survey
     *
     * @param surveyId survey id
     * @return buildings list
     */
    fun list(surveyId: UUID): Array<Building> {
        return api.listBuildings(surveyId)
    }

    /**
     * Updates building
     *
     * @param surveyId survey id
     * @param buildingId building id
     * @param building new building data
     * @return updated building
     */
    fun update(surveyId: UUID, buildingId: UUID, building: Building): Building {
        return api.updateBuilding(surveyId, buildingId, building)
    }

    /**
     * Deletes building information from the API
     *
     * @param building building information to delete
     */
    fun delete(building: Building) {
        api.deleteBuilding(building.surveyId, building.id!!)
        removeCloseable { closable: Any? ->
            if (closable !is Building) {
                return@removeCloseable false
            }
            closable.id == building.id
        }
    }

    /**
     * Asserts the amount of building records for a survey
     *
     * @param expected expected status
     * @param surveyId survey id
     */
    fun assertCount(expected: Int, surveyId: UUID) {
        assertEquals(
            expected,
            api.listBuildings(
                surveyId
            ).size
        )
    }

    /**
     * Asserts that finding building fails with the status
     *
     * @param expectedStatus expected status
     * @param surveyId survey id
     * @param buildingId building id
     */
    fun assertFindFailStatus(expectedStatus: Int, surveyId: UUID?, buildingId: UUID?) {
        try {
            api.findBuilding(surveyId!!, buildingId!!)
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
     * @param building building information
     */
    fun assertCreateFailStatus(expectedStatus: Int, surveyId: UUID?, building: Building) {
        try {
            create(surveyId!!, building)
            fail(String.format("Expected create to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }


    /**
     * Asserts create status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param surveyId survey id
     * @param buildingSurveyId building survey id
     */
    fun assertCreateFailStatus(expectedStatus: Int, surveyId: UUID?, buildingSurveyId: UUID) {
        try {
            create(surveyId!!, buildingData.copy(surveyId = buildingSurveyId))
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
     * @param buildingId building id
     * @param building building information
     */
    fun assertUpdateFailStatus(expectedStatus: Int, surveyId: UUID, buildingId: UUID, building: Building) {
        try {
            update(surveyId, buildingId, building)
            fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts delete status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param building building to delete
     */
    fun assertDeleteFailStatus(expectedStatus: Int, building: Building) {
        try {
            api.deleteBuilding(building.surveyId, building.id!!)
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
            api.listBuildings(
                surveyId!!
            )
            fail(String.format("Expected list to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    override fun clean(building: Building) {
        api.deleteBuilding(building.surveyId, building.id!!)
    }

    /**
     * Removes the building info from the closables list
     *
     * @param building building info
     */
    fun markAsDeleted(building: Building) {
        removeCloseable { closable: Any? ->
            if (closable !is Building) {
                return@removeCloseable false
            }
            closable.id == building.id
        }
    }
}
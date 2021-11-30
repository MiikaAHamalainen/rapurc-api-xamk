package fi.metatavu.rapurc.api.test.functional.tests

import fi.metatavu.rapurc.api.client.models.*
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.resources.KeycloakTestResource
import fi.metatavu.rapurc.api.test.functional.resources.MysqlTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Tests for Building type API
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlTestResource::class)
)
class BuildingTypeTestIT {

    /**
     * Tests building type creation
     */
    @Test
    fun create() {
        TestBuilder().use {
            it.userA.buildingTypes.assertCreateFailStatus(403)
            val buildingType = it.admin.buildingTypes.create()

            assertNotNull(buildingType)
            assertEquals(it.userA.buildingTypes.buildingType.name, buildingType!!.name)
            assertEquals(it.userA.buildingTypes.buildingType.code, buildingType.code)
        }
    }

    /**
     * Tests listing building types
     */
    @Test
    fun list() {
        TestBuilder().use {
            it.admin.buildingTypes.create()
            it.admin.buildingTypes.create()
            it.admin.buildingTypes.create()

            it.userA.buildingTypes.assertCount(3)
        }
    }

    /**
     * Tests finding building type
     */
    @Test
    fun find() {
        TestBuilder().use {
            val createdBuildingType = it.admin.buildingTypes.create()
            it.admin.buildingTypes.assertFindFailStatus(404, UUID.randomUUID())
            val foundBuildingType = it.admin.buildingTypes.findBuilding(createdBuildingType!!.id!!)

            assertNotNull(foundBuildingType)
            assertEquals(createdBuildingType.id, foundBuildingType.id)
            assertEquals(createdBuildingType.name, foundBuildingType.name)
            assertEquals(createdBuildingType.code, foundBuildingType.code)
        }
    }

    /**
     * Tests building type updates
     */
    @Test
    fun update() {
        TestBuilder().use {
            val createdBuildingType = it.admin.buildingTypes.create()
            val updateBuildingType = BuildingType(
                name = "new name",
                code = "new code",
                metadata = Metadata()
            )

            it.userA.buildingTypes.assertUpdateFailStatus(403, createdBuildingType!!.id!!, updateBuildingType)
            val updatedBuilding = it.admin.buildingTypes.update(createdBuildingType.id!!, updateBuildingType)

            assertNotNull(updatedBuilding)
            assertEquals(updateBuildingType.name, updatedBuilding.name)
            assertEquals(updateBuildingType.code, updatedBuilding.code)
        }
    }

    /**
     * Tests building type deletion
     */
    @Test
    fun delete() {
        TestBuilder().use {
            val createdSurveyA1 = it.userA.surveys.create()
            val buildingType = it.admin.buildingTypes.create()
            val building1 = it.userA.buildings.create(
                surveyId = createdSurveyA1.id!!,
                buildingSurveyId = createdSurveyA1.id,
                buildingTypeId = buildingType!!.id
            )

            it.userA.buildingTypes.assertDeleteFailStatus(403, buildingType)
            it.admin.buildingTypes.assertDeleteFailStatus(409, buildingType)
            it.admin.buildings.delete(building1!!)
            it.admin.buildingTypes.delete(buildingType)
        }
    }
}
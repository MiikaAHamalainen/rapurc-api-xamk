package fi.metatavu.rapurc.api.test.functional.tests

import fi.metatavu.rapurc.api.client.models.Address
import fi.metatavu.rapurc.api.client.models.Building
import fi.metatavu.rapurc.api.client.models.OtherStructure
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.resources.KeycloakTestResource
import fi.metatavu.rapurc.api.test.functional.resources.MysqlTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Tests for Buildings API
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlTestResource::class)
)
class BuildingTestIT {

    val buildingData = Building(
        surveyId = UUID.randomUUID(),
        propertyId = "property id 1",
        buildingId = "building id 1",
        floors = 5,
        basements = 1,
        facadeMaterial = "red bricks",
        otherStructures = arrayOf(
            OtherStructure(
                name = "bike house",
                description = "bike house"
            )
        ),
        address = Address(
            city = "mikkeli",
            postCode = "1222",
            streetAddress = "qqq"
        )
    )

    /**
     * Tests building creation
     */
    @Test
    fun create() {
        TestBuilder().use {
            val createdSurveyA1 = it.userA.surveys.create()

            it.userB.buildings.assertCreateFailStatus(
                403,
                createdSurveyA1.id,
                buildingData.copy(surveyId = createdSurveyA1.id!!)
            )

            val createdBuilding = it.userA.buildings.create(
                createdSurveyA1.id, buildingData.copy(
                    surveyId = createdSurveyA1.id
                )
            )

            assertNotNull(createdBuilding)
            assertEquals(buildingData.propertyId, createdBuilding!!.propertyId)
            assertEquals(buildingData.buildingId, createdBuilding.buildingId)
            assertEquals(buildingData.floors, createdBuilding.floors)
            assertEquals(buildingData.facadeMaterial, createdBuilding.facadeMaterial)

            assertEquals(
                buildingData.otherStructures!![0].name,
                createdBuilding.otherStructures!![0].name
            )
            assertEquals(
                buildingData.otherStructures!![0].description,
                createdBuilding.otherStructures[0].description
            )
        }
    }

    /**
     * Tests listing buildings
     */
    @Test
    fun list() {
        TestBuilder().use {
            val createdSurveyA1 = it.userA.surveys.create()
            val createdSurveyA2 = it.userA.surveys.create()
            it.userA.buildings.create(
                createdSurveyA1.id!!, buildingData.copy(
                    surveyId = createdSurveyA1.id
                )
            )
            it.userA.buildings.create(
                createdSurveyA2.id!!, buildingData.copy(
                    surveyId = createdSurveyA2.id
                )
            )

            it.userA.buildings.assertCount(1, createdSurveyA1.id)
            it.userA.buildings.assertCount(1, createdSurveyA2.id)
            it.userA.buildings.assertListFailStatus(404, UUID.randomUUID())
            it.userB.buildings.assertListFailStatus(403, createdSurveyA2.id)
        }
    }

    /**
     * Tests finding buildings
     */
    @Test
    fun find() {
        TestBuilder().use {
            val createdSurveyA1 = it.userA.surveys.create()
            val createdSurveyB1 = it.userB.surveys.create()
            val building1 = it.userA.buildings.create(
                createdSurveyA1.id!!, buildingData.copy(
                    surveyId = createdSurveyA1.id
                )
            )
            val building2 = it.userB.buildings.create(
                createdSurveyB1.id!!, buildingData.copy(
                    surveyId = createdSurveyB1.id
                )
            )

            val foundBuilding = it.userA.buildings.findBuilding(createdSurveyA1.id, building1!!.id!!)
            assertNotNull(foundBuilding)
            it.userA.buildings.assertFindFailStatus(403, createdSurveyA1.id, building2!!.id!!)
            it.userA.buildings.assertFindFailStatus(404, createdSurveyA1.id, UUID.randomUUID())
            it.userB.buildings.assertFindFailStatus(403, createdSurveyA1.id, building1.id!!)
        }
    }

    /**
     * Tests building updates
     */
    @Test
    fun update() {
        TestBuilder().use {
            val createdSurveyA1 = it.userA.surveys.create()
            val building1 = it.userA.buildings.create(
                createdSurveyA1.id!!, buildingData.copy(
                    surveyId = createdSurveyA1.id
                )
            )

            val newBuldingInfo = building1!!.copy(
                propertyId = "property id 2",
                facadeMaterial = "concrete",
                classificationCode = "code3",
                constructionYear = 1993,
                otherStructures = arrayOf(
                    OtherStructure(
                        name = "trash house",
                        description = "bike house"
                    ),
                    OtherStructure(
                        name = "trash house",
                        description = "trash house"
                    )
                )
            )

            val updatedBuilding = it.userA.buildings.update(createdSurveyA1.id, building1.id!!, newBuldingInfo)

            assertNotNull(updatedBuilding)
            assertEquals(2, updatedBuilding.otherStructures!!.size)
            assertEquals(newBuldingInfo.facadeMaterial, updatedBuilding.facadeMaterial)
            assertEquals(newBuldingInfo.propertyId, updatedBuilding.propertyId)
        }
    }

    /**
     * Tests building deletion
     */
    @Test
    fun delete() {
        TestBuilder().use {
            val createdSurveyA1 = it.userA.surveys.create()
            val createdSurveyB1 = it.userB.surveys.create()
            val building1 = it.userA.buildings.create(
                createdSurveyA1.id!!, buildingData.copy(
                    surveyId = createdSurveyA1.id
                )
            )
            val building2 = it.userB.buildings.create(
                createdSurveyB1.id!!, buildingData.copy(
                    surveyId = createdSurveyB1.id
                )
            )

            it.userA.buildings.assertDeleteFailStatus(403, building2!!)
            it.userA.buildings.delete(building1!!)
            it.userB.buildings.delete(building2)
            it.admin.buildings.assertCount(0, createdSurveyA1.id)
            it.admin.buildings.assertCount(0, createdSurveyB1.id)
        }
    }
}
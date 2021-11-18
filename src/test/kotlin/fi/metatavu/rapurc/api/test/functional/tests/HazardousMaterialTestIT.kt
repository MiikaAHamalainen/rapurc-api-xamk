package fi.metatavu.rapurc.api.test.functional.tests

import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.resources.KeycloakTestResource
import fi.metatavu.rapurc.api.test.functional.resources.MysqlTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Tests for Hazardous Material API
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlTestResource::class)
)
class HazardousMaterialTestIT {

    /**
     * Tests hazardous material creation
     */
    @Test
    fun create() {
        TestBuilder().use { testBuilder ->
            var materialID: UUID? = null
            try {
                val categoryId = testBuilder.admin.wasteCategories.createDefault().id
                testBuilder.userA.hazMaterials.assertCreateFailStatus(403, UUID.randomUUID())
                testBuilder.admin.hazMaterials.assertCreateFailStatus(404, UUID.randomUUID())

                val createdMaterial = testBuilder.admin.hazMaterials.create(categoryId!!)
                materialID = createdMaterial.id

                assertNotNull(createdMaterial.id)
                assertNotNull(createdMaterial.metadata.createdAt)
                assertNotNull(createdMaterial.metadata.modifiedAt)
                assertNotNull(createdMaterial.metadata.creatorId)
                assertNotNull(createdMaterial.metadata.lastModifierId)
                assertEquals(categoryId, createdMaterial.wasteCategoryId)
                assertEquals(testBuilder.userA.hazMaterials.defaultHazardousMaterial.name, createdMaterial.name)
                assertEquals(testBuilder.userA.hazMaterials.defaultHazardousMaterial.ewcSpecificationCode, createdMaterial.ewcSpecificationCode)
            } finally {
                if (materialID != null) {
                    testBuilder.admin.hazMaterials.delete(materialID)
                }
            }
        }
    }

    /**
     * Tests hazardous material listing
     */
    @Test
    fun list() {
        TestBuilder().use { testBuilder ->
            val category = testBuilder.admin.wasteCategories.createDefault()
            testBuilder.admin.hazMaterials.create(category.id!!)
            testBuilder.admin.hazMaterials.create(category.id)
            testBuilder.userA.hazMaterials.assertCount(2)
        }
    }

    /**
     * Tests hazardous material search
     */
    @Test
    fun find() {
        TestBuilder().use { testBuilder ->
            val category = testBuilder.admin.wasteCategories.createDefault()
            val createdMaterial = testBuilder.admin.hazMaterials.create(category.id!!)

            testBuilder.userA.hazMaterials.assertFindFailStatus(404, UUID.randomUUID())
            val foundMaterial = testBuilder.userA.hazMaterials.find(createdMaterial.id!!)
            assertEquals(createdMaterial.id, foundMaterial.id)
            assertEquals(createdMaterial.wasteCategoryId, foundMaterial.wasteCategoryId)
            assertEquals(createdMaterial.name, foundMaterial.name)
            assertEquals(createdMaterial.ewcSpecificationCode, foundMaterial.ewcSpecificationCode)
        }
    }

    /**
     * Tests hazardous material updates and access rights
     */
    @Test
    fun update() {
        TestBuilder().use { testBuilder ->
            var materialId: UUID? = null
            try {
                val category1 = testBuilder.admin.wasteCategories.createDefault()
                val createdMaterial = testBuilder.admin.hazMaterials.create(category1.id!!)
                materialId = createdMaterial.id
                val category2 = testBuilder.admin.wasteCategories.create(
                    category1.copy(
                        name = "new category"
                    )
                )

                val updateData = createdMaterial.copy(
                    name = "new material",
                    wasteCategoryId = category2.id!!
                )

                testBuilder.userA.hazMaterials.assertUpdateFailStatus(403, UUID.randomUUID(), updateData)
                testBuilder.admin.hazMaterials.assertUpdateFailStatus(404, UUID.randomUUID(), updateData)
                testBuilder.admin.hazMaterials.assertUpdateFailStatus(
                    404,
                    createdMaterial.id!!,
                    updateData.copy(wasteCategoryId = UUID.randomUUID())
                )
                val updated = testBuilder.admin.hazMaterials.update(createdMaterial.id, updateData)
                assertEquals(updateData.wasteCategoryId, updated.wasteCategoryId)
                assertEquals(updateData.id, updated.id)
                assertEquals(updateData.name, updated.name)
            } finally {
                if (materialId != null) {
                    testBuilder.admin.hazMaterials.delete(materialId)
                }

            }
        }
    }

    /**
     * Tests hazardous material deletion
     */
    @Test
    fun delete() {
        TestBuilder().use { testBuilder ->
            val category = testBuilder.admin.wasteCategories.createDefault()
            val created = testBuilder.admin.hazMaterials.create(category.id!!)
            testBuilder.admin.hazMaterials.delete(created.id!!)
            testBuilder.userA.hazMaterials.assertCount(0)
        }
    }
}
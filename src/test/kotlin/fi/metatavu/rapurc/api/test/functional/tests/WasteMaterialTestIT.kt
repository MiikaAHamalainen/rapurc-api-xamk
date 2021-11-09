package fi.metatavu.rapurc.api.test.functional.tests

import fi.metatavu.rapurc.api.client.models.Metadata
import fi.metatavu.rapurc.api.client.models.WasteCategory
import fi.metatavu.rapurc.api.client.models.WasteMaterial
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
 * Tests for Waste Material API
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlTestResource::class)
)
class WasteMaterialTestIT {

    /**
     * Tests waste material creation
     */
    @Test
    fun create() {
        TestBuilder().use { testBuilder ->
            var materialID: UUID? = null
            try {
                val categoryId = testBuilder.admin.wasteCategories.createDefault().id
                testBuilder.userA.wasteMaterials.assertCreateFailStatus(403, UUID.randomUUID())
                testBuilder.admin.wasteMaterials.assertCreateFailStatus(404, UUID.randomUUID())

                val createdMaterial = testBuilder.admin.wasteMaterials.create(categoryId!!)
                materialID = createdMaterial.id

                assertNotNull(createdMaterial.id)
                assertNotNull(createdMaterial.metadata.createdAt)
                assertNotNull(createdMaterial.metadata.modifiedAt)
                assertNotNull(createdMaterial.metadata.creatorId)
                assertNotNull(createdMaterial.metadata.lastModifierId)
                assertEquals(categoryId, createdMaterial.wasteCategoryId)
                assertEquals(testBuilder.userA.wasteMaterials.wasteMaterial.name, createdMaterial.name)
                assertEquals(testBuilder.userA.wasteMaterials.wasteMaterial.ewcSpecificationCode, createdMaterial.ewcSpecificationCode)
            } finally {
                if (materialID != null) {
                    testBuilder.admin.wasteMaterials.delete(materialID)
                }
            }
        }
    }

    /**
     * Tests waste material listing
     */
    @Test
    fun list() {
        TestBuilder().use { testBuilder ->
            val category = testBuilder.admin.wasteCategories.createDefault()
            testBuilder.admin.wasteMaterials.create(category.id!!)
            testBuilder.admin.wasteMaterials.create(category.id)
            testBuilder.userA.wasteMaterials.assertCount(2)
        }
    }

    /**
     * Tests waste material search
     */
    @Test
    fun find() {
        TestBuilder().use { testBuilder ->
            val category = testBuilder.admin.wasteCategories.createDefault()
            val createdMaterial = testBuilder.admin.wasteMaterials.create(category.id!!)

            testBuilder.userA.wasteMaterials.assertFindFailStatus(404, UUID.randomUUID())
            val foundMaterial = testBuilder.userA.wasteMaterials.find(createdMaterial.id!!)
            assertEquals(createdMaterial.id, foundMaterial.id)
            assertEquals(createdMaterial.wasteCategoryId, foundMaterial.wasteCategoryId)
            assertEquals(createdMaterial.name, foundMaterial.name)
            assertEquals(createdMaterial.ewcSpecificationCode, foundMaterial.ewcSpecificationCode)
        }
    }

    /**
     * Tests waste material updates and access rights
     */
    @Test
    fun update() {
        TestBuilder().use { testBuilder ->
            var materialId: UUID? = null
            try {
                val category1 = testBuilder.admin.wasteCategories.createDefault()
                val createdMaterial = testBuilder.admin.wasteMaterials.create(category1.id!!)
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

                testBuilder.userA.wasteMaterials.assertUpdateFailStatus(403, UUID.randomUUID(), updateData)
                testBuilder.admin.wasteMaterials.assertUpdateFailStatus(404, UUID.randomUUID(), updateData)
                testBuilder.admin.wasteMaterials.assertUpdateFailStatus(
                    404,
                    createdMaterial.id!!,
                    updateData.copy(wasteCategoryId = UUID.randomUUID())
                )
                val updated = testBuilder.admin.wasteMaterials.update(createdMaterial.id, updateData)
                assertEquals(updateData.wasteCategoryId, updated.wasteCategoryId)
                assertEquals(updateData.id, updated.id)
                assertEquals(updateData.name, updated.name)
            } finally {
                if (materialId != null) {
                    testBuilder.admin.wasteMaterials.delete(materialId)
                }

            }
        }
    }

    /**
     * Tests waste material deletion
     */
    @Test
    fun delete() {
        TestBuilder().use { testBuilder ->
            val category = testBuilder.admin.wasteCategories.createDefault()
            val created = testBuilder.admin.wasteMaterials.create(category.id!!)
            testBuilder.admin.wasteMaterials.delete(created.id!!)
            testBuilder.userA.wasteMaterials.assertCount(0)
        }
    }
}
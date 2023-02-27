package fi.metatavu.rapurc.api.test.functional.tests

import fi.metatavu.rapurc.api.client.models.LocalizedValue
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
                assertEquals(testBuilder.userA.wasteMaterials.wasteMaterial.localizedNames[0].value, createdMaterial.localizedNames.find { it.language == "en" }!!.value)
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
            assertEquals(createdMaterial.localizedNames[0].value, foundMaterial!!.localizedNames.find { it.language == "en" }!!.value)
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
                val category2 = testBuilder.admin.wasteCategories.create(category1)

                val updateData = createdMaterial.copy(
                    wasteCategoryId = category2.id!!,
                    localizedNames = arrayOf(
                        LocalizedValue("en", "new material name en"),
                        LocalizedValue("fr", "new material name fr")
                    )
                )

                testBuilder.userA.wasteMaterials.assertUpdateFailStatus(403, UUID.randomUUID(), updateData)
                testBuilder.admin.wasteMaterials.assertUpdateFailStatus(404, UUID.randomUUID(), updateData)
                testBuilder.admin.wasteMaterials.assertUpdateFailStatus(
                    404,
                    createdMaterial.id!!,
                    updateData.copy(wasteCategoryId = UUID.randomUUID())
                )
                testBuilder.admin.wasteMaterials.assertUpdateFailStatus(400, createdMaterial.id, updateData.copy(localizedNames = emptyArray()))

                val updated = testBuilder.admin.wasteMaterials.update(createdMaterial.id, updateData)
                assertEquals(updateData.wasteCategoryId, updated.wasteCategoryId)
                assertEquals(updateData.id, updated.id)
                val sorted = updated.localizedNames.sortedBy { it.language }
                assertEquals("new material name en", sorted[0].value)
                assertEquals("en", sorted[0].language)

                assertEquals("new material name fr", sorted[1].value)
                assertEquals("fr", sorted[1].language)
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
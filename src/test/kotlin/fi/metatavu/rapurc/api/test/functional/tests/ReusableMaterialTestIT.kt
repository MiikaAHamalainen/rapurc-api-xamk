package fi.metatavu.rapurc.api.test.functional.tests

import fi.metatavu.rapurc.api.client.models.LocalizedValue
import fi.metatavu.rapurc.api.client.models.Metadata
import fi.metatavu.rapurc.api.client.models.ReusableMaterial
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.resources.KeycloakTestResource
import fi.metatavu.rapurc.api.test.functional.resources.MysqlTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

/**
 * Tests for Reusable materials API
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlTestResource::class)
)
class ReusableMaterialTestIT {

    val reusableMaterial = ReusableMaterial(
        localizedNames = arrayOf(
            LocalizedValue("en", "material 1")
        ),
        metadata = Metadata()
    )

    /**
     * Tests creating reusable materials and access rights
     */
    @Test
    fun create() {
        TestBuilder().use { testBuilder ->
            testBuilder.userA.materials.assertCreateFailStatus(403, reusableMaterial)
            testBuilder.admin.materials.assertCreateFailStatus(400, reusableMaterial.copy(localizedNames = emptyArray()))
            testBuilder.admin.materials.assertCreateFailStatus(400, reusableMaterial.copy(localizedNames = arrayOf(
                LocalizedValue("en", "")
            )))

            val createdMaterial = testBuilder.admin.materials.create(reusableMaterial)
            assertEquals(reusableMaterial.localizedNames[0].value, createdMaterial.localizedNames.find { it.language == "en" }!!.value)
            assertNotNull(createdMaterial.id)
            assertNotNull(createdMaterial.metadata.createdAt)
        }
    }

    /**
     * Tests listing reusable materials
     */
    @Test
    fun list() {
        TestBuilder().use { testBuilder ->
            testBuilder.admin.materials.create(reusableMaterial)
            testBuilder.admin.materials.create(reusableMaterial)
            testBuilder.userA.materials.assertCount(2)
            testBuilder.admin.materials.assertCount(2)
        }
    }

    /**
     * Tests searching reusable materials
     */
    @Test
    fun find() {
        TestBuilder().use { testBuilder ->
            val created = testBuilder.admin.materials.create(reusableMaterial)
            val found = testBuilder.userA.materials.findMaterial(created.id!!)
            assertEquals(created.id, found.id)
            assertEquals(created.localizedNames[0].value, found.localizedNames.find { it.language == "en" }!!.value)

        }
    }

    /**
     * tests updating reusable materials and access rights
     */
    @Test
    fun update() {
        TestBuilder().use { testBuilder ->
            val created = testBuilder.admin.materials.create(reusableMaterial)
            val updateData = reusableMaterial.copy(localizedNames = arrayOf(
                LocalizedValue("en", "new material name en"),
                LocalizedValue("fr", "new material name fr")
            ))
            testBuilder.userA.materials.assertUpdateFailStatus(403, created.id!!, updateData)
            testBuilder.admin.materials.assertUpdateFailStatus(400, created.id, updateData.copy(localizedNames = emptyArray()))

            val updated = testBuilder.admin.materials.update(created.id, updateData)
            assertEquals(created.id, updated.id)
            val sorted = updated.localizedNames.sortedBy { it.language }
            assertEquals("new material name en", sorted[0].value)
            assertEquals("en", sorted[0].language)

            assertEquals("new material name fr", sorted[1].value)
            assertEquals("fr", sorted[1].language)
        }
    }

    /**
     * Tests deleting reusable materials and access rights
     */
    @Test
    fun delete() {
        TestBuilder().use { testBuilder ->
            val created = testBuilder.admin.materials.create(reusableMaterial)
            testBuilder.userA.materials.assertDeleteFailStatus(403, created)
            testBuilder.admin.materials.delete(created)
            testBuilder.admin.materials.assertFindFailStatus(404, created.id!!)
        }
    }
}
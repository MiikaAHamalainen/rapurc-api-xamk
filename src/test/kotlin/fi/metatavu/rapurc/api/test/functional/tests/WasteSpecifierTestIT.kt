package fi.metatavu.rapurc.api.test.functional.tests

import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.resources.KeycloakTestResource
import fi.metatavu.rapurc.api.test.functional.resources.MysqlTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Tests for Waste Specifier API
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlTestResource::class)
)
class WasteSpecifierTestIT {

    /**
     * Tests waste specifier creation and access rights
     */
    @Test
    fun create() {
        TestBuilder().use { testBuilder ->
            testBuilder.userA.wasteSpecifiers.assertCreateFailStatus(403)
            val created = testBuilder.admin.wasteSpecifiers.createDefault()
            assertEquals(testBuilder.admin.wasteSpecifiers.wasteSpecifier.name, created.name)
            assertNotNull(created.metadata.createdAt)
            assertNotNull(created.metadata.creatorId)
            assertNotNull(created.metadata.modifiedAt)
            assertNotNull(created.metadata.lastModifierId)
        }
    }

    /**
     * Tests waste specifier listing
     */
    @Test
    fun list() {
        TestBuilder().use { testBuilder ->
            testBuilder.admin.wasteSpecifiers.createDefault()
            testBuilder.admin.wasteSpecifiers.createDefault()
            testBuilder.userA.wasteSpecifiers.assertCount(2)
        }
    }

    /**
     * Tests waste specifier search
     */
    @Test
    fun find() {
        TestBuilder().use { testBuilder ->
            val created = testBuilder.admin.wasteSpecifiers.createDefault()
            testBuilder.admin.wasteSpecifiers.assertFindFailStatus(404, UUID.randomUUID())
            val found = testBuilder.admin.wasteSpecifiers.find(created.id!!)
            assertEquals(created.id, found.id)
        }
    }

    /**
     * Tests waste specifier updates and access rights
     */
    @Test
    fun update() {
        TestBuilder().use { testBuilder ->
            val created = testBuilder.admin.wasteSpecifiers.createDefault()
            val updateData = created.copy(name = "other name")
            testBuilder.admin.wasteSpecifiers.assertUpdateFailStatus(404, UUID.randomUUID(), updateData)
            testBuilder.userA.wasteSpecifiers.assertUpdateFailStatus(403, created.id!!, updateData)
            val updated = testBuilder.admin.wasteSpecifiers.update(created.id, updateData)
            assertEquals(created.id, updated.id)
            assertEquals(updateData.name, updated.name)
        }
    }

    /**
     * Tests waste specifier deletion and access rights
     */
    @Test
    fun delete() {
        TestBuilder().use { testBuilder ->
            val created = testBuilder.admin.wasteSpecifiers.createDefault()
            testBuilder.userA.wasteSpecifiers.assertDeleteFailStatus(403, created.id!!)
            testBuilder.admin.wasteSpecifiers.assertDeleteFailStatus(404, UUID.randomUUID())
            testBuilder.admin.wasteSpecifiers.delete(created.id)
            testBuilder.userA.wasteSpecifiers.assertCount(0)
        }
    }
}
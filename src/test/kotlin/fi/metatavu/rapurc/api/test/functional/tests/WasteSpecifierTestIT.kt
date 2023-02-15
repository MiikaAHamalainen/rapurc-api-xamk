package fi.metatavu.rapurc.api.test.functional.tests

import fi.metatavu.rapurc.api.client.models.LocalizedValue
import fi.metatavu.rapurc.api.client.models.Metadata
import fi.metatavu.rapurc.api.client.models.WasteSpecifier
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
            val created1 = testBuilder.admin.wasteSpecifiers.create(
                WasteSpecifier(
                    localizedNames = arrayOf(
                        LocalizedValue("fi", "finnish name 1"),
                        LocalizedValue("en", "english name 1")
                    ),
                    metadata = Metadata()
                )
            )

            val created2 = testBuilder.admin.wasteSpecifiers.create(
                WasteSpecifier(
                    localizedNames = arrayOf(
                        LocalizedValue("fi", "finnish name 2"),
                        LocalizedValue("en", "english name 2")
                    ),
                    metadata = Metadata()
                )
            )

            assertEquals("finnish name 1", created1!!.localizedNames.find { it.language == "fi" }!!.value)
            assertEquals("english name 1", created1.localizedNames.find { it.language == "en" }!!.value)

            assertEquals("english name 2", created2!!.localizedNames.find { it.language == "en" }!!.value)
            assertEquals("english name 2", created2.localizedNames.find { it.language == "en" }!!.value)

            assertNotNull(created1.metadata.createdAt)
        }
    }

    /**
     * Tests waste specifier listing
     */
    @Test
    fun list() {
        TestBuilder().use { testBuilder ->
            testBuilder.admin.wasteSpecifiers.create()
            testBuilder.admin.wasteSpecifiers.create()
            testBuilder.userA.wasteSpecifiers.assertCount(2)
        }
    }

    /**
     * Tests waste specifier search
     */
    @Test
    fun find() {
        TestBuilder().use { testBuilder ->
            val created = testBuilder.admin.wasteSpecifiers.create()
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
            val created = testBuilder.admin.wasteSpecifiers.create()
            val updateData = created.copy(
                localizedNames = arrayOf(
                    LocalizedValue("en", "newValue"),
                    LocalizedValue("fr", "newValue1")
                )
            )
            testBuilder.admin.wasteSpecifiers.assertUpdateFailStatus(404, UUID.randomUUID(), updateData)
            testBuilder.userA.wasteSpecifiers.assertUpdateFailStatus(403, created.id!!, updateData)
            val updated = testBuilder.admin.wasteSpecifiers.update(created.id, updateData)
            assertEquals(created.id, updated.id)
            assertEquals(2, updated.localizedNames.size)
            val sorted = updateData.localizedNames.sortedBy { it.language }
            assertEquals("newValue", sorted[0].value)
            assertEquals("en", sorted[0].language)

            assertEquals("newValue1", sorted[1].value)
            assertEquals("fr", sorted[1].language)
        }
    }

    /**
     * Tests waste specifier deletion and access rights
     */
    @Test
    fun delete() {
        TestBuilder().use { testBuilder ->
            val created = testBuilder.admin.wasteSpecifiers.create()
            testBuilder.userA.wasteSpecifiers.assertDeleteFailStatus(403, created.id!!)
            testBuilder.admin.wasteSpecifiers.assertDeleteFailStatus(404, UUID.randomUUID())
            testBuilder.admin.wasteSpecifiers.delete(created.id)
            testBuilder.userA.wasteSpecifiers.assertCount(0)
        }
    }
}
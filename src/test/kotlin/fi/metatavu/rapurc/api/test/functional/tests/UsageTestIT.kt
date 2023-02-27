package fi.metatavu.rapurc.api.test.functional.tests

import fi.metatavu.rapurc.api.client.models.LocalizedValue
import fi.metatavu.rapurc.api.client.models.Metadata
import fi.metatavu.rapurc.api.client.models.Usage
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.resources.KeycloakTestResource
import fi.metatavu.rapurc.api.test.functional.resources.MysqlTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

/**
 * Tests for Usage API
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlTestResource::class)
)
class UsageTestIT {

    /**
     * Tests create endpoints
     */
    @Test
    fun create() {
        TestBuilder().use { testBuilder ->
            testBuilder.userA.usages.assertCreateFailStatus(403)
            val createdUsage = testBuilder.admin.usages.create()
            assertEquals("default_usage", createdUsage.localizedNames.find { it.language == "en" }!!.value)

            assertNotNull(createdUsage.id)
            assertNotNull(createdUsage.metadata.createdAt)
        }
    }

    /**
     * Tests list endpoints
     */
    @Test
    fun list() {
        TestBuilder().use { testBuilder ->
            testBuilder.admin.usages.create()
            testBuilder.admin.usages.create()
            testBuilder.admin.usages.assertCount(2)
        }
    }

    /**
     * Tests find endpoints
     */
    @Test
    fun find() {
        TestBuilder().use { testBuilder ->
            val createdUsage = testBuilder.admin.usages.create()
            val foundUsage = testBuilder.admin.usages.find(createdUsage.id!!)
            assertEquals(createdUsage.id, foundUsage.id)
            assertEquals(createdUsage.localizedNames.find { it.language == "en" }!!.value, foundUsage.localizedNames.find { it.language == "en" }!!.value)
        }
    }

    /**
     * Tests update endpoints
     */
    @Test
    fun update() {
        TestBuilder().use { testBuilder ->
            val createdUsage = testBuilder.admin.usages.create()
            val updateData = Usage(
                localizedNames = arrayOf(
                    LocalizedValue("en", "new material name en"),
                    LocalizedValue("fr", "new material name fr")
                ),
                metadata = Metadata()
            )

            testBuilder.userA.usages.assertUpdateFailStatus(403, createdUsage.id!!, updateData)
            testBuilder.admin.usages.assertUpdateFailStatus(400, createdUsage.id, updateData.copy(localizedNames = emptyArray()))

            val updated = testBuilder.admin.usages.update(createdUsage.id, updateData)
            val sorted = updated.localizedNames.sortedBy { it.language }
            assertEquals("new material name en", sorted[0].value)
            assertEquals("en", sorted[0].language)

            assertEquals("new material name fr", sorted[1].value)
            assertEquals("fr", sorted[1].language)
        }
    }

    /**
     * Tests delete endpoints
     */
    @Test
    fun delete() {
        TestBuilder().use { testBuilder ->
            val createdUsage = testBuilder.admin.usages.create()
            testBuilder.userA.usages.assertDeleteFailStatus(403, createdUsage.id!!)
            testBuilder.admin.usages.delete(createdUsage.id)
            testBuilder.admin.usages.assertCount(0)
        }
    }
}
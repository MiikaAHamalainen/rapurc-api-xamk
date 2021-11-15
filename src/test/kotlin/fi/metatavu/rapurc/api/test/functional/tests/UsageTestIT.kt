package fi.metatavu.rapurc.api.test.functional.tests

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
            val createdUsage = testBuilder.admin.usages.create()
            assertEquals("default_usage", createdUsage.name)
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
            assertEquals(createdUsage.name, foundUsage.name)
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
                name = "updated_usage",
                metadata = Metadata()
            )

            val updated = testBuilder.admin.usages.update(createdUsage.id!!, updateData)
            assertEquals(updateData.name, updated.name)
        }
    }

    /**
     * Tests delete endpoints
     */
    @Test
    fun delete() {
        TestBuilder().use { testBuilder ->
            val createdUsage = testBuilder.admin.usages.create()
            testBuilder.admin.usages.delete(createdUsage.id!!)
            testBuilder.admin.usages.assertCount(0)
        }
    }
}
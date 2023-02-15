package fi.metatavu.rapurc.api.test.functional.resources

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.rapurc.api.client.apis.WasteSpecifiersApi
import fi.metatavu.rapurc.api.client.infrastructure.ApiClient
import fi.metatavu.rapurc.api.client.infrastructure.ClientException
import fi.metatavu.rapurc.api.client.models.LocalizedValue
import fi.metatavu.rapurc.api.client.models.Metadata
import fi.metatavu.rapurc.api.client.models.WasteSpecifier
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.impl.ApiTestBuilderResource
import org.junit.Assert
import java.util.*

/**
 * Test resource for Waste specifier API
 */
class WasteSpecifierTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<WasteSpecifier, ApiClient?>(testBuilder, apiClient) {

    private val wasteSpecifier = WasteSpecifier(
        localizedNames = arrayOf(
            LocalizedValue("en", "metal waste")
        ),
        metadata = Metadata()
    )

    override fun clean(WasteSpecifier: WasteSpecifier) {
        api.deleteWasteSpecifier(WasteSpecifier.id!!)
    }

    override fun getApi(): WasteSpecifiersApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return WasteSpecifiersApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Creates new waste specifier object
     *
     * @return created waste specifier
     */
    fun create(): WasteSpecifier {
        return addClosable(
            api.createWasteSpecifier(
                WasteSpecifier(
                    localizedNames = arrayOf(
                        LocalizedValue("en", "metal waste")
                    ),
                    metadata = Metadata()
                )
            )
        )
    }

    /**
     * Creates new waste specifier object
     *
     * @return created waste specifier
     */
    fun create(wasteSpecifier: WasteSpecifier): WasteSpecifier? {
        return addClosable(
            api.createWasteSpecifier(
                wasteSpecifier
            )
        )
    }

    /**
     * Finds waste specifier
     *
     * @param id waste specifier id
     * @return found waste specifier
     */
    fun find(id: UUID): WasteSpecifier {
        return api.findWasteSpecifier(id)
    }

    /**
     * Lists all waste specifiers
     *
     * @return waste specifier list
     */
    fun list(): Array<WasteSpecifier> {
        return api.listWasteSpecifiers()
    }

    /**
     * Updates waste specifier
     *
     * @param WasteSpecifierId id
     * @param WasteSpecifier new data
     * @return updated waste specifier
     */
    fun update(WasteSpecifierId: UUID, WasteSpecifier: WasteSpecifier): WasteSpecifier {
        return api.updateWasteSpecifier(WasteSpecifierId, WasteSpecifier)
    }

    /**
     * Deletes waste specifier
     *
     * @param id waste specifier to delete
     */
    fun delete(id: UUID) {
        api.deleteWasteSpecifier(id)
        removeCloseable { closable: Any? ->
            if (closable !is WasteSpecifier) {
                return@removeCloseable false
            }
            closable.id == id
        }
    }

    /**
     * Asserts the amount of waste specifier records for a survey
     *
     * @param expected expected status
     */
    fun assertCount(expected: Int) {
        Assert.assertEquals(
            expected,
            api.listWasteSpecifiers().size
        )
    }

    /**
     * Asserts that finding waste specifier fails with the status
     *
     * @param expectedStatus expected status
     * @param ud id
     */
    fun assertFindFailStatus(expectedStatus: Int, ud: UUID) {
        try {
            api.findWasteSpecifier(ud)
            Assert.fail(String.format("Expected find to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts create status fails with given status code
     *
     * @param expectedStatus expected status code
     */
    fun assertCreateFailStatus(expectedStatus: Int) {
        try {
            create(wasteSpecifier)
            Assert.fail(String.format("Expected create to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts update status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param WasteSpecifierId id
     * @param WasteSpecifier waste specifier information
     */
    fun assertUpdateFailStatus(expectedStatus: Int, WasteSpecifierId: UUID, WasteSpecifier: WasteSpecifier) {
        try {
            update(WasteSpecifierId, WasteSpecifier)
            Assert.fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts delete status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param WasteSpecifierId waste specifier to delete
     */
    fun assertDeleteFailStatus(expectedStatus: Int, WasteSpecifierId: UUID) {
        try {
            api.deleteWasteSpecifier(WasteSpecifierId)
            Assert.fail(String.format("Expected delete to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }
}
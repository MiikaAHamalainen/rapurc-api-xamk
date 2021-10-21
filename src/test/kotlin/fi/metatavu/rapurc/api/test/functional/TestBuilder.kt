package fi.metatavu.rapurc.api.test.functional

import fi.metatavu.rapurc.api.client.infrastructure.ApiClient
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.jaxrs.test.functional.builder.auth.AuthorizedTestBuilderAuthentication
import fi.metatavu.jaxrs.test.functional.builder.auth.KeycloakAccessTokenProvider
import fi.metatavu.rapurc.api.test.functional.auth.TestBuilderAuthentication
import fi.metatavu.rapurc.api.test.functional.settings.ApiTestSettings
import org.eclipse.microprofile.config.ConfigProvider
import java.io.IOException

/**
 * Abstract test builder class
 *
 * @author Jari Nykänen
 */
class TestBuilder: AbstractTestBuilder<ApiClient>() {

    val settings = ApiTestSettings()

    private var admin: TestBuilderAuthentication? = null

    override fun createTestBuilderAuthentication(
        testBuilder: AbstractTestBuilder<ApiClient>,
        accessTokenProvider: AccessTokenProvider
    ): AuthorizedTestBuilderAuthentication<ApiClient> {
        return TestBuilderAuthentication(this, accessTokenProvider)
    }

    /**
     * Returns authentication resource authenticated as manager
     *
     * @return authentication resource authenticated as manager
     * @throws IOException
     */
    @kotlin.jvm.Throws(IOException::class)
    fun admin(): TestBuilderAuthentication {
        if (admin == null) {
            val authServerUrl: String = ConfigProvider.getConfig().getValue("keycloak.url", String::class.java)
            val realm: String = ConfigProvider.getConfig().getValue("keycloak.realm", String::class.java)
            val clientId = "test"
            val username = "admin"
            val password = "adminPassword"
            admin = TestBuilderAuthentication(this, KeycloakAccessTokenProvider(authServerUrl, realm, clientId, username, password, null))
        }

        return admin!!
    }
}
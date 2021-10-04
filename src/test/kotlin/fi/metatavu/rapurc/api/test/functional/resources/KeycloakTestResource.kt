package fi.metatavu.rapurc.api.test.functional.resources

import dasniko.testcontainers.keycloak.KeycloakContainer
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager

/**
 * Class for Keycloak test resource.
 *
 * @author Jari Nykänen
 */
class KeycloakTestResource: QuarkusTestResourceLifecycleManager {

    override fun start(): MutableMap<String, String> {
        keycloak.start()
        val config: MutableMap<String, String> = HashMap()
        config["quarkus.oidc.auth-server-url"] = java.lang.String.format("%s/realms/rapurc", keycloak.authServerUrl)
        config["quarkus.oidc.client-id"] = "api"
        config["quarkus.oidc.credentials.secret"] = "bb6a066e-3728-460f-a97c-aa73b0318475"

        config["keycloak.url"] = keycloak.authServerUrl
        config["keycloak.realm"] = "rapurc"

        return config
    }

    override fun stop() {
        keycloak.stop()
    }

    companion object {
        val keycloak: KeycloakContainer = KeycloakContainer()
            .withRealmImportFile("kc.json")
    }
}
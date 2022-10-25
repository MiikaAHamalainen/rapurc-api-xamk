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

        config["rapurc.keycloak.api-admin.secret"] = "4f9fb574-f5c1-4e85-b99f-82b14fd1af12"
        config["rapurc.keycloak.api-admin.client"] = "admin-client"
        config["rapurc.keycloak.api-admin.password"] = "3455cbd2-1127-440d-853a-1c409d36880b"
        config["rapurc.keycloak.api-admin.user"] = "api-admin"

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
package fi.metatavu.rapurc.api.impl

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.RealmResource
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Keycloak controller
 */
@ApplicationScoped
class KeycloakController {

    @Inject
    @ConfigProperty(name = "keycloak.url")
    private lateinit var authServerUrl: String

    @Inject
    @ConfigProperty(name = "keycloak.realm")
    private lateinit var realm: String

    @Inject
    @ConfigProperty(name = "rapurc.keycloak.api-admin.secret")
    private lateinit var clientSecret: String

    @Inject
    @ConfigProperty(name = "rapurc.keycloak.api-admin.client")
    private lateinit var clientId: String

    @Inject
    @ConfigProperty(name = "rapurc.keycloak.api-admin.user")
    private lateinit var apiAdminUser: String

    @Inject
    @ConfigProperty(name = "rapurc.keycloak.api-admin.password")
    private lateinit var apiAdminPassword: String

    /**
     * Gets ID of the group user belongs to
     *
     * @param userId user id
     * @return user group id if belongs to any
     */
    fun getGroupId(userId: UUID): UUID? {
        val groups = realm().users().get(userId.toString())?.groups() ?: return null
        if (groups.size >= 1) {
            return UUID.fromString(groups[0].id)
        }

        return null
    }

    /**
     * Constructs a Keycloak client
     *
     * @return Keycloak client
     */
    private fun getKeycloakClient(): Keycloak {
        return KeycloakBuilder.builder()
            .serverUrl(authServerUrl)
            .realm(realm)
            .username(apiAdminUser)
            .password(apiAdminPassword)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .build()
    }

    /**
     * Gets realm
     *
     * @return realm
     */
    private fun realm(): RealmResource {
        return getKeycloakClient().realm(realm)
    }
}
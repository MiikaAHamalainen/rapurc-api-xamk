package fi.metatavu.rapurc.api

/**
 * Sealed class for user keycloak role
 *
 * @author Jari Nykänen
 */
sealed class UserRole(val role: String) {

    object USER: UserRole("user") {
        const val name = "user"
    }

    object ADMIN: UserRole("admin") {
        const val name = "admin"
    }

}
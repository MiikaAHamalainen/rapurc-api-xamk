package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.WasteSpecifier
import java.util.*
import javax.enterprise.context.ApplicationScoped

/**
 * DAO class for Waste Specifier entity
 */
@ApplicationScoped
class WasteSpecifierDAO: AbstractDAO<WasteSpecifier>() {

    /**
     * Creates waste specifier entity
     *
     * @param id id
     * @param name specifier name
     * @param creatorId creator id
     * @param modifierId modifier id
     * @return created waste specifier
     */
    fun create(
        id: UUID,
        name: String,
        creatorId: UUID,
        modifierId: UUID
    ): WasteSpecifier {
        val wasteSpecifier = WasteSpecifier()
        wasteSpecifier.id = id
        wasteSpecifier.name = name
        wasteSpecifier.creatorId = creatorId
        wasteSpecifier.lastModifierId = modifierId
        return persist(wasteSpecifier)
    }

    /**
     * Updates waste specifier name
     *
     * @param wasteSpecifier waste specifier to update
     * @param name new name
     * @param modifierId user id
     * @return updated waste specifier
     */
    fun updateName(wasteSpecifier: WasteSpecifier, name: String, modifierId: UUID): WasteSpecifier {
        wasteSpecifier.name = name
        wasteSpecifier.lastModifierId = modifierId
        return persist(wasteSpecifier)
    }
}
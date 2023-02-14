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
     * @param creatorId creator id
     * @param modifierId modifier id
     * @return created waste specifier
     */
    fun create(
        id: UUID,
        creatorId: UUID,
        modifierId: UUID
    ): WasteSpecifier {
        val wasteSpecifier = WasteSpecifier()
        wasteSpecifier.id = id
        wasteSpecifier.creatorId = creatorId
        wasteSpecifier.lastModifierId = modifierId
        return persist(wasteSpecifier)
    }

}
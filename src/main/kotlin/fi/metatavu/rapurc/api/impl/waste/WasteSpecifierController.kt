package fi.metatavu.rapurc.api.impl.waste

import fi.metatavu.rapurc.api.persistence.dao.WasteSpecifierDAO
import fi.metatavu.rapurc.api.persistence.model.WasteSpecifier
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for Waste Specifier entity
 */
@ApplicationScoped
class WasteSpecifierController {

    @Inject
    lateinit var wasteSpecifierDAO: WasteSpecifierDAO

    /**
     * Lists all waste specifiers
     *
     * @return waste specifiers
     */
    fun list(): List<WasteSpecifier> {
        return wasteSpecifierDAO.listAll()
    }

    /**
     * Creates new waste specifier
     *
     * @param wasteSpecifier new waste Specifier
     * @param userId user id
     * @return created waste Specifier
     */
    fun create(wasteSpecifier: fi.metatavu.rapurc.api.model.WasteSpecifier, userId: UUID): WasteSpecifier {
        return wasteSpecifierDAO.create(
            id = UUID.randomUUID(),
            name = wasteSpecifier.name,
            creatorId = userId,
            modifierId = userId
        )
    }

    /**
     * Finds waste specifier by id
     *
     * @param wasteSpecifierId id
     * @return found waste specifier or null
     */
    fun find(wasteSpecifierId: UUID): WasteSpecifier? {
        return wasteSpecifierDAO.findById(wasteSpecifierId)
    }

    /**
     * Updates waste specifier with new data
     *
     * @param wasteSpecifierToUpdate waste Specifier to update
     * @param wasteSpecifier new waste Specifier
     * @param userId user id
     * @return updated waste specifier
     */
    fun update(wasteSpecifierToUpdate: WasteSpecifier, wasteSpecifier: fi.metatavu.rapurc.api.model.WasteSpecifier, userId: UUID): WasteSpecifier {
        return wasteSpecifierDAO.updateName(wasteSpecifierToUpdate, wasteSpecifier.name, userId)
    }

    /**
     * Deletes waste specifier
     *
     * @param wasteSpecifier specifier to delete
     */
    fun delete(wasteSpecifier: WasteSpecifier) {
        wasteSpecifierDAO.delete(wasteSpecifier)
    }
}
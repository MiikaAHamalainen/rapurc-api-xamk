package fi.metatavu.rapurc.api.impl.waste

import fi.metatavu.rapurc.api.persistence.dao.LocalizedValueDAO
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

    @Inject
    lateinit var localizedValueDAO: LocalizedValueDAO

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
        val createdWasteSpecifier = wasteSpecifierDAO.create(
            id = UUID.randomUUID(),
            creatorId = userId,
            modifierId = userId
        )

        wasteSpecifier.name.forEach {
            localizedValueDAO.create(
                id = UUID.randomUUID(),
                value = it.value,
                language = it.language,
                wasteSpecifier = createdWasteSpecifier
            )
        }

        return createdWasteSpecifier
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
        localizedValueDAO.listByWasteSpecifier(wasteSpecifierToUpdate)
            .forEach { localizedValueDAO.delete(it) }

        wasteSpecifier.name.forEach {
            localizedValueDAO.create(
                id = UUID.randomUUID(),
                value = it.value,
                language = it.language,
                wasteSpecifier = wasteSpecifierToUpdate
            )
        }
        return wasteSpecifierToUpdate
    }

    /**
     * Deletes waste specifier
     *
     * @param wasteSpecifier specifier to delete
     */
    fun delete(wasteSpecifier: WasteSpecifier) {
        localizedValueDAO.listByWasteSpecifier(wasteSpecifier)
            .forEach { localizedValueDAO.delete(it) }
        wasteSpecifierDAO.delete(wasteSpecifier)
    }
}
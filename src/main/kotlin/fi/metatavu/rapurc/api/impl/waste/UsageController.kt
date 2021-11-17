package fi.metatavu.rapurc.api.impl.waste

import fi.metatavu.rapurc.api.persistence.dao.UsageDAO
import fi.metatavu.rapurc.api.persistence.model.WasteUsage
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for managing Usages
 */
@ApplicationScoped
class UsageController {

    @Inject
    lateinit var usageDAO: UsageDAO

    /**
     * Lists all usages
     *
     * @return usages
     */
    fun list(): List<WasteUsage> {
        return usageDAO.listAll()
    }

    /**
     * Creates usage entity
     *
     * @param name name
     * @param userId user id
     * @return created usage
     */
    fun create(
        name: String,
        userId: UUID
    ): WasteUsage {
        return usageDAO.create(
            id = UUID.randomUUID(),
            name = name,
            creatorId = userId,
            modifierId = userId
        )
    }

    /**
     * Finds usage by id
     *
     * @param usageId usage id
     * @return found usage or null
     */
    fun find(usageId: UUID): WasteUsage? {
        return usageDAO.findById(usageId)
    }

    /**
     * Updates old usage object with new data
     *
     * @param oldUsage usage to update
     * @param newUsage new data
     * @param userId user id
     * @return upfated Usage
     */
    fun update(oldUsage: WasteUsage, newUsage: fi.metatavu.rapurc.api.model.Usage, userId: UUID): WasteUsage {
        return usageDAO.updateName(oldUsage, newUsage.name, userId)
    }

    /**
     * Deletes Usage object
     *
     * @param usage usege to delete
     */
    fun delete(usage: WasteUsage) {
        usageDAO.delete(usage)
    }

}

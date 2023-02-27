package fi.metatavu.rapurc.api.impl.waste

import fi.metatavu.rapurc.api.model.Usage
import fi.metatavu.rapurc.api.persistence.dao.LocalizedValueDAO
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

    @Inject
    lateinit var localizedValueDAO: LocalizedValueDAO

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
     * @param wasteUsage rest object
     * @param userId user id
     * @return created usage
     */
    fun create(
        wasteUsage: Usage,
        userId: UUID
    ): WasteUsage {
        val createdWasteUsage = usageDAO.create(
            id = UUID.randomUUID(),
            creatorId = userId,
            modifierId = userId
        )

        wasteUsage.localizedNames.forEach {
            localizedValueDAO.create(
                id = UUID.randomUUID(),
                value = it.value,
                language = it.language,
                usage = createdWasteUsage
            )
        }

        return createdWasteUsage
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
    fun update(oldUsage: WasteUsage, newUsage: Usage, userId: UUID): WasteUsage {
        localizedValueDAO.listBy(usage = oldUsage)
            .forEach { localizedValueDAO.delete(it) }

        newUsage.localizedNames.forEach {
            localizedValueDAO.create(
                id = UUID.randomUUID(),
                value = it.value,
                language = it.language,
                usage = oldUsage
            )
        }
        return oldUsage
    }

    /**
     * Deletes Usage object
     *
     * @param usage usege to delete
     */
    fun delete(usage: WasteUsage) {
        localizedValueDAO.listBy(usage = usage)
            .forEach { localizedValueDAO.delete(it) }
        usageDAO.delete(usage)
    }

}

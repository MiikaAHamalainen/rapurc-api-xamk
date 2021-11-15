package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.WasteUsage
import java.util.*
import javax.enterprise.context.ApplicationScoped

/**
 * DAO class to manage Usage entity
 */
@ApplicationScoped
class UsageDAO: AbstractDAO<WasteUsage>() {

    /**
     * Creates usage entity
     *
     * @param id id
     * @param name usage name
     * @param creatorId creator id
     * @param modifierId modifier id
     * @return created waste category
     */
    fun create(
        id: UUID,
        name: String,
        creatorId: UUID,
        modifierId: UUID
    ): WasteUsage {
        val usage = WasteUsage()
        usage.id = id
        usage.name = name
        usage.creatorId = creatorId
        usage.lastModifierId = modifierId
        return persist(usage)
    }

    /**
     * Updates usage name
     *
     * @param usage usage to update
     * @param name new name
     * @param userId modifier id
     * @return updated usage
     */
    fun updateName(usage: WasteUsage, name: String, userId: UUID): WasteUsage {
        usage.name = name
        usage.lastModifierId = userId
        return persist(usage)
    }
}
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
     * @param creatorId creator id
     * @param modifierId modifier id
     * @return created waste category
     */
    fun create(
        id: UUID,
        creatorId: UUID,
        modifierId: UUID
    ): WasteUsage {
        val usage = WasteUsage()
        usage.id = id
        usage.creatorId = creatorId
        usage.lastModifierId = modifierId
        return persist(usage)
    }

}
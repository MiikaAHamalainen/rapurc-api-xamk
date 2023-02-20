package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.ReusableMaterial
import java.util.*
import javax.enterprise.context.ApplicationScoped

/**
 * DAO class for reusable materials
 */
@ApplicationScoped
class ReusableMaterialDAO: AbstractDAO<ReusableMaterial>() {

    /**
     * Creates new reusable material
     *
     * @param id id
     * @param creatorId creator id
     * @param lastModifierId creator id
     * @return new reusable material
     */
    fun create(
        id: UUID,
        creatorId: UUID,
        lastModifierId: UUID
    ): ReusableMaterial {
        val reusableMaterial = ReusableMaterial()
        reusableMaterial.id = id
        reusableMaterial.creatorId = creatorId
        reusableMaterial.lastModifierId = lastModifierId
        return persist(reusableMaterial)
    }

}
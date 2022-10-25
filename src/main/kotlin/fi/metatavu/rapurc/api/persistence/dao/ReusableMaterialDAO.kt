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
     * @param name name
     * @param creatorId creator id
     * @param lastModifierId creator id
     * @return new reusable material
     */
    fun create(
        id: UUID,
        name: String,
        creatorId: UUID,
        lastModifierId: UUID
    ): ReusableMaterial {
        val reusableMaterial = ReusableMaterial()
        reusableMaterial.id = id
        reusableMaterial.name = name
        reusableMaterial.creatorId = creatorId
        reusableMaterial.lastModifierId = lastModifierId
        return persist(reusableMaterial)
    }

    /**
     * Updates name
     *
     * @param materialToUpdate material to update
     * @param name new name
     * @param modifierId modifier id
     * @return updated material
     */
    fun updateName(materialToUpdate: ReusableMaterial, name: String, modifierId: UUID): ReusableMaterial {
        materialToUpdate.name = name
        materialToUpdate.lastModifierId = modifierId
        return persist(materialToUpdate)
    }
}
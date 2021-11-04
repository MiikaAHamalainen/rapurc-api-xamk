package fi.metatavu.rapurc.api.impl.materials

import fi.metatavu.rapurc.api.persistence.dao.ReusableMaterialDAO
import fi.metatavu.rapurc.api.persistence.model.ReusableMaterial
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Manages Resusable materials
 */
@ApplicationScoped
class ReusableMaterialController {

    @Inject
    private lateinit var reusableMaterialDAO: ReusableMaterialDAO

    /**
     * Lists all materials
     *
     * @return all reusable materials
     */
    fun listAll(): List<ReusableMaterial> {
        return reusableMaterialDAO.listAll()
    }

    /**
     * Creates new reusable material object
     *
     * @param material reusable material REST data
     * @param userId creator id
     * @return created reusable material
     */
    fun create(material: fi.metatavu.rapurc.api.model.ReusableMaterial, userId: UUID): ReusableMaterial {
        return reusableMaterialDAO.create(
            id = UUID.randomUUID(),
            name = material.name,
            creatorId = userId,
            lastModifierId = userId
        )
    }

    /**
     * Finds reusable material by id
     *
     * @param materialId material id
     * @return found reusable material or null
     */
    fun find(materialId: UUID): ReusableMaterial? {
        return reusableMaterialDAO.findById(materialId)
    }

    /**
     * Updates reusable material
     *
     * @param materialToUpdate old material
     * @param reusableMaterial new material data
     * @param userId modifier id
     * @return updated material
     */
    fun update(
        materialToUpdate: ReusableMaterial,
        reusableMaterial: fi.metatavu.rapurc.api.model.ReusableMaterial,
        userId: UUID)
    : ReusableMaterial {
        return reusableMaterialDAO.updateName(materialToUpdate, reusableMaterial.name, userId)
    }

    /**
     * Deletes reusable material
     *
     * @param materialToDelete material to delete
     */
    fun delete(materialToDelete: ReusableMaterial) {
        reusableMaterialDAO.delete(materialToDelete)
    }
}
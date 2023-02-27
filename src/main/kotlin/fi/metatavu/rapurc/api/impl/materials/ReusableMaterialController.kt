package fi.metatavu.rapurc.api.impl.materials

import fi.metatavu.rapurc.api.persistence.dao.LocalizedValueDAO
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
    lateinit var reusableMaterialDAO: ReusableMaterialDAO

    @Inject
    lateinit var localizedValueDAO: LocalizedValueDAO

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
    fun create(
        material: fi.metatavu.rapurc.api.model.ReusableMaterial,
        userId: UUID
    ): ReusableMaterial {
        val createdReusableMaterial = reusableMaterialDAO.create(
            id = UUID.randomUUID(),
            creatorId = userId,
            lastModifierId = userId
        )

        material.localizedNames.forEach {
            localizedValueDAO.create(
                id = UUID.randomUUID(),
                value = it.value,
                language = it.language,
                reusableMaterial = createdReusableMaterial
            )
        }

        return  createdReusableMaterial
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
        userId: UUID
    ): ReusableMaterial {
        localizedValueDAO.listBy(reusableMaterial = materialToUpdate)
            .forEach { localizedValueDAO.delete(it) }

        reusableMaterial.localizedNames.forEach {
            localizedValueDAO.create(
                id = UUID.randomUUID(),
                value = it.value,
                language = it.language,
                reusableMaterial = materialToUpdate
            )
        }

        return materialToUpdate
    }

    /**
     * Deletes reusable material
     *
     * @param materialToDelete material to delete
     */
    fun delete(materialToDelete: ReusableMaterial) {
        localizedValueDAO.listBy(reusableMaterial = materialToDelete)
            .forEach { localizedValueDAO.delete(it) }

        reusableMaterialDAO.delete(materialToDelete)
    }
}
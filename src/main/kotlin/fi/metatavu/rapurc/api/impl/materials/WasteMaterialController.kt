package fi.metatavu.rapurc.api.impl.materials

import fi.metatavu.rapurc.api.persistence.dao.WasteMaterialDAO
import fi.metatavu.rapurc.api.persistence.model.WasteCategory
import fi.metatavu.rapurc.api.persistence.model.WasteMaterial
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Manages waste material entities
 */
@ApplicationScoped
class WasteMaterialController {

    @Inject
    lateinit var wasteMaterialDAO: WasteMaterialDAO

    /**
     * Lists waste materials
     *
     * @param wasteCategory filter by waste category
     * @return list of waste materials
     */
    fun list(wasteCategory: WasteCategory?): List<WasteMaterial> {
        return wasteMaterialDAO.list(wasteCategory)
    }

    /**
     * Creates new waste material entry
     *
     * @param wasteMaterial new data
     * @param wasteCategory new category
     * @param userId user id
     * @return created waste material
     */
    fun create(wasteMaterial: fi.metatavu.rapurc.api.model.WasteMaterial, wasteCategory: WasteCategory, userId: UUID): WasteMaterial {
        return wasteMaterialDAO.create(
            id = UUID.randomUUID(),
            name = wasteMaterial.name,
            wasteCategory = wasteCategory,
            ewcSpecificationCode = wasteMaterial.ewcSpecificationCode,
            creatorId = userId,
            modifierId = userId
        )
    }

    /**
     * Finds waste material
     *
     * @param wasteMaterialId waste material id
     * @return found material or null
     */
    fun find(wasteMaterialId: UUID): WasteMaterial? {
        return wasteMaterialDAO.findById(wasteMaterialId)
    }

    /**
     * Updates waste material
     *
     * @param oldWasteMaterial old data
     * @param newWasteMaterial new data
     * @param newWasteCategory new category
     * @param userId user id
     * @return updated waste material
     */
    fun update(oldWasteMaterial: WasteMaterial, newWasteMaterial: fi.metatavu.rapurc.api.model.WasteMaterial, newWasteCategory: WasteCategory, userId: UUID): WasteMaterial {
        val result = wasteMaterialDAO.updateName(oldWasteMaterial, newWasteMaterial.name, userId)
        wasteMaterialDAO.updateEwcSpecificationCode(result, newWasteMaterial.ewcSpecificationCode, userId)
        wasteMaterialDAO.updateWasteCategory(result, newWasteCategory, userId)
        return result
    }

    /**
     * Deletes waste material
     *
     * @param wasteMaterial material to delete
     */
    fun delete(wasteMaterial: WasteMaterial) {
        wasteMaterialDAO.delete(wasteMaterial)
    }
}
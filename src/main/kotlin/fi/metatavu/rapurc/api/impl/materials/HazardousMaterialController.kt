package fi.metatavu.rapurc.api.impl.materials

import fi.metatavu.rapurc.api.persistence.dao.HazardousMaterialDAO
import fi.metatavu.rapurc.api.persistence.model.HazardousMaterial
import fi.metatavu.rapurc.api.persistence.model.WasteCategory
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Manages hazardous material entities
 */
@ApplicationScoped
class HazardousMaterialController {

    @Inject
    lateinit var hazardousMaterialDAO: HazardousMaterialDAO

    /**
     * Lists hazardous materials
     *
     * @param wasteCategory filter by waste category
     * @return list of hazardous materials
     */
    fun list(wasteCategory: WasteCategory?): List<HazardousMaterial> {
        return hazardousMaterialDAO.list(wasteCategory)
    }

    /**
     * Creates new hazardous material entry
     *
     * @param hazardousMaterial new hazardous data
     * @param wasteCategory new category
     * @param userId user id
     * @return created hazardous material
     */
    fun create(hazardousMaterial: fi.metatavu.rapurc.api.model.HazardousMaterial, wasteCategory: WasteCategory, userId: UUID): HazardousMaterial {
        return hazardousMaterialDAO.create(
            id = UUID.randomUUID(),
            name = hazardousMaterial.name,
            wasteCategory = wasteCategory,
            ewcSpecificationCode = hazardousMaterial.ewcSpecificationCode,
            creatorId = userId,
            modifierId = userId
        )
    }

    /**
     * Finds hazardous material
     *
     * @param materialId hazardous material id
     * @return found hazardous material or null
     */
    fun find(materialId: UUID): HazardousMaterial? {
        return hazardousMaterialDAO.findById(materialId)
    }

    /**
     * Updates hazardous material
     *
     * @param hazardousMaterial old data
     * @param newHazardousMaterial new data
     * @param newWasteCategory new category
     * @param userId user id
     * @return updated waste material
     */
    fun update(hazardousMaterial: HazardousMaterial, newHazardousMaterial: fi.metatavu.rapurc.api.model.HazardousMaterial, newWasteCategory: WasteCategory, userId: UUID): HazardousMaterial {
        val result = hazardousMaterialDAO.updateName(hazardousMaterial, newHazardousMaterial.name, userId)
        hazardousMaterialDAO.updateEwcSpecificationCode(result, newHazardousMaterial.ewcSpecificationCode, userId)
        hazardousMaterialDAO.updateWasteCategory(result, newWasteCategory, userId)
        return result
    }

    /**
     * Deletes hazardous material
     *
     * @param hazardousMaterial hazardous material to delete
     */
    fun delete(hazardousMaterial: HazardousMaterial) {
        hazardousMaterialDAO.delete(hazardousMaterial)
    }
}
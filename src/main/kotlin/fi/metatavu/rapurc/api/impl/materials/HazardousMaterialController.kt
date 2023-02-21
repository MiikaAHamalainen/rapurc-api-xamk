package fi.metatavu.rapurc.api.impl.materials

import fi.metatavu.rapurc.api.persistence.dao.HazardousMaterialDAO
import fi.metatavu.rapurc.api.persistence.dao.LocalizedValueDAO
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

    @Inject
    lateinit var localizedValueDAO: LocalizedValueDAO

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
        val createdHazMaterial = hazardousMaterialDAO.create(
            id = UUID.randomUUID(),
            wasteCategory = wasteCategory,
            ewcSpecificationCode = hazardousMaterial.ewcSpecificationCode,
            creatorId = userId,
            modifierId = userId
        )

        hazardousMaterial.localizedNames.forEach {
            localizedValueDAO.create(
                id = UUID.randomUUID(),
                value = it.value,
                language = it.language,
                hazardousMaterial = createdHazMaterial
            )
        }

        return createdHazMaterial
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
        val result = hazardousMaterialDAO.updateEwcSpecificationCode(hazardousMaterial, newHazardousMaterial.ewcSpecificationCode, userId)
        hazardousMaterialDAO.updateWasteCategory(result, newWasteCategory, userId)

        localizedValueDAO.listBy(hazardousMaterial = result)
            .forEach { localizedValueDAO.delete(it) }

        newHazardousMaterial.localizedNames.forEach {
            localizedValueDAO.create(
                id = UUID.randomUUID(),
                value = it.value,
                language = it.language,
                hazardousMaterial = result
            )
        }

        return result
    }

    /**
     * Deletes hazardous material
     *
     * @param hazardousMaterial hazardous material to delete
     */
    fun delete(hazardousMaterial: HazardousMaterial) {
        localizedValueDAO.listBy(hazardousMaterial = hazardousMaterial)
            .forEach { localizedValueDAO.delete(it) }
        hazardousMaterialDAO.delete(hazardousMaterial)
    }
}
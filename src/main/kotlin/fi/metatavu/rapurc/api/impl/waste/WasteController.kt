package fi.metatavu.rapurc.api.impl.waste

import fi.metatavu.rapurc.api.persistence.dao.SurveyDAO
import fi.metatavu.rapurc.api.persistence.dao.WasteDAO
import fi.metatavu.rapurc.api.persistence.model.Survey
import fi.metatavu.rapurc.api.persistence.model.WasteUsage
import fi.metatavu.rapurc.api.persistence.model.Waste
import fi.metatavu.rapurc.api.persistence.model.WasteMaterial
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller class for waste
 */
@ApplicationScoped
class WasteController {

    @Inject
    lateinit var wasteDAO: WasteDAO

    @Inject
    lateinit var surveyDAO: SurveyDAO

    /**
     * Lists waste based on filters
     *
     * @param survey survey filter
     * @param wasteMaterial waste material filter
     * @param usage usage filter
     * @return waste entries
     */
    fun list(survey: Survey?, wasteMaterial: WasteMaterial?, usage: WasteUsage?): List<Waste> {
        return wasteDAO.list(
            survey = survey,
            wasteMaterial = wasteMaterial,
            usage = usage
        )
    }

    /**
     * Creates new Waste entry
     *
     * @param usage usage of the waste
     * @param wasteMaterial material of the waste
     * @param amount waste amount
     * @param survey survey it belongs to
     * @param description waste description
     * @param userId creator id
     * @return saved waste
     */
    fun create(
        usage: WasteUsage,
        wasteMaterial: WasteMaterial,
        amount: Double,
        survey: Survey,
        description: String?,
        userId: UUID
    ): Waste {
        surveyDAO.update(survey, userId)
        return wasteDAO.create(
            id = UUID.randomUUID(),
            usage = usage,
            wasteMaterial = wasteMaterial,
            amount = amount,
            survey = survey,
            description = description,
            creatorId = userId,
            modifierId = userId
        )
    }

    /**
     * Finds waste
     *
     * @param wasteId waste id
     * @return waste if found or null
     */
    fun findById(wasteId: UUID): Waste? {
        return wasteDAO.findById(wasteId)
    }

    /**
     * Updates waste
     *
     * @param wasteToUpdate waste to update
     * @param waste new waste data
     * @param newWasteMaterial new waste material
     * @param newUsage new usage of the waste
     * @param userId modifier id
     * @return updated waste
     */
    fun updateWaste(wasteToUpdate: Waste, waste: fi.metatavu.rapurc.api.model.Waste, newWasteMaterial: WasteMaterial, newUsage: WasteUsage, userId: UUID): Waste {
        surveyDAO.update(wasteToUpdate.survey!!, userId)
        val result = wasteDAO.updateUsage(wasteToUpdate, newUsage, userId)
        wasteDAO.updateWasteMaterial(result, newWasteMaterial, userId)
        wasteDAO.updateAmount(result, waste.amount, userId)
        wasteDAO.updateDescription(result, waste.description, userId)
        return result
    }

    /**
     * Deletes waste
     *
     * @param waste waste to delete
     */
    fun delete(waste: Waste, userId:UUID) {
        surveyDAO.update(waste.survey!!, userId)
        wasteDAO.delete(waste)
    }

}
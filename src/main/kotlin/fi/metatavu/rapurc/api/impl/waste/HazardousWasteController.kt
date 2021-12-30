package fi.metatavu.rapurc.api.impl.waste

import fi.metatavu.rapurc.api.persistence.dao.HazardousWasteDAO
import fi.metatavu.rapurc.api.persistence.dao.SurveyDAO
import fi.metatavu.rapurc.api.persistence.model.*
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Manages hazardous waste entity
 */
@ApplicationScoped
class HazardousWasteController {

    @Inject
    lateinit var hazardousWasteDAO: HazardousWasteDAO

    @Inject
    lateinit var surveyDAO: SurveyDAO

    /**
     * Lists hazardous waste
     *
     * @param survey survey filter
     * @param wasteSpecifier waste specifier filter
     * @param hazardousMaterial hazardous material filter
     * @return hazardous waste entries
     */
    fun list(survey: Survey?, wasteSpecifier: WasteSpecifier?, hazardousMaterial: HazardousMaterial?): List<HazardousWaste> {
        return hazardousWasteDAO.list(
            survey = survey,
            wasteSpecifier = wasteSpecifier,
            hazardousMaterial = hazardousMaterial
        )
    }

    /**
     * Creates new hazardous waste entry
     *
     * @param survey survey it belongs to
     * @param hazardousMaterial hazardous Material
     * @param wasteSpecifier waste Specifier
     * @param amount hazardous waste amount
     * @param description hazardous waste description
     * @param userId creator id
     * @return saved hazardous waste
     */
    fun create(
        survey: Survey,
        hazardousMaterial: HazardousMaterial,
        wasteSpecifier: WasteSpecifier?,
        amount: Double,
        description: String?,
        userId: UUID
    ): HazardousWaste {
        surveyDAO.update(survey, userId)
        return hazardousWasteDAO.create(
            id = UUID.randomUUID(),
            survey = survey,
            hazardousMaterial = hazardousMaterial,
            wasteSpecifier = wasteSpecifier,
            amount = amount,
            description = description,
            creatorId = userId,
            modifierId = userId
        )
    }

    /**
     * Finds hazardous waste
     *
     * @param hazardousWasteId hazardous waste id
     * @return hazardous waste if found or null
     */
    fun findById(hazardousWasteId: UUID): HazardousWaste? {
        return hazardousWasteDAO.findById(hazardousWasteId)
    }

    /**
     * Updates hazardous Waste
     *
     * @param hazardousWaste hazardous waste to update
     * @param newHazardousWaste new hazardous Waste data
     * @param hazardousMaterial new hazardous Material
     * @param wasteSpecifier new waste specifier
     * @param userId modifier id
     * @return updated Hazardous Waste
     */
    fun updateWaste(
        hazardousWaste: HazardousWaste,
        newHazardousWaste: fi.metatavu.rapurc.api.model.HazardousWaste,
        hazardousMaterial: HazardousMaterial,
        wasteSpecifier: WasteSpecifier,
        userId: UUID
    ): HazardousWaste {
        surveyDAO.update(hazardousWaste.survey!!, userId)
        val result = hazardousWasteDAO.updateHazardousMaterial(hazardousWaste, hazardousMaterial, userId)
        hazardousWasteDAO.updateWasteSpecifier(result, wasteSpecifier, userId)
        hazardousWasteDAO.updateAmount(result, newHazardousWaste.amount, userId)
        return hazardousWasteDAO.updateDescription(result, newHazardousWaste.description, userId)
    }

    /**
     * Deletes hazardous
     *
     * @param hazardousWaste hazardous waste to delete
     * @param userId user id
     */
    fun delete(hazardousWaste: HazardousWaste, userId: UUID) {
        surveyDAO.update(hazardousWaste.survey!!, userId)
        hazardousWasteDAO.delete(hazardousWaste)
    }

}

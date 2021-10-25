package fi.metatavu.rapurc.api.impl.surveys

import fi.metatavu.rapurc.api.model.SurveyStatus
import fi.metatavu.rapurc.api.persistence.dao.SurveyDAO
import fi.metatavu.rapurc.api.persistence.model.Survey
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for surveys
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class SurveyController {

    @Inject
    lateinit var surveyDAO: SurveyDAO

    /**
     * Lists surveys with given filters
     *
     * @param firstResult first result
     * @param maxResults maximum amount of results
     * @param address filter by address
     * @param status filter by status
     * @return List of visitor variables
     */
    fun list(firstResult: Int, maxResults: Int, address: String?, status: SurveyStatus?): List<Survey> {
        return surveyDAO.list(
            firstResult = firstResult,
            maxResults = maxResults,
            address = address,
            status = status
        )
    }

    /**
     * Creates new survey
     *
     * @param status new status
     * @param creatorId creator's ID
     * @return updated survey
     */
    fun create(status: SurveyStatus, creatorId: UUID): Survey {
        return surveyDAO.create(
            id = UUID.randomUUID(),
            status = status,
            creatorId = creatorId,
            lastModifierId = creatorId
        )
    }

    /**
     * Finds survey with id
     *
     * @param surveyId survey ID
     * @return survey or null if not found
     */
    fun find(surveyId: UUID): Survey? {
        return surveyDAO.findById(id = surveyId)
    }

    /**
     * Updates survey
     *
     * @param survey survey to update
     * @param status new status
     * @param lastModifierId last modifier's id
     * @return updated survey
     */
    fun update(survey: Survey, status: SurveyStatus, lastModifierId: UUID): Survey {
        return surveyDAO.updateStatus(survey = survey, status = status, lastModifierId = lastModifierId)
    }

    /**
     * Deletes survey
     *
     * @param survey survey to delete
     */
    fun deleteSurvey(survey: Survey) {
        surveyDAO.delete(survey)
    }
}
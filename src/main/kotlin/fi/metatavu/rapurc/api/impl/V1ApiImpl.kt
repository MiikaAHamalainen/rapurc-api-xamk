package fi.metatavu.rapurc.api.impl

import fi.metatavu.rapurc.api.impl.surveys.SurveyController
import fi.metatavu.rapurc.api.impl.translate.SurveyTranslator
import fi.metatavu.rapurc.api.model.Survey
import fi.metatavu.rapurc.api.model.SurveyStatus
import fi.metatavu.rapurc.api.spec.V1Api
import java.util.*
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.core.Response

/**
 * V1 API implementation
 *
 * @author Jari Nykänen
 */
@RequestScoped
@Transactional
class V1ApiImpl: V1Api, AbstractApi()  {

    @Inject
    lateinit var surveyController: SurveyController

    @Inject
    lateinit var surveyTranslator: SurveyTranslator

    /* SURVEYS */

    override fun listSurveys(firstResult: Int?, maxResults: Int?, address: String?, status: SurveyStatus?): Response {
        loggedUserId ?: return createForbidden(NO_LOGGED_USER_ID)
        val surveys = surveyController.list(
            firstResult = firstResult ?: 0,
            maxResults = maxResults ?: 10,
            address = address,
            status = status
        )

        return createOk(surveys.map(surveyTranslator::translate))
    }

    override fun createSurvey(survey: Survey?): Response {
        val userId = loggedUserId ?: return createForbidden(NO_LOGGED_USER_ID)
        survey ?: return createBadRequest(MISSING_REQUEST_BODY)
        val status = survey.status

        val createdSurvey = surveyController.create(status = status, creatorId = userId)
        return createOk(surveyTranslator.translate(createdSurvey))
    }

    override fun findSurvey(surveyId: UUID?): Response {
        loggedUserId ?: return createForbidden(NO_LOGGED_USER_ID)
        surveyId ?: return createBadRequest(createMissingIdFromRequestMessage(target = SURVEY))

        val foundSurvey = surveyController.find(surveyId = surveyId)
            ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        return createOk(surveyTranslator.translate(foundSurvey))
    }

    override fun updateSurvey(surveyId: UUID?, survey: Survey?): Response {
        val userId = loggedUserId ?: return createForbidden(NO_LOGGED_USER_ID)
        survey ?: return createBadRequest(MISSING_REQUEST_BODY)
        surveyId ?: return createBadRequest(createMissingIdFromRequestMessage(target = SURVEY))

        val surveyToUpdate = surveyController.find(surveyId = surveyId)
            ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        val status = survey.status
        val updatedSurvey = surveyController.update(
            survey = surveyToUpdate,
            status = status,
            lastModifierId = userId
        )

        return createOk(surveyTranslator.translate(updatedSurvey))
    }

    override fun deleteSurvey(surveyId: UUID?): Response {
        loggedUserId ?: return createForbidden(NO_LOGGED_USER_ID)
        surveyId ?: return createBadRequest(createMissingIdFromRequestMessage(target = SURVEY))

        val surveyToDelete = surveyController.find(surveyId = surveyId)
            ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyController.deleteSurvey(surveyToDelete)

        return createNoContent()
    }

    override fun ping(): Response {
        return createOk("pong")
    }

    companion object {
        const val SURVEY = "Survey"
    }

}

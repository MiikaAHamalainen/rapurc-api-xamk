package fi.metatavu.rapurc.api.impl

import fi.metatavu.rapurc.api.UserRole
import fi.metatavu.rapurc.api.impl.surveys.SurveyController
import fi.metatavu.rapurc.api.impl.translate.SurveyTranslator
import fi.metatavu.rapurc.api.model.Survey
import fi.metatavu.rapurc.api.model.SurveyStatus
import fi.metatavu.rapurc.api.spec.V1Api
import java.util.*
import javax.annotation.security.RolesAllowed
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
class V1ApiImpl : V1Api, AbstractApi() {

    @Inject
    lateinit var keycloakController: KeycloakController

    @Inject
    lateinit var surveyController: SurveyController

    @Inject
    lateinit var surveyTranslator: SurveyTranslator

    /* SURVEYS */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listSurveys(firstResult: Int?, maxResults: Int?, address: String?, status: SurveyStatus?): Response {
        val userId = loggedUserId ?: return createForbidden(NO_LOGGED_USER_ID)

        var groupId: UUID? = null
        if (!isAdmin()) {
            groupId = keycloakController.getGroupId(userId) ?: return createForbidden(createMissingGroupIdMessage(userId = userId))
        }

        val surveys = surveyController.list(
            firstResult = firstResult ?: 0,
            maxResults = maxResults ?: 10,
            address = address,
            status = status,
            keycloakGroupId = groupId
        )

        return createOk(surveys.map(surveyTranslator::translate))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun createSurvey(survey: Survey?): Response {
        val userId = loggedUserId ?: return createForbidden(NO_LOGGED_USER_ID)
        survey ?: return createBadRequest(MISSING_REQUEST_BODY)
        val status = survey.status

        val groupId = keycloakController.getGroupId(userId) ?: return createForbidden(createMissingGroupIdMessage(userId = userId))
        val createdSurvey = surveyController.create(status = status, keycloakGroupId = groupId, creatorId = userId)
        return createOk(surveyTranslator.translate(createdSurvey))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun findSurvey(surveyId: UUID?): Response {
        val userId = loggedUserId ?: return createForbidden(NO_LOGGED_USER_ID)
        surveyId ?: return createBadRequest(createMissingIdFromRequestMessage(target = SURVEY))

        val foundSurvey = surveyController.find(surveyId = surveyId)
            ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        if (!isAdmin()) {
            val groupId = keycloakController.getGroupId(userId) ?: return createForbidden(createMissingGroupIdMessage(userId = userId))
            if (groupId != foundSurvey.keycloakGroupId) {
                return createForbidden(createWrongGroupMessage(userId = userId))
            }
        }

        return createOk(surveyTranslator.translate(foundSurvey))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun updateSurvey(surveyId: UUID?, survey: Survey?): Response {
        val userId = loggedUserId ?: return createForbidden(NO_LOGGED_USER_ID)
        survey ?: return createBadRequest(MISSING_REQUEST_BODY)
        surveyId ?: return createBadRequest(createMissingIdFromRequestMessage(target = SURVEY))

        val surveyToUpdate = surveyController.find(surveyId = surveyId)
            ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        if (!isAdmin()) {
            val groupId = keycloakController.getGroupId(userId) ?: return createForbidden(createMissingGroupIdMessage(userId = userId))
            if (groupId != surveyToUpdate.keycloakGroupId) {
                return createForbidden(createWrongGroupMessage(userId = userId))
            }
        }

        val status = survey.status
        val updatedSurvey = surveyController.update(
            survey = surveyToUpdate,
            status = status,
            lastModifierId = userId
        )

        return createOk(surveyTranslator.translate(updatedSurvey))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun deleteSurvey(surveyId: UUID?): Response {
        val userId = loggedUserId ?: return createForbidden(NO_LOGGED_USER_ID)
        surveyId ?: return createBadRequest(createMissingIdFromRequestMessage(target = SURVEY))

        val surveyToDelete = surveyController.find(surveyId = surveyId)
            ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        if (!isAdmin()) {
            val groupId = keycloakController.getGroupId(userId) ?: return createForbidden(createMissingGroupIdMessage(userId = userId))
            if (groupId != surveyToDelete.keycloakGroupId) {
                return createForbidden(createWrongGroupMessage(userId = userId))
            }
        }

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

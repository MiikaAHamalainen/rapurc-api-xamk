package fi.metatavu.rapurc.api.impl

import fi.metatavu.rapurc.api.UserRole
import fi.metatavu.rapurc.api.impl.owners.OwnerInformationController
import fi.metatavu.rapurc.api.impl.surveys.SurveyController
import fi.metatavu.rapurc.api.impl.translate.OwnerInformationTranslator
import fi.metatavu.rapurc.api.impl.translate.SurveyTranslator
import fi.metatavu.rapurc.api.model.OwnerInformation
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

    @Inject
    lateinit var ownerInformationController: OwnerInformationController

    @Inject
    lateinit var ownerInformationTranslator: OwnerInformationTranslator

    /* SURVEYS */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listSurveys(firstResult: Int?, maxResults: Int?, address: String?, status: SurveyStatus?): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

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
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
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
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
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
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
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

    /* OWNERS */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listOwnerInformation(surveyId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        if (!isAdmin()) {
            val groupId = keycloakController.getGroupId(userId) ?: return createForbidden(createMissingGroupIdMessage(userId = userId))
            if (groupId != survey.keycloakGroupId) {
                return createForbidden(createWrongGroupMessage(userId = userId))
            }
        }

        val ownerInformationList = ownerInformationController.list(survey = survey)
        return createOk(ownerInformationList.map(ownerInformationTranslator::translate))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun createOwnerInformation(surveyId: UUID, ownerInformation: OwnerInformation): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        if (ownerInformation.contactPerson == null) {
            return createBadRequest(createMissingObjectFromRequestMessage(CONTACT_PERSON))
        }

        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        if (ownerInformation.surveyId != surveyId) {
            return createForbidden(WRONG_SURVEY_FOR_OWNER_INFORMATION)
        }

        if (!isAdmin()) {
            val groupId = keycloakController.getGroupId(userId) ?: return createForbidden(createMissingGroupIdMessage(userId = userId))
            if (groupId != survey.keycloakGroupId) {
                return createForbidden(createWrongGroupMessage(userId = userId))
            }
        }

        val createdOwnerInformation = ownerInformationController.create(
            survey = survey,
            ownerName = ownerInformation.ownerName,
            businessId = ownerInformation.businessId,
            contactPerson = ownerInformation.contactPerson,
            userId = userId
        )

        return createOk(ownerInformationTranslator.translate(createdOwnerInformation))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun findOwnerInformation(surveyId: UUID, ownerId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        val foundOwnerInformation = ownerInformationController.find(ownerId) ?: return createNotFound(createNotFoundMessage(target = OWNER_INFORMATION, id = ownerId))
        if (foundOwnerInformation.survey != survey) {
            return createForbidden(WRONG_SURVEY_FOR_OWNER_INFORMATION)
        }

        if (!isAdmin()) {
            val groupId = keycloakController.getGroupId(userId) ?: return createForbidden(createMissingGroupIdMessage(userId = userId))
            if (groupId != survey.keycloakGroupId) {
                return createForbidden(createWrongGroupMessage(userId = userId))
            }
        }

        return createOk(ownerInformationTranslator.translate(foundOwnerInformation))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun updateOwnerInformation(
        surveyId: UUID,
        ownerId: UUID,
        payload: OwnerInformation
    ): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        if (payload.contactPerson == null) {
            return createBadRequest(createMissingObjectFromRequestMessage(CONTACT_PERSON))
        }

        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        val ownerInformationToUpdate = ownerInformationController.find(ownerId) ?: return createNotFound(createNotFoundMessage(target = OWNER_INFORMATION, id = ownerId))
        if (ownerInformationToUpdate.survey != survey) {
            return createForbidden(WRONG_SURVEY_FOR_OWNER_INFORMATION)
        }

        val newSurvey = surveyController.find(surveyId = payload.surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = payload.surveyId))

        if (!isAdmin()) {
            val groupId = keycloakController.getGroupId(userId) ?: return createForbidden(createMissingGroupIdMessage(userId = userId))

            if (groupId != survey.keycloakGroupId) {
                return createForbidden(createWrongGroupMessage(userId = userId))
            }

            if (groupId != newSurvey.keycloakGroupId) {
                return createForbidden(createWrongGroupMessage(userId = userId))
            }
        }

        val updatedOwnerInformation = ownerInformationController.update(ownerInformationToUpdate, payload, newSurvey, userId)
        return createOk(ownerInformationTranslator.translate(updatedOwnerInformation))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun deleteOwnerInformation(surveyId: UUID, ownerId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        if (!isAdmin()) {
            val groupId = keycloakController.getGroupId(userId) ?: return createForbidden(createMissingGroupIdMessage(userId = userId))
            if (groupId != survey.keycloakGroupId) {
                return createForbidden(createWrongGroupMessage(userId = userId))
            }
        }

        val ownerInformationToDelete = ownerInformationController.find(ownerId) ?: return createNotFound(createNotFoundMessage(target = OWNER_INFORMATION, id = ownerId))

        if (ownerInformationToDelete.survey != survey) {
            return createForbidden(WRONG_SURVEY_FOR_OWNER_INFORMATION)
        }

        ownerInformationController.delete(ownerInformationToDelete)
        return createNoContent()
    }

    override fun ping(): Response {
        return createOk("pong")
    }

    companion object {

        /**
         * Creates missing group id from user message
         *
         * @param userId user id
         */
        protected fun createMissingGroupIdMessage(userId: UUID): String {
            return "User $userId belongs to no group"
        }

        /**
         * Creates wrong group id message
         *
         * @param userId user id
         */
        protected fun createWrongGroupMessage(userId: UUID): String {
            return "User $userId belongs to different group"
        }

        const val WRONG_SURVEY_FOR_OWNER_INFORMATION = "Owner information belongs to different survey!"
        const val SURVEY = "Survey"
        const val OWNER_INFORMATION = "Owner information"
        const val CONTACT_PERSON = "Contact person"
    }

}

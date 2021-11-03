package fi.metatavu.rapurc.api.impl

import fi.metatavu.rapurc.api.UserRole
import fi.metatavu.rapurc.api.impl.buildings.BuildingController
import fi.metatavu.rapurc.api.impl.materials.ReusableController
import fi.metatavu.rapurc.api.impl.materials.ReusableMaterialController
import fi.metatavu.rapurc.api.impl.owners.OwnerInformationController
import fi.metatavu.rapurc.api.impl.surveys.SurveyController
import fi.metatavu.rapurc.api.impl.translate.*
import fi.metatavu.rapurc.api.model.*
import fi.metatavu.rapurc.api.spec.V1Api
import java.time.LocalDate
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

    @Inject
    lateinit var buildingController: BuildingController

    @Inject
    lateinit var buildingTranslator: BuildingTranslator

    @Inject
    lateinit var reusableController: ReusableController

    @Inject
    lateinit var reusableTranslator: ReusableTranslator

    @Inject
    lateinit var reuseableMaterialController: ReusableMaterialController

    @Inject
    lateinit var reusableMaterialTranslator: ReusableMaterialTranslator

    /* SURVEYS */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listSurveys(
        firstResult: Int?,
        maxResults: Int?,
        address: String?,
        status: SurveyStatus?,
        type: SurveyType?,
        startDate: LocalDate?,
        endDate: LocalDate?
    ): Response? {
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
            type = type,
            startDate = startDate,
            endDate = endDate,
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
        val createdSurvey = surveyController.create(
            status = status,
            keycloakGroupId = groupId,
            type = survey.type,
            startDate = survey.startDate,
            endDate = survey.endDate,
            creatorId = userId
        )
        return createOk(surveyTranslator.translate(createdSurvey))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun findSurvey(surveyId: UUID?): Response {
        val userId = loggedUserId ?: return createForbidden(NO_LOGGED_USER_ID)
        surveyId ?: return createBadRequest(createMissingIdFromRequestMessage(target = SURVEY))

        val foundSurvey = surveyController.find(surveyId = surveyId)
            ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, foundSurvey)?.let { return it }

        return createOk(surveyTranslator.translate(foundSurvey))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun updateSurvey(surveyId: UUID?, survey: Survey?): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        survey ?: return createBadRequest(MISSING_REQUEST_BODY)
        surveyId ?: return createBadRequest(createMissingIdFromRequestMessage(target = SURVEY))

        val surveyToUpdate = surveyController.find(surveyId = surveyId)
            ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, surveyToUpdate)?.let { return it }

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

        surveyAccessRightsCheck(userId, surveyToDelete)?.let { return it }

        surveyController.deleteSurvey(surveyToDelete)

        return createNoContent()
    }

    /* OWNERS */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listOwnerInformation(surveyId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val ownerInformationList = ownerInformationController.list(survey = survey)
        return createOk(ownerInformationList.map(ownerInformationTranslator::translate))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun createOwnerInformation(surveyId: UUID, ownerInformation: OwnerInformation): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        ownerInformation.contactPerson ?: return createBadRequest(createMissingObjectFromRequestMessage(CONTACT_PERSON))

        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        if (ownerInformation.surveyId != surveyId) {
            return createForbidden(createWrongSurveyMessage(OWNER_INFORMATION, surveyId))
        }

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val createdOwnerInformation = ownerInformationController.create(
            survey = survey,
            ownerName = ownerInformation.ownerName,
            businessId = ownerInformation.businessId,
            contactPerson = ownerInformation.contactPerson,
            creatorId = userId
        )

        return createOk(ownerInformationTranslator.translate(createdOwnerInformation))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun findOwnerInformation(surveyId: UUID, ownerId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        val foundOwnerInformation = ownerInformationController.find(ownerId) ?: return createNotFound(createNotFoundMessage(target = OWNER_INFORMATION, id = ownerId))
        if (foundOwnerInformation.survey != survey) {
            return createForbidden(createWrongSurveyMessage(OWNER_INFORMATION, surveyId))
        }

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        return createOk(ownerInformationTranslator.translate(foundOwnerInformation))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun updateOwnerInformation(
        surveyId: UUID,
        ownerId: UUID,
        payload: OwnerInformation
    ): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        payload.contactPerson ?: return createBadRequest(createMissingObjectFromRequestMessage(CONTACT_PERSON))

        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        val ownerInformationToUpdate = ownerInformationController.find(ownerId) ?: return createNotFound(createNotFoundMessage(target = OWNER_INFORMATION, id = ownerId))
        if (ownerInformationToUpdate.survey != survey) {
            return createForbidden(createWrongSurveyMessage(OWNER_INFORMATION, surveyId))
        }

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val updatedOwnerInformation = ownerInformationController.update(ownerInformationToUpdate, payload, userId)
        return createOk(ownerInformationTranslator.translate(updatedOwnerInformation))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun deleteOwnerInformation(surveyId: UUID, ownerId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val ownerInformationToDelete = ownerInformationController.find(ownerId) ?: return createNotFound(createNotFoundMessage(target = OWNER_INFORMATION, id = ownerId))

        if (ownerInformationToDelete.survey != survey) {
            return createForbidden(createWrongSurveyMessage(OWNER_INFORMATION, surveyId))
        }

        ownerInformationController.delete(ownerInformationToDelete)
        return createNoContent()
    }

    /* Buildings */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listBuildings(surveyId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val buildings = buildingController.list(survey = survey)
        return createOk(buildings.map(buildingTranslator::translate))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun createBuilding(surveyId: UUID, building: Building): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        building.address ?: return createBadRequest(createMissingObjectFromRequestMessage(ADDRESS))

        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        if (building.surveyId != surveyId) {
            return createForbidden(createWrongSurveyMessage(BUILDING, surveyId))
        }

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val createdBuilding = buildingController.create(
            survey = survey,
            building = building,
            creatorId = userId
        )

        return createOk(buildingTranslator.translate(createdBuilding))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun findBuilding(surveyId: UUID, buildingId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        val foundBuilding = buildingController.find(buildingId) ?: return createNotFound(createNotFoundMessage(target = BUILDING, id = buildingId))
        if (foundBuilding.survey != survey) {
            return createForbidden(createWrongSurveyMessage(BUILDING, surveyId))
        }

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        return createOk(buildingTranslator.translate(foundBuilding))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun updateBuilding(surveyId: UUID, buildingId: UUID, payload: Building): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        payload.address ?: return createBadRequest(createMissingObjectFromRequestMessage(ADDRESS))

        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        val buildingToUpdate = buildingController.find(buildingId) ?: return createNotFound(createNotFoundMessage(target = BUILDING, id = buildingId))
        if (buildingToUpdate.survey != survey) {
            return createForbidden(createWrongSurveyMessage(BUILDING, surveyId))
        }

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val updatedBuilding = buildingController.update(buildingToUpdate, payload, userId)
        return createOk(buildingTranslator.translate(updatedBuilding))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun deleteBuilding(surveyId: UUID, buildingId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val buildingToDelete = buildingController.find(buildingId) ?: return createNotFound(createNotFoundMessage(target = BUILDING, id = buildingId))

        if (buildingToDelete.survey != survey) {
            return createForbidden(createWrongSurveyMessage(BUILDING, surveyId))
        }

        buildingController.delete(buildingToDelete)
        return createNoContent()
    }

    /* REUSABLE MATERIALS */

    @RolesAllowed(value = [ UserRole.ADMIN.name, UserRole.USER.name ])
    override fun listReusableMaterials(): Response {
        val allMaterials = reuseableMaterialController.listAll()
        return createOk(allMaterials.map(reusableMaterialTranslator::translate))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun createReusableMaterial(reusableMaterial: ReusableMaterial): Response {
        reusableMaterial.name ?: return createBadRequest(createMissingObjectFromRequestMessage("Reusable material name"))
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val createdMaterial = reuseableMaterialController.create(reusableMaterial, userId)
        return createOk(reusableMaterialTranslator.translate(createdMaterial))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name, UserRole.USER.name ])
    override fun findReusableMaterial(reusableMaterialId: UUID): Response {
        loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val foundMaterial = reuseableMaterialController.find(reusableMaterialId) ?: return createNotFound(createNotFoundMessage(REUSABLE_MATERIAL, reusableMaterialId))
        return createOk(reusableMaterialTranslator.translate(foundMaterial))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun updateReusableMaterial(reusableMaterialId: UUID, reusableMaterial: ReusableMaterial): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val materialToUpdate = reuseableMaterialController.find(reusableMaterialId) ?: return createNotFound(createNotFoundMessage(REUSABLE_MATERIAL, reusableMaterialId))
        val updatedMaterial = reuseableMaterialController.update(materialToUpdate, reusableMaterial, userId)
        return createOk(reusableMaterialTranslator.translate(updatedMaterial))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun deleteReusableMaterial(reusableMaterialId: UUID): Response {
        loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val materialToDelete = reuseableMaterialController.find(reusableMaterialId) ?: return createNotFound(createNotFoundMessage(REUSABLE_MATERIAL, reusableMaterialId))

        if (reusableController.listByMaterial(materialToDelete)?.isNotEmpty() == true) {
            return createBadRequest("Reusables depend on this material!")
        }

        reuseableMaterialController.delete(materialToDelete)
        return createNoContent()
    }

    /* REUSABLES */

    override fun listSurveyReusables(surveyId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val reusables = reusableController.listBySurvey(survey)
        return createOk(reusables?.map(reusableTranslator::translate))
    }

    override fun createSurveyReusable(surveyId: UUID, reusable: Reusable): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }
        reusable.componentName ?: return createBadRequest(createMissingObjectFromRequestMessage("Component name"))
        reuseableMaterialController.find(reusable.reusableMaterialId) ?: return createNotFound(createNotFoundMessage(REUSABLE_MATERIAL, reusable.reusableMaterialId))

        val createdReusable = reusableController.create(reusable, survey, userId)
        return createOk(reusableTranslator.translate(createdReusable))
    }

    override fun findSurveyReusable(surveyId: UUID, reusableId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val reusable = reusableController.findById(reusableId) ?: return createNotFound(createNotFoundMessage(REUSABLE, reusableId))

        if (reusable.survey != survey) {
            return createForbidden(createWrongSurveyMessage(REUSABLE, surveyId))
        }

        return createOk(reusableTranslator.translate(reusable))
    }

    override fun updateSurveyReusable(surveyId: UUID, reusableId: UUID, reusable: Reusable): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val reusableToUpdate = reusableController.findById(reusableId) ?: return createNotFound(createNotFoundMessage(REUSABLE, reusableId))

        if (reusableToUpdate.survey != survey) {
            return createForbidden(createWrongSurveyMessage(REUSABLE, surveyId))
        }

        reuseableMaterialController.find(reusable.reusableMaterialId) ?: return createNotFound(createNotFoundMessage(REUSABLE_MATERIAL, reusable.reusableMaterialId))
        reusable.componentName ?: return createBadRequest(createMissingObjectFromRequestMessage("Component name"))

        val updatedReusable = reusableController.updateReusable(reusableToUpdate, reusable, userId)
        return createOk(reusableTranslator.translate(updatedReusable))
    }

    override fun deleteSurveyReusable(surveyId: UUID, reusableId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val reusableToDelete = reusableController.findById(reusableId) ?: return createNotFound(createNotFoundMessage(REUSABLE, reusableId))

        if (reusableToDelete.survey != survey) {
            return createForbidden(createWrongSurveyMessage(REUSABLE, surveyId))
        }

        reusableController.delete(reusableToDelete)
        return createNoContent()
    }

    override fun ping(): Response {
        return createOk("pong")
    }

    /**
     * Checks the access rights of a user for a survey
     *
     * @param userId user id
     * @param survey survey
     * @return null if no errors or response with error code
     */
    private fun surveyAccessRightsCheck(userId: UUID, survey: fi.metatavu.rapurc.api.persistence.model.Survey): Response? {
        if (!isAdmin()) {
            val groupId = keycloakController.getGroupId(userId) ?: return createForbidden(createMissingGroupIdMessage(userId = userId))
            if (groupId != survey.keycloakGroupId) {
                return createForbidden(createWrongGroupMessage(userId = userId))
            }
        }

        return null
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

        /**
         * Creates wrong survey message
         *
         * @param target object
         * @param surveyId survey id
         * @return error message
         */
        protected fun createWrongSurveyMessage(target: String, surveyId: UUID): String {
            return "$target belongs to different survey than $surveyId"
        }

        const val SURVEY = "Survey"
        const val OWNER_INFORMATION = "Owner information"
        const val BUILDING = "Building"
        const val CONTACT_PERSON = "Contact person"
        const val ADDRESS = "Address"
        const val REUSABLE = "Reusable"
        const val REUSABLE_MATERIAL = "Reusable materials"
    }

}

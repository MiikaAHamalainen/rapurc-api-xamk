package fi.metatavu.rapurc.api.impl

import fi.metatavu.rapurc.api.UserRole
import fi.metatavu.rapurc.api.impl.buildings.BuildingController
import fi.metatavu.rapurc.api.impl.buildings.BuildingTypeController
import fi.metatavu.rapurc.api.impl.materials.HazardousMaterialController
import fi.metatavu.rapurc.api.impl.materials.ReusableController
import fi.metatavu.rapurc.api.impl.materials.ReusableMaterialController
import fi.metatavu.rapurc.api.impl.materials.WasteMaterialController
import fi.metatavu.rapurc.api.impl.owners.OwnerInformationController
import fi.metatavu.rapurc.api.impl.surveyors.SurveyorController
import fi.metatavu.rapurc.api.impl.surveys.AttachmentController
import fi.metatavu.rapurc.api.impl.surveys.SurveyController
import fi.metatavu.rapurc.api.impl.translate.*
import fi.metatavu.rapurc.api.impl.waste.*
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
    lateinit var surveyorController: SurveyorController

    @Inject
    lateinit var surveyorTranslator: SurveyorTranslator

    @Inject
    lateinit var ownerInformationController: OwnerInformationController

    @Inject
    lateinit var ownerInformationTranslator: OwnerInformationTranslator

    @Inject
    lateinit var buildingController: BuildingController

    @Inject
    lateinit var buildingTranslator: BuildingTranslator

    @Inject
    lateinit var buildingTypeController: BuildingTypeController

    @Inject
    lateinit var buildingTypeTranslator: BuildingTypeTranslator

    @Inject
    lateinit var reusableController: ReusableController

    @Inject
    lateinit var reusableTranslator: ReusableTranslator

    @Inject
    lateinit var reuseableMaterialController: ReusableMaterialController

    @Inject
    lateinit var reusableMaterialTranslator: ReusableMaterialTranslator

    @Inject
    lateinit var wasteMaterialController: WasteMaterialController

    @Inject
    lateinit var wasteMaterialTranslator: WasteMaterialTranslator

    @Inject
    lateinit var wasteCategoryController: WasteCategoryController

    @Inject
    lateinit var wasteCategoryTranslator: WasteCategoryTranslator

    @Inject
    lateinit var wasteController: WasteController

    @Inject
    lateinit var wasteTranslator: WasteTranslator

    @Inject
    lateinit var usageController: UsageController

    @Inject
    lateinit var usageTranslator: UsageTranslator

    @Inject
    lateinit var hazardousMaterialController: HazardousMaterialController

    @Inject
    lateinit var hazardousMaterialTranslator: HazardousMaterialTranslator

    @Inject
    lateinit var wasteSpecifierController: WasteSpecifierController

    @Inject
    lateinit var wasteSpecifierTranslator: WasteSpecifierTranslator

    @Inject
    lateinit var hazardousWasteController: HazardousWasteController

    @Inject
    lateinit var hazardousWasteTranslator: HazardousWasteTranslator

    @Inject
    lateinit var attachmentsController: AttachmentController

    @Inject
    lateinit var attachmentTranslator: AttachmentTranslator


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

        val updatedSurvey = surveyController.update(
            survey = surveyToUpdate,
            status = survey.status,
            startDate = survey.startDate,
            endDate = survey.endDate,
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

        surveyController.deleteSurvey(surveyToDelete, userId)

        return createNoContent()
    }

    /** Surveyors */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listSurveyors(surveyId: UUID): Response {
        loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        val surveyors = surveyorController.list(survey = survey)
        return createOk(surveyors.map(surveyorTranslator::translate))
    }

    override fun createSurveyor(surveyId: UUID, surveyor: Surveyor): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val createdSurveyor = surveyorController.create(
            survey = survey,
            surveyor = surveyor,
            creatorId = userId
        )

        return createOk(surveyorTranslator.translate(createdSurveyor))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun findSurveyor(surveyId: UUID, surveyorId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val foundSurveyor = surveyorController.find(surveyorId = surveyorId) ?: return createNotFound(createNotFoundMessage(target = SURVEYOR, id = surveyorId))

        return createOk(surveyorTranslator.translate(foundSurveyor))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun updateSurveyor(surveyId: UUID, surveyorId: UUID, surveyor: Surveyor): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val foundSurveyor = surveyorController.find(surveyorId = surveyorId) ?: return createNotFound(createNotFoundMessage(target = SURVEYOR, id = surveyorId))

        val updatedSurveyor = surveyorController.update(
            surveyorToUpdate = foundSurveyor,
            surveyor = surveyor,
            lastModifierId = userId
        )

        return createOk(surveyorTranslator.translate(updatedSurveyor))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun deleteSurveyor(surveyId: UUID, surveyorId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val foundSurveyor = surveyorController.find(surveyorId = surveyorId) ?: return createNotFound(createNotFoundMessage(target = SURVEYOR, id = surveyorId))
        surveyorController.delete(surveyorToDelete = foundSurveyor, userId = userId)

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

        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        if (ownerInformation.surveyId != surveyId) {
            return createForbidden(createWrongSurveyMessage(target = OWNER_INFORMATION, surveyId = surveyId))
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
            return createForbidden(createWrongSurveyMessage(target = OWNER_INFORMATION, surveyId = surveyId))
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

        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        val ownerInformationToUpdate = ownerInformationController.find(ownerId) ?: return createNotFound(createNotFoundMessage(target = OWNER_INFORMATION, id = ownerId))
        if (ownerInformationToUpdate.survey != survey) {
            return createForbidden(createWrongSurveyMessage(target = OWNER_INFORMATION, surveyId = surveyId))
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
            return createForbidden(createWrongSurveyMessage(target = OWNER_INFORMATION, surveyId = surveyId))
        }

        ownerInformationController.delete(ownerInformationToDelete, userId)
        return createNoContent()
    }

    /* Building types */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listBuildingTypes(): Response {
        return createOk(buildingTypeController.list().map(buildingTypeTranslator::translate))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun createBuildingType(buildingType: BuildingType): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val createdBuildingType = buildingTypeController.create(
            name = buildingType.name,
            code = buildingType.code,
            userId = userId
        )

        return createOk(buildingTypeTranslator.translate(createdBuildingType))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun findBuildingType(buildingTypeId: UUID): Response {
        val foundBuildingType = buildingTypeController.find(buildingTypeId) ?: return createNotFound(createNotFoundMessage(target = BUILDING_TYPE, id = buildingTypeId))

        return createOk(buildingTypeTranslator.translate(foundBuildingType))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun updateBuildingType(buildingTypeId: UUID, buildingType: BuildingType): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val buildingTypeToUpdate = buildingTypeController.find(buildingTypeId) ?: return createNotFound(createNotFoundMessage(target = BUILDING_TYPE, id = buildingTypeId))
        val updatedBuildingType = buildingTypeController.update(
            oldBuildingType = buildingTypeToUpdate,
            newBuildingType = buildingType,
            modifierId = userId
        )

        return createOk(buildingTypeTranslator.translate(updatedBuildingType))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun deleteBuildingType(buildingTypeId: UUID): Response {
        val buildingTypeToDelete = buildingTypeController.find(buildingTypeId) ?: return createNotFound(createNotFoundMessage(target = BUILDING_TYPE, id = buildingTypeId))

        if (buildingController.list(survey = null, buildingType = buildingTypeToDelete).isNotEmpty()) {
            return createConflict(createDeleteConflictMessage(target = BUILDING_TYPE, dependentObject = BUILDING, id = buildingTypeId))
        }

        buildingTypeController.delete(buildingTypeToDelete)
        return createNoContent()
    }

    /* Buildings */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listBuildings(surveyId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val buildings = buildingController.list(survey = survey, buildingType = null)
        return createOk(buildings.map(buildingTranslator::translate))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun createBuilding(surveyId: UUID, building: Building): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        if (building.surveyId != surveyId) {
            return createForbidden(createWrongSurveyMessage(target = BUILDING, surveyId = surveyId))
        }

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        var buildingType: fi.metatavu.rapurc.api.persistence.model.BuildingType? = null
        if (building.buildingTypeId != null) {
            buildingType = buildingTypeController.find(buildingTypeId = building.buildingTypeId) ?: return createNotFound(createNotFoundMessage(target = BUILDING_TYPE, id = building.buildingTypeId))
        }

        val createdBuilding = buildingController.create(
            survey = survey,
            building = building,
            buildingType = buildingType,
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
            return createForbidden(createWrongSurveyMessage(target = BUILDING, surveyId = surveyId))
        }

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        return createOk(buildingTranslator.translate(foundBuilding))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun updateBuilding(surveyId: UUID, buildingId: UUID, payload: Building): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        val buildingToUpdate = buildingController.find(buildingId) ?: return createNotFound(createNotFoundMessage(target = BUILDING, id = buildingId))
        if (buildingToUpdate.survey != survey) {
            return createForbidden(createWrongSurveyMessage(target = BUILDING, surveyId = surveyId))
        }

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        var buildingType: fi.metatavu.rapurc.api.persistence.model.BuildingType? = null
        if (payload.buildingTypeId != null) {
            buildingType = buildingTypeController.find(buildingTypeId = payload.buildingTypeId) ?: return createNotFound(createNotFoundMessage(target = BUILDING_TYPE, id = payload.buildingTypeId))
        }

        val updatedBuilding = buildingController.update(
            buildingToUpdate = buildingToUpdate,
            building = payload,
            newBuildingType = buildingType,
            userId = userId
        )

        return createOk(buildingTranslator.translate(updatedBuilding))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun deleteBuilding(surveyId: UUID, buildingId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val buildingToDelete = buildingController.find(buildingId) ?: return createNotFound(createNotFoundMessage(target = BUILDING, id = buildingId))

        if (buildingToDelete.survey != survey) {
            return createForbidden(createWrongSurveyMessage(target = BUILDING, surveyId = surveyId))
        }

        buildingController.delete(buildingToDelete, userId)
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
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val createdMaterial = reuseableMaterialController.create(reusableMaterial, userId)
        return createOk(reusableMaterialTranslator.translate(createdMaterial))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name, UserRole.USER.name ])
    override fun findReusableMaterial(reusableMaterialId: UUID): Response {
        loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val foundMaterial = reuseableMaterialController.find(reusableMaterialId) ?: return createNotFound(createNotFoundMessage(target = REUSABLE_MATERIAL, id = reusableMaterialId))
        return createOk(reusableMaterialTranslator.translate(foundMaterial))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun updateReusableMaterial(reusableMaterialId: UUID, reusableMaterial: ReusableMaterial): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val materialToUpdate = reuseableMaterialController.find(reusableMaterialId) ?: return createNotFound(createNotFoundMessage(target = REUSABLE_MATERIAL, id = reusableMaterialId))
        val updatedMaterial = reuseableMaterialController.update(
            materialToUpdate = materialToUpdate,
            reusableMaterial = reusableMaterial,
            userId = userId
        )

        return createOk(reusableMaterialTranslator.translate(updatedMaterial))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun deleteReusableMaterial(reusableMaterialId: UUID): Response {
        loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val materialToDelete = reuseableMaterialController.find(reusableMaterialId) ?: return createNotFound(createNotFoundMessage(target = REUSABLE_MATERIAL, id = reusableMaterialId))

        if (reusableController.list(survey = null, material = materialToDelete)?.isNotEmpty() == true) {
            return createBadRequest("Reusables depend on this material!")
        }

        reuseableMaterialController.delete(materialToDelete)
        return createNoContent()
    }

    /* REUSABLES */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listSurveyReusables(surveyId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val reusables = reusableController.list(survey = survey, material = null)
        return createOk(reusables?.map(reusableTranslator::translate))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun createSurveyReusable(surveyId: UUID, reusable: Reusable): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }
        reuseableMaterialController.find(reusable.reusableMaterialId) ?: return createNotFound(createNotFoundMessage(target = REUSABLE_MATERIAL, id = reusable.reusableMaterialId))

        val createdReusable = reusableController.create(
            reusable = reusable,
            survey = survey,
            userId = userId
        )

        return createOk(reusableTranslator.translate(createdReusable))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun findSurveyReusable(surveyId: UUID, reusableId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val reusable = reusableController.findById(reusableId) ?: return createNotFound(createNotFoundMessage(target = REUSABLE, id = reusableId))

        if (reusable.survey != survey) {
            return createForbidden(createWrongSurveyMessage(target = REUSABLE, surveyId = surveyId))
        }

        return createOk(reusableTranslator.translate(reusable))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun updateSurveyReusable(surveyId: UUID, reusableId: UUID, reusable: Reusable): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val reusableToUpdate = reusableController.findById(reusableId) ?: return createNotFound(createNotFoundMessage(target = REUSABLE, id = reusableId))

        if (reusableToUpdate.survey != survey) {
            return createForbidden(createWrongSurveyMessage(REUSABLE, surveyId))
        }

        reuseableMaterialController.find(reusable.reusableMaterialId) ?: return createNotFound(createNotFoundMessage(target = REUSABLE_MATERIAL, id = reusable.reusableMaterialId))

        val updatedReusable = reusableController.updateReusable(
            reusableToUpdate = reusableToUpdate,
            reusable = reusable,
            userId = userId
        )

        return createOk(reusableTranslator.translate(updatedReusable))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun deleteSurveyReusable(surveyId: UUID, reusableId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val reusableToDelete = reusableController.findById(reusableId) ?: return createNotFound(createNotFoundMessage(target = REUSABLE, id= reusableId))

        if (reusableToDelete.survey != survey) {
            return createForbidden(createWrongSurveyMessage(target = REUSABLE, surveyId = surveyId))
        }

        reusableController.delete(reusableToDelete, userId)
        return createNoContent()
    }

    /* Waste category */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listWasteCategories(): Response {
        val categories = wasteCategoryController.list()
        return createOk(categories.map(wasteCategoryTranslator::translate))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun createWasteCategory(wasteCategory: WasteCategory): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val createdCategory = wasteCategoryController.create(
            wasteCategory = wasteCategory,
            userId = userId
        )

        return createOk(wasteCategoryTranslator.translate(createdCategory))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun findWasteCategory(wasteCategoryId: UUID): Response {
        val foundWasteCategory = wasteCategoryController.find(wasteCategoryId = wasteCategoryId) ?: return createNotFound(createNotFoundMessage(target = WASTE_CATEGORY, id = wasteCategoryId))

        return createOk(wasteCategoryTranslator.translate(foundWasteCategory))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun updateWasteCategory(wasteCategoryId: UUID, payload: WasteCategory): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val foundWasteCategory = wasteCategoryController.find(wasteCategoryId = wasteCategoryId) ?: return createNotFound(createNotFoundMessage(target = WASTE_CATEGORY, id = wasteCategoryId))
        val updated = wasteCategoryController.update(
            categoryToUpdate = foundWasteCategory,
            wasteCategory = payload,
            userId = userId
        )

        return createOk(wasteCategoryTranslator.translate(updated))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun deleteWasteCategory(wasteCategoryId: UUID): Response {
        val foundWasteCategory = wasteCategoryController.find(wasteCategoryId = wasteCategoryId) ?: return createNotFound(createNotFoundMessage(target = WASTE_CATEGORY, id = wasteCategoryId))

        val materialsForCategory = wasteMaterialController.list(wasteCategory = foundWasteCategory)
        val hazMaterialsForCategory = hazardousMaterialController.list(wasteCategory = foundWasteCategory)
        if (materialsForCategory.isNotEmpty() || hazMaterialsForCategory.isNotEmpty()) {
            return createConflict("Materials belong to this category")
        }

        wasteCategoryController.delete(foundWasteCategory)

        return createNoContent()
    }

    /* Waste material */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listWasteMaterials(): Response {
        val wasteMaterials = wasteMaterialController.list(wasteCategory = null)
        return createOk(wasteMaterials.map(wasteMaterialTranslator::translate))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun createWasteMaterial(wasteMaterial: WasteMaterial): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val foundWasteCategory = wasteCategoryController.find(wasteCategoryId = wasteMaterial.wasteCategoryId) ?: return createNotFound(createNotFoundMessage(target = WASTE_CATEGORY, id = wasteMaterial.wasteCategoryId))
        val createdWasteMaterial = wasteMaterialController.create(wasteMaterial, foundWasteCategory, userId)

        return createOk(wasteMaterialTranslator.translate(createdWasteMaterial))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun findWasteMaterial(wasteMaterialId: UUID): Response {
        val foundWasteMaterial = wasteMaterialController.find(wasteMaterialId = wasteMaterialId) ?: return createNotFound(createNotFoundMessage(target = WASTE_MATERIAL, id = wasteMaterialId))
        return createOk(wasteMaterialTranslator.translate(foundWasteMaterial))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun updateWasteMaterial(wasteMaterialId: UUID, wasteMaterial: WasteMaterial): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val materialToUpdate = wasteMaterialController.find(wasteMaterialId = wasteMaterialId) ?: return createNotFound(createNotFoundMessage(target = WASTE_MATERIAL, id = wasteMaterialId))
        val newWasteCategory = wasteCategoryController.find(wasteCategoryId = wasteMaterial.wasteCategoryId) ?: return createNotFound(createNotFoundMessage(target = WASTE_CATEGORY, id = wasteMaterial.wasteCategoryId))
        val updatedWasteMaterial = wasteMaterialController.update(
            oldWasteMaterial = materialToUpdate,
            newWasteMaterial = wasteMaterial,
            newWasteCategory = newWasteCategory,
            userId = userId
        )

        return createOk(wasteMaterialTranslator.translate(updatedWasteMaterial))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun deleteWasteMaterial(wasteMaterialId: UUID): Response {
        val materialToDelete = wasteMaterialController.find(wasteMaterialId = wasteMaterialId) ?: return createNotFound(createNotFoundMessage(target = WASTE_MATERIAL, id = wasteMaterialId))

        if (wasteController.list(survey = null, wasteMaterial = materialToDelete, usage = null).isNotEmpty()) {
            return createConflict(createDeleteConflictMessage(target = WASTE_MATERIAL, dependentObject = WASTE, id = wasteMaterialId))
        }

        wasteMaterialController.delete(materialToDelete)
        return createNoContent()
    }

    /* Survey waste */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listSurveyWastes(surveyId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val wastes = wasteController.list(survey = survey, wasteMaterial = null, usage = null)
        return createOk(wastes.map(wasteTranslator::translate))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun createSurveyWaste(surveyId: UUID, waste: Waste): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))
        val wasteMaterial = wasteMaterialController.find(waste.wasteMaterialId) ?: return createNotFound(createNotFoundMessage(target = WASTE_MATERIAL, id = waste.wasteMaterialId))
        val usage = usageController.find(waste.usageId) ?: return createNotFound(createNotFoundMessage(target = USAGE, id = waste.usageId))

        val createdWaste = wasteController.create(
            usage = usage,
            wasteMaterial = wasteMaterial,
            amount = waste.amount,
            survey = survey,
            description = waste.description,
            userId = userId
        )

        return createOk(wasteTranslator.translate(createdWaste))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun findSurveyWaste(surveyId: UUID, wasteId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val foundWaste = wasteController.findById(wasteId) ?: return createNotFound(createNotFoundMessage(target = WASTE, id = wasteId))

        if (foundWaste.survey != survey) {
            return createForbidden(createWrongSurveyMessage(target = WASTE, surveyId = surveyId))
        }

        return createOk(wasteTranslator.translate(foundWaste))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun updateSurveyWaste(surveyId: UUID, wasteId: UUID, waste: Waste): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val wasteToUpdate = wasteController.findById(wasteId) ?: return createNotFound(createNotFoundMessage(target = WASTE, id = wasteId))

        if (wasteToUpdate.survey != survey) {
            return createForbidden(createWrongSurveyMessage(WASTE, surveyId))
        }

        val wasteMaterial = wasteMaterialController.find(waste.wasteMaterialId) ?: return createNotFound(createNotFoundMessage(target = WASTE_MATERIAL, id = waste.wasteMaterialId))
        val usage = usageController.find(waste.usageId) ?: return createNotFound(createNotFoundMessage(target = USAGE, id = waste.usageId))

        val updatedWaste = wasteController.updateWaste(
            wasteToUpdate = wasteToUpdate,
            waste = waste,
            newWasteMaterial = wasteMaterial,
            newUsage = usage,
            userId = userId
        )

        return createOk(wasteTranslator.translate(updatedWaste))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun deleteSurveyWaste(surveyId: UUID, wasteId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val wasteToDelete = wasteController.findById(wasteId) ?: return createNotFound(createNotFoundMessage(target = WASTE, id= wasteId))

        if (wasteToDelete.survey != survey) {
            return createForbidden(createWrongSurveyMessage(target = WASTE, surveyId = surveyId))
        }

        wasteController.delete(wasteToDelete, userId)
        return createNoContent()
    }

    /* Usages */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listUsages(): Response {
        return createOk(usageController.list().map(usageTranslator::translate))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun createUsage(usage: Usage): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val createdUsage = usageController.create(
            name = usage.name,
            userId = userId
        )

        return createOk(usageTranslator.translate(createdUsage))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun findUsage(usageId: UUID): Response {
        val foundUsage = usageController.find(usageId) ?: return createNotFound(createNotFoundMessage(target = USAGE, id = usageId))

        return createOk(usageTranslator.translate(foundUsage))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun updateUsage(usageId: UUID, usage: Usage): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val usageToUpdate = usageController.find(usageId) ?: return createNotFound(createNotFoundMessage(target = USAGE, id = usageId))
        val updatedUsage = usageController.update(
            oldUsage = usageToUpdate,
            newUsage = usage,
            userId = userId
        )

        return createOk(usageTranslator.translate(updatedUsage))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun deleteUsage(usageId: UUID): Response {
        val usageToDelete = usageController.find(usageId) ?: return createNotFound(createNotFoundMessage(target = USAGE, id = usageId))

        if (wasteController.list(survey = null, wasteMaterial = null, usage = usageToDelete).isNotEmpty()) {
            return createConflict(createDeleteConflictMessage(target = USAGE, dependentObject = WASTE, id = usageId))
        }

        usageController.delete(usageToDelete)
        return createNoContent()
    }

    /* Hazardous materials */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listHazardousMaterials(): Response {
        val hazardousMaterials = hazardousMaterialController.list(wasteCategory = null)
        return createOk(hazardousMaterials.map(hazardousMaterialTranslator::translate))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun createHazardousMaterial(hazardousMaterial: HazardousMaterial): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val foundWasteCategory = wasteCategoryController.find(wasteCategoryId = hazardousMaterial.wasteCategoryId) ?: return createNotFound(createNotFoundMessage(target = WASTE_CATEGORY, id = hazardousMaterial.wasteCategoryId))
        val foundHazardousMaterial = hazardousMaterialController.create(hazardousMaterial, foundWasteCategory, userId)

        return createOk(hazardousMaterialTranslator.translate(foundHazardousMaterial))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun findHazardousMaterial(hazardousMaterialId: UUID): Response {
        val hazardousMaterial = hazardousMaterialController.find(materialId = hazardousMaterialId) ?: return createNotFound(createNotFoundMessage(target = HAZ_MATERIAL, id = hazardousMaterialId))
        return createOk(hazardousMaterialTranslator.translate(hazardousMaterial))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun updateHazardousMaterial(hazardousMaterialId: UUID, hazardousMaterial: HazardousMaterial): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val materialToUpdate = hazardousMaterialController.find(materialId = hazardousMaterialId) ?: return createNotFound(createNotFoundMessage(target = HAZ_MATERIAL, id = hazardousMaterialId))
        val newWasteCategory = wasteCategoryController.find(wasteCategoryId = hazardousMaterial.wasteCategoryId) ?: return createNotFound(createNotFoundMessage(target = WASTE_CATEGORY, id = hazardousMaterial.wasteCategoryId))
        val updatedMaterial = hazardousMaterialController.update(
            hazardousMaterial = materialToUpdate,
            newHazardousMaterial = hazardousMaterial,
            newWasteCategory = newWasteCategory,
            userId = userId
        )

        return createOk(hazardousMaterialTranslator.translate(updatedMaterial))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun deleteHazardousMaterial(hazardousMaterialId: UUID): Response {
        val materialToDelete = hazardousMaterialController.find(materialId = hazardousMaterialId) ?: return createNotFound(createNotFoundMessage(target = HAZ_MATERIAL, id = hazardousMaterialId))

        if (hazardousWasteController.list(survey = null, wasteSpecifier = null, hazardousMaterial = materialToDelete).isNotEmpty()) {
            return createConflict(createDeleteConflictMessage(target = HAZ_MATERIAL, dependentObject = HAZARDOUS_WASTE, id = hazardousMaterialId))
        }

        hazardousMaterialController.delete(materialToDelete)
        return createNoContent()
    }

    /* Waste specifiers */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listWasteSpecifiers(): Response {
        val wasteSpecifiers = wasteSpecifierController.list()

        return createOk(wasteSpecifiers.map(wasteSpecifierTranslator::translate))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun createWasteSpecifier(wasteSpecifier: WasteSpecifier): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val createdWasteSpecifier = wasteSpecifierController.create(
            wasteSpecifier = wasteSpecifier,
            userId = userId
        )

        return createOk(wasteSpecifierTranslator.translate(createdWasteSpecifier))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun findWasteSpecifier(wasteSpecifierId: UUID): Response {
        val foundWasteSpecifier = wasteSpecifierController.find(wasteSpecifierId = wasteSpecifierId) ?: return createNotFound(createNotFoundMessage(target = WASTE_SPECIFIER, id = wasteSpecifierId))

        return createOk(wasteSpecifierTranslator.translate(foundWasteSpecifier))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun updateWasteSpecifier(wasteSpecifierId: UUID, wasteSpecifier: WasteSpecifier): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val wasteSpecifierToUpdate = wasteSpecifierController.find(wasteSpecifierId = wasteSpecifierId) ?: return createNotFound(createNotFoundMessage(target = WASTE_SPECIFIER, id = wasteSpecifierId))
        val updatedWasteSpecifier = wasteSpecifierController.update(
            wasteSpecifierToUpdate = wasteSpecifierToUpdate,
            wasteSpecifier = wasteSpecifier,
            userId = userId
        )

        return createOk(wasteSpecifierTranslator.translate(updatedWasteSpecifier))
    }

    @RolesAllowed(value = [ UserRole.ADMIN.name ])
    override fun deleteWasteSpecifier(wasteSpecifierId: UUID): Response {
        val specifierToDelete = wasteSpecifierController.find(wasteSpecifierId = wasteSpecifierId) ?: return createNotFound(createNotFoundMessage(target = WASTE_SPECIFIER, id = wasteSpecifierId))

        val dependentHazWastes = hazardousWasteController.list(
            survey = null,
            wasteSpecifier = specifierToDelete,
            hazardousMaterial = null
        )

        if (dependentHazWastes.isNotEmpty()) {
            return createConflict(createDeleteConflictMessage(target = WASTE_SPECIFIER, dependentObject = HAZARDOUS_WASTE, id = wasteSpecifierId))
        }

        wasteSpecifierController.delete(specifierToDelete)
        return createNoContent()
    }

    /* Survey hazardous waste */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listSurveyHazardousWastes(surveyId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val hazardousWasteList = hazardousWasteController.list(
            survey = survey,
            wasteSpecifier = null,
            hazardousMaterial = null
        )
        return createOk(hazardousWasteList.map(hazardousWasteTranslator::translate))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun createSurveyHazardousWaste(surveyId: UUID, hazardousWaste: HazardousWaste): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))
        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val hazardousMaterial = hazardousMaterialController.find(hazardousWaste.hazardousMaterialId) ?: return createNotFound(createNotFoundMessage(target = HAZ_MATERIAL, id = hazardousWaste.hazardousMaterialId))
        val wasteSpecifier = if (hazardousWaste.wasteSpecifierId == null) null else wasteSpecifierController.find(hazardousWaste.wasteSpecifierId) ?: return createNotFound(createNotFoundMessage(target = WASTE_SPECIFIER, id = hazardousWaste.wasteSpecifierId))

        val createdHazardousWaste = hazardousWasteController.create(
            survey = survey,
            hazardousMaterial = hazardousMaterial,
            wasteSpecifier = wasteSpecifier,
            amount = hazardousWaste.amount,
            description = hazardousWaste.description,
            userId = userId
        )

        return createOk(hazardousWasteTranslator.translate(createdHazardousWaste))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun findSurveyHazardousWaste(surveyId: UUID, hazardousWasteId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))
        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val foundHazardousWaste = hazardousWasteController.findById(hazardousWasteId) ?: return createNotFound(createNotFoundMessage(target = HAZARDOUS_WASTE, id = hazardousWasteId))
        if (foundHazardousWaste.survey != survey) {
            return createForbidden(createWrongSurveyMessage(target = HAZARDOUS_WASTE, surveyId = surveyId))
        }

        return createOk(hazardousWasteTranslator.translate(foundHazardousWaste))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun updateSurveyHazardousWaste(
        surveyId: UUID,
        hazardousWasteId: UUID,
        hazardousWaste: HazardousWaste
    ): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))
        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val hazardousWasteToUpdate = hazardousWasteController.findById(hazardousWasteId) ?: return createNotFound(createNotFoundMessage(target = HAZARDOUS_WASTE, id = hazardousWasteId))
        if (hazardousWasteToUpdate.survey != survey) {
            return createForbidden(createWrongSurveyMessage(target = HAZARDOUS_WASTE, surveyId = surveyId))
        }

        val hazardousMaterial = hazardousMaterialController.find(hazardousWaste.hazardousMaterialId) ?: return createNotFound(createNotFoundMessage(target = HAZ_MATERIAL, id = hazardousWaste.hazardousMaterialId))
        val wasteSpecifier = if (hazardousWaste.wasteSpecifierId == null) null else wasteSpecifierController.find(hazardousWaste.wasteSpecifierId) ?: return createNotFound(createNotFoundMessage(target = WASTE_SPECIFIER, id = hazardousWaste.wasteSpecifierId))

        val updatedHazardousWaste = hazardousWasteController.updateWaste(
            hazardousWaste = hazardousWasteToUpdate,
            newHazardousWaste = hazardousWaste,
            hazardousMaterial = hazardousMaterial,
            wasteSpecifier = wasteSpecifier,
            userId = userId
        )

        return createOk(hazardousWasteTranslator.translate(updatedHazardousWaste))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun deleteSurveyHazardousWaste(surveyId: UUID, hazardousWasteId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)

        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))
        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val hazardousWasteToDelete = hazardousWasteController.findById(hazardousWasteId) ?: return createNotFound(createNotFoundMessage(target = HAZARDOUS_WASTE, id = hazardousWasteId))
        if (hazardousWasteToDelete.survey != survey) {
            return createForbidden(createWrongSurveyMessage(target = HAZARDOUS_WASTE, surveyId = surveyId))
        }

        hazardousWasteController.delete(hazardousWasteToDelete, userId)
        return createNoContent()
    }

    /* ATTACHMENTS */

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun listSurveyAttachments(surveyId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val attachments = attachmentsController.list(survey = survey)
        return createOk(attachments.map(attachmentTranslator::translate))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun createSurveyAttachment(surveyId: UUID, attachment: Attachment): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val createdAttachment = attachmentsController.create(
            attachment = attachment,
            survey = survey,
            userId = userId
        )

        return createOk(attachmentTranslator.translate(createdAttachment))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun findSurveyAttachment(surveyId: UUID, attachmentId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val attachment = attachmentsController.findById(attachmentId) ?: return createNotFound(createNotFoundMessage(target = ATTACHMENT, id = attachmentId))

        if (attachment.survey != survey) {
            return createForbidden(createWrongSurveyMessage(target = ATTACHMENT, surveyId = surveyId))
        }

        return createOk(attachmentTranslator.translate(attachment))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun updateSurveyAttachment(
        surveyId: UUID,
        attachmentId: UUID,
        attachment: Attachment
    ): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val attachmentToUpdate = attachmentsController.findById(attachmentId) ?: return createNotFound(createNotFoundMessage(target = ATTACHMENT, id = attachmentId))

        if (attachmentToUpdate.survey != survey) {
            return createForbidden(createWrongSurveyMessage(ATTACHMENT, surveyId))
        }

        val updatedAttachment = attachmentsController.updateAttachment(
            attachmentToUpdate = attachmentToUpdate,
            attachment = attachment,
            userId = userId
        )

        return createOk(attachmentTranslator.translate(updatedAttachment))
    }

    @RolesAllowed(value = [ UserRole.USER.name ])
    override fun deleteSurveyAttachment(surveyId: UUID, attachmentId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_LOGGED_USER_ID)
        val survey = surveyController.find(surveyId = surveyId) ?: return createNotFound(createNotFoundMessage(target = SURVEY, id = surveyId))

        surveyAccessRightsCheck(userId, survey)?.let { return it }

        val attachmentToDelete = attachmentsController.findById(attachmentId) ?: return createNotFound(createNotFoundMessage(target = ATTACHMENT, id= attachmentId))

        if (attachmentToDelete.survey != survey) {
            return createForbidden(createWrongSurveyMessage(target = ATTACHMENT, surveyId = surveyId))
        }

        attachmentsController.delete(attachmentToDelete, userId)
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


        /**
         * Creates conflict of deletion message
         *
         * @param target object to delete
         * @param dependentObject object that depends on target
         * @param id object id
         * @return error message
         */
        protected fun createDeleteConflictMessage(target: String, dependentObject: String, id: UUID): String {
            return "$dependentObject depend on $target $id"
        }

        const val SURVEY = "Survey"
        const val SURVEYOR = "Surveyor"
        const val OWNER_INFORMATION = "Owner information"
        const val BUILDING = "Building"
        const val REUSABLE = "Reusable"
        const val REUSABLE_MATERIAL = "Reusable materials"
        const val WASTE_CATEGORY = "Waste category"
        const val WASTE_MATERIAL = "Waste material"
        const val WASTE = "Waste"
        const val USAGE = "Usage"
        const val HAZ_MATERIAL = "Hazardous material"
        const val WASTE_SPECIFIER = "Waste specifier"
        const val HAZARDOUS_WASTE = "Hazardous waste"
        const val BUILDING_TYPE = "Building type"
        const val ATTACHMENT = "Attachment"
    }

}

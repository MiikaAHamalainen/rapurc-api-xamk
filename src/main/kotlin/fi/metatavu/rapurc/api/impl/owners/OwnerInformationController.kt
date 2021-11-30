package fi.metatavu.rapurc.api.impl.owners

import fi.metatavu.rapurc.api.model.ContactPerson
import fi.metatavu.rapurc.api.persistence.dao.OwnerInformationDAO
import fi.metatavu.rapurc.api.persistence.dao.SurveyDAO
import fi.metatavu.rapurc.api.persistence.model.OwnerInformation
import fi.metatavu.rapurc.api.persistence.model.Survey
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Manages Owner Information objects
 */
@ApplicationScoped
class OwnerInformationController {

    @Inject
    lateinit var ownerInformationDAO: OwnerInformationDAO

    @Inject
    lateinit var surveyDAO: SurveyDAO

    /**
     * Creates new owner information entity
     *
     * @param survey survey
     * @param ownerName owner name
     * @param businessId business id
     * @param contactPerson info of contact person
     * @param creatorId creator id
     * @return created owner information object
     */
    fun create(
        survey: Survey,
        ownerName: String?,
        businessId: String?,
        contactPerson: ContactPerson?,
        creatorId: UUID
    ): OwnerInformation {
        surveyDAO.update(survey, creatorId)
        return ownerInformationDAO.create(
            id = UUID.randomUUID(),
            survey = survey,
            ownerName = ownerName,
            businessId = businessId,
            firstName = contactPerson?.firstName,
            lastName = contactPerson?.lastName,
            phone = contactPerson?.phone,
            email = contactPerson?.email,
            profession = contactPerson?.profession,
            creatorId = creatorId,
            lastModifierId = creatorId
        )
    }

    /**
    * Lists owner information records based on the survey
    *
    * @param survey survey to filter by
    * @return filtered owner information objects
    */
    fun list(survey: Survey): MutableList<OwnerInformation> {
        return ownerInformationDAO.list(survey)
    }

    /**
     * Finds owner information object
     *
     * @param ownerId owner id
     * @return found owner information or null
     */
    fun find(ownerId: UUID): OwnerInformation? {
        return ownerInformationDAO.findById(ownerId)
    }

    /**
     * Updates owner information with new data
     *
     * @param ownerInformationToUpdate old object
     * @param newOwnerInformation new object
     * @param userId modifier id
     * @return updated OwnerInformation
     */
    fun update(
        ownerInformationToUpdate: OwnerInformation,
        newOwnerInformation: fi.metatavu.rapurc.api.model.OwnerInformation,
        userId: UUID
    ): OwnerInformation {
        surveyDAO.update(ownerInformationToUpdate.survey!!, userId)
        var result = ownerInformationDAO.updateOwnerName(ownerInformationToUpdate, newOwnerInformation.ownerName, userId)
        ownerInformationDAO.updateBusinessId(result, newOwnerInformation.businessId, userId)
        ownerInformationDAO.updateFirstName(result, newOwnerInformation.contactPerson?.firstName, userId)
        ownerInformationDAO.updateLastName(result, newOwnerInformation.contactPerson?.lastName, userId)
        ownerInformationDAO.updatePhone(result, newOwnerInformation.contactPerson?.phone, userId)
        ownerInformationDAO.updateEmail(result, newOwnerInformation.contactPerson?.email, userId)
        result = ownerInformationDAO.updateProfession(result, newOwnerInformation.contactPerson?.profession, userId)
        return result
    }

    /**
     * Deletes owner information
     *
     * @param ownerInformationToDelete owner information to delete
     * @param userId user id
     */
    fun delete(ownerInformationToDelete: OwnerInformation, userId: UUID) {
        surveyDAO.update(ownerInformationToDelete.survey!!, userId)
        ownerInformationDAO.delete(ownerInformationToDelete)
    }

}

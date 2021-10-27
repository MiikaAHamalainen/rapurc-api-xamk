package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.model.ContactPerson
import fi.metatavu.rapurc.api.persistence.model.OwnerInformation
import javax.enterprise.context.ApplicationScoped

/**
 * Translates JPA Owner Information to REST Owner Information
 */
@ApplicationScoped
class OwnerInformationTranslator: AbstractTranslator<OwnerInformation, fi.metatavu.rapurc.api.model.OwnerInformation>() {

    override fun translate(entity: OwnerInformation): fi.metatavu.rapurc.api.model.OwnerInformation {
        val result = fi.metatavu.rapurc.api.model.OwnerInformation()

        result.id = entity.id
        result.businessId = entity.businessId
        result.ownerName = entity.ownerName
        result.surveyId = entity.survey?.id

        val contactPerson = ContactPerson()
        contactPerson.email = entity.email
        contactPerson.phone = entity.phone
        contactPerson.firstName = entity.firstName
        contactPerson.lastName = entity.lastName
        contactPerson.profession = entity.profession
        result.contactPerson = contactPerson

        result.createdAt = entity.createdAt
        result.creatorId = entity.creatorId
        result.modifiedAt = entity.modifiedAt
        result.lastModifierId = entity.lastModifierId
        return result
    }
}

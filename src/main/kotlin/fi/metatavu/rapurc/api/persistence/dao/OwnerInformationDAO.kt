package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.OwnerInformation
import fi.metatavu.rapurc.api.persistence.model.OwnerInformation_
import fi.metatavu.rapurc.api.persistence.model.Survey
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

/**
 * DAO class for OwnerInformation
 */
@ApplicationScoped
class OwnerInformationDAO: AbstractDAO<OwnerInformation>() {

    /**
     * Creates owner information entity
     *
     * @param id id
     * @param survey survey it belongs to
     * @param ownerName owner name
     * @param businessId business id
     * @param firstName first name
     * @param lastName last name
     * @param phone phone number
     * @param email email address
     * @param profession profession
     * @param creatorId creator id
     * @param lastModifierId last modifier id
     * @return saved Owner Information entity
     */
    fun create(
        id: UUID,
        survey: Survey,
        ownerName: String?,
        businessId: String?,
        firstName: String?,
        lastName: String?,
        phone: String?,
        email: String?,
        profession: String?,
        creatorId: UUID,
        lastModifierId: UUID
    ): OwnerInformation {
        val ownerInformation = OwnerInformation()
        ownerInformation.id = id
        ownerInformation.survey = survey
        ownerInformation.ownerName = ownerName
        ownerInformation.businessId = businessId
        ownerInformation.firstName = firstName
        ownerInformation.lastName = lastName
        ownerInformation.phone = phone
        ownerInformation.email = email
        ownerInformation.profession = profession
        ownerInformation.creatorId = creatorId
        ownerInformation.lastModifierId = lastModifierId
        return persist(ownerInformation)
    }

    /**
     * Lists all Owner Information entries based on survey
     *
     * @param survey survey to filter by
     * @return owner information entries
     */
    fun list(survey: Survey): MutableList<OwnerInformation> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria: CriteriaQuery<OwnerInformation> = criteriaBuilder.createQuery(OwnerInformation::class.java)
        val root: Root<OwnerInformation> = criteria.from(OwnerInformation::class.java)

        criteria.select(root)
        criteria.where(criteriaBuilder.equal(root.get(OwnerInformation_.survey), survey))
        val query: TypedQuery<OwnerInformation> = entityManager.createQuery(criteria)

        return query.resultList
    }

    fun updateOwnerName(ownerInformationToUpdate: OwnerInformation, ownerName: String?, userId: UUID): OwnerInformation {
        ownerInformationToUpdate.ownerName = ownerName
        ownerInformationToUpdate.lastModifierId = userId
        return persist(ownerInformationToUpdate)
    }

    fun updateBusinessId(ownerInformation: OwnerInformation, businessId: String?, userId: UUID): OwnerInformation {
        ownerInformation.businessId = businessId
        ownerInformation.lastModifierId = userId
        return persist(ownerInformation)
    }

    fun updateFirstName(ownerInformation: OwnerInformation, firstName: String?, userId: UUID): OwnerInformation {
        ownerInformation.firstName = firstName
        ownerInformation.lastModifierId = userId
        return persist(ownerInformation)
    }

    fun updateLastName(ownerInformation: OwnerInformation, lastName: String?, userId: UUID): OwnerInformation {
        ownerInformation.lastName = lastName
        ownerInformation.lastModifierId = userId
        return persist(ownerInformation)
    }

    fun updatePhone(ownerInformation: OwnerInformation, phone: String?, userId: UUID): OwnerInformation {
        ownerInformation.phone = phone
        ownerInformation.lastModifierId = userId
        return persist(ownerInformation)
    }

    fun updateEmail(ownerInformation: OwnerInformation, email: String?, userId: UUID): OwnerInformation {
        ownerInformation.email = email
        ownerInformation.lastModifierId = userId
        return persist(ownerInformation)
    }

    fun updateProfession(ownerInformation: OwnerInformation, profession: String?, userId: UUID): OwnerInformation {
        ownerInformation.profession = profession
        ownerInformation.lastModifierId = userId
        return persist(ownerInformation)
    }

}

package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.*
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * DAO class for Waste entity
 */
@ApplicationScoped
class WasteDAO: AbstractDAO<Waste>() {

    /**
     * Lists waste objects
     *
     * @param survey survey filter
     * @return wastes
     */
    fun list(survey: Survey): List<Waste> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria: CriteriaQuery<Waste> = criteriaBuilder.createQuery(Waste::class.java)
        val root: Root<Waste> = criteria.from(Waste::class.java)

        criteria.select(root)

        val restrictions = ArrayList<Predicate>()

        restrictions.add(criteriaBuilder.equal(root.get(Waste_.survey), survey))

        criteria.where(*restrictions.toTypedArray())
        val query: TypedQuery<Waste> = entityManager.createQuery(criteria)
        return query.resultList
    }

    /**
     * Creates waste entity
     *
     * @param id id
     * @param usage usage name
     * @param wasteMaterial waste material
     * @param amount waste amount
     * @param survey survey
     * @param description waste description
     * @param creatorId creator id
     * @param modifierId modifier id
     * @return created waste category
     */
    fun create(
        id: UUID,
        usage: WasteUsage,
        wasteMaterial: WasteMaterial,
        amount: Double,
        survey: Survey,
        description: String?,
        creatorId: UUID,
        modifierId: UUID
    ): Waste {
        val waste = Waste()
        waste.id = id
        waste.usage = usage
        waste.wasteMaterial = wasteMaterial
        waste.amount = amount
        waste.survey = survey
        waste.description = description
        waste.creatorId = creatorId
        waste.lastModifierId = modifierId
        return persist(waste)
    }

    /**
     * Updates usage of the waste
     *
     * @param waste waste entity to update
     * @param usage new usage
     * @param userId modifier id
     * @return updated usage
     */
    fun updateUsage(waste: Waste, usage: WasteUsage, userId: UUID): Waste {
        waste.usage = usage
        waste.lastModifierId = userId
        return persist(waste)
    }

    /**
     * Updates wasteMaterial of the waste
     *
     * @param waste waste entity to update
     * @param wasteMaterial new wasteMaterial
     * @param userId modifier id
     * @return updated usage
     */
    fun updateWasteMaterial(waste: Waste, wasteMaterial: WasteMaterial, userId: UUID): Waste {
        waste.wasteMaterial = wasteMaterial
        waste.lastModifierId = userId
        return persist(waste)
    }

    /**
     * Updates amount of the waste
     *
     * @param waste waste entity to update
     * @param amount new amount
     * @param userId modifier id
     * @return updated usage
     */
    fun updateAmount(waste: Waste, amount: Double, userId: UUID): Waste {
        waste.amount = amount
        waste.lastModifierId = userId
        return persist(waste)
    }

    /**
     * Updates description of the waste
     *
     * @param waste waste entity to update
     * @param description new description
     * @param userId modifier id
     * @return updated usage
     */
    fun updateDescription(waste: Waste, description: String?, userId: UUID): Waste {
        waste.description = description
        waste.lastModifierId = userId
        return persist(waste)
    }

}

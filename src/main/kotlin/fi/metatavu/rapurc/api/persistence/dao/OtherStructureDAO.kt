package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.*
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

/**
 * DAO class for Other Structure object
 */
@ApplicationScoped
class OtherStructureDAO: AbstractDAO<OtherStructure>() {

    /**
     * Creates other structure object
     *
     * @param id id
     * @param name name
     * @param description description
     * @param building building
     * @param creatorId creator id
     * @param modifierId creator id
     * @return created other structure
     */
    fun create(
        id: UUID,
        name: String,
        description: String,
        building: Building,
        creatorId: UUID,
        modifierId: UUID
    ): OtherStructure {
        val otherStructure = OtherStructure()
        otherStructure.id = id
        otherStructure.name = name
        otherStructure.description = description
        otherStructure.building = building
        otherStructure.creatorId = creatorId
        otherStructure.lastModifierId = modifierId
        return persist(otherStructure)
    }

    /**
     * Lists all structures that belong to the building
     *
     * @param building building
     * @return filtered structures
     */
    fun listByBuilding(building: Building): MutableList<OtherStructure>? {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria: CriteriaQuery<OtherStructure> = criteriaBuilder.createQuery(OtherStructure::class.java)
        val root: Root<OtherStructure> = criteria.from(OtherStructure::class.java)

        criteria.select(root)
        criteria.where(criteriaBuilder.equal(root.get(OtherStructure_.building), building))
        criteria.orderBy(criteriaBuilder.asc(root.get(OtherStructure_.createdAt)))
        val query: TypedQuery<OtherStructure> = entityManager.createQuery(criteria)

        return query.resultList
    }

}

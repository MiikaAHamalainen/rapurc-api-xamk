package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.model.Unit
import fi.metatavu.rapurc.api.model.Usability
import fi.metatavu.rapurc.api.persistence.model.*
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * DAO class for Reusable entity
 */
@ApplicationScoped
class ReusableDAO: AbstractDAO<Reusable>() {

    /**
     * Creates Reusable entity
     *
     * @param id id
     * @param survey survey
     * @param componentName component name
     * @param materialId reusable material id
     * @param usability usability
     * @param amount amount
     * @param unit unit
     * @param description description
     * @param amountAsWaste amount marked as waste
     * @param creatorId creator id
     * @param lastModifierId modifier id
     * @return created reusable
     */
    fun create(
        id: UUID,
        survey: Survey,
        componentName: String,
        materialId: UUID,
        usability: Usability,
        amount: Double?,
        unit: Unit?,
        description: String?,
        amountAsWaste: Double?,
        creatorId: UUID,
        lastModifierId: UUID
    ): Reusable {
        val reusable = Reusable()
        reusable.id = id
        reusable.survey = survey
        reusable.componentName = componentName
        reusable.materialId = materialId
        reusable.usability = usability
        reusable.amount = amount
        reusable.unit = unit
        reusable.description = description
        reusable.amountAsWaste = amountAsWaste
        reusable.creatorId = creatorId
        reusable.lastModifierId = lastModifierId
        return persist(reusable)
    }

    /**
     * Lists Reusables by survey
     *
     * @param survey filter survey
     * @param material filter material
     * @return reusables
     */
    fun list(survey: Survey?, material: ReusableMaterial?): MutableList<Reusable> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria: CriteriaQuery<Reusable> = criteriaBuilder.createQuery(Reusable::class.java)
        val root: Root<Reusable> = criteria.from(Reusable::class.java)

        criteria.select(root)

        val restrictions = ArrayList<Predicate>()

        if (survey != null) {
            restrictions.add(criteriaBuilder.equal(root.get(Reusable_.survey), survey))
        }

        if (material != null) {
            restrictions.add(criteriaBuilder.equal(root.get(Reusable_.materialId), material.id))
        }

        criteria.where(*restrictions.toTypedArray())
        criteria.orderBy(criteriaBuilder.asc(root.get(Reusable_.createdAt)))
        val query: TypedQuery<Reusable> = entityManager.createQuery(criteria)
        return query.resultList
    }

    /**
     * Updates reusable component name
     *
     * @param reusable reusable to update
     * @param componentName component name
     * @param modifierId modifier id
     * @return updated reusable
     */
    fun updateComponentName(reusable: Reusable, componentName: String, modifierId: UUID): Reusable {
        reusable.componentName = componentName
        reusable.lastModifierId = modifierId
        return persist(reusable)
    }

    /**
     * Updates reusable materialId
     *
     * @param reusable reusable to update
     * @param materialId materialId
     * @param modifierId modifier id
     * @return updated reusable
     */
    fun updateMaterialId(reusable: Reusable, materialId: UUID, modifierId: UUID): Reusable {
        reusable.materialId = materialId
        reusable.lastModifierId = modifierId
        return persist(reusable)
    }

    /**
     * Updates reusable usability
     *
     * @param reusable reusable to update
     * @param usability usability
     * @param modifierId modifier id
     * @return updated reusable
     */
    fun updateUsability(reusable: Reusable, usability: Usability, modifierId: UUID): Reusable {
        reusable.usability = usability
        reusable.lastModifierId = modifierId
        return persist(reusable)
    }

    /**
     * Updates reusable amount
     *
     * @param reusable reusable to update
     * @param amount amount
     * @param modifierId modifier id
     * @return updated reusable
     */
    fun updateAmount(reusable: Reusable, amount: Double?, modifierId: UUID): Reusable {
        reusable.amount = amount
        reusable.lastModifierId = modifierId
        return persist(reusable)
    }

    /**
     * Updates reusable unit
     *
     * @param reusable reusable to update
     * @param unit unit
     * @param modifierId modifier id
     * @return updated reusable
     */
    fun updateUnit(reusable: Reusable, unit: Unit?, modifierId: UUID): Reusable {
        reusable.unit = unit
        reusable.lastModifierId = modifierId
        return persist(reusable)
    }

    /**
     * Updates reusable description
     *
     * @param reusable reusable to update
     * @param description description
     * @param modifierId modifier id
     * @return updated reusable
     */
    fun updateDescription(reusable: Reusable, description: String?, modifierId: UUID): Reusable {
        reusable.description = description
        reusable.lastModifierId = modifierId
        return persist(reusable)
    }

    /**
     * Updates reusable amountAsWaste
     *
     * @param reusable reusable to update
     * @param amountAsWaste amount As Waste
     * @param modifierId modifier id
     * @return updated reusable
     */
    fun updateAmountAsWaste(reusable: Reusable, amountAsWaste: Double?, modifierId: UUID): Reusable {
        reusable.amountAsWaste = amountAsWaste
        reusable.lastModifierId = modifierId
        return persist(reusable)
    }
}
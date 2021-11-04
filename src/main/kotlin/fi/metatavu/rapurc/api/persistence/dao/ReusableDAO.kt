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
     * @param images images uris
     * @param creatorId creator id
     * @param lastModifierId modifier id
     * @return created reusable
     */
    fun create(
        id: UUID,
        survey: Survey,
        componentName: String,
        materialId: UUID,
        usability: Usability?,
        amount: Double?,
        unit: Unit?,
        description: String?,
        images: List<String>?,
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
        reusable.images = images
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
        val query: TypedQuery<Reusable> = entityManager.createQuery(criteria)
        return query.resultList
    }

    fun updateComponentName(reusableToUpdate: Reusable, componentName: String, userId: UUID): Reusable {
        reusableToUpdate.componentName = componentName
        reusableToUpdate.lastModifierId = userId
        return persist(reusableToUpdate)
    }

    fun updateMaterialId(reusable: Reusable, materialId: UUID?, userId: UUID): Reusable {
        reusable.materialId = materialId
        reusable.lastModifierId = userId
        return persist(reusable)
    }

    fun updateUsability(reusable: Reusable, usability: Usability?, userId: UUID): Reusable {
        reusable.usability = usability
        reusable.lastModifierId = userId
        return persist(reusable)
    }

    fun updateAmount(reusable: Reusable, amount: Double?, userId: UUID): Reusable {
        reusable.amount = amount
        reusable.lastModifierId = userId
        return persist(reusable)
    }

    fun updateUnit(reusable: Reusable, unit: Unit?, userId: UUID): Reusable {
        reusable.unit = unit
        reusable.lastModifierId = userId
        return persist(reusable)
    }

    fun updateDescription(reusable: Reusable, description: String?, userId: UUID): Reusable {
        reusable.description = description
        reusable.lastModifierId = userId
        return persist(reusable)
    }

    fun updateImages(reusable: Reusable, images: List<String>?, userId: UUID): Reusable {
        reusable.images = images
        reusable.lastModifierId = userId
        return persist(reusable)
    }
}
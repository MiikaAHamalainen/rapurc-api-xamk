package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.*
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * DAO class for hazardous materials
 */
@ApplicationScoped
class HazardousMaterialDAO: AbstractDAO<HazardousMaterial>(){

    /**
     * Creates waste material
     *
     * @param id id
     * @param wasteCategory waste category
     * @param ewcSpecificationCode EWC category id
     * @param creatorId creator id
     * @param modifierId modifier id
     * @return created waste material
     */
    fun create(
        id: UUID,
        wasteCategory: WasteCategory,
        ewcSpecificationCode: String,
        creatorId: UUID,
        modifierId: UUID
    ): HazardousMaterial {
        val wasteMaterial = HazardousMaterial()
        wasteMaterial.id = id
        wasteMaterial.wasteCategory = wasteCategory
        wasteMaterial.ewcSpecificationCode = ewcSpecificationCode
        wasteMaterial.creatorId = creatorId
        wasteMaterial.lastModifierId = modifierId
        return persist(wasteMaterial)
    }

    /**
     * Lists materials with given filters
     *
     * @param wasteCategory filter by waste category
     * @return list of waste materials
     */
    fun list(wasteCategory: WasteCategory?): List<HazardousMaterial> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria: CriteriaQuery<HazardousMaterial> = criteriaBuilder.createQuery(HazardousMaterial::class.java)
        val root: Root<HazardousMaterial> = criteria.from(HazardousMaterial::class.java)

        criteria.select(root)
        val restrictions = mutableListOf<Predicate>()

        if (wasteCategory != null) {
            restrictions.add(criteriaBuilder.equal(root.get(HazardousMaterial_.wasteCategory), wasteCategory))
        }

        criteria.where(criteriaBuilder.and(*restrictions.toTypedArray()))
        criteria.orderBy(criteriaBuilder.asc(root.get(HazardousMaterial_.createdAt)))
        val query: TypedQuery<HazardousMaterial> = entityManager.createQuery(criteria)
        return query.resultList
    }

    /**
     * Updates waste category
     *
     * @param hazardousMaterial hazardous material to update
     * @param newWasteCategory new waste category
     * @param modifierId modifier id
     * @return updated hazardous material
     */
    fun updateWasteCategory(hazardousMaterial: HazardousMaterial, newWasteCategory: WasteCategory, modifierId: UUID): HazardousMaterial {
        hazardousMaterial.wasteCategory = newWasteCategory
        hazardousMaterial.lastModifierId = modifierId
        return persist(hazardousMaterial)
    }

    /**
     * Updates waste category
     *
     * @param hazardousMaterial hazardous material to update
     * @param ewcSpecificationCode new ewcSpecificationCode
     * @param modifierId modifier id
     * @return updated hazardous material
     */
    fun updateEwcSpecificationCode(hazardousMaterial: HazardousMaterial, ewcSpecificationCode: String, modifierId: UUID): HazardousMaterial {
        hazardousMaterial.ewcSpecificationCode = ewcSpecificationCode
        hazardousMaterial.lastModifierId = modifierId
        return persist(hazardousMaterial)
    }

}
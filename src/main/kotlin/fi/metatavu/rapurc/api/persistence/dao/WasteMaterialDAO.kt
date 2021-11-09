package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.*
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * DAO class for waste materials
 */
@ApplicationScoped
class WasteMaterialDAO: AbstractDAO<WasteMaterial>(){

    /**
     * Creates waste material
     *
     * @param id id
     * @param name material name
     * @param wasteCategory waste category
     * @param ewcSpecificationCode EWC category id
     * @param creatorId creator id
     * @param modifierId modifier id
     * @return created waste material
     */
    fun create(
        id: UUID,
        name: String,
        wasteCategory: WasteCategory,
        ewcSpecificationCode: String,
        creatorId: UUID,
        modifierId: UUID
    ): WasteMaterial {
        val wasteMaterial = WasteMaterial()
        wasteMaterial.id = id
        wasteMaterial.name = name
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
    fun list(wasteCategory: WasteCategory?): List<WasteMaterial> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria: CriteriaQuery<WasteMaterial> = criteriaBuilder.createQuery(WasteMaterial::class.java)
        val root: Root<WasteMaterial> = criteria.from(WasteMaterial::class.java)

        criteria.select(root)
        val restrictions = mutableListOf<Predicate>()

        if (wasteCategory != null) {
            restrictions.add(criteriaBuilder.equal(root.get(WasteMaterial_.wasteCategory), wasteCategory))
        }

        criteria.where(criteriaBuilder.and(*restrictions.toTypedArray()))
        val query: TypedQuery<WasteMaterial> = entityManager.createQuery(criteria)
        return query.resultList
    }

    fun updateWasteCategory(wasteMaterial: WasteMaterial, newWasteCategory: WasteCategory, userId: UUID): WasteMaterial {
        wasteMaterial.wasteCategory = newWasteCategory
        wasteMaterial.lastModifierId = userId
        return persist(wasteMaterial)
    }

    fun updateName(wasteMaterial: WasteMaterial, name: String, userId: UUID): WasteMaterial {
        wasteMaterial.name = name
        wasteMaterial.lastModifierId = userId
        return persist(wasteMaterial)
    }

    fun updateEwcSpecificationCode(wasteMaterial: WasteMaterial, ewcSpecificationCode: String?, userId: UUID): WasteMaterial {
        wasteMaterial.ewcSpecificationCode = ewcSpecificationCode
        wasteMaterial.lastModifierId = userId
        return persist(wasteMaterial)
    }

}
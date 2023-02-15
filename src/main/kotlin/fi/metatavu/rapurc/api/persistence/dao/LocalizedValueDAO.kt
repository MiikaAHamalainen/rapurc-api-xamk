package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.*
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

/**
 * DAO class for Localized values
 */
@ApplicationScoped
class LocalizedValueDAO: AbstractDAO<LocalizedValue>() {

    /**
     * Creates localized value
     * @param id id
     * @param language language code
     * @param value localized string
     * @param wasteSpecifier optional waste specifier link
     */
    fun create(
        id: UUID,
        language: String,
        value: String,
        wasteSpecifier: WasteSpecifier?
    ): LocalizedValue {
        val localizedValue = LocalizedValue()
        localizedValue.id = id
        localizedValue.language = language
        localizedValue.value = value
        localizedValue.wasteSpecifier = wasteSpecifier
        return persist(localizedValue)
    }

    /**
     * Lists localized values by waste specifier
     *
     * @param wasteSpecifier waste specifier
     * @return localized values
     */
    fun listByWasteSpecifier(wasteSpecifier: WasteSpecifier): List<LocalizedValue> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria: CriteriaQuery<LocalizedValue> = criteriaBuilder.createQuery(LocalizedValue::class.java)
        val root: Root<LocalizedValue> = criteria.from(LocalizedValue::class.java)

        criteria.select(root)
        criteria.where(criteriaBuilder.equal(root.get(LocalizedValue_.wasteSpecifier), wasteSpecifier))
        val query: TypedQuery<LocalizedValue> = entityManager.createQuery(criteria)

        return query.resultList
    }

}

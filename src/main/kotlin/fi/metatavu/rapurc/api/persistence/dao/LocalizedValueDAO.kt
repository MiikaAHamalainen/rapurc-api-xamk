package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.*
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * DAO class for Localized values
 */
@ApplicationScoped
class LocalizedValueDAO: AbstractDAO<LocalizedValue>() {

    /**
     * Creates localized value
     *
     * @param id id
     * @param language language code
     * @param value localized string
     * @param wasteSpecifier optional waste specifier link
     * @param wasteCategory optional wasteCategory link
     * @param usage optional usage link
     * @param reusableMaterial optional reusableMaterial link
     * @param hazardousMaterial optional hazardousMaterial link
     * @param buildingType optional buildingType link
     * @param wasteMaterial optional wasteMaterial link
     * @return created localized value
     */
    fun create(
        id: UUID,
        language: String,
        value: String,
        wasteSpecifier: WasteSpecifier? = null,
        wasteCategory: WasteCategory? = null,
        usage: WasteUsage? = null,
        reusableMaterial: ReusableMaterial? = null,
        hazardousMaterial: HazardousMaterial? = null,
        buildingType: BuildingType? = null,
        wasteMaterial: WasteMaterial? = null
    ): LocalizedValue {
        val localizedValue = LocalizedValue()
        localizedValue.id = id
        localizedValue.language = language
        localizedValue.value = value
        localizedValue.wasteSpecifier = wasteSpecifier
        localizedValue.wasteCategory = wasteCategory
        localizedValue.usage = usage
        localizedValue.reusableMaterial = reusableMaterial
        localizedValue.hazardousMaterial = hazardousMaterial
        localizedValue.buildingType = buildingType
        localizedValue.wasteMaterial = wasteMaterial
        return persist(localizedValue)
    }

    /**
     * Lists localized values
     *
     * @param wasteSpecifier optional waste specifier link
     * @param wasteCategory optional wasteCategory link
     * @param usage optional usage link
     * @param reusableMaterial optional reusableMaterial link
     * @param hazardousMaterial optional hazardousMaterial link
     * @param buildingType optional buildingType link
     * @param wasteMaterial optional wasteMaterial link
     * @return localized values
     */
    fun listBy(
        wasteSpecifier: WasteSpecifier? = null,
        wasteCategory: WasteCategory? = null,
        usage: WasteUsage? = null,
        reusableMaterial: ReusableMaterial? = null,
        hazardousMaterial: HazardousMaterial? = null,
        buildingType: BuildingType? = null,
        wasteMaterial: WasteMaterial? = null
    ): List<LocalizedValue> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria: CriteriaQuery<LocalizedValue> = criteriaBuilder.createQuery(LocalizedValue::class.java)
        val root: Root<LocalizedValue> = criteria.from(LocalizedValue::class.java)

        criteria.select(root)
        val restrictions = ArrayList<Predicate>()

        if (wasteSpecifier != null) {
            restrictions.add(criteriaBuilder.equal(root.get(LocalizedValue_.wasteSpecifier), wasteSpecifier))
        }

        if (wasteCategory != null) {
            restrictions.add(criteriaBuilder.equal(root.get(LocalizedValue_.wasteCategory), wasteCategory))
        }

        if (usage != null) {
            restrictions.add(criteriaBuilder.equal(root.get(LocalizedValue_.usage), usage))
        }

        if (reusableMaterial != null) {
            restrictions.add(criteriaBuilder.equal(root.get(LocalizedValue_.reusableMaterial), reusableMaterial))
        }

        if (hazardousMaterial != null) {
            restrictions.add(criteriaBuilder.equal(root.get(LocalizedValue_.hazardousMaterial), hazardousMaterial))
        }

        if (buildingType != null) {
            restrictions.add(criteriaBuilder.equal(root.get(LocalizedValue_.buildingType), buildingType))
        }

        if (wasteMaterial != null) {
            restrictions.add(criteriaBuilder.equal(root.get(LocalizedValue_.wasteMaterial), wasteMaterial))
        }

        criteria.select(root)
        criteria.where(*restrictions.toTypedArray())
        criteria.orderBy(criteriaBuilder.desc(root.get(LocalizedValue_.language)))
        val query: TypedQuery<LocalizedValue> = entityManager.createQuery(criteria)

        return query.resultList
    }
}

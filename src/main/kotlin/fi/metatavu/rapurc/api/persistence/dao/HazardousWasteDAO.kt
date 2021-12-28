package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.*
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * DAO class for Hazardous Waste entity
 */
@ApplicationScoped
class HazardousWasteDAO: AbstractDAO<HazardousWaste>() {

    /**
     * Lists Hazardous waste objects
     *
     * @param survey survey filter
     * @param wasteSpecifier waste specifier filter
     * @param hazardousMaterial hazardous material filter
     * @return Hazardous wastes
     */
    fun list(survey: Survey?, wasteSpecifier: WasteSpecifier?, hazardousMaterial: HazardousMaterial?): List<HazardousWaste> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria: CriteriaQuery<HazardousWaste> = criteriaBuilder.createQuery(HazardousWaste::class.java)
        val root: Root<HazardousWaste> = criteria.from(HazardousWaste::class.java)

        criteria.select(root)

        val restrictions = ArrayList<Predicate>()

        if (survey != null) {
            restrictions.add(criteriaBuilder.equal(root.get(HazardousWaste_.survey), survey))
        }

        if (wasteSpecifier != null) {
            restrictions.add(criteriaBuilder.equal(root.get(HazardousWaste_.wasteSpecifier), wasteSpecifier))
        }

        if (hazardousMaterial != null) {
            restrictions.add(criteriaBuilder.equal(root.get(HazardousWaste_.hazardousMaterial), hazardousMaterial))
        }

        criteria.where(*restrictions.toTypedArray())
        criteria.orderBy(criteriaBuilder.asc(root.get(HazardousWaste_.createdAt)))
        val query: TypedQuery<HazardousWaste> = entityManager.createQuery(criteria)
        return query.resultList
    }

    /**
     * Creates new hazardous waste object
     *
     * @param id id
     * @param survey survey
     * @param hazardousMaterial hazardous Material
     * @param wasteSpecifier waste Specifier
     * @param amount amount
     * @param description description
     * @param creatorId user id
     * @param modifierId user id
     * @return created hazardous waste
     */
    fun create(
        id: UUID,
        survey: Survey,
        hazardousMaterial: HazardousMaterial,
        wasteSpecifier: WasteSpecifier?,
        amount: Double,
        description: String?,
        creatorId: UUID,
        modifierId: UUID
    ): HazardousWaste {
        val waste = HazardousWaste()
        waste.id = id
        waste.survey = survey
        waste.hazardousMaterial = hazardousMaterial
        waste.wasteSpecifier = wasteSpecifier
        waste.amount = amount
        waste.description = description
        waste.creatorId = creatorId
        waste.lastModifierId = modifierId
        return persist(waste)
    }

    /**
     * Updates material of hazardous waste
     *
     * @param hazardousWaste hazardous waste to update
     * @param hazardousMaterial new hazardous material
     * @param modifierId modifier id
     * @return updated hazardous waste
     */
    fun updateHazardousMaterial(hazardousWaste: HazardousWaste, hazardousMaterial: HazardousMaterial, modifierId: UUID): HazardousWaste {
        hazardousWaste.hazardousMaterial = hazardousMaterial
        hazardousWaste.lastModifierId = modifierId
        return persist(hazardousWaste)
    }

    /**
     * Updates wasteSpecifier of hazardous waste
     *
     * @param hazardousWaste hazardous waste to update
     * @param wasteSpecifier new wasteSpecifier
     * @param modifierId modifier id
     * @return updated hazardous waste
     */
    fun updateWasteSpecifier(hazardousWaste: HazardousWaste, wasteSpecifier: WasteSpecifier, modifierId: UUID): HazardousWaste {
        hazardousWaste.wasteSpecifier = wasteSpecifier
        hazardousWaste.lastModifierId = modifierId
        return persist(hazardousWaste)
    }

    /**
     * Updates amount of hazardous waste
     *
     * @param hazardousWaste hazardous waste to update
     * @param amount new amount
     * @param modifierId modifier id
     * @return updated hazardous waste
     */
    fun updateAmount(hazardousWaste: HazardousWaste, amount: Double, modifierId: UUID): HazardousWaste {
        hazardousWaste.amount = amount
        hazardousWaste.lastModifierId = modifierId
        return persist(hazardousWaste)
    }

    /**
     * Updates description of hazardous waste
     *
     * @param hazardousWaste hazardous waste to update
     * @param description new description
     * @param modifierId modifier id
     * @return updated hazardous waste
     */
    fun updateDescription(hazardousWaste: HazardousWaste, description: String?, modifierId: UUID): HazardousWaste {
        hazardousWaste.description = description
        hazardousWaste.lastModifierId = modifierId
        return persist(hazardousWaste)
    }
}

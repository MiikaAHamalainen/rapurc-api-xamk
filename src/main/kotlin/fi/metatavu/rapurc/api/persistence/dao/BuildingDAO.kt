package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.Building
import fi.metatavu.rapurc.api.persistence.model.Survey
import fi.metatavu.rapurc.api.persistence.model.Survey_
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * DAO class for Building JPA object
 */
@ApplicationScoped
class BuildingDAO: AbstractDAO<Building>() {

    /**
     * Creates a building
     *
     * @param id id
     * @param survey survey
     * @param propertyId property id
     * @param buildingId building id
     * @param classificationCode classification code
     * @param constructionYear construction year
     * @param space space of building
     * @param volume volume of building
     * @param floors number of floors
     * @param basements number of basements
     * @param foundation foundation
     * @param supportStructure support structure
     * @param facadeMaterial facade material
     * @param roofType roof type
     * @param streetAddress street address
     * @param city city
     * @param postCode post code
     * @return created building
     */
    fun create(
        id: UUID?,
        survey: Survey?,
        propertyId: String?,
        buildingId: String?,
        classificationCode: String?,
        constructionYear: Int?,
        space: Int?,
        volume: Int?,
        floors: Int?,
        basements: Int?,
        foundation: String?,
        supportStructure: String?,
        facadeMaterial: String?,
        roofType: String?,
        streetAddress: String?,
        city: String?,
        postCode: String?
    ) : Building {
        val building = Building()
        building.id = id
        building.survey = survey
        building.propertyId = propertyId
        building.buildingId = buildingId
        building.classificationCode = classificationCode
        building.constructionYear = constructionYear
        building.space = space
        building.volume = volume
        building.floors = floors
        building.basements = basements
        building.foundation = foundation
        building.supportStructure = supportStructure
        building.facadeMaterial = facadeMaterial
        building.roofType = roofType
        building.streetAddress = streetAddress
        building.city = city
        building.postCode = postCode
        return persist(building)
    }

    /**
     * Lists buildings added in survey
     *
     * @param survey survey
     * @return filtered buildings list
     */
    fun list(survey: Survey): List<Building> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria: CriteriaQuery<Building> = criteriaBuilder.createQuery(Building::class.java)
        val root: Root<Building> = criteria.from(Building::class.java)

        val restrictions = ArrayList<Predicate>()


        criteriaBuilder.equal(root.get(Building_.survey), survey)

        criteria.select(root)
        criteria.where(*restrictions.toTypedArray())
        val query: TypedQuery<Building> = entityManager.createQuery(criteria)
        return query.resultList
    }
}
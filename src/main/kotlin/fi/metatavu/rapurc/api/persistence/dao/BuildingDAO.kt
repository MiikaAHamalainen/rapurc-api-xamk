package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.Building
import fi.metatavu.rapurc.api.persistence.model.Building_
import fi.metatavu.rapurc.api.persistence.model.OtherStructure
import fi.metatavu.rapurc.api.persistence.model.Survey
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
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
     * @param otherStructures other building structures
     * @param creatorId creator id
     * @param lastModifierId last modifier id
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
        postCode: String?,
        otherStructures: List<OtherStructure>?,
        creatorId: UUID,
        lastModifierId: UUID
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
        building.otherStructures = otherStructures
        building.creatorId = creatorId
        building.lastModifierId = lastModifierId
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

        criteria.select(root)
        criteria.where(criteriaBuilder.equal(root.get(Building_.survey), survey))
        val query: TypedQuery<Building> = entityManager.createQuery(criteria)
        return query.resultList
    }

    fun updateSurvey(building: Building, newSurvey: Survey, userId: UUID): Building {
        building.survey = newSurvey
        building.lastModifierId = userId
        return persist(building)
    }

    fun updatePropertyId(building: Building, propertyId: String?, userId: UUID): Building {
        building.propertyId = propertyId
        building.lastModifierId = userId
        return persist(building)
    }

    fun updateBuildingId(building: Building, buildingId: String?, userId: UUID): Building {
        building.buildingId = buildingId
        building.lastModifierId = userId
        return persist(building)
    }

    fun updateClassificationCode(building: Building, classificationCode: String?, userId: UUID): Building {
        building.classificationCode = classificationCode
        building.lastModifierId = userId
        return persist(building)
    }

    fun updateConstructionYear(building: Building, constructionYear: Int?, userId: UUID): Building {
        building.constructionYear = constructionYear
        building.lastModifierId = userId
        return persist(building)
    }

    fun updateSpace(building: Building, space: Int?, userId: UUID): Building {
        building.space = space
        building.lastModifierId = userId
        return persist(building)
    }

    fun updateVolume(building: Building, volume: Int?, userId: UUID): Building {
        building.volume = volume
        building.lastModifierId = userId
        return persist(building)
    }

    fun updateFloors(building: Building, floors: Int, userId: UUID): Building {
        building.floors = floors
        building.lastModifierId = userId
        return persist(building)
    }

    fun updateBasements(building: Building, basements: Int?, userId: UUID): Building {
        building.basements = basements
        building.lastModifierId = userId
        return persist(building)
    }

    fun updateFoundation(building: Building, foundation: String?, userId: UUID): Building {
        building.foundation = foundation
        building.lastModifierId = userId
        return persist(building)
    }

    fun updateSupportStructure(building: Building, supportingStructure: String?, userId: UUID): Building {
        building.supportStructure = supportingStructure
        building.lastModifierId = userId
        return persist(building)
    }

    fun updateFacadeMateria(building: Building, facadeMaterial: String?, userId: UUID): Building {
        building.facadeMaterial = facadeMaterial
        building.lastModifierId = userId
        return persist(building)
    }

    fun updateRoofType(building: Building, roofType: String?, userId: UUID): Building {
        building.roofType = roofType
        building.lastModifierId = userId
        return persist(building)
    }

    fun updateStreetAddress(building: Building, streetAddress: String?, userId: UUID): Building {
        building.streetAddress = streetAddress
        building.lastModifierId = userId
        return persist(building)
    }

    fun updateCity(building: Building, city: String?, userId: UUID): Building {
        building.city = city
        building.lastModifierId = userId
        return persist(building)
    }

    fun updatePostCode(building: Building, postCode: String?, userId: UUID): Building {
        building.postCode = postCode
        building.lastModifierId = userId
        return persist(building)
    }

    fun updateOtherBuildings(building: Building, otherStructures: List<OtherStructure>?, userId: UUID): Building {
        building.otherStructures = otherStructures
        building.lastModifierId = userId
        return persist(building)
    }

}
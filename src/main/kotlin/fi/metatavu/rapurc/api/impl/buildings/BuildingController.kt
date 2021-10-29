package fi.metatavu.rapurc.api.impl.buildings

import fi.metatavu.rapurc.api.persistence.dao.BuildingDAO
import fi.metatavu.rapurc.api.persistence.dao.OtherStructureDAO
import fi.metatavu.rapurc.api.persistence.model.Building
import fi.metatavu.rapurc.api.persistence.model.Survey
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for Buildings and included Other Structures
 */
@ApplicationScoped
class BuildingController {

    @Inject
    private lateinit var buildingDAO: BuildingDAO

    @Inject
    private lateinit var otherStuctureDao: OtherStructureDAO

    /**
     * Lists buildings based on survey
     *
     * @param survey survey
     * @return filtered building list
     */
    fun list(survey: Survey): List<Building> {
        return buildingDAO.list(survey)
    }

    /**
     * Creates building
     *
     * @param survey survey which the building belongs to
     * @param building building data
     * @param creatorId creator id
     * @return created Building
     */
    fun create(survey: Survey, building: fi.metatavu.rapurc.api.model.Building, creatorId: UUID): Building {
        val createdBuilding = buildingDAO.create(
            id = UUID.randomUUID(),
            survey = survey,
            propertyId = building.propertyId,
            buildingId = building.buildingId,
            classificationCode = building.classificationCode,
            constructionYear = building.constructionYear,
            space = building.space,
            volume = building.volume,
            floors = building.floors,
            basements = building.basements,
            foundation = building.foundation,
            supportStructure = building.supportingStructure,
            facadeMaterial = building.facadeMaterial,
            roofType = building.roofType,
            streetAddress = building.address.streetAddress,
            city = building.address.city,
            postCode = building.address.postCode,
            creatorId = creatorId,
            lastModifierId = creatorId
        )

        building.otherStructures?.forEach { otherStructure ->
            otherStuctureDao.create(
                id = UUID.randomUUID(),
                name = otherStructure.name,
                description = otherStructure.description,
                building = createdBuilding
            )
        }

        return createdBuilding
    }

    /**
     * Finds building in the db
     *
     * @param buildingId building id
     * @return found building or null
     */
    fun find(buildingId: UUID): Building? {
        return buildingDAO.findById(buildingId)
    }

    /**
     * Updates building
     *
     * @param buildingToUpdate original building to update
     * @param building new building data
     * @param userId modifier id
     * @return update Building
     */
    fun update(buildingToUpdate: Building, building: fi.metatavu.rapurc.api.model.Building, userId: UUID): Building {
        val result = buildingDAO.updatePropertyId(buildingToUpdate, building.propertyId, userId)
        buildingDAO.updateBuildingId(result, building.buildingId, userId)
        buildingDAO.updateClassificationCode(result, building.classificationCode, userId)
        buildingDAO.updateConstructionYear(result, building.constructionYear, userId)
        buildingDAO.updateSpace(result, building.space, userId)
        buildingDAO.updateVolume(result, building.volume, userId)
        buildingDAO.updateFloors(result, building.floors, userId)
        buildingDAO.updateBasements(result, building.basements, userId)
        buildingDAO.updateFoundation(result, building.foundation, userId)
        buildingDAO.updateSupportStructure(result, building.supportingStructure, userId)
        buildingDAO.updateFacadeMateria(result, building.facadeMaterial, userId)
        buildingDAO.updateRoofType(result, building.roofType, userId)
        buildingDAO.updateStreetAddress(result, building.address.streetAddress, userId)
        buildingDAO.updateCity(result, building.address.city, userId)
        buildingDAO.updatePostCode(result, building.address.postCode, userId)

        otherStuctureDao.listByBuilding(buildingToUpdate)?.forEach(otherStuctureDao::delete)
        building.otherStructures?.forEach { otherStructure ->
            otherStuctureDao.create(
                id = UUID.randomUUID(),
                name = otherStructure.name,
                description = otherStructure.description,
                building = result
            )
        }
        return result
    }

    /**
     * Deletes building
     *
     * @param buildingToDelete building to delete
     */
    fun delete(buildingToDelete: Building) {
        otherStuctureDao.listByBuilding(buildingToDelete)?.forEach(otherStuctureDao::delete)
        buildingDAO.delete(buildingToDelete)
    }

}

package fi.metatavu.rapurc.api.impl.buildings

import fi.metatavu.rapurc.api.persistence.dao.BuildingTypeDAO
import fi.metatavu.rapurc.api.persistence.model.BuildingType
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for Building Type objects
 */
@ApplicationScoped
class BuildingTypeController {

    @Inject
    lateinit var buildingTypeDAO: BuildingTypeDAO

    /**
     * Lists all building types
     * @return building types
     */
    fun list(): List<BuildingType> {
        return buildingTypeDAO.listAll()
    }

    /**
     * Creates building type
     *
     * @param name name
     * @param code code
     * @param userId user id
     * @return created building type
     */
    fun create(name: String, code: String, userId: UUID): BuildingType {
        return buildingTypeDAO.create(
            id = UUID.randomUUID(),
            code = code,
            name = name,
            creatorId = userId,
            lastModifierId = userId
        )
    }

    /**
     * Finds building type
     *
     * @param buildingTypeId id
     * @return found building type or null
     */
    fun find(buildingTypeId: UUID): BuildingType? {
        return buildingTypeDAO.findById(buildingTypeId)
    }

    /**
     * Updates building type with new data
     *
     * @param oldBuildingType old object
     * @param newBuildingType new object
     * @param modifierId modifier id
     * @return updated building type
     */
    fun update(oldBuildingType: BuildingType, newBuildingType: fi.metatavu.rapurc.api.model.BuildingType, modifierId: UUID): BuildingType {
        val result = buildingTypeDAO.updateName(oldBuildingType, newBuildingType.name, modifierId)
        buildingTypeDAO.updateCode(result, newBuildingType.code, modifierId)
        return result
    }

    /**
     * Deletes building type
     *
     * @param buildingType building type to delete
     */
    fun delete(buildingType: BuildingType) {
        buildingTypeDAO.delete(buildingType)
    }

}
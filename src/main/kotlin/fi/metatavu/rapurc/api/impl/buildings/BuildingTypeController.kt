package fi.metatavu.rapurc.api.impl.buildings

import fi.metatavu.rapurc.api.persistence.dao.BuildingTypeDAO
import fi.metatavu.rapurc.api.persistence.dao.LocalizedValueDAO
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

    @Inject
    lateinit var localizedValueDAO: LocalizedValueDAO

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
     * @param buildingType rest object
     * @param userId user id
     * @return created building type
     */
    fun create(buildingType: fi.metatavu.rapurc.api.model.BuildingType, userId: UUID): BuildingType {
        val createdBuildingType = buildingTypeDAO.create(
            id = UUID.randomUUID(),
            code = buildingType.code,
            creatorId = userId,
            lastModifierId = userId
        )

        buildingType.localizedNames.forEach {
            localizedValueDAO.create(
                id = UUID.randomUUID(),
                value = it.value,
                language = it.language,
                buildingType = createdBuildingType
            )
        }

        return createdBuildingType
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
        val result = buildingTypeDAO.updateCode(oldBuildingType, newBuildingType.code, modifierId)

        localizedValueDAO.listBy(buildingType = result)
            .forEach { localizedValueDAO.delete(it) }

        newBuildingType.localizedNames.forEach {
            localizedValueDAO.create(
                id = UUID.randomUUID(),
                value = it.value,
                language = it.language,
                buildingType = result
            )
        }

        return result
    }

    /**
     * Deletes building type
     *
     * @param buildingType building type to delete
     */
    fun delete(buildingType: BuildingType) {
        localizedValueDAO.listBy(buildingType = buildingType)
            .forEach { localizedValueDAO.delete(it) }
        buildingTypeDAO.delete(buildingType)
    }

}
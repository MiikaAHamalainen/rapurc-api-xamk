package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.BuildingType
import java.util.*
import javax.enterprise.context.ApplicationScoped

/**
 * DAO class for building type entity
 */
@ApplicationScoped
class BuildingTypeDAO: AbstractDAO<BuildingType>() {

    /**
     * Creates building type
     *
     * @param id id
     * @param code code
     * @param name name
     * @param creatorId creator id
     * @param lastModifierId modifier id
     * @return created image
     */
    fun create(
        id: UUID,
        code: String,
        name: String,
        creatorId: UUID,
        lastModifierId: UUID
    ): BuildingType {
        val buildingType = BuildingType()
        buildingType.id = id
        buildingType.code = code
        buildingType.name = name
        buildingType.creatorId = creatorId
        buildingType.lastModifierId = lastModifierId
        return persist(buildingType)
    }

    /**
     * Updates name of building type
     *
     * @param buildingType building type to update
     * @param name new name
     * @param lastModifierId modifier id
     * @return updated building type
     */
    fun updateName(buildingType: BuildingType, name: String, lastModifierId: UUID): BuildingType {
        buildingType.name = name
        buildingType.lastModifierId = lastModifierId
        return buildingType
    }

    /**
     * Updates code of building type
     *
     * @param buildingType building type to update
     * @param code new code
     * @param lastModifierId modifier id
     * @return updated building type
     */
    fun updateCode(buildingType: BuildingType, code: String, lastModifierId: UUID): BuildingType {
        buildingType.code = code
        buildingType.lastModifierId = lastModifierId
        return buildingType
    }
}
package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.model.BuildingType
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translates building type jpa object to rest
 */
@ApplicationScoped
class BuildingTypeTranslator: AbstractTranslator<fi.metatavu.rapurc.api.persistence.model.BuildingType, BuildingType>() {

    @Inject
    lateinit var metadataTranslator: MetadataTranslator

    override fun translate(entity: fi.metatavu.rapurc.api.persistence.model.BuildingType): BuildingType {
        val buildingType = BuildingType()
        buildingType.id = entity.id
        buildingType.name = entity.name
        buildingType.code = entity.code
        buildingType.metadata = metadataTranslator.translate(entity)
        return buildingType
    }

}

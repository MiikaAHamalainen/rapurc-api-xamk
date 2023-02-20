package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.model.BuildingType
import fi.metatavu.rapurc.api.persistence.dao.LocalizedValueDAO
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translates building type jpa object to rest
 */
@ApplicationScoped
class BuildingTypeTranslator: AbstractTranslator<fi.metatavu.rapurc.api.persistence.model.BuildingType, BuildingType>() {

    @Inject
    lateinit var metadataTranslator: MetadataTranslator

    @Inject
    lateinit var localizedValueTranslator: LocalizedValueTranslator

    @Inject
    lateinit var localizedValueDAO: LocalizedValueDAO

    override fun translate(entity: fi.metatavu.rapurc.api.persistence.model.BuildingType): BuildingType {
        val buildingType = BuildingType()
        buildingType.id = entity.id
        buildingType.localizedNames = localizedValueDAO.listBy(buildingType = entity).map(
            localizedValueTranslator::translate
        )
        buildingType.code = entity.code
        buildingType.metadata = metadataTranslator.translate(entity)
        return buildingType
    }

}

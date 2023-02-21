package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.persistence.dao.LocalizedValueDAO
import fi.metatavu.rapurc.api.persistence.model.HazardousMaterial
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translates JPA Hazardous material entity into REST object
 */
@ApplicationScoped
class HazardousMaterialTranslator: AbstractTranslator<HazardousMaterial, fi.metatavu.rapurc.api.model.HazardousMaterial>() {

    @Inject
    lateinit var metadataTranslator: MetadataTranslator

    @Inject
    lateinit var localizedValueTranslator: LocalizedValueTranslator

    @Inject
    lateinit var localizedValueDAO: LocalizedValueDAO

    override fun translate(entity: HazardousMaterial): fi.metatavu.rapurc.api.model.HazardousMaterial {
        val hazardousMaterial = fi.metatavu.rapurc.api.model.HazardousMaterial()
        hazardousMaterial.id = entity.id
        hazardousMaterial.localizedNames = localizedValueDAO.listBy(hazardousMaterial = entity).map(
            localizedValueTranslator::translate
        )
        hazardousMaterial.wasteCategoryId = entity.wasteCategory?.id
        hazardousMaterial.ewcSpecificationCode = entity.ewcSpecificationCode
        hazardousMaterial.metadata = metadataTranslator.translate(entity)
        return hazardousMaterial
    }

}
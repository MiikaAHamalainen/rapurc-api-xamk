package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.persistence.dao.LocalizedValueDAO
import fi.metatavu.rapurc.api.persistence.model.WasteMaterial
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translates JPA Waste material entity into REST object
 */
@ApplicationScoped
class WasteMaterialTranslator: AbstractTranslator<WasteMaterial, fi.metatavu.rapurc.api.model.WasteMaterial>() {

    @Inject
    lateinit var metadataTranslator: MetadataTranslator

    @Inject
    lateinit var localizedValueTranslator: LocalizedValueTranslator

    @Inject
    lateinit var localizedValueDAO: LocalizedValueDAO

    override fun translate(entity: WasteMaterial): fi.metatavu.rapurc.api.model.WasteMaterial {
        val wasteMaterial = fi.metatavu.rapurc.api.model.WasteMaterial()
        wasteMaterial.id = entity.id
        wasteMaterial.localizedNames = localizedValueDAO.listBy(wasteMaterial = entity).map(
            localizedValueTranslator::translate
        )
        wasteMaterial.wasteCategoryId = entity.wasteCategory?.id
        wasteMaterial.ewcSpecificationCode = entity.ewcSpecificationCode
        wasteMaterial.metadata = metadataTranslator.translate(entity)
        return wasteMaterial
    }

}
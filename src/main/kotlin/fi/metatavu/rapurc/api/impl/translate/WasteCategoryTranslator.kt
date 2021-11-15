package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.persistence.model.WasteCategory
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translates JPA Waste Category entity into REST object
 */
@ApplicationScoped
class WasteCategoryTranslator: AbstractTranslator<WasteCategory, fi.metatavu.rapurc.api.model.WasteCategory>() {

    @Inject
    lateinit var metadataTranslator: MetadataTranslator

    override fun translate(entity: WasteCategory): fi.metatavu.rapurc.api.model.WasteCategory {
        val wasteCategory = fi.metatavu.rapurc.api.model.WasteCategory()
        wasteCategory.id = entity.id
        wasteCategory.name = entity.name
        wasteCategory.ewcCode = entity.ewcCode
        wasteCategory.metadata = metadataTranslator.translate(entity)
        return wasteCategory
    }
}
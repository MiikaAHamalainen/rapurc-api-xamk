package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.model.Waste
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translates Waste JPA object to REST object
 */
@ApplicationScoped
class WasteTranslator: AbstractTranslator<fi.metatavu.rapurc.api.persistence.model.Waste, Waste>() {

    @Inject
    lateinit var metadataTranslator: MetadataTranslator

    override fun translate(entity: fi.metatavu.rapurc.api.persistence.model.Waste): Waste {
        val waste = Waste()
        waste.id = entity.id
        waste.amount = entity.amount
        waste.wasteMaterialId = entity.wasteMaterial?.id
        waste.description = entity.description
        waste.usageId = entity.usage?.id
        waste.metadata = metadataTranslator.translate(entity)
        return waste
    }

}
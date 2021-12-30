package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.model.HazardousWaste
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translates Hazardous Waste JPA to REST object
 */
@ApplicationScoped
class HazardousWasteTranslator: AbstractTranslator<fi.metatavu.rapurc.api.persistence.model.HazardousWaste, HazardousWaste>() {

    @Inject
    lateinit var metadataTranslator: MetadataTranslator

    override fun translate(entity: fi.metatavu.rapurc.api.persistence.model.HazardousWaste): HazardousWaste {
        val hazardousWaste = HazardousWaste()
        hazardousWaste.id = entity.id
        hazardousWaste.hazardousMaterialId = entity.hazardousMaterial!!.id
        hazardousWaste.wasteSpecifierId = entity.wasteSpecifier?.id
        hazardousWaste.amount = entity.amount
        hazardousWaste.description = entity.description
        hazardousWaste.metadata = metadataTranslator.translate(entity)
        return hazardousWaste
    }

}

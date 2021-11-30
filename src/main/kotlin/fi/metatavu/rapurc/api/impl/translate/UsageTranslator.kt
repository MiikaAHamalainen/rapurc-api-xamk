package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.model.Usage
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translates Waste Usage JPA object to REST Usage object
 */
@ApplicationScoped
class UsageTranslator: AbstractTranslator<fi.metatavu.rapurc.api.persistence.model.WasteUsage, Usage>() {

    @Inject
    lateinit var metadataTranslator: MetadataTranslator

    override fun translate(entity: fi.metatavu.rapurc.api.persistence.model.WasteUsage): Usage {
        val usage = Usage()
        usage.id = entity.id
        usage.name = entity.name
        usage.metadata = metadataTranslator.translate(entity)
        return usage
    }

}

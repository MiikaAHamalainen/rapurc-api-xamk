package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.model.Usage
import fi.metatavu.rapurc.api.persistence.dao.LocalizedValueDAO
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translates Waste Usage JPA object to REST Usage object
 */
@ApplicationScoped
class UsageTranslator: AbstractTranslator<fi.metatavu.rapurc.api.persistence.model.WasteUsage, Usage>() {

    @Inject
    lateinit var metadataTranslator: MetadataTranslator

    @Inject
    lateinit var localizedValueTranslator: LocalizedValueTranslator

    @Inject
    lateinit var localizedValueDAO: LocalizedValueDAO

    override fun translate(entity: fi.metatavu.rapurc.api.persistence.model.WasteUsage): Usage {
        val usage = Usage()
        usage.id = entity.id
        usage.localizedNames = localizedValueDAO.listBy(usage = entity).map(
            localizedValueTranslator::translate
        )
        usage.metadata = metadataTranslator.translate(entity)
        return usage
    }

}

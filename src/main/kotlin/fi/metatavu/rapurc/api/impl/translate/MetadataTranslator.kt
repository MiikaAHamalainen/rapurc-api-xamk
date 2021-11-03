package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.model.Metadata
import fi.metatavu.rapurc.api.persistence.model.GeneralInfo
import javax.enterprise.context.ApplicationScoped

/**
 * Translates JPA additional metadata info into REST Metadata object
 */
@ApplicationScoped
class MetadataTranslator: AbstractTranslator<GeneralInfo, Metadata>() {
    override fun translate(entity: GeneralInfo): Metadata {
        val metadata = Metadata()
        metadata.createdAt = entity.createdAt
        metadata.modifiedAt = entity.modifiedAt
        metadata.creatorId = entity.creatorId
        metadata.lastModifierId = entity.lastModifierId
        return metadata
    }

}

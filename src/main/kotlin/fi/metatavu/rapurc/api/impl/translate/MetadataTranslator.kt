package fi.metatavu.rapurc.api.impl.translate

import javax.enterprise.context.ApplicationScoped

/**
 * Translates JPA additional metadata info into REST Metadata object
 */
@ApplicationScoped
class MetadataTranslator: AbstractTranslator<fi.metatavu.rapurc.api.persistence.model.Metadata, fi.metatavu.rapurc.api.model.Metadata>() {
    override fun translate(entity: fi.metatavu.rapurc.api.persistence.model.Metadata): fi.metatavu.rapurc.api.model.Metadata {
        val metadata = fi.metatavu.rapurc.api.model.Metadata()
        metadata.createdAt = entity.createdAt
        metadata.modifiedAt = entity.modifiedAt
        metadata.creatorId = entity.creatorId
        metadata.lastModifierId = entity.lastModifierId
        return metadata
    }

}

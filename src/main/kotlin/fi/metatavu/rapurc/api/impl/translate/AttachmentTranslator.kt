package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.persistence.model.Attachment
import java.net.URI
import javax.enterprise.context.ApplicationScoped

/**
 * Translates JPA attachment to REST Attachment object
 */
@ApplicationScoped
class AttachmentTranslator:
    AbstractTranslator<Attachment, fi.metatavu.rapurc.api.model.Attachment>() {
    override fun translate(entity: Attachment): fi.metatavu.rapurc.api.model.Attachment {
        val attachment = fi.metatavu.rapurc.api.model.Attachment()
        attachment.id = entity.id
        attachment.name = entity.name
        attachment.url = URI.create(entity.url!!)
        attachment.description = entity.description
        attachment.surveyId = entity.survey!!.id
        return attachment
    }

}

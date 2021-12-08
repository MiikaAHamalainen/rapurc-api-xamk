package fi.metatavu.rapurc.api.impl.surveys

import fi.metatavu.rapurc.api.persistence.dao.AttachmentDAO
import fi.metatavu.rapurc.api.persistence.dao.SurveyDAO
import fi.metatavu.rapurc.api.persistence.model.Attachment
import fi.metatavu.rapurc.api.persistence.model.Survey
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for Attachments
 */
@ApplicationScoped
class AttachmentController {

    @Inject
    lateinit var attachmentDAO: AttachmentDAO

    @Inject
    lateinit var surveyDAO: SurveyDAO

    /**
     * Lists attachments for the survey
     *
     * @param survey survey filter
     * @return attachments
     */
    fun list(survey: Survey): List<Attachment> {
        return attachmentDAO.list(survey)
    }

    /**
     * Creates survey attachment
     *
     * @param attachment new attachment rest object
     * @param survey survey
     * @param userId user id
     * @return created attachment
     */
    fun create(attachment: fi.metatavu.rapurc.api.model.Attachment, survey: Survey, userId: UUID): Attachment {
        return attachmentDAO.create(
            id = UUID.randomUUID(),
            survey = survey,
            name = attachment.name,
            url = attachment.url,
            description = attachment.description,
            creatorId = userId,
            lastModifierId = userId
        )
    }

    /**
     * Finds attachment by id
     *
     * @param attachmentId attachment id
     * @return found attachment or null
     */
    fun findById(attachmentId: UUID): Attachment? {
        return attachmentDAO.findById(attachmentId)
    }

    /**
     * Updates attachment with new data
     *
     * @param attachmentToUpdate original attachment
     * @param attachment update REST data
     * @param userId modifier id
     * @return updated attachment
     */
    fun updateAttachment(
        attachmentToUpdate: Attachment,
        attachment: fi.metatavu.rapurc.api.model.Attachment,
        userId: UUID
    ): Attachment {
        val result = attachmentDAO.updateName(attachmentToUpdate, attachment.name, userId)
        attachmentDAO.updateURL(result, attachment.url, userId)
        return attachmentDAO.updateDescription(result, attachment.description, userId)
    }

    /**
     * Deletes attachment and updates the survey
     *
     * @param attachment attachment
     * @param userId user id
     */
    fun delete(attachment: Attachment, userId: UUID) {
        attachmentDAO.delete(attachment)
        surveyDAO.update(attachment.survey!!, userId)
    }

}

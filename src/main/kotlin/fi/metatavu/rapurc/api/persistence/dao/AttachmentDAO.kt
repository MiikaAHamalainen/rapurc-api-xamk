package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.Attachment
import fi.metatavu.rapurc.api.persistence.model.Attachment_
import fi.metatavu.rapurc.api.persistence.model.Survey
import java.net.URI
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

/**
 * DAO class for Building JPA object
 */
@ApplicationScoped
class AttachmentDAO: AbstractDAO<Attachment>() {

    /**
     * Creates a building
     *
     * @param id id
     * @param survey survey
     * @param name attachment name
     * @param url url of the attachment file in s3
     * @param description optional description
     * @param creatorId creator id
     * @param lastModifierId last modifier id
     * @return created building
     */
    fun create(
        id: UUID?,
        survey: Survey,
        name: String,
        url: URI,
        description: String?,
        creatorId: UUID,
        lastModifierId: UUID
    ) : Attachment {
        val attachment = Attachment()
        attachment.id = id
        attachment.name = name
        attachment.survey = survey
        attachment.url = url.toString()
        attachment.description = description
        attachment.creatorId = creatorId
        attachment.lastModifierId = lastModifierId
        return persist(attachment)
    }

    /**
     * Lists attachments added to survey
     *
     * @param survey survey
     * @return survey attachment list
     */
    fun list(survey: Survey): List<Attachment> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria: CriteriaQuery<Attachment> =
            criteriaBuilder.createQuery(Attachment::class.java)
        val root: Root<Attachment> = criteria.from(Attachment::class.java)

        criteria.select(root)
        criteria.select(root)
        criteria.where(criteriaBuilder.equal(root.get(Attachment_.survey), survey))
        criteria.orderBy(criteriaBuilder.asc(root.get(Attachment_.createdAt)))
        val query: TypedQuery<Attachment> = entityManager.createQuery(criteria)
        return query.resultList
    }

    /**
     * Updates name
     *
     * @param attachment attachment to update
     * @param name new name
     * @param modifierId modifier id
     * @return updated attachment
     */
    fun updateName(attachment: Attachment, name: String, modifierId: UUID): Attachment {
        attachment.name = name
        attachment.lastModifierId = modifierId
        return persist(attachment)
    }

    /**
     * Updates url
     *
     * @param attachment attachment to update
     * @param url new url
     * @param modifierId modifier id
     * @return updated attachment
     */
    fun updateURL(attachment: Attachment, url: URI, modifierId: UUID): Attachment {
        attachment.url = url.toString()
        attachment.lastModifierId = modifierId
        return persist(attachment)
    }

    /**
     * Updates description
     *
     * @param attachment attachment to update
     * @param description new description
     * @param modifierId modifier id
     * @return updated attachment
     */
    fun updateDescription(attachment: Attachment, description: String?, modifierId: UUID): Attachment {
        attachment.description = description
        attachment.lastModifierId = modifierId
        return persist(attachment)
    }

}
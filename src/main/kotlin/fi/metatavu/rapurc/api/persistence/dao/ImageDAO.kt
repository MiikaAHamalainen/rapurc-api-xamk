package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.Image
import fi.metatavu.rapurc.api.persistence.model.Image_
import fi.metatavu.rapurc.api.persistence.model.Reusable
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery

/**
 * DAO class to manage Image entity
 */
@ApplicationScoped
class ImageDAO: AbstractDAO<Image>() {

    /**
     * Creates image
     *
     * @param id id
     * @param imageUri image uri
     * @param reusable reusable
     * @return created image
     */
    fun create(
        id: UUID,
        imageUri: String,
        reusable: Reusable
    ): Image {
        val image = Image()
        image.id = id
        image.imageUri = imageUri
        image.reusable = reusable
        return persist(image)
    }

    /**
     * Lists images by reusable
     *
     * @param reusable reusable to filter by
     * @return images
     */
    fun list(reusable: Reusable): List<Image> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(Image::class.java)
        val root = criteria.from(Image::class.java)

        criteria.select(root)
        criteria.where(criteriaBuilder.equal(root.get(Image_.reusable), reusable))

        val query: TypedQuery<Image> = entityManager.createQuery(criteria)
        criteria.orderBy(criteriaBuilder.asc(root.get(Image_.reusable)))
        return query.resultList
    }
}

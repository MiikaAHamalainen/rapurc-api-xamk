package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.Image
import java.util.*
import javax.enterprise.context.ApplicationScoped

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
     * @return created image
     */
    fun create(
        id: UUID,
        imageUri: String
    ): Image {
        val image = Image()
        image.id = id
        image.imageUri = imageUri
        return persist(image)
    }
}

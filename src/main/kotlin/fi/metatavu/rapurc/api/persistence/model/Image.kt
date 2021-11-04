package fi.metatavu.rapurc.api.persistence.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

/**
 * Image entity
 */
@Entity
class Image {

    @Id
    var id: UUID? = null

    @Column(nullable = false)
    var imageUri: String? = null
}
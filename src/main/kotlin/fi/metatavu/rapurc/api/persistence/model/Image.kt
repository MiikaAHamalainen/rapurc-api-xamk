package fi.metatavu.rapurc.api.persistence.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

/**
 * Image entity
 */
@Entity
class Image {

    @Id
    var id: UUID? = null

    @Column(nullable = false)
    var imageUri: String? = null

    @ManyToOne
    var reusable: Reusable? = null
}
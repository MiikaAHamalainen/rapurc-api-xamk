package fi.metatavu.rapurc.api.persistence.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.constraints.NotEmpty

/**
 * Attachment entity
 */
@Entity
class Attachment: Metadata() {

    @Id
    var id: UUID? = null

    @ManyToOne
    var survey: Survey? = null

    @Column(nullable = false)
    @NotEmpty
    var url: String? = null

    @Column(nullable = false)
    @NotEmpty
    var name: String? = null

    @Column
    var description: String? = null
}
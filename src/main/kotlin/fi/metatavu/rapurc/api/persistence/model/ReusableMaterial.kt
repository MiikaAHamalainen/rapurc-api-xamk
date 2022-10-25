package fi.metatavu.rapurc.api.persistence.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotEmpty

/**
 * Entity for Reusable Material
 */
@Entity
class ReusableMaterial: Metadata() {

    @Id
    var id: UUID? = null

    @NotEmpty
    @Column(nullable = false)
    var name: String? = null

}
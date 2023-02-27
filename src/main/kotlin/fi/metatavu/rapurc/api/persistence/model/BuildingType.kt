package fi.metatavu.rapurc.api.persistence.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotEmpty

/**
 * Entity for building type
 */
@Entity
class BuildingType: Metadata() {

    @Id
    var id: UUID? = null

    @Column(nullable = false)
    @NotEmpty
    var code: String? = null
}
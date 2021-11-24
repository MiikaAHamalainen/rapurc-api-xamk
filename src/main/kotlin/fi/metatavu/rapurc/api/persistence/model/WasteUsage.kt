package fi.metatavu.rapurc.api.persistence.model

import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotEmpty

/**
 * Entity class for Waste usage object
 */
@Entity
class WasteUsage: Metadata() {

    @Id
    var id: UUID? = null

    @NotEmpty
    @Column(nullable = false)
    var name: String? = null

}
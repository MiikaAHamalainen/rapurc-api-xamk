package fi.metatavu.rapurc.api.persistence.model

import java.util.*
import javax.persistence.*

/**
 * Entity class for Waste usage object
 */
@Entity
class WasteUsage: Metadata() {

    @Id
    var id: UUID? = null

    @Column(nullable = false)
    var name: String? = null

}
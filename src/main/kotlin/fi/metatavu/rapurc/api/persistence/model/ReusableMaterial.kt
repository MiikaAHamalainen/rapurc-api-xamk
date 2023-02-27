package fi.metatavu.rapurc.api.persistence.model

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

/**
 * Entity for Reusable Material
 */
@Entity
class ReusableMaterial: Metadata() {

    @Id
    var id: UUID? = null
}
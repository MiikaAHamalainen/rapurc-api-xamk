package fi.metatavu.rapurc.api.persistence.model

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne


/**
 * Entity for Waste Specifier
 */
@Entity
class WasteSpecifier: Metadata() {

    @Id
    var id: UUID? = null

}
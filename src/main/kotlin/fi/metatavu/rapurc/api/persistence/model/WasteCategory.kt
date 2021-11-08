package fi.metatavu.rapurc.api.persistence.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

/**
 * Waste category entity
 */
@Entity
class WasteCategory: GeneralInfo() {

    @Id
    var id: UUID? = null

    @Column(nullable = false)
    var name: String? = null

    @Column(nullable = false)
    var ewcCode: String? = null
}
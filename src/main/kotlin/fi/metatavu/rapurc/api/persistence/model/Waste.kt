package fi.metatavu.rapurc.api.persistence.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

/**
 * Entity class for Waste
 */
@Entity
class Waste: Metadata() {

    @Id
    var id: UUID? = null

    @ManyToOne
    var wasteMaterial: WasteMaterial? = null

    @ManyToOne
    var survey: Survey? = null

    @ManyToOne
    var usage: WasteUsage? = null

    @Column(nullable = false)
    var amount: Double? = null

    @Column
    var description: String? = null

}
package fi.metatavu.rapurc.api.persistence.model

import java.util.*
import javax.persistence.*

/**
 * Entity class for Hazardous Waste
 */
@Entity
class HazardousWaste: Metadata() {

    @Id
    var id: UUID? = null

    @ManyToOne
    var survey: Survey? = null

    @ManyToOne
    var hazardousMaterial: HazardousMaterial? = null

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    var wasteSpecifier: WasteSpecifier? = null

    @Column(nullable = false)
    var amount: Double? = null

    @Column
    var description: String? = null

}
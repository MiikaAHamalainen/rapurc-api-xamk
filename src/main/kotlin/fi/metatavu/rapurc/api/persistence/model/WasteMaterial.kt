package fi.metatavu.rapurc.api.persistence.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.constraints.NotEmpty

/**
 * Entity for Waste Material
 */
@Entity
class WasteMaterial: Metadata() {

    @Id
    var id: UUID? = null

    @NotEmpty
    @Column(nullable = false)
    var name: String? = null

    @ManyToOne
    var wasteCategory: WasteCategory? = null

    @NotEmpty
    @Column(nullable = false)
    var ewcSpecificationCode: String? = null
}
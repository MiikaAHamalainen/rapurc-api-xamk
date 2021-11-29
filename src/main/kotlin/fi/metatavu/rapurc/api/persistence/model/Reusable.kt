package fi.metatavu.rapurc.api.persistence.model

import fi.metatavu.rapurc.api.model.Unit
import fi.metatavu.rapurc.api.model.Usability
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotEmpty

/**
 * Entity class for Reusable
 */
@Entity
class Reusable: Metadata() {

    @Id
    var id: UUID? = null

    @ManyToOne
    var survey: Survey? = null

    @NotEmpty
    @Column(nullable = false)
    var componentName: String? = null

    @Column(nullable = false)
    var materialId: UUID? = null

    @Column
    var usability: Usability? = null

    @Column
    var amount: Double? = null

    @Column
    var unit: Unit? = null

    @Column
    var description: String? = null

    @Column
    var amountAsWaste: Double? = null

}
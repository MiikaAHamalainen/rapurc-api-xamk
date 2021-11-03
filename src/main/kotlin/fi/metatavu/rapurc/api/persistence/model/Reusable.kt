package fi.metatavu.rapurc.api.persistence.model

import fi.metatavu.rapurc.api.model.Unit
import fi.metatavu.rapurc.api.model.Usability
import java.net.URI
import java.util.*
import javax.persistence.*

/**
 * Entity class for Reusable
 */
@Entity
class Reusable: GeneralInfo() {

    @Id
    var id: UUID? = null

    @ManyToOne
    var survey: Survey? = null

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

    @ElementCollection
    var images: List<URI>? = null

}
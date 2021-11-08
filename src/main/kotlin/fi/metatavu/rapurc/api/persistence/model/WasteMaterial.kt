package fi.metatavu.rapurc.api.persistence.model

import org.hibernate.annotations.CollectionId
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

/**
 * Entity for Waste Material
 */
@Entity
class WasteMaterial: GeneralInfo() {

    @Id
    var id: UUID? = null

    @Column(nullable = false)
    var name: String? = null

    @ManyToOne
    var wasteCategory: WasteCategory? = null

    @Column(nullable = false)
    var ewcSpecificationCode: String? = null
}
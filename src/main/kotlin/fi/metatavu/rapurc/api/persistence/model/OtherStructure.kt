package fi.metatavu.rapurc.api.persistence.model

import java.util.*
import javax.persistence.*

/**
 * Other building structure to be demolished
 */
@Entity
class OtherStructure: Metadata() {

    @Id
    var id: UUID? = null

    @Column
    var name: String? = null

    @Column
    var description: String? = null

    @ManyToOne
    var building: Building? = null
}
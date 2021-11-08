package fi.metatavu.rapurc.api.persistence.model

import java.util.*
import javax.persistence.*

/**
 * JPA entity representing owner information
 */
@Entity
class OwnerInformation: Metadata() {

    @Id
    var id: UUID? = null

    @ManyToOne
    var survey: Survey? = null

    @Column
    var ownerName: String? = null

    @Column
    var businessId: String? = null

    @Column
    var firstName: String? = null

    @Column
    var lastName: String? = null

    @Column
    var phone: String? = null

    @Column
    var email: String? = null

    @Column
    var profession: String? = null
}
package fi.metatavu.rapurc.api.persistence.model

import javax.persistence.Column
import javax.persistence.Embeddable

/**
 * Other building structure to be demolished
 */
@Embeddable
class OtherStructure {

    @Column
    var name: String? = null

    @Column
    var description: String? = null
}
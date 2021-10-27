package fi.metatavu.rapurc.api.persistence.model

import fi.metatavu.rapurc.api.model.SurveyStatus
import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*

/**
 * JPA entity representing surveys
 *
 * @author Jari Nykänen
 */
@Entity
class Survey: GeneralInfo() {

    @Id
    var id: UUID? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: SurveyStatus? = null

    @Column(nullable = false)
    var keycloakGroupId: UUID? = null

}
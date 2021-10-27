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
class Survey {

    @Id
    var id: UUID? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: SurveyStatus? = null

    @Column(nullable = false)
    var keycloakGroupId: UUID? = null

    @Column(nullable = false)
    var creatorId: UUID? = null

    @Column(nullable = false)
    var createdAt: OffsetDateTime? = null

    @Column(nullable = false)
    var lastModifierId: UUID? = null

    @Column(nullable = false)
    var modifiedAt: OffsetDateTime? = null

    /**
     * JPA pre-persist event handler
     */
    @PrePersist
    fun onCreate() {
        createdAt = OffsetDateTime.now()
        modifiedAt = OffsetDateTime.now()
    }

    /**
     * JPA pre-update event handler
     */
    @PreUpdate
    fun onUpdate() {
        modifiedAt = OffsetDateTime.now()
    }
}
package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.persistence.model.Survey
import javax.enterprise.context.ApplicationScoped

/**
 * Translator for translating JPA survey entities into REST resources
 */
@ApplicationScoped
class SurveyTranslator: AbstractTranslator<Survey, fi.metatavu.rapurc.api.model.Survey>() {

    override fun translate(entity: Survey): fi.metatavu.rapurc.api.model.Survey {
        val result = fi.metatavu.rapurc.api.model.Survey()

        result.id = entity.id
        result.status = entity.status
        result.type = entity.type
        result.startDate = entity.startDate
        result.endDate = entity.endDate
        result.creatorId = entity.creatorId
        result.createdAt = entity.createdAt
        result.lastModifierId = entity.lastModifierId
        result.modifiedAt = entity.modifiedAt

        return result
    }

}
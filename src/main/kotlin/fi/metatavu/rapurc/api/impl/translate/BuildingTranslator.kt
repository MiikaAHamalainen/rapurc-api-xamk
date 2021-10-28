package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.persistence.model.Building
import javax.enterprise.context.ApplicationScoped

/**
 * Translates JPA Building to REST Building
 */
@ApplicationScoped
class BuildingTranslator: AbstractTranslator<Building, fi.metatavu.rapurc.api.model.Building>() {
    override fun translate(entity: Building): fi.metatavu.rapurc.api.model.Building {
        val result = fi.metatavu.rapurc.api.model.Building()
        result.id = entity.id
        result.surveyId = entity.survey?.id
        result.propertyId = entity.propertyId
        result.buildingId = entity.buildingId
        result.classificationCode = entity.classificationCode
        result.constructionYear = entity.constructionYear
        result.space = entity.space
        result.volume = entity.volume
        result.floors = entity.floors
        result.basements = entity.basements
        result.foundation = entity.foundation
        result.supportinStructure = entity.supportStructure
        result.facadeMaterial = entity.facadeMaterial
        result.roofType = entity.roofType
        result.address.city = entity.city
        result.address.streetAddress = entity.streetAddress
        result.address.postCode = entity.postCode
        result.createdAt = entity.createdAt
        result.creatorId = entity.creatorId
        result.modifiedAt = entity.modifiedAt
        result.lastModifierId = entity.lastModifierId
        return result
    }

}

package fi.metatavu.rapurc.api.impl.buildings

import fi.metatavu.rapurc.api.persistence.dao.BuildingDAO
import fi.metatavu.rapurc.api.persistence.model.Building
import fi.metatavu.rapurc.api.persistence.model.Survey
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for Buildings
 */
@ApplicationScoped
class BuildingController {

    @Inject
    private lateinit var buildingDAO: BuildingDAO

    /**
     * Lists buildings based on survey
     *
     * @param survey survey
     * @return filtered building list
     */
    fun list(survey: Survey): List<Building> {
        return buildingDAO.list(survey)
    }

    fun create(survey: Survey, building: fi.metatavu.rapurc.api.model.Building, userId: UUID): Any {
        return buildingDAO.create(
            id = UUID.randomUUID(),
            survey = survey,
            propertyId = building.propertyId,
            buildingId = building.buildingId,
            classificationCode = building.classificationCode,
            constructionYear = building.constructionYear,
            space = building.space,
            volume = building.volume,
            floors = building.floors,
            basements = building.basements,
            foundation = building.foundation,
            supportStructure = building.supportinStructure,
            facadeMaterial = building.facadeMaterial,
            roofType = building.roofType,
            streetAddress = building.streetAddress,

        )
    }

}

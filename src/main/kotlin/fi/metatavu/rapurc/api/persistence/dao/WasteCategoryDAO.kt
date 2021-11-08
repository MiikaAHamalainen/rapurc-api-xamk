package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.WasteCategory
import java.util.*
import javax.enterprise.context.ApplicationScoped

/**
 * DAO class for Waste Category entity
 */
@ApplicationScoped
class WasteCategoryDAO: AbstractDAO<WasteCategory>() {

    /**
     * Creates waste category entity
     *
     * @param id id
     * @param name category name
     * @param ewcCode first four characters of EWC code
     * @param creatorId creator id
     * @param modifierId modifier id
     * @return saved waste category
     */
    fun create(
        id: UUID,
        name: String,
        ewcCode: String,
        creatorId: UUID,
        modifierId: UUID
    ): WasteCategory {
        val wasteCategory = WasteCategory()
        wasteCategory.id = id
        wasteCategory.name = name
        wasteCategory.ewcCode = ewcCode
        wasteCategory.creatorId = creatorId
        wasteCategory.lastModifierId = modifierId
        return persist(wasteCategory)
    }

    fun updateName(wasteCategory: WasteCategory, name: String, userId: UUID): WasteCategory {
        wasteCategory.name = name
        wasteCategory.lastModifierId = userId
        return persist(wasteCategory)
    }

    fun updateEwcCode(wasteCategory: WasteCategory, ewcCode: String, userId: UUID): WasteCategory {
        wasteCategory.ewcCode = ewcCode
        wasteCategory.lastModifierId = userId
        return persist(wasteCategory)
    }
}
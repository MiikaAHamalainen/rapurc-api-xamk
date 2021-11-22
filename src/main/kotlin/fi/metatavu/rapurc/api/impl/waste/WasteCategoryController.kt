package fi.metatavu.rapurc.api.impl.waste

import fi.metatavu.rapurc.api.persistence.dao.WasteCategoryDAO
import fi.metatavu.rapurc.api.persistence.model.WasteCategory
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for Waste Category entity
 */
@ApplicationScoped
class WasteCategoryController {

    @Inject
    lateinit var wasteCategoryDAO: WasteCategoryDAO

    /**
     * Lists all waste categories
     *
     * @return waste categories
     */
    fun list(): List<WasteCategory> {
        return wasteCategoryDAO.listAll()
    }

    /**
     * Creates new waste category
     *
     * @param wasteCategory new data
     * @param userId user id
     * @return created object
     */
    fun create(wasteCategory: fi.metatavu.rapurc.api.model.WasteCategory, userId: UUID): WasteCategory {
        return wasteCategoryDAO.create(
            id = UUID.randomUUID(),
            name = wasteCategory.name,
            ewcCode = wasteCategory.ewcCode,
            creatorId = userId,
            modifierId = userId
        )
    }

    /**
     * Finds waste category by id
     *
     * @param wasteCategoryId id
     * @return found waste category or null
     */
    fun find(wasteCategoryId: UUID): WasteCategory? {
        return wasteCategoryDAO.findById(wasteCategoryId)
    }

    /**
     * Updates waste category with new data
     *
     * @param categoryToUpdate category to update
     * @param wasteCategory new data
     * @param userId user id
     * @return updated waste category
     */
    fun update(categoryToUpdate: WasteCategory, wasteCategory: fi.metatavu.rapurc.api.model.WasteCategory, userId: UUID): WasteCategory {
        val result = wasteCategoryDAO.updateName(categoryToUpdate, wasteCategory.name, userId)
        return wasteCategoryDAO.updateEwcCode(result, wasteCategory.ewcCode, userId)
    }

    /**
     * Deletes waste category
     *
     * @param wasteCategory category to delete
     */
    fun delete(wasteCategory: WasteCategory) {
        wasteCategoryDAO.delete(wasteCategory)
    }
}
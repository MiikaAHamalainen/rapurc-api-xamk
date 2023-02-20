package fi.metatavu.rapurc.api.impl.waste

import fi.metatavu.rapurc.api.persistence.dao.LocalizedValueDAO
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

    @Inject
    lateinit var localizedValueDAO: LocalizedValueDAO

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
        val createdWasteCategory = wasteCategoryDAO.create(
            id = UUID.randomUUID(),
            ewcCode = wasteCategory.ewcCode,
            creatorId = userId,
            modifierId = userId
        )

        wasteCategory.localizedNames.forEach {
            localizedValueDAO.create(
                id = UUID.randomUUID(),
                value = it.value,
                language = it.language,
                wasteCategory = createdWasteCategory
            )
        }

        return createdWasteCategory
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

        localizedValueDAO.listBy(wasteCategory = categoryToUpdate)
            .forEach { localizedValueDAO.delete(it) }

        wasteCategory.localizedNames.forEach {
            localizedValueDAO.create(
                id = UUID.randomUUID(),
                value = it.value,
                language = it.language,
                wasteCategory = categoryToUpdate
            )
        }

        return wasteCategoryDAO.updateEwcCode(categoryToUpdate, wasteCategory.ewcCode, userId)
    }

    /**
     * Deletes waste category
     *
     * @param wasteCategory category to delete
     */
    fun delete(wasteCategory: WasteCategory) {
        localizedValueDAO.listBy(wasteCategory = wasteCategory)
            .forEach { localizedValueDAO.delete(it) }
        wasteCategoryDAO.delete(wasteCategory)
    }
}
package fi.metatavu.rapurc.api.impl.materials

import fi.metatavu.rapurc.api.persistence.dao.ImageDAO
import fi.metatavu.rapurc.api.persistence.dao.ReusableDAO
import fi.metatavu.rapurc.api.persistence.model.Reusable
import fi.metatavu.rapurc.api.persistence.model.ReusableMaterial
import fi.metatavu.rapurc.api.persistence.model.Survey
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Manages reusables
 */
@ApplicationScoped
class ReusableController {

    @Inject
    private lateinit var reusableDAO: ReusableDAO

    @Inject
    private lateinit var imageDAO: ImageDAO

    /**
     * Lists Reusables by survey
     *
     * @param survey survey to filter by
     * @param material material to filter by
     * @return reusables
     */
    fun list(survey: Survey?, material: ReusableMaterial?): MutableList<Reusable>? {
        return reusableDAO.list(survey, material)
    }

    /**
     * Creates new Reusable
     *
     * @param reusable REST Reusable object
     * @param survey survey it belongs to
     * @param userId creator id
     * @return created Reusable
     */
    fun create(reusable: fi.metatavu.rapurc.api.model.Reusable, survey: Survey, userId: UUID): Reusable {
        val createdReusable = reusableDAO.create(
            id = UUID.randomUUID(),
            survey = survey,
            componentName = reusable.componentName,
            materialId = reusable.reusableMaterialId,
            usability = reusable.usability,
            amount = reusable.amount,
            unit = reusable.unit,
            description = reusable.description,
            creatorId = userId,
            lastModifierId = userId
        )

        reusable.images.map { uri ->
            imageDAO.create(
                id = UUID.randomUUID(),
                imageUri = uri.toString(),
                reusable = createdReusable
            )
        }

        return createdReusable
    }

    /**
     * Finds Reusable by id
     *
     * @param reusableId reusable id
     * @return found reusable
     */
    fun findById(reusableId: UUID): Reusable? {
        return reusableDAO.findById(reusableId)
    }

    /**
     * Updates reusable
     *
     * @param reusableToUpdate old reusable
     * @param reusable new reusable
     * @param userId modifier id
     * @return updated reusable
     */
    fun updateReusable(reusableToUpdate: Reusable, reusable: fi.metatavu.rapurc.api.model.Reusable, userId: UUID): Reusable {
        imageDAO.list(reusableToUpdate).forEach(imageDAO::delete)
        reusable.images.map { uri ->
            imageDAO.create(
                id = UUID.randomUUID(),
                imageUri = uri.toString(),
                reusable = reusableToUpdate
            )
        }

        val result = reusableDAO.updateComponentName(reusableToUpdate, reusable.componentName, userId)
        reusableDAO.updateMaterialId(result, reusable.reusableMaterialId, userId)
        reusableDAO.updateUsability(result, reusable.usability, userId)
        reusableDAO.updateAmount(result, reusable.amount, userId)
        reusableDAO.updateUnit(result, reusable.unit, userId)
        return reusableDAO.updateDescription(result, reusable.description, userId)
    }

    /**
     * Deletes reusable
     *
     * @param reusableToDelete reusable to delete
     */
    fun delete(reusableToDelete: Reusable) {
        imageDAO.list(reusableToDelete).forEach { image ->
            imageDAO.delete(image)
        }

        reusableDAO.delete(reusableToDelete)
    }

}
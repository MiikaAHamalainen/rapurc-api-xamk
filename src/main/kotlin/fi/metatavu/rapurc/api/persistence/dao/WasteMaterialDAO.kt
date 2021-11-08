package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.persistence.model.WasteCategory
import fi.metatavu.rapurc.api.persistence.model.WasteMaterial
import java.util.*
import javax.enterprise.context.ApplicationScoped

/**
 * DAO class for waste materials
 */
@ApplicationScoped
class WasteMaterialDAO: AbstractDAO<WasteMaterial>(){

    /**
     * Creates waste material
     *
     * @param id id
     * @param name material name
     * @param wasteCategory waste category
     * @param ewcSpecificationCode EWC category id
     * @param creatorId creator id
     * @param modifierId modifier id
     * @return created waste material
     */
    fun create(
        id: UUID,
        name: String,
        wasteCategory: WasteCategory,
        ewcSpecificationCode: String,
        creatorId: UUID,
        modifierId: UUID
    ): WasteMaterial {
        val wasteMaterial = WasteMaterial()
        wasteMaterial.id = id
        wasteMaterial.name = name
        wasteMaterial.wasteCategory = wasteCategory
        wasteMaterial.ewcSpecificationCode = ewcSpecificationCode
        wasteMaterial.creatorId = creatorId
        wasteMaterial.lastModifierId = modifierId
        return persist(wasteMaterial)
    }
}
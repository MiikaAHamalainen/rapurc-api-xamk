package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.persistence.model.Reusable
import java.net.URI
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translates JPA reusable entity to REST
 */
@ApplicationScoped
class ReusableTranslator: AbstractTranslator<Reusable, fi.metatavu.rapurc.api.model.Reusable>() {

    @Inject
    lateinit var metadataTranslator: MetadataTranslator

    override fun translate(entity: Reusable): fi.metatavu.rapurc.api.model.Reusable {
        val reusable = fi.metatavu.rapurc.api.model.Reusable()
        reusable.id = entity.id
        reusable.componentName = entity.componentName
        reusable.reusableMaterialId = entity.materialId
        reusable.usability = entity.usability
        reusable.amount = entity.amount
        reusable.unit = entity.unit
        reusable.description = entity.description
        reusable.images = entity.images?.map { image -> URI.create(image.imageUri!!) }
        reusable.metadata = metadataTranslator.translate(entity)
        return reusable
    }

}

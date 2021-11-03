package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.persistence.model.Reusable
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
        reusable.amount = entity.amount
        reusable.unit = entity.unit
        reusable.description = entity.description
        reusable.images = entity.images
        reusable.metadata = metadataTranslator.translate(entity)
        return reusable
    }

}

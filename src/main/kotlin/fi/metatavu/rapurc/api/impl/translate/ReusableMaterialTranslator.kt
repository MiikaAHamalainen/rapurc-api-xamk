package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.model.ReusableMaterial
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translates ReusableMaterial JPA object into REST object
 */
@ApplicationScoped
class ReusableMaterialTranslator: AbstractTranslator<fi.metatavu.rapurc.api.persistence.model.ReusableMaterial, ReusableMaterial>() {

    @Inject
    lateinit var metadataTranslator: MetadataTranslator

    override fun translate(entity: fi.metatavu.rapurc.api.persistence.model.ReusableMaterial): ReusableMaterial {
        val reusable = ReusableMaterial()
        reusable.id = entity.id
        reusable.name = entity.name
        reusable.metadata = metadataTranslator.translate(entity)
        return reusable
    }

}

package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.model.ReusableMaterial
import fi.metatavu.rapurc.api.persistence.dao.LocalizedValueDAO
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translates ReusableMaterial JPA object into REST object
 */
@ApplicationScoped
class ReusableMaterialTranslator: AbstractTranslator<fi.metatavu.rapurc.api.persistence.model.ReusableMaterial, ReusableMaterial>() {

    @Inject
    lateinit var metadataTranslator: MetadataTranslator

    @Inject
    lateinit var localizedValueTranslator: LocalizedValueTranslator

    @Inject
    lateinit var localizedValueDAO: LocalizedValueDAO

    override fun translate(entity: fi.metatavu.rapurc.api.persistence.model.ReusableMaterial): ReusableMaterial {
        val reusable = ReusableMaterial()
        reusable.id = entity.id
        reusable.localizedNames = localizedValueDAO.listBy(reusableMaterial = entity).map(
            localizedValueTranslator::translate
        )
        reusable.metadata = metadataTranslator.translate(entity)
        return reusable
    }

}

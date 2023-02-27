package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.persistence.dao.LocalizedValueDAO
import fi.metatavu.rapurc.api.persistence.model.WasteSpecifier
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translates JPA Waste Specifier entity into REST object
 */
@ApplicationScoped
class WasteSpecifierTranslator : AbstractTranslator<WasteSpecifier, fi.metatavu.rapurc.api.model.WasteSpecifier>() {

    @Inject
    lateinit var metadataTranslator: MetadataTranslator

    @Inject
    lateinit var localizedValueTranslator: LocalizedValueTranslator

    @Inject
    lateinit var localizedValueDAO: LocalizedValueDAO

    override fun translate(entity: WasteSpecifier): fi.metatavu.rapurc.api.model.WasteSpecifier {
        val wasteSpecifier = fi.metatavu.rapurc.api.model.WasteSpecifier()
        wasteSpecifier.id = entity.id
        wasteSpecifier.localizedNames = localizedValueDAO.listBy(entity).map(
            localizedValueTranslator::translate
        )
        wasteSpecifier.metadata = metadataTranslator.translate(entity)
        return wasteSpecifier
    }
}
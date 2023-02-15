package fi.metatavu.rapurc.api.impl.translate

import fi.metatavu.rapurc.api.persistence.model.LocalizedValue
import javax.enterprise.context.ApplicationScoped

/**
 * Translator for translating JPA localized values into REST objects
 */
@ApplicationScoped
class LocalizedValueTranslator : AbstractTranslator<LocalizedValue, fi.metatavu.rapurc.api.model.LocalizedValue>() {

    override fun translate(entity: LocalizedValue): fi.metatavu.rapurc.api.model.LocalizedValue {
        val localizedValue = fi.metatavu.rapurc.api.model.LocalizedValue()
        localizedValue.value = entity.value
        localizedValue.language = entity.language
        return localizedValue
    }

}
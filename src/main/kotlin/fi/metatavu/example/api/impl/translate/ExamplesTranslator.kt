package fi.metatavu.example.api.impl.translate

import fi.metatavu.example.api.persistence.model.Example
import javax.enterprise.context.ApplicationScoped

/**
 * Translator class for Examples
 */
@ApplicationScoped
class ExamplesTranslator: AbstractTranslator<Example, fi.metatavu.example.api.model.Example>() {

    override fun translate(entity: Example): fi.metatavu.example.api.model.Example {
        val translated = fi.metatavu.example.api.model.Example()
        translated.id = entity.id
        translated.name = entity.name
        translated.amount = entity.amount

        return translated
    }

}
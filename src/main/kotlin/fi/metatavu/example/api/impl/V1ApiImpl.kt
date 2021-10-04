package fi.metatavu.example.api.impl

import fi.metatavu.example.api.example.ExamplesController
import fi.metatavu.example.api.impl.translate.ExamplesTranslator
import fi.metatavu.example.api.spec.V1Api
import java.util.*
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.core.Response

/**
 * V1 API implementation
 *
 * @author Jari Nykänen
 */
@RequestScoped
@Transactional
class V1ApiImpl: V1Api, AbstractApi()  {

    @Inject
    private lateinit var examplesController: ExamplesController

    @Inject
    private lateinit var examplesTranslator: ExamplesTranslator

    /* EXAMPLES */

    override fun listExamples(): Response {
        loggedUserId ?: return createUnauthorized(NO_VALID_USER_MESSAGE)
        val examples = examplesController.list()

        return createOk(examples.map(examplesTranslator::translate))
    }

    override fun createExample(example: fi.metatavu.example.api.model.Example?): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_VALID_USER_MESSAGE)
        example ?: return createBadRequest("Missing request body!")

        val name = example.name
        val amount = example.amount

        val createdExample = examplesController.create(
            name = name,
            amount = amount,
            creatorId = userId
        )

        return createOk(examplesTranslator.translate(createdExample))
    }

    override fun findExample(exampleId: UUID?): Response {
        loggedUserId ?: return createUnauthorized(NO_VALID_USER_MESSAGE)
        exampleId ?: return createBadRequest("Missing example ID from request!")

        val foundExample = examplesController.findExample(exampleId) ?: return createNotFound("Example with ID $exampleId could not be found")
        return createOk(examplesTranslator.translate(foundExample))

    }

    override fun updateExample(exampleId: UUID?, example: fi.metatavu.example.api.model.Example?): Response {
        val userId = loggedUserId ?: return createUnauthorized(NO_VALID_USER_MESSAGE)
        exampleId ?: return createBadRequest("Missing example ID from request!")
        example ?: return createBadRequest("Missing request body!")

        val name = example.name
        val amount = example.amount

        val exampleToUpdate = examplesController.findExample(exampleId) ?: return createNotFound("Example with ID $exampleId could not be found")
        val updatedExample = examplesController.update(
            example = exampleToUpdate,
            name = name,
            amount = amount,
            modifierId = userId
        )

        return createOk(examplesTranslator.translate(updatedExample))
    }

    override fun deleteExample(exampleId: UUID?): Response {
        loggedUserId ?: return createUnauthorized(NO_VALID_USER_MESSAGE)
        exampleId ?: return createBadRequest("Missing example ID from request!")

        val foundExample = examplesController.findExample(exampleId) ?: return createNotFound("Example with ID $exampleId could not be found")
        examplesController.deleteExample(foundExample)

        return createNoContent()
    }

    override fun ping(): Response {
        return createOk("pong")
    }

    companion object {
        const val NO_VALID_USER_MESSAGE = "No valid user!"
    }

}

package fi.metatavu.example.api.test.functional.impl

import fi.metatavu.example.api.client.apis.ExamplesApi
import fi.metatavu.example.api.client.infrastructure.ApiClient
import fi.metatavu.example.api.client.models.*
import fi.metatavu.example.api.test.functional.TestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import java.util.*

/**
 * Resource for testings Examples API
 *
 * @author Jari Nykänen
 */
class ExamplesTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
):ApiTestBuilderResource<Example, ApiClient?>(testBuilder, apiClient) {

    override fun clean(example: Example) {
        return api.deleteExample(example.id!!)
    }

    override fun getApi(): ExamplesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return ExamplesApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Lists examples
     *
     * @return list of examples
     */
    fun listExamples(): Array<Example> {
        return api.listExamples()
    }

    /**
     * Updates a example
     */
    fun updateExample(exampleId: UUID, example: Example): Example {
        return api.updateExample(exampleId = exampleId, example = example)
    }

    /**
     * Finds example from the API
     *
     * @param exampleId example id
     * @return Found example or null if not found
     */
    fun findExample(exampleId: UUID): Example {
        return api.findExample(exampleId = exampleId)
    }

    /**
     * Deletes a example from the API
     *
     * @param exampleId example id
     */
    fun deleteExample(exampleId: UUID) {
        api.deleteExample(exampleId = exampleId)
        removeCloseable{ closable: Any ->
            if (closable !is Example) {
                return@removeCloseable false
            }

            val closeableExample: Example = closable
            closeableExample.id!! == exampleId
        }
    }

    /**
     * Creates a example with default values
     *
     * @return Created example
     */
    fun createDefaultExample(): Example {
        val example = Example(
            name = "name",
            amount = 100
        )

        return createExample(example)
    }

    /**
     * Creates example and adds closable
     *
     * @param example example to create
     * @return created example
     */
    private fun createExample(example: Example): Example {
        val createdExample = api.createExample(example = example)
        addClosable(createdExample)
        return createdExample
    }
}

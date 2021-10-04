package fi.metatavu.example.api.test.functional.auth

import fi.metatavu.example.api.client.infrastructure.ApiClient
import fi.metatavu.example.api.test.functional.TestBuilder
import fi.metatavu.example.api.test.functional.impl.ExamplesTestBuilderResource
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.jaxrs.test.functional.builder.auth.AuthorizedTestBuilderAuthentication


/**
 * Test builder authentication
 *
 * @author Jari Nykänen
 *
 * @param testBuilder test builder instance
 * @param accessTokenProvider access token provider
 */
class TestBuilderAuthentication(
    private val testBuilder: TestBuilder,
    accessTokenProvider: AccessTokenProvider
): AuthorizedTestBuilderAuthentication<ApiClient>(testBuilder, accessTokenProvider) {

    private var accessTokenProvider: AccessTokenProvider? = accessTokenProvider

    var examples: ExamplesTestBuilderResource = ExamplesTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())

    /**
     * Creates a API client
     *
     * @param accessToken access token
     * @return API client
     */
    override fun createClient(accessToken: String): ApiClient {
        val result = ApiClient(testBuilder.settings.apiBasePath)
        ApiClient.accessToken = accessToken
        return result
    }

}
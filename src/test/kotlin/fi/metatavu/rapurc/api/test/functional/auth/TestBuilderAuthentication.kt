package fi.metatavu.rapurc.api.test.functional.auth

import fi.metatavu.rapurc.api.client.infrastructure.ApiClient
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.jaxrs.test.functional.builder.auth.AuthorizedTestBuilderAuthentication
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.resources.BuildingTestBuilderResource
import fi.metatavu.rapurc.api.test.functional.resources.OwnerInformationTestBuilderResource
import fi.metatavu.rapurc.api.test.functional.resources.SurveyTestBuilderResource

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
    val surveys: SurveyTestBuilderResource = SurveyTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())
    val owners: OwnerInformationTestBuilderResource = OwnerInformationTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())
    val buildings: BuildingTestBuilderResource = BuildingTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())

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
package fi.metatavu.rapurc.api.test.functional.resources

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.rapurc.api.client.apis.SurveyAttachmentsApi
import fi.metatavu.rapurc.api.client.infrastructure.ApiClient
import fi.metatavu.rapurc.api.client.infrastructure.ClientException
import fi.metatavu.rapurc.api.client.models.Attachment
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.impl.ApiTestBuilderResource
import org.junit.Assert
import java.util.*

/**
 * Test builder resource for Survey Attachments API
 */
class AttachmentTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<Attachment, ApiClient?>(testBuilder, apiClient) {

    val attachment = Attachment(
        name = "attachment",
        url = "https://s3.jpg"
    )

    override fun getApi(): SurveyAttachmentsApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return SurveyAttachmentsApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Creates new attachment object
     *
     * @param surveyId survey id
     * @param attachment attachment
     * @return created attachment
     */
    fun create(surveyId: UUID?, attachment: Attachment): Attachment {
        return addClosable(api.createSurveyAttachment(surveyId!!, attachment))
    }

    /**
     * Finds attachment
     *
     * @param surveyId survey id
     * @param attachmentId material id
     * @return found OwnerInformation
     */
    fun findAttachment(surveyId: UUID?, attachmentId: UUID?): Attachment {
        return api.findSurveyAttachment(surveyId!!, attachmentId!!)
    }

    /**
     * Lists all attachment entries for a survey
     *
     * @param surveyId survey id
     * @return attachment list
     */
    fun list(surveyId: UUID?): Array<Attachment> {
        return api.listSurveyAttachments(surveyId!!)
    }

    /**
     * Updates attachment
     *
     * @param surveyId survey id
     * @param attachmentId attachment id
     * @param attachment attachment
     * @return updated attachment
     */
    fun update(surveyId: UUID?, attachmentId: UUID?, attachment: Attachment?): Attachment {
        return api.updateSurveyAttachment(surveyId!!, attachmentId!!, attachment!!)
    }

    /**
     * Deletes attachment from the API
     *
     * @param attachment attachment to delete
     */
    fun delete(attachment: Attachment) {
        api.deleteSurveyAttachment(attachment.surveyId!!, attachment.id!!)
        removeCloseable { closable: Any? ->
            if (closable !is Attachment) {
                return@removeCloseable false
            }
            closable.id == attachment.id
        }
    }


    /**
     * Asserts that finding attachment fails with the status
     *
     * @param expectedStatus expected status
     * @param surveyId survey id
     * @param attachmentId attachment id
     */
    fun assertFindFailStatus(expectedStatus: Int, surveyId: UUID?, attachmentId: UUID?) {
        try {
            api.findSurveyAttachment(surveyId!!, attachmentId!!)
            Assert.fail(String.format("Expected find to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts create status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param surveyId survey id
     * @param attachment attachment
     */
    fun assertCreateFailStatus(expectedStatus: Int, surveyId: UUID?, attachment: Attachment) {
        try {
            create(surveyId!!, attachment)
            Assert.fail(String.format("Expected create to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts update status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param attachmentId attachment id
     * @param attachment attachment
     */
    fun assertUpdateFailStatus(expectedStatus: Int, surveyId: UUID?, attachmentId: UUID?, attachment: Attachment) {
        try {
            update(surveyId, attachmentId, attachment)
            Assert.fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts delete status fails with given status code
     *
     * @param expectedStatus expected status code
     * @param surveyId survey id
     * @param attachmentId attachment id
     */
    fun assertDeleteFailStatus(expectedStatus: Int, surveyId: UUID?, attachmentId: UUID?) {
        try {
            api.deleteSurveyAttachment(surveyId!!, attachmentId!!)
            Assert.fail(String.format("Expected delete to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    /**
     * Asserts list status fails with given status code
     *
     * @param expectedStatus expected status code
     */
    fun assertListFailStatus(expectedStatus: Int, surveyId: UUID?) {
        try {
            api.listSurveyAttachments(surveyId!!)
            Assert.fail(String.format("Expected list to fail with status %d", expectedStatus))
        } catch (e: ClientException) {
            Assert.assertEquals(expectedStatus.toLong(), e.statusCode.toLong())
        }
    }

    override fun clean(attachment: Attachment) {
        delete(attachment)
    }
}
package fi.metatavu.rapurc.api.test.functional.tests

import fi.metatavu.rapurc.api.client.models.SurveyStatus
import fi.metatavu.rapurc.api.test.functional.TestBuilder
import fi.metatavu.rapurc.api.test.functional.resources.KeycloakTestResource
import fi.metatavu.rapurc.api.test.functional.resources.MysqlTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Functional test for surveys
 *
 * @author Antti Leppä
 * @author Jari Nykänen
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlTestResource::class)
)
class SurveyTestIT {

    /**
     * Tests survey creation
     */
    @Test
    fun create() {
        TestBuilder().use {
            val createdSurvey = it.userA.surveys.create()
            assertNotNull(createdSurvey)

            it.admin.surveys.assertCreateFailStatus(403, SurveyStatus.dRAFT)
        }
    }

    /**
     * Tests survey listing
     */
    @Test
    fun list() {
        TestBuilder().use {
            val emptyList = it.admin.surveys.listSurveys(
                firstResult = null,
                maxResult = null,
                address = null,
                status = null
            )

            assertEquals(0, emptyList.size)

            val survey = it.userA.surveys.create()
            it.userA.surveys.create()
            it.userB.surveys.create()

            val listWithThreeItems = it.admin.surveys.listSurveys(
                firstResult = null,
                maxResult = null,
                address = null,
                status = null
            )

            assertEquals(3, listWithThreeItems.size)

            it.admin.surveys.updateSurvey(
                survey.copy(status = SurveyStatus.dONE)
            )

            val listWithDraftStatus = it.admin.surveys.listSurveys(
                firstResult = null,
                maxResult = null,
                address = null,
                status = SurveyStatus.dRAFT
            )

            assertEquals(2, listWithDraftStatus.size)

            val listWithDoneStatus = it.admin.surveys.listSurveys(
                firstResult = null,
                maxResult = null,
                address = null,
                status = SurveyStatus.dONE
            )

            assertEquals(1, listWithDoneStatus.size)

            val listByGroupA = it.userA.surveys.listSurveys(
                firstResult = null,
                maxResult = null,
                address = null,
                status = null
            )
            assertEquals(2, listByGroupA.size)

            val listByGroupB = it.userB.surveys.listSurveys(
                firstResult = null,
                maxResult = null,
                address = null,
                status = null
            )
            assertEquals(1, listByGroupB.size)
        }
    }

    /**
     * Tests finding survey
     */
    @Test
    fun find() {
        TestBuilder().use {
            val survey = it.userA.surveys.create()
            val foundSurvey = it.userA.surveys.findSurvey(surveyId = survey.id!!)

            assertEquals(survey.id, foundSurvey.id)
            assertEquals(survey.status, foundSurvey.status)

            it.admin.surveys.assertFindFailStatus(expectedStatus = 404, surveyId = UUID.randomUUID())
            it.userB.surveys.assertFindFailStatus(expectedStatus = 403, surveyId = survey.id)
        }
    }

    /**
     * Tests survey updating
     */
    @Test
    fun update() {
        TestBuilder().use {
            val survey = it.userA.surveys.create()
            val updateData = survey.copy(status = SurveyStatus.dONE)
            val updatedSurvey = it.userA.surveys.updateSurvey(body = updateData)

            assertEquals(survey.id, updatedSurvey.id)
            assertNotEquals(survey.status, updatedSurvey.status)
            assertEquals(SurveyStatus.dONE, updatedSurvey.status)

            it.userB.surveys.assertUpdateFailStatus(expectedStatus = 403, updateData)
            it.admin.surveys.assertUpdateFailStatus(expectedStatus = 404, survey.copy(id = UUID.randomUUID()))
        }
    }

    /**
     * Tests survey deletion
     */
    @Test
    fun delete() {
        TestBuilder().use {
            val emptyList = it.admin.surveys.listSurveys(
                firstResult = null,
                maxResult = null,
                address = null,
                status = null
            )

            assertEquals(0, emptyList.size)

            val survey = it.userA.surveys.create()
            val anotherSurvey = it.userB.surveys.create()

            val listWithTwoItems = it.admin.surveys.listSurveys(
                firstResult = null,
                maxResult = null,
                address = null,
                status = null
            )

            assertEquals(2, listWithTwoItems.size)

            it.admin.surveys.assertDeleteFailStatus(expectedStatus = 404, survey.copy(id = UUID.randomUUID()))
            it.userB.surveys.assertDeleteFailStatus(expectedStatus = 403, survey)

            it.admin.surveys.delete(survey = survey)
            it.userB.surveys.delete(survey = anotherSurvey)

            it.admin.surveys.assertDeleteFailStatus(expectedStatus = 404, survey)

            val finalEmptyList = it.admin.surveys.listSurveys(
                firstResult = null,
                maxResult = null,
                address = null,
                status = null
            )

            assertEquals(0, finalEmptyList.size)
        }
    }
}
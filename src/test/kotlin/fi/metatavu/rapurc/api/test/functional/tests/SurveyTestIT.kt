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
            val createdSurvey = it.admin().surveys.create()
            assertNotNull(createdSurvey)
        }
    }

    /**
     * Tests survey listing
     */
    @Test
    fun list() {
        TestBuilder().use {
            val emptyList = it.admin().surveys.listSurveys(
                firstResult = null,
                maxResult = null,
                address = null,
                status = null
            )

            assertEquals(0, emptyList.size)

            val survey = it.admin().surveys.create()
            it.admin().surveys.create()
            it.admin().surveys.create()

            val listWithThreeItems = it.admin().surveys.listSurveys(
                firstResult = null,
                maxResult = null,
                address = null,
                status = null
            )

            assertEquals(3, listWithThreeItems.size)

            it.admin().surveys.updateSurvey(
                survey.copy(status = SurveyStatus.dONE)
            )

            val listWithDraftStatus = it.admin().surveys.listSurveys(
                firstResult = null,
                maxResult = null,
                address = null,
                status = SurveyStatus.dRAFT
            )

            assertEquals(2, listWithDraftStatus.size)

            val listWithDoneStatus = it.admin().surveys.listSurveys(
                firstResult = null,
                maxResult = null,
                address = null,
                status = SurveyStatus.dONE
            )

            assertEquals(1, listWithDoneStatus.size)
        }
    }

    /**
     * Tests finding survey
     */
    @Test
    fun find() {
        TestBuilder().use {
            val survey = it.admin().surveys.create()
            val foundSurvey = it.admin().surveys.findSurvey(surveyId = survey.id!!)

            assertEquals(survey.id, foundSurvey.id)
            assertEquals(survey.status, foundSurvey.status)

            it.admin().surveys.assertFindFailStatus(expectedStatus = 404, surveyId = UUID.randomUUID())
        }
    }

    /**
     * Tests survey updating
     */
    @Test
    fun update() {
        TestBuilder().use {
            val survey = it.admin().surveys.create()
            val updatedSurvey = it.admin().surveys.updateSurvey(body = survey.copy(status = SurveyStatus.dONE))

            assertEquals(survey.id, updatedSurvey.id)
            assertNotEquals(survey.status, updatedSurvey.status)
            assertEquals(SurveyStatus.dONE, updatedSurvey.status)

            it.admin().surveys.assertUpdateFailStatus(expectedStatus = 404, survey.copy(id = UUID.randomUUID()))

        }
    }

    /**
     * Tests survey deletion
     */
    @Test
    fun delete() {
        TestBuilder().use {
            val emptyList = it.admin().surveys.listSurveys(
                firstResult = null,
                maxResult = null,
                address = null,
                status = null
            )

            assertEquals(0, emptyList.size)

            val survey = it.admin().surveys.create()
            val anotherSurvey = it.admin().surveys.create()

            val listWithTwoItems = it.admin().surveys.listSurveys(
                firstResult = null,
                maxResult = null,
                address = null,
                status = null
            )

            assertEquals(2, listWithTwoItems.size)

            it.admin().surveys.assertDeleteFailStatus(expectedStatus = 404, survey.copy(id = UUID.randomUUID()))

            it.admin().surveys.delete(survey = survey)
            it.admin().surveys.delete(survey = anotherSurvey)

            it.admin().surveys.assertDeleteFailStatus(expectedStatus = 404, survey)
        }
    }
}
package fi.metatavu.rapurc.api.persistence.dao

import fi.metatavu.rapurc.api.model.SurveyStatus
import fi.metatavu.rapurc.api.persistence.model.Survey
import fi.metatavu.rapurc.api.persistence.model.Survey_
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.TypedQuery
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * DAO class for survey
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class SurveyDAO: AbstractDAO<Survey>() {

    /**
     * Creates new Survey
     *
     * @param id id
     * @param status status
     * @param keycloakGroupId keycloak group id
     * @param creatorId creator's id
     * @param lastModifierId last modifier's id
     * @return created survey
     */
    fun create(id: UUID, status: SurveyStatus, keycloakGroupId: UUID, creatorId: UUID, lastModifierId: UUID): Survey {
        val survey = Survey()
        survey.id = id
        survey.status = status
        survey.keycloakGroupId = keycloakGroupId
        survey.creatorId = creatorId
        survey.lastModifierId = lastModifierId
        return persist(survey)
    }

    /**
     * Lists surveys with given filters
     *
     * @param firstResult first result
     * @param maxResults maximum amount of results
     * @param address filter by address
     * @param status filter by status
     * @param keycloakGroupId filter by group id
     * @return List of visitor variables
     */
    fun list(firstResult: Int, maxResults: Int, address: String?, status: SurveyStatus?, keycloakGroupId: UUID?): List<Survey> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria: CriteriaQuery<Survey> = criteriaBuilder.createQuery(Survey::class.java)
        val root: Root<Survey> = criteria.from(Survey::class.java)

        val restrictions = ArrayList<Predicate>()

        if (address != null) {
            TODO("Not yet implemented")
        }

        if (status != null) {
            restrictions.add(criteriaBuilder.equal(root.get(Survey_.status), status))
        }

        if (keycloakGroupId != null) {
            restrictions.add(criteriaBuilder.equal(root.get(Survey_.keycloakGroupId), keycloakGroupId))
        }

        criteria.select(root)
        criteria.where(*restrictions.toTypedArray())
        val query: TypedQuery<Survey> = entityManager.createQuery(criteria)
        query.firstResult = firstResult
        query.maxResults = maxResults
        return query.resultList
    }

    /**
     * Updates status
     *
     * @param survey survey to update
     * @param status new status
     * @param lastModifierId last modifier's id
     * @return updated survey
     */
    fun updateStatus(survey: Survey, status: SurveyStatus, lastModifierId: UUID): Survey {
        survey.status = status
        survey.lastModifierId = lastModifierId
        return persist(survey)
    }

}
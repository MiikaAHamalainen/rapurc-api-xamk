package fi.metatavu.rapurc.api.impl

import fi.metatavu.example.api.spec.V1Api
import javax.enterprise.context.RequestScoped
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


    override fun ping(): Response {
        return createOk("pong")
    }

    companion object {
        const val NO_VALID_USER_MESSAGE = "No valid user!"
    }

}

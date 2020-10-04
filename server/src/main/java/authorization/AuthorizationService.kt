package authorization

import core.NotAuthorizedException

class AuthorizationService {
    fun getUserIDFromAuthToken(authToken: String): String {
        // If userID is null, that means the authToken wasn't valid so we should throw an exception.
        val authorizationDAO = AuthorizationDAO()
        return authorizationDAO.getUserIDFromAuthToken(authToken) ?: throw NotAuthorizedException()
    }

    fun getGroupIDFromAuthToken(authToken: String): String {
        // If groupID is null, that means the authToken wasn't valid so we should throw an exception.
        val authorizationDAO = AuthorizationDAO()
        return authorizationDAO.getGroupIDFromAuthToken(authToken) ?: throw NotAuthorizedException()
    }
}
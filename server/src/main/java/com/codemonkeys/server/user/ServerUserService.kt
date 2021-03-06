package com.codemonkeys.server.user

import com.codemonkeys.shared.user.IUserService
import com.codemonkeys.shared.user.User
import com.codemonkeys.shared.user.requests.CreateUserRequest
import com.codemonkeys.shared.user.requests.LoginUserRequest
import com.codemonkeys.shared.user.requests.LogoutUserRequest
import com.codemonkeys.shared.user.requests.UpdateUserRequest
import com.codemonkeys.shared.user.responses.CreateUserResponse
import com.codemonkeys.shared.user.responses.LoginUserResponse
import com.codemonkeys.shared.user.responses.LogoutUserResponse
import com.codemonkeys.shared.user.responses.UpdateUserResponse

class ServerUserService : IUserService {
    override fun createUser(user: User): CreateUserResponse {
        val userDAO = UserDynamoDAO()
        val request = CreateUserRequest(user)
        return userDAO.createUser(request)
    }

    override fun loginUser(username: String, password: String): LoginUserResponse {
        val userDAO = UserDynamoDAO()
        val request = LoginUserRequest(username, password)
        return userDAO.loginUser(request)
    }

    override fun logoutUser(authToken: String): LogoutUserResponse {
        val userDAO = UserDynamoDAO()
        val request = LogoutUserRequest(authToken)
        return userDAO.logoutUser(request)
    }

    override fun updateUser(authToken: String, currentPassword: String, newPassword: String): UpdateUserResponse {
        val userDAO = UserDynamoDAO()
        val request = UpdateUserRequest(authToken, currentPassword, newPassword)
        return userDAO.updateUser(request)
    }
}
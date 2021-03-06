package com.codemonkeys.server.user

import com.codemonkeys.server.BaseDynamoTest
import com.codemonkeys.server.authorization.AuthorizationTestResources
import org.junit.Assert.*
import org.junit.Test
import com.codemonkeys.server.user.handlers.CreateUserHandler
import com.codemonkeys.server.user.handlers.LoginUserHandler
import com.codemonkeys.server.user.handlers.LogoutUserHandler
import com.codemonkeys.server.user.handlers.UpdateUserHandler
import com.codemonkeys.shared.user.requests.CreateUserRequest
import com.codemonkeys.shared.user.requests.LoginUserRequest
import com.codemonkeys.shared.user.requests.LogoutUserRequest
import com.codemonkeys.shared.user.requests.UpdateUserRequest

class TestUserHandlers : BaseDynamoTest() {

    @Test
    fun testCreateUser() {
        val createUserHandler = CreateUserHandler()
        val request =
            CreateUserRequest(UserTestResources.GROUP_1_NEW_USER_1)
        val response = createUserHandler.handle(request)

        assertFalse(response.didErrorOccur)
        assertNull(response.errorMessage)

        val request2 =
            CreateUserRequest(UserTestResources.GROUP_1_NEW_USER_BAD_GROUP)
        val response2 = createUserHandler.handle(request2)

        assertTrue(response2.didErrorOccur)
        assertNotNull(response2.errorMessage)

        val request3 =
            CreateUserRequest(UserTestResources.GROUP_1_NEW_USER_2)
        val response3 = createUserHandler.handle(request3)

        assertFalse(response3.didErrorOccur)
        assertNull(response3.errorMessage)
    }

    @Test
    fun testUpdateUser() {
        val handler = UpdateUserHandler()
        val request = UpdateUserRequest(
            AuthorizationTestResources.GROUP_1_USER_AUTHTOKEN,
            UserTestResources.GROUP_1_USER_1_PASSWORD,
            UserTestResources.GROUP_1_USER_1_NEW_PASSWORD
        )
        val response = handler.handle(request)

        assertFalse(response.didErrorOccur)
        assertNull(response.errorMessage)

        val request2 = UpdateUserRequest(
            AuthorizationTestResources.INVALID_AUTHTOKEN,
            UserTestResources.GROUP_1_USER_1_PASSWORD,
            UserTestResources.GROUP_1_USER_1_NEW_PASSWORD
        )
        val response2 = handler.handle(request2)

        assertTrue(response2.didErrorOccur)
        assertNotNull(response2.errorMessage)
    }

    @Test
    fun testLoginUser() {
        val handler = LoginUserHandler()
        val request = LoginUserRequest(
            UserTestResources.GROUP_1_USER_1_USERNAME,
            UserTestResources.GROUP_1_USER_1_PASSWORD
        )
        val response = handler.handle(request)

        assertFalse(response.didErrorOccur)
        assertNull(response.errorMessage)

        val request2 = LoginUserRequest(
            UserTestResources.GROUP_1_USER_1_USERNAME,
            UserTestResources.INVALID_PASSWORD
        )
        val response2 = handler.handle(request2)

        assertTrue(response2.didErrorOccur)
        assertNotNull(response2.errorMessage)

        val request3 = LoginUserRequest(
            UserTestResources.INVALID_USERNAME,
            UserTestResources.GROUP_1_USER_1_PASSWORD
        )
        val response3 = handler.handle(request3)

        assertTrue(response3.didErrorOccur)
        assertNotNull(response3.errorMessage)
    }

    @Test
    fun testLogoutUser() {
        val handler = LogoutUserHandler()
        val request =
            LogoutUserRequest(AuthorizationTestResources.GROUP_1_USER_AUTHTOKEN)
        val response = handler.handle(request)

        assertFalse(response.didErrorOccur)
        assertNull(response.errorMessage)

        val request2 =
            LogoutUserRequest(AuthorizationTestResources.INVALID_AUTHTOKEN)
        val response2 = handler.handle(request2)

        assertFalse(response2.didErrorOccur)
        assertNull(response2.errorMessage)
    }
}
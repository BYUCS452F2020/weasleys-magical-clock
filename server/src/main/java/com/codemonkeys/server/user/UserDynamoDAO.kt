package com.codemonkeys.server.user

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.KeyAttribute
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import com.codemonkeys.server.core.dao.BaseDynamoDAO
import com.codemonkeys.shared.user.User
import com.codemonkeys.shared.user.requests.CreateUserRequest
import com.codemonkeys.shared.user.requests.LoginUserRequest
import com.codemonkeys.shared.user.requests.LogoutUserRequest
import com.codemonkeys.shared.user.requests.UpdateUserRequest
import com.codemonkeys.shared.user.responses.CreateUserResponse
import com.codemonkeys.shared.user.responses.LoginUserResponse
import com.codemonkeys.shared.user.responses.LogoutUserResponse
import com.codemonkeys.shared.user.responses.UpdateUserResponse
import java.util.*

class UserDynamoDAO : BaseDynamoDAO(), IUserDAO {
    override fun createUser(request: CreateUserRequest): CreateUserResponse {
        this.useDynamoConnection {
            val table = it.getTable(TABLE_NAME)

            table.putItem(
                Item()
                    .withPrimaryKey("PK", request.user.groupID)
                    .withString("SK", "USER-$request.user.userID")
                    .withString("userName", request.user.userName)
                    .withString("passWord", request.user.password)
                    .withInt("clockHandIndex", request.user.clockHandIndex)
                    .withString("currentZoneID", request.user.currentZoneID)
            )

            val token = UUID.randomUUID().toString()

            table.putItem(
                Item()
                    .withPrimaryKey("PK", "TOKEN-$token")
                    .withString("SK", "Token")
                    .withString("userID", request.user.userID)
                    .withString("groupID", request.user.groupID)
                    .withString("authToken", token)
            )

            return CreateUserResponse(token)
        }
        return CreateUserResponse(null, "Error inserting user")
    }

    override fun getUser(userID: String): User? {
        this.useDynamoConnection {
            val table = it.getTable(TABLE_NAME)

            val spec = QuerySpec()
                .withKeyConditionExpression("SK = :sk")
                .withValueMap(
                    ValueMap()
                        .withString(":sk", "USER-$userID")
                )

            val results = table.query(spec)
            return this.getQueryResults(User::class.java, results)[0]
        }
        return null;
    }

    override fun loginUser(request: LoginUserRequest): LoginUserResponse {
        this.useDynamoConnection {
            val table = it.getTable(TABLE_NAME)
            val index = table.getIndex("userName-index")

            val spec = QuerySpec()
                .withKeyConditionExpression("userName = :userName")
                .withValueMap(
                    ValueMap()
                        .withString(":userName", request.userName)
                )

            val results = index.query(spec)
            println(results.toList().toString())
            val users = this.getQueryResults(User::class.java, results)


            if (users.count() == 0) {
                return LoginUserResponse(null, "Invalid username")
            }

            val user = users[0]

            return if (user.password == request.password) {
                val token = UUID.randomUUID().toString()

                table.putItem(
                    Item()
                        .withPrimaryKey("PK", "TOKEN-$token")
                        .withString("SK", "Token")
                        .withString("userID", user.userID)
                        .withString("groupID", user.groupID)
                        .withString("authToken", token)
                )

                LoginUserResponse(token)
            } else {
                LoginUserResponse(null, "Invalid credentials")
            }
        }
        return LoginUserResponse(null, "Error accessing database")
    }

    override fun logoutUser(request: LogoutUserRequest): LogoutUserResponse {
        this.useDynamoConnection {
            val table = it.getTable(TABLE_NAME)

            val spec = DeleteItemSpec()
                .withPrimaryKey(
                    KeyAttribute("PK","TOKEN-${request.authToken}"),
                    KeyAttribute("SK", "Token")
                    )

            return try {
                table.deleteItem(spec)
                LogoutUserResponse()
            } catch (e: Exception) {
                println(e.message)
                LogoutUserResponse("Error removing authorization")
            }
        }
        return LogoutUserResponse("Error accessing database")
    }

    override fun updateUser(request: UpdateUserRequest): UpdateUserResponse {
        this.useDynamoConnection {
            val table = it.getTable(TABLE_NAME)

            val spec = QuerySpec()
                .withKeyConditionExpression("PK = :pk")
                .withValueMap(
                    ValueMap()
                        .withString(":pk", "TOKEN-${request.authToken}")
                )

            val results = table.query(spec)

            if (results.count() == 0) {
                return UpdateUserResponse("Invalid token")
            }
            println(results.toList().toString())
            println(results.toList()[0].get("userID"))

            val uspec = UpdateItemSpec()
                .withPrimaryKey(
                    KeyAttribute("PK","TOKEN-${request.authToken}"),
                    KeyAttribute("SK", "USER-${results.toList()[0].get("userID")}")
                )
                .addAttributeUpdate(AttributeUpdate("password").put(request.newPassword)
                )

            try {
                table.updateItem(uspec)
            } catch (e: java.lang.Exception) {
                return UpdateUserResponse("Error updating database")
            }
            return UpdateUserResponse()
        }
        return UpdateUserResponse("Faack");
    }
}
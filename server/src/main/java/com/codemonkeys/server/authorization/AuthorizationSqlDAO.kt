package com.codemonkeys.server.authorization

import com.codemonkeys.server.core.dao.BaseSqlDAO

class AuthorizationSqlDAO : BaseSqlDAO(), IAuthorizationDAO {
    private val DELETE_AUTHTOKEN_SQL = """
        DELETE FROM AuthToken
            WHERE authToken = ?;
    """
    override fun deleteAuthToken(authToken: String): Boolean {
        val connection = this.openConnection()
        val statement = connection.prepareStatement(DELETE_AUTHTOKEN_SQL)
        statement.setString(1, authToken)

        return try {
            val resultSet = statement.execute()
            connection.commit()
            true
        } catch(e: Exception) {
            false
        } finally {
            this.closeConnection(connection)
        }
    }

    private val INSERT_AUTHTOKEN_SQL = """
        INSERT INTO AuthToken(authToken, userID, groupID)
            VALUES(?, ?, ?);
    """

    override fun insertAuthToken(authToken:String, userID: String, groupID: String?): Boolean {
        val connection = this.openConnection()

        val statement = connection.prepareStatement(INSERT_AUTHTOKEN_SQL)
        statement.setString(1, authToken)
        statement.setString(2, userID)
        statement.setString(3, groupID)
        return try {
            val resultSet = statement.execute()
            connection.commit()
            true
        } catch(e: Exception) {
            false
        } finally {
            this.closeConnection(connection)
        }
    }

    private val GET_USERID_SQL = """
        SELECT userID
            FROM AuthToken
            WHERE authToken = ?;
    """
    // If the authToken belongs to a User, returns the userID of that user.
    // If the authToken belongs to either a Group or nobody, returns null.
    override fun getUserIDFromAuthToken(authToken: String): String? {
        val connection = this.openConnection()

        val statement = connection.prepareStatement(GET_USERID_SQL)
        statement.setString(1, authToken)
        val resultSet = statement.executeQuery()
        val userIDs = this.getSimpleQueryResults<String>(resultSet)

        this.closeConnection(connection)
        return userIDs.firstOrNull()
    }

    private val GET_GROUPID_SQL = """
        SELECT IFNULL(u.groupID, a.groupID)
            FROM AuthToken a
                LEFT JOIN User u ON u.userID=a.userID
            WHERE authToken = ?;
    """
    // If the authToken belongs to a Group, returns the Group's groupID.
    // If the authToken belongs to a User, returns the groupID that the user belongs to.
    // If the authToken belongs to neither, returns null.
    override fun getGroupIDFromAuthToken(authToken: String): String? {
        val connection = this.openConnection()

        val statement = connection.prepareStatement(GET_GROUPID_SQL)
        statement.setString(1, authToken)
        val resultSet = statement.executeQuery()
        val groupIDs = this.getSimpleQueryResults<String>(resultSet)

        this.closeConnection(connection)
        return groupIDs.firstOrNull()
    }
}

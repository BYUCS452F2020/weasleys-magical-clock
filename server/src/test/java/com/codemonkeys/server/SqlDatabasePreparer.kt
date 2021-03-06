package com.codemonkeys.server

import com.codemonkeys.server.core.dao.BaseSqlDAO

class SqlDatabasePreparer: BaseSqlDAO() {
    // https://stackoverflow.com/a/42740416/6634972
    fun prepareDatabase() {
        val createDatabaseSQL = SqlDatabasePreparer::class.java.getResource("/DatabaseSQL/create_database.sql").readText()
        val createTestDataSQL = SqlDatabasePreparer::class.java.getResource("/DatabaseSQL/create_test_data.sql").readText()

        val connection = this.openConnection()
        connection.autoCommit = false
        try {
            this.executeBatch(connection, createDatabaseSQL)
            this.executeBatch(connection, createTestDataSQL)

            connection.commit()
        } catch (e:Exception) {
            connection.rollback()
            throw e
        }
        finally {
            this.closeConnection(connection)
        }
    }
}
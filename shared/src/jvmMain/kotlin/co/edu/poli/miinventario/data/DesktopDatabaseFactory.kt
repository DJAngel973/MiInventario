package co.edu.poli.miinventario.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import co.edu.poli.miinventario.database.InventoryDatabase
import java.io.File
import app.cash.sqldelight.db.QueryResult

class DesktopDatabaseFactory {

    fun create(): InventoryDatabase {
        val databaseDirectory = File(
            System.getProperty("user.home"),
            ".miinventario"
        )

        if (!databaseDirectory.exists()) {
            check(databaseDirectory.mkdirs()) {
                "No se pudo crear el directorio de la base de datos"
            }
        }

        val databaseFile = File(
            databaseDirectory,
            "miinventario.db"
        )

        val driver = JdbcSqliteDriver(
            url = "jdbc:sqlite:${databaseFile.absolutePath}"
        )

        try {
            val schemaVersion = driver.executeQuery(
                identifier = null,
                sql = "PRAGMA user_version",
                mapper = { cursor ->
                    QueryResult.Value(
                        if (cursor.next().value) {
                            cursor.getLong(0) ?: 0L
                        } else {
                            0L
                        }
                    )
                },
                parameters = 0
            ).value

            when (schemaVersion) {
                0L -> {
                    InventoryDatabase.Schema.create(driver)
                    driver.execute(
                        identifier = null,
                        sql = "PRAGMA user_version = ${InventoryDatabase.Schema.version}",
                        parameters = 0
                    )
                }

                InventoryDatabase.Schema.version -> Unit

                else -> error(
                    "Versión de base de datos no compatible: $schemaVersion"
                )
            }

            return InventoryDatabase(driver)
        } catch (exception: Exception) {
            driver.close()
            throw exception
        }
    }
}

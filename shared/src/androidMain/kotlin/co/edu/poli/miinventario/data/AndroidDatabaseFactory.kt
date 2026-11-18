package co.edu.poli.miinventario.data

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import co.edu.poli.miinventario.database.InventoryDatabase

class AndroidDatabaseFactory(
    private val context: Context
) {
    fun create(): InventoryDatabase {
        val driver = AndroidSqliteDriver(
            schema = InventoryDatabase.Schema,
            context = context.applicationContext,
            name = "miinventario.db"
        )

        return InventoryDatabase(driver)
    }
}
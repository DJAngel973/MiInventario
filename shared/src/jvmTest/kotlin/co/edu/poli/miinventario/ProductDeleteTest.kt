package co.edu.poli.miinventario

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import co.edu.poli.miinventario.data.SqlDelightProductRepository
import co.edu.poli.miinventario.database.InventoryDatabase
import co.edu.poli.miinventario.product.Product
import co.edu.poli.miinventario.product.ProductCategory
import co.edu.poli.miinventario.product.ProductUnit
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProductDeleteTest {
    @Test
    fun deletesOnlySelectedUuidAndRefreshesGetAll() = withDatabase { repository ->
        val selected = product("Arroz", ProductCategory.COCINA)
        val other = product("Jabón", ProductCategory.ASEO)
        repository.save(selected)
        repository.save(other)

        assertTrue(repository.deleteById(selected.id))
        assertEquals(listOf(other), repository.getAll())
    }

    @Test
    fun missingUuidDoesNotDeleteOtherProducts() = withDatabase { repository ->
        val existing = product("Arroz", ProductCategory.COCINA)
        repository.save(existing)

        assertFalse(repository.deleteById("uuid-inexistente"))
        assertEquals(listOf(existing), repository.getAll())
    }

    @Test
    fun deletingSameUuidTwiceReportsMissingOnSecondAttempt() = withDatabase { repository ->
        val existing = product("Arroz", ProductCategory.COCINA)
        repository.save(existing)

        assertTrue(repository.deleteById(existing.id))
        assertFalse(repository.deleteById(existing.id))
        assertTrue(repository.getAll().isEmpty())
    }

    private fun product(name: String, category: ProductCategory) =
        Product(name, category, 2.0, ProductUnit.UNIT, 1.0)

    private fun withDatabase(block: suspend (SqlDelightProductRepository) -> Unit) = runBlocking {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            InventoryDatabase.Schema.create(driver)
            block(SqlDelightProductRepository(InventoryDatabase(driver)))
        } finally {
            driver.close()
        }
    }
}

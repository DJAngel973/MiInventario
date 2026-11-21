package co.edu.poli.miinventario

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import co.edu.poli.miinventario.data.SqlDelightProductRepository
import co.edu.poli.miinventario.database.InventoryDatabase
import co.edu.poli.miinventario.product.Product
import co.edu.poli.miinventario.product.ProductCategory
import co.edu.poli.miinventario.product.ProductQuantityService
import co.edu.poli.miinventario.product.ProductUnit
import co.edu.poli.miinventario.product.canSaveQuantity
import co.edu.poli.miinventario.product.isValidQuantity
import co.edu.poli.miinventario.product.quantityAtSteps
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ProductQuantityTest {
    @Test
    fun unitStepsIncreaseAndDecreaseByWholeNumbers() {
        val product = product(ProductUnit.UNIT, 2.0)
        assertEquals(3.0, quantityAtSteps(product.quantity, 1, product.unit))
        assertEquals(1.0, quantityAtSteps(product.quantity, -1, product.unit))
        assertTrue(isValidQuantity(product, quantityAtSteps(product.quantity, -2, product.unit)))
        assertFalse(isValidQuantity(product, quantityAtSteps(product.quantity, -3, product.unit)))
        assertFalse(isValidQuantity(product, 1.5))
    }

    @Test
    fun measuredUnitsUseHalfStepsAndAllowDecimals() {
        for (unit in listOf(ProductUnit.KILOGRAM, ProductUnit.POUND, ProductUnit.LITER)) {
            val product = product(unit, 1.25)
            assertEquals(1.75, quantityAtSteps(product.quantity, 1, unit))
            assertEquals(0.75, quantityAtSteps(product.quantity, -1, unit))
            assertTrue(isValidQuantity(product, 0.75))
            assertFalse(isValidQuantity(product, -0.25))
        }
    }

    @Test
    fun stepsAreCalculatedFromStoredValueAndCanReturnToIt() {
        val product = product(ProductUnit.LITER, 0.1)
        assertEquals(5.1, quantityAtSteps(product.quantity, 10, product.unit))
        assertEquals(product.quantity, quantityAtSteps(product.quantity, 0, product.unit))
        assertFalse(canSaveQuantity(product, product.quantity))
    }

    @Test
    fun savesOnlyQuantityAndCanReadItBack() = withDatabase { repository ->
        val original = product(ProductUnit.KILOGRAM, 2.0)
        repository.save(original)

        assertTrue(ProductQuantityService(repository).update(original, 2.5))
        assertEquals(listOf(original.copy(quantity = 2.5)), repository.getAll())
    }

    @Test
    fun proposedChangeWithoutSaveDoesNotTouchDatabase() = withDatabase { repository ->
        val original = product(ProductUnit.UNIT, 2.0)
        repository.save(original)
        val proposed = quantityAtSteps(original.quantity, 1, original.unit)

        assertEquals(3.0, proposed)
        assertEquals(listOf(original), repository.getAll())
    }

    @Test
    fun missingUuidDoesNotInsertOrChangeAnotherProduct() = withDatabase { repository ->
        val existing = product(ProductUnit.UNIT, 2.0)
        val missing = product(ProductUnit.UNIT, 1.0)
        repository.save(existing)

        assertFalse(ProductQuantityService(repository).update(missing, 2.0))
        assertEquals(listOf(existing), repository.getAll())
    }

    @Test
    fun invalidQuantityIsRejectedBeforePersistence() = withDatabase { repository ->
        val original = product(ProductUnit.UNIT, 2.0)
        repository.save(original)

        assertFailsWith<IllegalArgumentException> {
            ProductQuantityService(repository).update(original, -1.0)
        }
        assertFailsWith<IllegalArgumentException> {
            ProductQuantityService(repository).update(original, 2.5)
        }
        assertEquals(listOf(original), repository.getAll())
    }

    private fun product(unit: ProductUnit, quantity: Double) = Product(
        name = "Aceite", category = ProductCategory.COCINA, quantity = quantity,
        unit = unit, minimumQuantity = 1.0
    )

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

package co.edu.poli.miinventario

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import co.edu.poli.miinventario.data.SqlDelightProductRepository
import co.edu.poli.miinventario.database.InventoryDatabase
import co.edu.poli.miinventario.product.DuplicateProductException
import co.edu.poli.miinventario.product.Product
import co.edu.poli.miinventario.product.ProductCategory
import co.edu.poli.miinventario.product.ProductUnit
import co.edu.poli.miinventario.product.ProductWriteService
import co.edu.poli.miinventario.ui.state.RegisterProductState
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class ProductWriteServiceTest {
    @Test
    fun rejectsSameNameAndCategoryIgnoringCaseAndOuterWhitespace() = withDatabase { repository ->
        val service = ProductWriteService(repository)
        service.register(product("Jabón", ProductCategory.ASEO))

        val error = assertFailsWith<DuplicateProductException> {
            service.register(product("  jABÓN  ", ProductCategory.ASEO))
        }
        assertEquals(
            "Este producto ya está registrado. Verifica el producto existente o utiliza un nombre más específico.",
            error.message
        )
        assertEquals(1, repository.getAll().size)
    }

    @Test
    fun allowsDifferentNamesAndSameNameInOtherCategory() = withDatabase { repository ->
        val service = ProductWriteService(repository)
        service.register(product("Jabón", ProductCategory.ASEO))
        service.register(product("Detergente", ProductCategory.ASEO))
        service.register(product("jabón", ProductCategory.COCINA))

        assertEquals(3, repository.getAll().size)
    }

    @Test
    fun editKeepsNameAndUuidWithoutAddingRow() = withDatabase { repository ->
        val original = product("Arroz", ProductCategory.COCINA)
        val service = ProductWriteService(repository)
        service.register(original)
        val form = RegisterProductState(original)
        assertEquals(original.name, form.name)
        assertEquals(original.category, form.category)
        assertEquals("2", form.quantity)
        assertEquals(original.unit, form.unit)
        assertEquals("1", form.minimumQuantity)
        form.unit = ProductUnit.LITER
        form.quantity = "5.5"
        form.minimumQuantity = "1.5"

        val updated = assertNotNull(form.validate())
        service.update(updated)

        assertEquals(original.id, updated.id)
        assertEquals(1, repository.getAll().size)
        assertEquals(5.5, repository.getAll().single().quantity)
        assertEquals(1.5, repository.getAll().single().minimumQuantity)
        assertEquals(ProductUnit.LITER, repository.getAll().single().unit)
        assertEquals(original.id, repository.getAll().single().id)
    }

    @Test
    fun editRejectsDuplicateOfAnotherProductButLeavesExistingRows() = withDatabase { repository ->
        val first = product("Arroz", ProductCategory.COCINA)
        val second = product("Aceite", ProductCategory.COCINA)
        val service = ProductWriteService(repository)
        service.register(first)
        service.register(second)

        assertFailsWith<DuplicateProductException> {
            service.update(second.copy(name = " ARROZ "))
        }
        assertEquals(setOf("Arroz", "Aceite"), repository.getAll().map { it.name }.toSet())
    }

    @Test
    fun legacyDuplicatesRemainEditableWithoutChangingTheirNames() = withDatabase { repository ->
        val first = product("Arroz", ProductCategory.COCINA)
        val second = product(" arroz ", ProductCategory.COCINA)
        repository.save(first)
        repository.save(second)

        ProductWriteService(repository).update(second.copy(quantity = 4.0))

        assertEquals(2, repository.getAll().size)
        assertEquals(4.0, repository.getAll().first { it.id == second.id }.quantity)
    }

    @Test
    fun editRejectsCategoryChangeThatCreatesDuplicate() = withDatabase { repository ->
        val aseo = product("Aceite", ProductCategory.ASEO)
        val cocina = product("Aceite", ProductCategory.COCINA)
        repository.save(aseo)
        repository.save(cocina)

        assertFailsWith<DuplicateProductException> {
            ProductWriteService(repository).update(aseo.copy(category = ProductCategory.COCINA))
        }
        assertEquals(ProductCategory.ASEO, repository.getAll().first { it.id == aseo.id }.category)
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

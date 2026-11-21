package co.edu.poli.miinventario

import co.edu.poli.miinventario.product.Product
import co.edu.poli.miinventario.product.ProductCategory
import co.edu.poli.miinventario.product.ProductRepository
import co.edu.poli.miinventario.product.ProductUnit
import co.edu.poli.miinventario.ui.state.ProductListLoader
import co.edu.poli.miinventario.ui.state.ProductListState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ProductListLoaderTest {
    @Test
    fun returnsEmptyWhenRepositoryHasNoProducts() = runBlocking {
        val repository = FakeRepository()

        assertEquals(ProductListState.Empty, ProductListLoader(repository).load())
        assertEquals(1, repository.getAllCalls)
    }

    @Test
    fun returnsAllProductsAndRefreshesOnAnotherLoad() = runBlocking {
        val repository = FakeRepository()
        val loader = ProductListLoader(repository)
        val first = Product("Arroz", ProductCategory.COCINA, 2.0, ProductUnit.UNIT, 1.0)
        val second = Product("Jabón", ProductCategory.ASEO, 0.5, ProductUnit.LITER, 0.25)
        repository.products = listOf(first)

        assertEquals(ProductListState.Content(listOf(first)), loader.load())
        repository.products = listOf(first, second)
        assertEquals(ProductListState.Content(listOf(first, second)), loader.load())
        assertEquals(2, repository.getAllCalls)
    }

    @Test
    fun returnsErrorAndCanRetryAfterFailure() = runBlocking {
        val repository = FakeRepository()
        val loader = ProductListLoader(repository)
        repository.failure = IllegalStateException("database unavailable")

        assertEquals(ProductListState.Error, loader.load())
        repository.failure = null
        assertEquals(ProductListState.Empty, loader.load())
        assertEquals(2, repository.getAllCalls)
    }

    @Test
    fun doesNotTurnCancellationIntoError() = runBlocking {
        val repository = FakeRepository()
        repository.failure = CancellationException("screen closed")

        assertFailsWith<CancellationException> { ProductListLoader(repository).load() }
        Unit
    }

    private class FakeRepository : ProductRepository {
        var products: List<Product> = emptyList()
        var failure: Exception? = null
        var getAllCalls = 0

        override suspend fun save(product: Product) = Unit

        override suspend fun update(product: Product) = Unit

        override suspend fun getAll(): List<Product> {
            getAllCalls++
            failure?.let { throw it }
            return products
        }
    }
}

package co.edu.poli.miinventario.ui.state

import co.edu.poli.miinventario.product.Product
import co.edu.poli.miinventario.product.ProductRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed interface ProductListState {
    data object Loading : ProductListState
    data object Empty : ProductListState
    data class Content(val products: List<Product>) : ProductListState
    data object Error : ProductListState
}

/** Carga el inventario sin vincular el acceso el repositorio con Compose ni con el hilo de la interfaz de usuario. */
class ProductListLoader(private val repository: ProductRepository) {
    suspend fun load(): ProductListState = try {
        val products = withContext(Dispatchers.IO) { repository.getAll() }
        if (products.isEmpty()) ProductListState.Empty else ProductListState.Content(products)
    } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        ProductListState.Error
    }
}

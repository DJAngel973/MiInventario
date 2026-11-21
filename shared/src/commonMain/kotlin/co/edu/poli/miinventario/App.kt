package co.edu.poli.miinventario

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import co.edu.poli.miinventario.product.Product
import co.edu.poli.miinventario.product.ProductCategory
import co.edu.poli.miinventario.product.ProductRepository
import co.edu.poli.miinventario.product.ProductQuantityService
import co.edu.poli.miinventario.product.ProductWriteService
import co.edu.poli.miinventario.ui.screens.HomeScreen
import co.edu.poli.miinventario.ui.screens.EditProductScreen
import co.edu.poli.miinventario.ui.screens.ProductDetailScreen
import co.edu.poli.miinventario.ui.screens.RegisterProductScreen
import co.edu.poli.miinventario.ui.screens.ReplenishmentScreen
import co.edu.poli.miinventario.ui.state.ProductListLoader
import co.edu.poli.miinventario.ui.state.ProductListState
import co.edu.poli.miinventario.ui.theme.MiInventarioTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun App(repository: ProductRepository) {
    val productListLoader = remember(repository) { ProductListLoader(repository) }
    val productWriteService = remember(repository) { ProductWriteService(repository) }
    val productQuantityService = remember(repository) { ProductQuantityService(repository) }
    AppContent(
        productListLoader = productListLoader,
        onSaveProduct = { product ->
            withContext(Dispatchers.IO) {
                productWriteService.register(product)
            }
        },
        onUpdateProduct = { product ->
            withContext(Dispatchers.IO) {
                productWriteService.update(product)
            }
        },
        onDeleteProduct = { id ->
            withContext(Dispatchers.IO) { repository.deleteById(id) }
        },
        onUpdateQuantity = { product, quantity ->
            withContext(Dispatchers.IO) { productQuantityService.update(product, quantity) }
        }
    )
}

@Composable
private fun AppContent(
    productListLoader: ProductListLoader,
    onSaveProduct: suspend (Product) -> Unit,
    onUpdateProduct: suspend (Product) -> Unit,
    onDeleteProduct: suspend (String) -> Boolean,
    onUpdateQuantity: suspend (Product, Double) -> Boolean
) {
    var showRegisterScreen by remember {
        mutableStateOf(false)
    }
    var productListState: ProductListState by remember { mutableStateOf(ProductListState.Loading) }
    var reloadRequest by remember { mutableStateOf(0) }
    var selectedCategory: ProductCategory? by remember { mutableStateOf(null) }
    var editingProduct: Product? by remember { mutableStateOf(null) }
    var selectedProduct: Product? by remember { mutableStateOf(null) }
    var showReplenishmentScreen by remember { mutableStateOf(false) }

    LaunchedEffect(showRegisterScreen, editingProduct, selectedProduct, reloadRequest, productListLoader) {
        if (!showRegisterScreen && editingProduct == null && selectedProduct == null) {
            productListState = ProductListState.Loading
            productListState = productListLoader.load()
        }
    }

    MiInventarioTheme {
        if (showRegisterScreen) {
            RegisterProductScreen(
                onBack = { showRegisterScreen = false },
                onSaveProduct = onSaveProduct
            )
        } else if (editingProduct != null) {
            EditProductScreen(
                product = editingProduct!!,
                onBack = { editingProduct = null },
                onUpdateProduct = onUpdateProduct
            )
        } else if (selectedProduct != null) {
            ProductDetailScreen(
                product = selectedProduct!!,
                onBack = { selectedProduct = null },
                onEdit = {
                    editingProduct = selectedProduct
                    selectedProduct = null
                },
                onDeleteProduct = onDeleteProduct,
                onUpdateQuantity = onUpdateQuantity,
                onQuantitySaved = { quantity ->
                    selectedProduct = selectedProduct?.copy(quantity = quantity)
                },
                onDeleted = { selectedProduct = null }
            )
        } else if (showReplenishmentScreen) {
            ReplenishmentScreen(
                state = productListState,
                onBack = { showReplenishmentScreen = false },
                onSelectProduct = { selectedProduct = it },
                onRetry = { reloadRequest++ }
            )
        } else {
            HomeScreen(
                state = productListState,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                onAddProduct = { showRegisterScreen = true },
                onSelectProduct = { selectedProduct = it },
                onShowReplenishment = { showReplenishmentScreen = true },
                onRetry = { reloadRequest++ }
            )
        }
    }
}

@Preview
@Composable
private fun AppPreview() {
    MiInventarioTheme {
        HomeScreen(
            state = ProductListState.Empty,
            selectedCategory = null,
            onCategorySelected = {},
            onAddProduct = {},
            onSelectProduct = {},
            onShowReplenishment = {},
            onRetry = {}
        )
    }
}

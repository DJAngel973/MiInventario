package co.edu.poli.miinventario

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import co.edu.poli.miinventario.product.Product
import co.edu.poli.miinventario.product.ProductRepository
import co.edu.poli.miinventario.ui.screens.HomeScreen
import co.edu.poli.miinventario.ui.screens.RegisterProductScreen
import co.edu.poli.miinventario.ui.state.ProductListLoader
import co.edu.poli.miinventario.ui.state.ProductListState
import co.edu.poli.miinventario.ui.theme.MiInventarioTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun App(repository: ProductRepository) {
    val productListLoader = remember(repository) { ProductListLoader(repository) }
    AppContent(
        productListLoader = productListLoader,
        onSaveProduct = { product ->
            withContext(Dispatchers.IO) {
                repository.save(product)
            }
        }
    )
}

@Composable
private fun AppContent(
    productListLoader: ProductListLoader,
    onSaveProduct: suspend (Product) -> Unit
) {
    var showRegisterScreen by remember {
        mutableStateOf(false)
    }
    var productListState: ProductListState by remember { mutableStateOf(ProductListState.Loading) }
    var reloadRequest by remember { mutableStateOf(0) }

    LaunchedEffect(showRegisterScreen, reloadRequest, productListLoader) {
        if (!showRegisterScreen) {
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
        } else {
            HomeScreen(
                state = productListState,
                onAddProduct = { showRegisterScreen = true },
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
            onAddProduct = {},
            onRetry = {}
        )
    }
}

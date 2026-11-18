package co.edu.poli.miinventario

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import co.edu.poli.miinventario.product.ProductRepository
import co.edu.poli.miinventario.ui.screens.HomeScreen
import co.edu.poli.miinventario.ui.screens.RegisterProductScreen
import co.edu.poli.miinventario.ui.theme.MiInventarioTheme
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import co.edu.poli.miinventario.product.Product
@Composable
fun App(repository: ProductRepository) {
    AppContent(
        onSaveProduct = { product ->
            withContext(Dispatchers.IO) {
                repository.save(product)
            }
        }
    )
}

@Composable
private fun AppContent(
    onSaveProduct: suspend (Product) -> Unit
) {
    var showRegisterScreen by remember {
        mutableStateOf(false)
    }

    MiInventarioTheme {
        if (showRegisterScreen) {
            RegisterProductScreen(
                onBack = { showRegisterScreen = false },
                onSaveProduct = onSaveProduct
            )
        } else {
            HomeScreen(
                onAddProduct = { showRegisterScreen = true }
            )
        }
    }
}

@Preview
@Composable
private fun AppPreview() {
    AppContent(onSaveProduct = {})
}

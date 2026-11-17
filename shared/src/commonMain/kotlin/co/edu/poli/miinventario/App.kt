package co.edu.poli.miinventario

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import co.edu.poli.miinventario.ui.screens.HomeScreen
import co.edu.poli.miinventario.ui.theme.MiInventarioTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.edu.poli.miinventario.ui.screens.RegisterProductScreen

@Composable
@Preview
fun App() {

    var showRegisterScreen by remember {
        mutableStateOf(false)
    }

    MiInventarioTheme {
        if (showRegisterScreen) {
            RegisterProductScreen(
                onBack = { showRegisterScreen = false }
            )
        } else {
            HomeScreen(
                onAddProduct = { showRegisterScreen = true }
            )
        }
    }
}

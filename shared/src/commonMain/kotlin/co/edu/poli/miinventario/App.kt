package co.edu.poli.miinventario

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import co.edu.poli.miinventario.ui.screens.HomeScreen
import co.edu.poli.miinventario.ui.theme.MiInventarioTheme

@Composable
@Preview
fun App() {
    MiInventarioTheme {
        HomeScreen()
    }
}

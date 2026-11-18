package co.edu.poli.miinventario

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import co.edu.poli.miinventario.data.DesktopDatabaseFactory
import co.edu.poli.miinventario.data.SqlDelightProductRepository

fun main() {
    val database = DesktopDatabaseFactory().create()
    val repository = SqlDelightProductRepository(database)

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "MiInventario"
        ) {
            App(repository)
        }
    }
}

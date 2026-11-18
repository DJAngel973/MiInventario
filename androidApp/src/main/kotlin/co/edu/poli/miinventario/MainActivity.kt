package co.edu.poli.miinventario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import co.edu.poli.miinventario.data.AndroidDatabaseFactory
import co.edu.poli.miinventario.data.SqlDelightProductRepository

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AndroidDatabaseFactory(this).create()
        val repository = SqlDelightProductRepository(database)

        setContent {
            App(repository)
        }
    }
}

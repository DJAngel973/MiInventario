package co.edu.poli.miinventario.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun MiInventarioTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = InventoryColors.blue,
            onPrimary = InventoryColors.onBlue,
            background = InventoryColors.background,
            onBackground = InventoryColors.text,
            surface = InventoryColors.card,
            onSurface = InventoryColors.text,
            surfaceVariant = InventoryColors.header,
            onSurfaceVariant = InventoryColors.mutedText
        ),
        content = content
    )
}

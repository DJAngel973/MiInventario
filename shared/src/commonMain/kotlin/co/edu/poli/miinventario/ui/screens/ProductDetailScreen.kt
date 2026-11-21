package co.edu.poli.miinventario.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.poli.miinventario.product.Product
import co.edu.poli.miinventario.product.ProductCategory
import co.edu.poli.miinventario.product.canSaveQuantity
import co.edu.poli.miinventario.product.isValidQuantity
import co.edu.poli.miinventario.product.needsReplenishment
import co.edu.poli.miinventario.product.quantityAtSteps
import co.edu.poli.miinventario.ui.theme.InventoryColors
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private val deleteRed = Color(0xFFFF6B6B)

@Composable
fun ProductDetailScreen(
    product: Product,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleteProduct: suspend (String) -> Boolean,
    onUpdateQuantity: suspend (Product, Double) -> Boolean,
    onQuantitySaved: (Double) -> Unit,
    onDeleted: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var showConfirmation by remember(product.id) { mutableStateOf(false) }
    var isDeleting by remember(product.id) { mutableStateOf(false) }
    var deleteError by remember(product.id) { mutableStateOf<String?>(null) }
    var quantitySteps by remember(product.id) { mutableStateOf(0L) }
    var isSavingQuantity by remember(product.id) { mutableStateOf(false) }
    var quantityError by remember(product.id) { mutableStateOf<String?>(null) }
    var quantitySaved by remember(product.id) { mutableStateOf(false) }
    val proposedQuantity = quantityAtSteps(product.quantity, quantitySteps, product.unit)
    val isBusy = isDeleting || isSavingQuantity

    Column(modifier = Modifier.fillMaxSize().background(InventoryColors.background).safeContentPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(60.dp).background(InventoryColors.header).padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack, enabled = !isBusy) {
                Text("←", style = MaterialTheme.typography.titleLarge, color = InventoryColors.text)
            }
            Text("Detalle del producto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = InventoryColors.text)
        }

        Column(
            modifier = Modifier.weight(1f).widthIn(max = 560.dp).fillMaxWidth()
                .align(Alignment.CenterHorizontally).verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(), color = InventoryColors.card,
                shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, InventoryColors.border)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val isKitchen = product.category == ProductCategory.COCINA
                    Box(
                        modifier = Modifier.size(62.dp).background(
                            if (isKitchen) Color(0xFF3A2C0F) else InventoryColors.header,
                            RoundedCornerShape(14.dp)
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            product.name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isKitchen) Color(0xFFF5C45E) else InventoryColors.blue
                        )
                    }
                    Column {
                        Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = InventoryColors.text)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(product.category.label, style = MaterialTheme.typography.bodySmall, color = InventoryColors.blue)
                            if (product.needsReplenishment()) {
                                Text(
                                    "Por reponer", style = MaterialTheme.typography.labelSmall, color = deleteRed,
                                    modifier = Modifier.background(Color(0xFF3A1F25), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(), color = InventoryColors.card,
                shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, InventoryColors.border)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                    DetailRow("Cantidad disponible", "${product.quantity.displayQuantity()} ${product.unit.symbol}", product.needsReplenishment())
                    DetailRow("Cantidad mínima", "${product.minimumQuantity.displayQuantity()} ${product.unit.symbol}")
                    DetailRow("Unidad de medida", product.unit.symbol)
                }
            }

            Text(
                "ACTUALIZAR CANTIDAD",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = InventoryColors.mutedText
            )
            Surface(
                modifier = Modifier.fillMaxWidth(), color = InventoryColors.card,
                shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, InventoryColors.border)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            quantitySteps--
                            quantityError = null
                            quantitySaved = false
                        },
                        enabled = !isBusy && isValidQuantity(
                            product, quantityAtSteps(product.quantity, quantitySteps - 1, product.unit)
                        ),
                        modifier = Modifier.size(46.dp), shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        border = BorderStroke(1.dp, InventoryColors.blue)
                    ) { Text("−", color = InventoryColors.blue) }

                    Text(
                        "${proposedQuantity.displayQuantity()} ${product.unit.symbol}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = InventoryColors.text
                    )

                    Button(
                        onClick = {
                            quantitySteps++
                            quantityError = null
                            quantitySaved = false
                        },
                        enabled = !isBusy && isValidQuantity(
                            product, quantityAtSteps(product.quantity, quantitySteps + 1, product.unit)
                        ),
                        modifier = Modifier.size(46.dp), shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = InventoryColors.blue, contentColor = InventoryColors.onBlue
                        )
                    ) { Text("+") }
                }
            }

            if (product.needsReplenishment()) {
                Text(
                    "⚠ En el mínimo o por debajo (${product.minimumQuantity.displayQuantity()} ${product.unit.symbol})",
                    color = deleteRed,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            quantityError?.let { Text(it, color = deleteRed, style = MaterialTheme.typography.bodyMedium) }
            if (quantitySaved) {
                Text("Cantidad actualizada correctamente", color = InventoryColors.blue)
            }
            Button(
                onClick = {
                    if (!isBusy && canSaveQuantity(product, proposedQuantity)) {
                        isSavingQuantity = true
                        quantityError = null
                        quantitySaved = false
                        scope.launch {
                            try {
                                if (onUpdateQuantity(product, proposedQuantity)) {
                                    onQuantitySaved(proposedQuantity)
                                    quantitySteps = 0
                                    quantitySaved = true
                                } else {
                                    quantityError = "Este producto ya no existe en el inventario."
                                }
                            } catch (exception: Exception) {
                                if (exception is CancellationException) throw exception
                                quantityError = "No se pudo guardar la cantidad. Inténtalo de nuevo."
                            } finally {
                                isSavingQuantity = false
                            }
                        }
                    }
                },
                enabled = !isBusy && canSaveQuantity(product, proposedQuantity),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = InventoryColors.blue, contentColor = InventoryColors.onBlue
                )
            ) { Text(if (isSavingQuantity) "Guardando..." else "Guardar cantidad") }

            deleteError?.let { Text(it, color = deleteRed, style = MaterialTheme.typography.bodyMedium) }
        }

        Column(
            modifier = Modifier.widthIn(max = 560.dp).fillMaxWidth().align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onEdit, enabled = !isBusy, modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, InventoryColors.blue)
            ) { Text("Editar producto", color = InventoryColors.blue) }
            OutlinedButton(
                onClick = { deleteError = null; showConfirmation = true },
                enabled = !isBusy,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, deleteRed),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = deleteRed)
            ) { Text("Eliminar producto") }
        }
    }

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { if (!isDeleting) showConfirmation = false },
            title = { Text("Eliminar producto") },
            text = { Text("¿Eliminar «${product.name}» del inventario? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isDeleting) {
                            isDeleting = true
                            deleteError = null
                            scope.launch {
                                try {
                                    if (onDeleteProduct(product.id)) {
                                        showConfirmation = false
                                        onDeleted()
                                    } else {
                                        showConfirmation = false
                                        deleteError = "Este producto ya no existe en el inventario."
                                    }
                                } catch (exception: Exception) {
                                    if (exception is CancellationException) throw exception
                                    showConfirmation = false
                                    deleteError = "No se pudo eliminar el producto. Inténtalo de nuevo."
                                } finally {
                                    isDeleting = false
                                }
                            }
                        }
                    },
                    enabled = !isDeleting,
                    colors = ButtonDefaults.buttonColors(containerColor = deleteRed, contentColor = InventoryColors.onBlue)
                ) { Text(if (isDeleting) "Eliminando..." else "Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmation = false }, enabled = !isDeleting) { Text("Cancelar") }
            },
            containerColor = InventoryColors.card,
            titleContentColor = InventoryColors.text,
            textContentColor = InventoryColors.mutedText
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = InventoryColors.mutedText)
        Spacer(Modifier.size(8.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (highlight) deleteRed else InventoryColors.text)
    }
}

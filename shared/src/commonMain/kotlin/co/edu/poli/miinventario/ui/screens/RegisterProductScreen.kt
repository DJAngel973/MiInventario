package co.edu.poli.miinventario.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.poli.miinventario.product.DuplicateProductException
import co.edu.poli.miinventario.product.Product
import co.edu.poli.miinventario.product.ProductCategory
import co.edu.poli.miinventario.product.ProductUnit
import co.edu.poli.miinventario.ui.state.RegisterProductState
import co.edu.poli.miinventario.ui.theme.InventoryColors
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun RegisterProductScreen(onBack: () -> Unit, onSaveProduct: suspend (Product) -> Unit) {
    ProductFormScreen(null, onBack, onSaveProduct)
}

@Composable
fun EditProductScreen(product: Product, onBack: () -> Unit, onUpdateProduct: suspend (Product) -> Unit) {
    ProductFormScreen(product, onBack, onUpdateProduct)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductFormScreen(original: Product?, onBack: () -> Unit, onSave: suspend (Product) -> Unit) {
    val form = remember(original?.id) { RegisterProductState(original) }
    val scope = rememberCoroutineScope()
    var isSaving by remember(original?.id) { mutableStateOf(false) }
    var saveError by remember(original?.id) { mutableStateOf<String?>(null) }
    var saveSuccess by remember(original?.id) { mutableStateOf(false) }
    var unitExpanded by remember { mutableStateOf(false) }

    fun clearFeedback() {
        form.clearError()
        saveSuccess = false
        saveError = null
    }

    Column(modifier = Modifier.fillMaxSize().background(InventoryColors.background).safeContentPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(60.dp).background(InventoryColors.header).padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack, enabled = !isSaving) {
                Text("←", color = InventoryColors.text, style = MaterialTheme.typography.titleLarge)
            }
            Text(
                if (original == null) "Registrar producto" else "Editar producto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = InventoryColors.text
            )
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier.widthIn(max = 560.dp).fillMaxWidth().verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column {
                    FieldLabel("Nombre del producto *")
                    OutlinedTextField(
                        value = form.name,
                        onValueChange = { form.name = it; clearFeedback() },
                        placeholder = { Text("Ej. Detergente líquido") },
                        singleLine = true,
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column {
                    FieldLabel("Categoría *")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        ProductCategory.entries.forEach { category ->
                            val selected = form.category == category
                            OutlinedButton(
                                onClick = { form.category = category; clearFeedback() },
                                enabled = !isSaving,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, if (selected) InventoryColors.blue else InventoryColors.border),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (selected) InventoryColors.header else InventoryColors.card,
                                    contentColor = if (selected) InventoryColors.blue else InventoryColors.text
                                )
                            ) { Text(category.label) }
                        }
                    }
                }

                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    if (maxWidth >= 300.dp) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                FieldLabel("Cantidad *")
                                QuantityField(form.quantity, { form.quantity = it; clearFeedback() }, !isSaving)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                FieldLabel("Unidad")
                                UnitField(form.unit, unitExpanded, { unitExpanded = it }, !isSaving) {
                                    form.unit = it; clearFeedback()
                                }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Column {
                                FieldLabel("Cantidad *")
                                QuantityField(form.quantity, { form.quantity = it; clearFeedback() }, !isSaving)
                            }
                            Column {
                                FieldLabel("Unidad")
                                UnitField(form.unit, unitExpanded, { unitExpanded = it }, !isSaving) {
                                    form.unit = it; clearFeedback()
                                }
                            }
                        }
                    }
                }

                Column {
                    FieldLabel("Cantidad mínima *")
                    OutlinedTextField(
                        value = form.minimumQuantity,
                        onValueChange = { form.minimumQuantity = it; clearFeedback() },
                        singleLine = true,
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "El producto aparecerá en «Productos por reponer» cuando su cantidad sea igual o menor a este valor. * Campos obligatorios.",
                        style = MaterialTheme.typography.bodySmall,
                        color = InventoryColors.mutedText
                    )
                }

                form.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                saveError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                if (saveSuccess) {
                    Text(
                        if (original == null) "Producto agregado correctamente" else "Producto actualizado correctamente",
                        color = InventoryColors.blue
                    )
                }
            }
        }

        Row(
            modifier = Modifier.widthIn(max = 560.dp).fillMaxWidth().align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onBack, enabled = !isSaving, modifier = Modifier.weight(1f)) {
                Text(if (original != null && saveSuccess) "Volver al inventario" else "Cancelar")
            }
            Button(
                onClick = {
                    if (!isSaving) {
                        val product = form.validate()
                        if (product != null) {
                            isSaving = true
                            saveError = null
                            saveSuccess = false
                            scope.launch {
                                try {
                                    onSave(product)
                                    if (original == null) form.clear()
                                    saveSuccess = true
                                } catch (exception: DuplicateProductException) {
                                    saveError = exception.message
                                } catch (exception: Exception) {
                                    if (exception is CancellationException) throw exception
                                    saveError = if (original == null) "No se pudo guardar el producto" else "No se pudo actualizar el producto"
                                } finally {
                                    isSaving = false
                                }
                            }
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (isSaving) "Guardando..." else if (original == null) "Guardar" else "Guardar cambios")
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = InventoryColors.text)
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun QuantityField(value: String, onValueChange: (String) -> Unit, enabled: Boolean) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, placeholder = { Text("0") },
        singleLine = true, enabled = enabled, modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitField(
    unit: ProductUnit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    enabled: Boolean,
    onUnitChange: (ProductUnit) -> Unit
) {
    ExposedDropdownMenuBox(expanded = expanded && enabled, onExpandedChange = { if (enabled) onExpandedChange(it) }) {
        OutlinedTextField(
            value = unit.symbol,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded && enabled, onDismissRequest = { onExpandedChange(false) }) {
            ProductUnit.entries.forEach { option ->
                DropdownMenuItem(text = { Text(option.symbol) }, onClick = {
                    onUnitChange(option)
                    onExpandedChange(false)
                })
            }
        }
    }
}

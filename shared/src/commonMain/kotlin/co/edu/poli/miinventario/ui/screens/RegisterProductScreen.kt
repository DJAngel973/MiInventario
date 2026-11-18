package co.edu.poli.miinventario.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.poli.miinventario.ui.theme.InventoryColors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import co.edu.poli.miinventario.product.ProductCategory
import co.edu.poli.miinventario.product.ProductUnit
import co.edu.poli.miinventario.ui.state.RegisterProductState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import co.edu.poli.miinventario.product.Product
import kotlinx.coroutines.CancellationException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterProductScreen(
    onBack: () -> Unit,
    onSaveProduct: suspend (Product) -> Unit) {

    val form = remember  { RegisterProductState() }
    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    var saveError by remember { mutableStateOf<String?>(null) }
    var saveSuccess by remember { mutableStateOf(false) }

    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    var unitExpanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InventoryColors.background)
            .safeContentPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registrar producto",
            color = InventoryColors.text
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = form.name,
            onValueChange = {
                form.name = it
                saveSuccess = false
            },
            enabled = !isSaving,
            label = { Text("Nombre del producto") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = categoryExpanded && !isSaving,
            onExpandedChange = {
                if (!isSaving) {
                    categoryExpanded = it
                }
            }
        ) {
            OutlinedTextField(
                value = form.category.label,
                onValueChange = {},
                readOnly = true,
                enabled = !isSaving,
                label = { Text("Categoría") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = categoryExpanded
                    )
                },
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable
                    )
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = categoryExpanded && !isSaving,
                onDismissRequest = { categoryExpanded = false }
            ) {
                ProductCategory.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        enabled = !isSaving,
                        onClick = {
                            form.category = option
                            categoryExpanded = false
                            saveSuccess = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = form.quantity,
            onValueChange = {
                form.quantity = it
                saveSuccess = false
            },
            enabled = !isSaving,
            label = { Text("Cantidad disponible") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = unitExpanded && !isSaving,
            onExpandedChange = {
                if (!isSaving) {
                    unitExpanded = it
                }
            }
        ) {
            OutlinedTextField(
                value = form.unit.symbol,
                onValueChange = {},
                readOnly = true,
                enabled = !isSaving,
                label = { Text("Unidad de medida") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = unitExpanded
                    )
                },
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable
                    )
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = unitExpanded && !isSaving,
                onDismissRequest = { unitExpanded = false }
            ) {
                ProductUnit.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.symbol) },
                        enabled = !isSaving,
                        onClick = {
                            form.unit = option
                            unitExpanded = false
                            saveSuccess = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = form.minimumQuantity,
            onValueChange = {
                form.minimumQuantity = it
                saveSuccess = false
            },
            enabled = !isSaving,
            label = { Text("Cantidad mínima para reponer") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        form.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        saveError?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (saveSuccess) {
            Text(
                text = "Producto agregado correctamente",
                color = InventoryColors.blue
            )
        }

        Button(
            onClick = {
                val product = form.validate()

                if (product != null && !isSaving) {
                    scope.launch {
                        isSaving = true
                        saveError = null
                        saveSuccess =false

                        try {
                            onSaveProduct(product)
                            form.clear()
                            saveSuccess = true
                        } catch (exception: Exception) {
                            if (exception is CancellationException) {
                                throw exception
                            }
                            saveError = "No se pudo guardar el producto"
                        } finally {
                            isSaving = false
                        }
                    }
                }
            },
            enabled =!isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSaving) "Guardando..." else "Guardar producto")
        }

        Button(
            onClick = onBack,
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Volver al inicio")
        }
    }
}

package co.edu.poli.miinventario.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.poli.miinventario.product.Product
import co.edu.poli.miinventario.product.ProductCategory
import co.edu.poli.miinventario.product.countByCategory
import co.edu.poli.miinventario.product.filterByCategory
import co.edu.poli.miinventario.ui.state.ProductListState
import co.edu.poli.miinventario.ui.theme.InventoryColors

@Composable
fun HomeScreen(
    state: ProductListState,
    selectedCategory: ProductCategory?,
    onCategorySelected: (ProductCategory?) -> Unit,
    onAddProduct: () -> Unit,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .background(InventoryColors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .background(InventoryColors.header)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "MiInventario",
                    style = MaterialTheme.typography.titleLarge,
                    color = InventoryColors.text
                )
            }

            val products = when (state) {
                ProductListState.Empty -> emptyList()
                is ProductListState.Content -> state.products
                else -> null
            }
            if (products != null) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CategoryFilterChip("Todos", null, products, selectedCategory, onCategorySelected)
                    CategoryFilterChip("Aseo", ProductCategory.ASEO, products, selectedCategory, onCategorySelected)
                    CategoryFilterChip("Cocina", ProductCategory.COCINA, products, selectedCategory, onCategorySelected)
                }
                Text(
                    text = "${products.size} PRODUCTOS REGISTRADOS",
                    style = MaterialTheme.typography.labelMedium,
                    color = InventoryColors.mutedText,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    ProductListState.Loading -> CircularProgressIndicator(color = InventoryColors.blue)
                    ProductListState.Empty -> MessageCard(
                        title = "No hay productos registrados",
                        message = "Agrega tu primer producto para comenzar"
                    )
                    ProductListState.Error -> MessageCard(
                        title = "No se pudieron cargar los productos",
                        message = "Verifica e intenta nuevamente",
                        action = {
                            Button(onClick = onRetry) { Text("Reintentar") }
                        }
                    )
                    is ProductListState.Content -> {
                        val visibleProducts = state.products.filterByCategory(selectedCategory)
                        if (visibleProducts.isEmpty()) {
                            MessageCard(
                                title = "No hay productos en esta categoría",
                                message = "Selecciona otra categoría para ver productos"
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth().fillMaxHeight(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(visibleProducts, key = { it.id }) { product ->
                                    ProductCard(product)
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddProduct,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = InventoryColors.blue,
            contentColor = InventoryColors.onBlue
        ) {
            Text(
                text = "+",
                fontSize = 32.sp,
                modifier = Modifier.semantics {
                    contentDescription = "Agregar producto"
                }
            )
        }
    }
}

@Composable
private fun CategoryFilterChip(
    label: String,
    category: ProductCategory?,
    products: List<Product>,
    selectedCategory: ProductCategory?,
    onCategorySelected: (ProductCategory?) -> Unit
) {
    FilterChip(
        selected = selectedCategory == category,
        onClick = { onCategorySelected(category) },
        label = { Text("$label ${products.countByCategory(category)}") },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = InventoryColors.card,
            labelColor = InventoryColors.text,
            selectedContainerColor = InventoryColors.header,
            selectedLabelColor = InventoryColors.blue
        )
    )
}

@Composable
private fun MessageCard(
    title: String,
    message: String,
    action: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.widthIn(max = 520.dp).fillMaxWidth(),
        color = InventoryColors.card,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, InventoryColors.border)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = InventoryColors.text, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, style = MaterialTheme.typography.bodyLarge, color = InventoryColors.mutedText, textAlign = TextAlign.Center)
            if (action != null) {
                Spacer(modifier = Modifier.height(20.dp))
                action()
            }
        }
    }
}

@Composable
private fun ProductCard(product: Product) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = InventoryColors.card,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, InventoryColors.border)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val isKitchen = product.category == ProductCategory.COCINA
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isKitchen) Color(0xFF3A2C0F) else InventoryColors.header,
                        RoundedCornerShape(11.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isKitchen) Color(0xFFF5C45E) else InventoryColors.blue
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = InventoryColors.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${product.category.label} · Mín. ${product.minimumQuantity.displayQuantity()} ${product.unit.symbol}",
                    style = MaterialTheme.typography.bodySmall,
                    color = InventoryColors.mutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "${product.quantity.displayQuantity()} ${product.unit.symbol}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = InventoryColors.text,
                maxLines = 1
            )
        }
    }
}

internal fun Double.displayQuantity(): String = toString().replace(".0E", "E").removeSuffix(".0")

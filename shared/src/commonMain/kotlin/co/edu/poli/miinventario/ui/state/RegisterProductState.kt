package co.edu.poli.miinventario.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import co.edu.poli.miinventario.product.Product
import co.edu.poli.miinventario.product.ProductCategory
import co.edu.poli.miinventario.product.ProductUnit
import co.edu.poli.miinventario.product.ProductValidationError
import co.edu.poli.miinventario.product.ProductValidationResult
import co.edu.poli.miinventario.product.validateProduct

class RegisterProductState {

    var name by mutableStateOf("")
    var category by mutableStateOf(ProductCategory.ASEO)
    var quantity by mutableStateOf("")
    var unit by mutableStateOf(ProductUnit.UNIT)
    var minimumQuantity by mutableStateOf("")

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun validate(): Product? {
        errorMessage = null

        val parsedQuantity = quantity.trim().toDoubleOrNull()
        val parsedMinimum = minimumQuantity.trim().toDoubleOrNull()

        if (parsedQuantity == null || parsedMinimum == null) {
            errorMessage = "Ingresa cantidades numéricas válidas"
            return null
        }

        val product = Product(
            name = name,
            category = category,
            quantity = parsedQuantity,
            unit = unit,
            minimumQuantity = parsedMinimum
        )

        return when (val result = validateProduct(product)) {
            is ProductValidationResult.Valid -> result.product

            is ProductValidationResult.Invalid -> {
                errorMessage = result.errors.firstOrNull()?.toMessage()
                null
            }
        }
    }
}

private fun ProductValidationError.toMessage(): String = when (this) {
    ProductValidationError.NAME_REQUIRED ->
        "Ingresa el nombre del producto"

    ProductValidationError.QUANTITY_NOT_FINITE,
    ProductValidationError.QUANTITY_NEGATIVE ->
        "La cantidad disponible debe ser válida y no negativa"

    ProductValidationError.MINIMUM_QUANTITY_NOT_FINITE,
    ProductValidationError.MINIMUM_QUANTITY_NEGATIVE ->
        "La cantidad mínima debe ser válida y no negativa"

    ProductValidationError.QUANTITY_MUST_BE_INTEGER_FOR_UNIT ->
        "La cantidad disponible debe ser entera para unidades"

    ProductValidationError.MINIMUM_QUANTITY_MUST_BE_INTEGER_FOR_UNIT ->
        "La cantidad mínima debe ser entera para unidades"
}

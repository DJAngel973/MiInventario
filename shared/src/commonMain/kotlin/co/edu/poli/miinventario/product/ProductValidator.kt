package co.edu.poli.miinventario.product

/** Errores posibles al validar los datos de RF1. */
enum class ProductValidationError {
    NAME_REQUIRED,
    QUANTITY_NOT_FINITE,
    QUANTITY_NEGATIVE,
    MINIMUM_QUANTITY_NOT_FINITE,
    MINIMUM_QUANTITY_NEGATIVE,
    QUANTITY_MUST_BE_INTEGER_FOR_UNIT,
    MINIMUM_QUANTITY_MUST_BE_INTEGER_FOR_UNIT
}

/** Resultado explícito: producto normalizado si es válido, errores en caso contrario. */
sealed interface ProductValidationResult {
    data class Valid(val product: Product) : ProductValidationResult
    data class Invalid(val errors: List<ProductValidationError>) : ProductValidationResult
}

/**
 * Valida RF1 y devuelve el nombre sin espacios al inicio o al final.
 * No modifica el producto original.
 */
fun validateProduct(product: Product): ProductValidationResult {
    val errors = buildList {
        if (product.name.isBlank()) {
            add(ProductValidationError.NAME_REQUIRED)
        }

        if (!product.quantity.isFinite()) {
            add(ProductValidationError.QUANTITY_NOT_FINITE)
        } else if (product.quantity < 0.0) {
            add(ProductValidationError.QUANTITY_NEGATIVE)
        }

        if (!product.minimumQuantity.isFinite()) {
            add(ProductValidationError.MINIMUM_QUANTITY_NOT_FINITE)
        } else if (product.minimumQuantity < 0.0) {
            add(ProductValidationError.MINIMUM_QUANTITY_NEGATIVE)
        }

        if (!product.unit.allowsDecimalQuantity) {
            if (product.quantity.isFinite() && !product.quantity.isWholeNumber()) {
                add(ProductValidationError.QUANTITY_MUST_BE_INTEGER_FOR_UNIT)
            }
            if (product.minimumQuantity.isFinite() && !product.minimumQuantity.isWholeNumber()) {
                add(ProductValidationError.MINIMUM_QUANTITY_MUST_BE_INTEGER_FOR_UNIT)
            }
        }
    }

    if (errors.isNotEmpty()) {
        return ProductValidationResult.Invalid(errors)
    }

    return ProductValidationResult.Valid(product.copy(name = product.name.trim()))
}

private fun Double.isWholeNumber(): Boolean = this % 1.0 == 0.0

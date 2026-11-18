package co.edu.poli.miinventario.product

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/** Categorías disponibles para los productos del inventario. */
enum class ProductCategory(val label: String) {
    ASEO("Aseo"),
    COCINA("Cocina")
}

/** Unidades de medida */
enum class ProductUnit(
    val symbol: String,
    val allowsDecimalQuantity: Boolean
) {
    UNIT("und", allowsDecimalQuantity = false),
    KILOGRAM("kg", allowsDecimalQuantity = true),
    POUND("lb", allowsDecimalQuantity = true),
    LITER("L", allowsDecimalQuantity = true)
}

/**
 * Producto del inventario.
 *
 * El identificador se genera en el código común para que el modelo pueda usarse
 * antes de contar con una base de datos. La persistencia podrá guardar ese mismo
 * UUID como texto cuando se implemente.
 */
data class Product @OptIn(ExperimentalUuidApi::class) constructor(
    val name: String,
    val category: ProductCategory,
    val quantity: Double,
    val unit: ProductUnit,
    val minimumQuantity: Double,
    val id: String = Uuid.random().toString()
)

package co.edu.poli.miinventario.product

/** Los pasos de la interfaz se calculan desde la cantidad almacenada, no por sumas sucesivas. */
fun quantityAtSteps(storedQuantity: Double, steps: Long, unit: ProductUnit): Double =
    storedQuantity + steps * if (unit == ProductUnit.UNIT) 1.0 else 0.5

fun isValidQuantity(product: Product, proposedQuantity: Double): Boolean =
    validateProduct(product.copy(quantity = proposedQuantity)) is ProductValidationResult.Valid

fun canSaveQuantity(product: Product, proposedQuantity: Double): Boolean =
    proposedQuantity != product.quantity && isValidQuantity(product, proposedQuantity)

class ProductQuantityService(private val repository: ProductRepository) {
    suspend fun update(product: Product, proposedQuantity: Double): Boolean {
        require(canSaveQuantity(product, proposedQuantity)) { "La cantidad propuesta no es válida" }
        return repository.updateQuantity(product.id, proposedQuantity)
    }
}

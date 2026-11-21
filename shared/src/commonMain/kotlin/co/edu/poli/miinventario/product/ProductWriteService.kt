package co.edu.poli.miinventario.product

class DuplicateProductException : Exception(
    "Este producto ya está registrado. Verifica el producto existente o utiliza un nombre más específico."
)

/** Verifica duplicados antes de escribir; no sustituye una restricción UNIQUE ante escrituras simultáneas. */
class ProductWriteService(private val repository: ProductRepository) {
    suspend fun register(product: Product) {
        checkDuplicate(product, repository.getAll(), excludedId = null)
        repository.save(product)
    }

    suspend fun update(product: Product) {
        val existingProducts = repository.getAll()
        val original = existingProducts.firstOrNull { it.id == product.id }
            ?: error("El producto que se intenta actualizar ya no existe")
        if (!sameNameAndCategory(original, product)) {
            checkDuplicate(product, existingProducts, excludedId = product.id)
        }
        repository.update(product)
    }

    private fun checkDuplicate(product: Product, existingProducts: List<Product>, excludedId: String?) {
        val duplicate = existingProducts.any { existing ->
            existing.id != excludedId &&
                sameNameAndCategory(existing, product)
        }
        if (duplicate) throw DuplicateProductException()
    }

    private fun sameNameAndCategory(first: Product, second: Product): Boolean =
        first.category == second.category &&
            first.name.trim().equals(second.name.trim(), ignoreCase = true)
}

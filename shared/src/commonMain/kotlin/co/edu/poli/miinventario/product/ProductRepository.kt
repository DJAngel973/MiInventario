package co.edu.poli.miinventario.product

interface ProductRepository {

    suspend fun save(product: Product)

    suspend fun update(product: Product)

    /** Actualiza solo la cantidad; devuelve false si el UUID ya no existe. */
    suspend fun updateQuantity(id: String, quantity: Double): Boolean

    /** Devuelve false si el UUID ya no existe. */
    suspend fun deleteById(id: String): Boolean

    suspend fun getAll(): List<Product>
}

package co.edu.poli.miinventario.product

interface ProductRepository {

    suspend fun save(product: Product)

    suspend fun update(product: Product)

    /** Devuelve false si el UUID ya no existe. */
    suspend fun deleteById(id: String): Boolean

    suspend fun getAll(): List<Product>
}

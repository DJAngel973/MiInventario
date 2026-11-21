package co.edu.poli.miinventario.product

interface ProductRepository {

    suspend fun save(product: Product)

    suspend fun update(product: Product)

    suspend fun getAll(): List<Product>
}

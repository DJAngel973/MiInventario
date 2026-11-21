
package co.edu.poli.miinventario.data

import co.edu.poli.miinventario.database.InventoryDatabase
import co.edu.poli.miinventario.product.Product
import co.edu.poli.miinventario.product.ProductCategory
import co.edu.poli.miinventario.product.ProductRepository
import co.edu.poli.miinventario.product.ProductUnit

class SqlDelightProductRepository(
    database: InventoryDatabase
) : ProductRepository {

    private val queries = database.productQueries

    override suspend fun save(product: Product) {
        queries.insertProduct(
            id = product.id,
            name = product.name,
            category = product.category.name,
            quantity = product.quantity,
            unit = product.unit.name,
            minimumQuantity = product.minimumQuantity
        )
    }

    override suspend fun update(product: Product) {
        queries.updateProduct(
            name = product.name,
            category = product.category.name,
            quantity = product.quantity,
            unit = product.unit.name,
            minimumQuantity = product.minimumQuantity,
            id = product.id
        )
    }

    override suspend fun deleteById(id: String): Boolean = queries.transactionWithResult {
        val exists = queries.selectProductById(id).executeAsOneOrNull() != null
        if (exists) queries.deleteProduct(id)
        exists
    }

    override suspend fun getAll(): List<Product> {
        return queries.selectAllProducts()
            .executeAsList()
            .map { row ->
                Product(
                    id = row.id,
                    name = row.name,
                    category = ProductCategory.valueOf(row.category),
                    quantity = row.quantity,
                    unit = ProductUnit.valueOf(row.unit),
                    minimumQuantity = row.minimumQuantity
                )
            }
    }
}

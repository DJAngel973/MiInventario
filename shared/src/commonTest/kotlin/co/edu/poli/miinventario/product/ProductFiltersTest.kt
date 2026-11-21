package co.edu.poli.miinventario.product

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductFiltersTest {
    private val soap = Product("Jabón", ProductCategory.ASEO, 3.0, ProductUnit.UNIT, 2.0)
    private val detergent = Product("Detergente", ProductCategory.ASEO, 1.0, ProductUnit.KILOGRAM, 1.0)
    private val rice = Product("Arroz", ProductCategory.COCINA, 2.0, ProductUnit.KILOGRAM, 1.0)
    private val products = listOf(soap, rice, detergent)

    @Test
    fun allReturnsEveryProductInOriginalOrder() {
        assertEquals(products, products.filterByCategory(null))
    }

    @Test
    fun aseoReturnsOnlyAseoProducts() {
        assertEquals(listOf(soap, detergent), products.filterByCategory(ProductCategory.ASEO))
    }

    @Test
    fun cocinaReturnsOnlyCocinaProducts() {
        assertEquals(listOf(rice), products.filterByCategory(ProductCategory.COCINA))
    }

    @Test
    fun categoryWithoutProductsIsEmpty() {
        assertTrue(listOf(rice).filterByCategory(ProductCategory.ASEO).isEmpty())
    }

    @Test
    fun countersMatchRegisteredProducts() {
        assertEquals(3, products.countByCategory(null))
        assertEquals(2, products.countByCategory(ProductCategory.ASEO))
        assertEquals(1, products.countByCategory(ProductCategory.COCINA))
        assertEquals(0, emptyList<Product>().countByCategory(ProductCategory.COCINA))
    }
}

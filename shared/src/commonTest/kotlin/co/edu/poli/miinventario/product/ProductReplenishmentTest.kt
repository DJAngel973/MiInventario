package co.edu.poli.miinventario.product

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProductReplenishmentTest {
    @Test
    fun aboveMinimumDoesNotNeedReplenishment() {
        assertFalse(product(5.0, 2.0).needsReplenishment())
    }

    @Test
    fun equalToMinimumNeedsReplenishmentIncludingZero() {
        assertTrue(product(2.0, 2.0).needsReplenishment())
        assertTrue(product(0.0, 0.0).needsReplenishment())
    }

    @Test
    fun belowMinimumNeedsReplenishment() {
        assertTrue(product(1.0, 2.0).needsReplenishment())
        assertTrue(product(0.0, 2.0).needsReplenishment())
    }

    @Test
    fun decimalQuantitiesFollowSameRule() {
        assertTrue(product(0.25, 0.5, ProductUnit.LITER).needsReplenishment())
        assertTrue(product(0.5, 0.5, ProductUnit.KILOGRAM).needsReplenishment())
        assertFalse(product(0.75, 0.5, ProductUnit.POUND).needsReplenishment())
    }

    @Test
    fun filtersAndCountsAllCategoriesIndependentlyOfRf3Filter() {
        val aseo = product(1.0, 2.0, category = ProductCategory.ASEO)
        val cocina = product(0.0, 0.0, category = ProductCategory.COCINA)
        val enough = product(5.0, 2.0, category = ProductCategory.COCINA)
        val products = listOf(aseo, cocina, enough)

        assertEquals(listOf(aseo, cocina), products.productsToReplenish())
        assertEquals(2, products.replenishmentCount())
        assertEquals(1, products.filterByCategory(ProductCategory.COCINA).productsToReplenish().size)
        assertEquals(3, products.size)
    }

    @Test
    fun returnsEmptyListWhenNoProductNeedsReplenishment() {
        val products = listOf(product(3.0, 1.0))
        assertTrue(products.productsToReplenish().isEmpty())
        assertEquals(0, products.replenishmentCount())
    }

    @Test
    fun resultChangesWhenQuantityChangesWithoutMutatingOriginal() {
        val original = product(1.0, 2.0)
        val updated = original.copy(quantity = 3.0)
        assertTrue(original.needsReplenishment())
        assertFalse(updated.needsReplenishment())
        assertEquals(1, listOf(original).replenishmentCount())
        assertEquals(0, listOf(updated).replenishmentCount())
    }

    private fun product(
        quantity: Double,
        minimum: Double,
        unit: ProductUnit = ProductUnit.UNIT,
        category: ProductCategory = ProductCategory.ASEO
    ) = Product("Producto", category, quantity, unit, minimum)
}

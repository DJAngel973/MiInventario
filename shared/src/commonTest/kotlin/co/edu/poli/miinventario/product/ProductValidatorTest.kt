package co.edu.poli.miinventario.product

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ProductValidatorTest {

    @Test
    fun trimsNameBeforeReturningValidProduct() {
        val result = validateProduct(product(name = "  Jabón de loza  "))

        val valid = assertIs<ProductValidationResult.Valid>(result)
        assertEquals("Jabón de loza", valid.product.name)
    }

    @Test
    fun rejectsBlankName() {
        val result = validateProduct(product(name = "  \t "))

        val invalid = assertIs<ProductValidationResult.Invalid>(result)
        assertTrue(ProductValidationError.NAME_REQUIRED in invalid.errors)
    }

    @Test
    fun allowsZeroQuantityAndZeroMinimum() {
        val result = validateProduct(product(quantity = 0.0, minimumQuantity = 0.0))

        assertIs<ProductValidationResult.Valid>(result)
    }

    @Test
    fun allowsQuantityEqualToMinimum() {
        val result = validateProduct(product(quantity = 2.0, minimumQuantity = 2.0))

        assertIs<ProductValidationResult.Valid>(result)
    }

    @Test
    fun rejectsNegativeQuantityAndMinimum() {
        val result = validateProduct(product(quantity = -0.1, minimumQuantity = -1.0))

        val invalid = assertIs<ProductValidationResult.Invalid>(result)
        assertTrue(ProductValidationError.QUANTITY_NEGATIVE in invalid.errors)
        assertTrue(ProductValidationError.MINIMUM_QUANTITY_NEGATIVE in invalid.errors)
    }

    @Test
    fun rejectsNonFiniteQuantities() {
        val result = validateProduct(product(quantity = Double.NaN, minimumQuantity = Double.POSITIVE_INFINITY))

        val invalid = assertIs<ProductValidationResult.Invalid>(result)
        assertTrue(ProductValidationError.QUANTITY_NOT_FINITE in invalid.errors)
        assertTrue(ProductValidationError.MINIMUM_QUANTITY_NOT_FINITE in invalid.errors)
    }

    @Test
    fun unitRequiresWholeQuantityAndMinimum() {
        val result = validateProduct(
            product(quantity = 1.5, unit = ProductUnit.UNIT, minimumQuantity = 2.5)
        )

        val invalid = assertIs<ProductValidationResult.Invalid>(result)
        assertTrue(ProductValidationError.QUANTITY_MUST_BE_INTEGER_FOR_UNIT in invalid.errors)
        assertTrue(ProductValidationError.MINIMUM_QUANTITY_MUST_BE_INTEGER_FOR_UNIT in invalid.errors)
    }

    @Test
    fun measurementUnitsAllowDecimalValues() {
        val measurementUnits = listOf(ProductUnit.KILOGRAM, ProductUnit.POUND, ProductUnit.LITER)

        measurementUnits.forEach { unit ->
            val result = validateProduct(product(quantity = 0.25, unit = unit, minimumQuantity = 0.75))
            assertIs<ProductValidationResult.Valid>(result, "Expected decimals to be valid for ${unit.symbol}")
        }
    }

    @Test
    fun acceptsBothCategoriesAndAllDeclaredUnits() {
        assertEquals(setOf("Aseo", "Cocina"), ProductCategory.entries.map { it.label }.toSet())
        assertEquals(setOf("und", "kg", "lb", "L"), ProductUnit.entries.map { it.symbol }.toSet())
    }

    @Test
    fun generatesAnIdentifierAndDoesNotRequireUniqueNames() {
        val first = product(name = "Arroz")
        val second = product(name = "Arroz")

        assertFalse(first.id.isBlank())
        assertNotEquals(first.id, second.id)
        assertIs<ProductValidationResult.Valid>(validateProduct(first))
        assertIs<ProductValidationResult.Valid>(validateProduct(second))
    }

    private fun product(
        name: String = "Arroz",
        category: ProductCategory = ProductCategory.COCINA,
        quantity: Double = 1.0,
        unit: ProductUnit = ProductUnit.UNIT,
        minimumQuantity: Double = 0.0
    ) = Product(
        name = name,
        category = category,
        quantity = quantity,
        unit = unit,
        minimumQuantity = minimumQuantity
    )
}

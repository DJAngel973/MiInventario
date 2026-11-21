package co.edu.poli.miinventario.ui.screens

import kotlin.test.Test
import kotlin.test.assertEquals

class QuantityDisplayTest {
    @Test
    fun omitsUnnecessaryFractionForWholeQuantity() {
        assertEquals("2", 2.0.displayQuantity())
        assertEquals("0", 0.0.displayQuantity())
    }

    @Test
    fun retainsFractionForMeasuredQuantity() {
        assertEquals("1.5", 1.5.displayQuantity())
        assertEquals("0.25", 0.25.displayQuantity())
    }
}

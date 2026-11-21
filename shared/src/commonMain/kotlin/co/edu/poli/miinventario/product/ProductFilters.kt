package co.edu.poli.miinventario.product

/** Una categoría nula representa el filtro «Todos». */
fun List<Product>.filterByCategory(category: ProductCategory?): List<Product> =
    if (category == null) this else filter { it.category == category }

fun List<Product>.countByCategory(category: ProductCategory?): Int =
    if (category == null) size else count { it.category == category }

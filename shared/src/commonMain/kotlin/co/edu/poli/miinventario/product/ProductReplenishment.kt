package co.edu.poli.miinventario.product

fun Product.needsReplenishment(): Boolean = quantity <= minimumQuantity

fun List<Product>.productsToReplenish(): List<Product> = filter { it.needsReplenishment() }

fun List<Product>.replenishmentCount(): Int = count { it.needsReplenishment() }

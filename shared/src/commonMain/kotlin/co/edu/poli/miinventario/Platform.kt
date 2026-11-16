package co.edu.poli.miinventario

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
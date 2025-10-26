package universidadnacional.reto8.data.model

data class Empresa(
    val id: Long = 0,
    val nombre: String,
    val url: String,
    val telefono: String,
    val email: String,
    val productosServicios: String,
    val clasificacion: Clasificacion
)

enum class Clasificacion {
    CONSULTORIA,
    DESARROLLO_MEDIDA,
    FABRICA_SOFTWARE;

    fun toDisplayString(): String {
        return when (this) {
            CONSULTORIA -> "Consultoría"
            DESARROLLO_MEDIDA -> "Desarrollo a la medida"
            FABRICA_SOFTWARE -> "Fábrica de software"
        }
    }

    companion object {
        fun fromDisplayString(displayString: String): Clasificacion {
            return when (displayString) {
                "Consultoría" -> CONSULTORIA
                "Desarrollo a la medida" -> DESARROLLO_MEDIDA
                "Fábrica de software" -> FABRICA_SOFTWARE
                else -> CONSULTORIA // default
            }
        }
    }
}

package com.example.sgva.infrastructure.parsing

import java.text.Normalizer

/**
 * Normalización de valores en la capa de infraestructura (`DESIGN_SGVA.md` §5).
 *
 * Toda inconsistencia de formato de los datos de origen se corrige aquí, antes
 * de construir la entidad de dominio: separadores en los periodos, acentos y
 * mayúsculas en los enums, celdas en blanco, decimales de Excel. El dominio
 * recibe siempre valores con el formato canónico que sus invariantes esperan.
 */
internal object NormalizadorDatos {

    private val ACENTOS = Regex("\\p{M}+")
    private val SEPARADORES_CLAVE = Regex("[\\s\\-/_]+")

    /** Recorta espacios; `null` se trata como cadena vacía. */
    fun texto(valor: String?): String = valor?.trim().orEmpty()

    /**
     * Reduce un periodo académico a su forma canónica `AAAAS` conservando solo
     * los dígitos: `"2026-1"`, `"2026_1"`, `"2026 / 1"` -> `"20261"`.
     */
    fun periodoAcademico(valor: String?): String = texto(valor).filter(Char::isDigit)

    /**
     * Normaliza un valor a la convención de constantes de enum de Kotlin: sin
     * acentos, en mayúsculas y con los separadores convertidos en `_`.
     * `"tiempo completo"` -> `"TIEMPO_COMPLETO"`, `"Cátedra"` -> `"CATEDRA"`.
     */
    fun claveEnum(valor: String?): String =
        Normalizer.normalize(texto(valor), Normalizer.Form.NFD)
            .replace(ACENTOS, "")
            .replace(SEPARADORES_CLAVE, "_")
            .uppercase()

    /**
     * Interpreta un entero opcional: la cadena vacía es `null`; se aceptan
     * decimales con parte fraccionaria cero (`"210.0"` -> `210`), habituales al
     * leer celdas numéricas de Excel.
     *
     * @throws NumberFormatException si el valor no es un entero reconocible.
     */
    fun enteroOpcional(valor: String?): Int? {
        val limpio = texto(valor)
        if (limpio.isEmpty()) return null

        limpio.toIntOrNull()?.let { return it }

        val decimal = limpio.toDoubleOrNull()
        if (decimal != null && decimal % 1.0 == 0.0) return decimal.toInt()

        throw NumberFormatException("'$limpio' no es un número entero válido")
    }

    /**
     * Interpreta un entero obligatorio (mismas reglas de [enteroOpcional] para
     * decimales de Excel); a diferencia de aquel, una cadena vacía no es válida.
     *
     * @throws NumberFormatException si el valor está vacío o no es un entero reconocible.
     */
    fun entero(valor: String?): Int =
        enteroOpcional(valor) ?: throw NumberFormatException("el valor es obligatorio y no puede estar vacío")
}

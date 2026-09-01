package com.example.sgva.domain

/**
 * Indicador cuantitativo de calidad, resultado consolidado de los casos de uso
 * de los Módulos 1 y 2 (`DESIGN_SGVA.md` §1). Es el output principal que consume
 * el Dashboard.
 *
 * @property nombre nombre del indicador (ej.: "Tasa de Deserción", "Promedio Saber Pro").
 * @property valor resultado numérico calculado; debe ser un número finito.
 * @property periodo periodo académico `AAAAS` al que corresponde el valor, o
 *   [CONSOLIDADO] cuando el indicador agrega todos los periodos.
 * @property facultad facultad a la que corresponde, o [TODAS_LAS_FACULTADES]
 *   cuando el indicador no se filtró por facultad.
 */
data class IndicadorCalidad(
    val nombre: String,
    val valor: Double,
    val periodo: String,
    val facultad: String,
) {
    init {
        ReglasDeFormato.exigirNoVacio(nombre, "nombre")
        ReglasDeFormato.exigirNoVacio(periodo, "periodo")
        ReglasDeFormato.exigirNoVacio(facultad, "facultad")
        if (!valor.isFinite()) {
            throw ValorIndicadorInvalidoException(nombre, valor)
        }
    }

    companion object {
        /** Valor de [periodo] cuando el indicador agrega todos los periodos académicos. */
        const val CONSOLIDADO: String = "CONSOLIDADO"

        /** Valor de [facultad] cuando el indicador no se filtró por facultad. */
        const val TODAS_LAS_FACULTADES: String = "TODAS"

        /** Nombres canónicos de los indicadores del Módulo 1 (`DESIGN_SGVA.md` §1). */
        const val EVOLUCION_MATRICULA: String = "Evolución de Matrícula"
        const val TASA_DESERCION: String = "Tasa de Deserción"
        const val PROMEDIO_SABER_PRO: String = "Promedio Saber Pro"
    }
}

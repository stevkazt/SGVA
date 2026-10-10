package com.example.sgva.domain

/**
 * Objeto de valor puro que agrupa las ponderaciones del modelo institucional
 * de autoevaluación de 12 factores y 48 características (`DESIGN_SGVA.md`
 * §0 y §0.1, V5). No depende de frameworks, bases de datos ni sistemas de
 * archivos: se construye siempre a partir de datos externos (la hoja «1.
 * Ponderación» del modelo 2025) y no contiene ningún valor hardcodeado. La
 * carga concreta desde una fuente externa (archivo, base de datos, etc.) es
 * un detalle de infraestructura ajeno a esta clase.
 *
 * @property caracteristicas catálogo completo de [Caracteristica] del
 *   modelo; el peso de cada una dentro de su factor vive en
 *   `Caracteristica.ponderacion`.
 * @property pesosFactores peso de cada factor (identificador de factor ->
 *   peso) dentro del promedio global.
 * @property pesoEncuestas peso de la fuente de encuestas de percepción en la
 *   mezcla de fuentes de una característica (en el modelo vigente, `0.4`).
 * @property pesoDocumental peso de la evaluación documental y estadística en
 *   esa misma mezcla (en el modelo vigente, `0.6`).
 */
data class ConfiguracionPonderaciones(
    val caracteristicas: List<Caracteristica>,
    val pesosFactores: Map<String, Double>,
    val pesoEncuestas: Double,
    val pesoDocumental: Double,
) {
    init {
        ReglasDeFormato.exigirPonderacionNoNegativa(pesoEncuestas, "la mezcla de encuestas")
        ReglasDeFormato.exigirPonderacionNoNegativa(pesoDocumental, "la mezcla documental")

        pesosFactores.forEach { (factor, peso) ->
            ReglasDeFormato.exigirPonderacionNoNegativa(peso, "el factor '$factor'")
        }
        ReglasDeFormato.exigirSumaUno(pesosFactores.values.sum(), "los factores")

        caracteristicas.groupBy { it.factor }.forEach { (factor, caracteristicasDelFactor) ->
            val suma = caracteristicasDelFactor.sumOf { it.ponderacion }
            ReglasDeFormato.exigirSumaUno(suma, "las características del factor '$factor'")
        }
    }
}

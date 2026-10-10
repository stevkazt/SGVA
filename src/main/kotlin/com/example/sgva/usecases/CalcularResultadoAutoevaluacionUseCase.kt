package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.Caracteristica
import com.example.sgva.domain.ConfiguracionPonderaciones
import com.example.sgva.domain.ResultadoAutoevaluacion

/**
 * Módulo 4 — resultado de autoevaluación del programa (`DESIGN_SGVA.md` §0.1
 * "Reglas de cálculo del resultado de autoevaluación" y §2).
 *
 * Calcula, para un [programa] y un [periodo], el promedio por característica,
 * por factor y global del modelo institucional de 12 factores y 48
 * características, a partir de los `ResultadoEncuestaPregunta` y
 * `EvaluacionDocumental` ya registrados. El universo de factores y
 * características, y todos los pesos usados en la mezcla, provienen
 * exclusivamente de [configuracion]: este caso de uso no hardcodea ningún
 * peso ni ningún identificador de factor o característica.
 *
 * Aplica exactamente las reglas de `DESIGN_SGVA.md` §0.1, sin criterios
 * adicionales no documentados.
 */
class CalcularResultadoAutoevaluacionUseCase(
    private val repositorio: AcademicDataRepository,
    private val configuracion: ConfiguracionPonderaciones,
) {

    fun ejecutar(programa: String, periodo: String): ResultadoAutoevaluacion {
        val promediosEncuestaPorCaracteristica = repositorio.listarResultadosEncuestaPregunta()
            .filter { it.programa == programa && it.periodo == periodo }
            .groupBy { it.caracteristica }
            .mapValues { (_, resultados) -> promedioDeEncuesta(resultados.mapNotNull { it.promedio }) }

        val documentalPorCaracteristica = repositorio.listarEvaluacionesDocumentales()
            .filter { it.programa == programa && it.periodo == periodo }
            .associateBy { it.caracteristica }

        val promediosPorCaracteristica = configuracion.caracteristicas.associate { caracteristica ->
            caracteristica.id to promedioDeCaracteristica(
                resultadoEncuesta = promediosEncuestaPorCaracteristica[caracteristica.id],
                documental = documentalPorCaracteristica[caracteristica.id]?.calificacion,
            )
        }

        val promediosPorFactor = configuracion.pesosFactores.keys.associateWith { factor ->
            promedioPonderadoReponderado(
                caracteristicasDelFactor(factor).mapNotNull { caracteristica ->
                    promediosPorCaracteristica[caracteristica.id]?.let { it to caracteristica.ponderacion }
                },
            )
        }

        val promedioGlobal = promedioPonderadoReponderado(
            configuracion.pesosFactores.mapNotNull { (factor, peso) ->
                promediosPorFactor[factor]?.let { it to peso }
            },
        )

        return ResultadoAutoevaluacion(
            programa = programa,
            periodo = periodo,
            promediosPorCaracteristica = promediosPorCaracteristica,
            promediosPorFactor = promediosPorFactor,
            promedioGlobal = promedioGlobal,
        )
    }

    private fun caracteristicasDelFactor(factor: String): List<Caracteristica> =
        configuracion.caracteristicas.filter { it.factor == factor }

    /**
     * Regla 1 de §0.1: promedio de los promedios de las preguntas con valor
     * de la característica, o ausente si ninguna pregunta tiene valor.
     */
    private fun promedioDeEncuesta(promedios: List<Double>): Double? =
        if (promedios.isEmpty()) null else promedios.average()

    /**
     * Regla 2 de §0.1: mezcla documental/encuesta de una característica, el
     * valor documental completo si la encuesta está ausente, o ausente si no
     * hay evaluación documental.
     */
    private fun promedioDeCaracteristica(resultadoEncuesta: Double?, documental: Double?): Double? = when {
        documental == null -> null
        resultadoEncuesta == null -> documental
        else -> documental * configuracion.pesoDocumental + resultadoEncuesta * configuracion.pesoEncuestas
    }

    /**
     * Reglas 3 y 4 de §0.1: promedio ponderado de los valores presentes,
     * reponderando sus pesos de configuración entre sí. Ausente si no hay
     * ningún valor presente, o si los pesos de los valores presentes suman
     * cero (no hay ninguna base de reponderación válida).
     */
    private fun promedioPonderadoReponderado(valoresConPeso: List<Pair<Double, Double>>): Double? {
        if (valoresConPeso.isEmpty()) return null
        val sumaPesos = valoresConPeso.sumOf { it.second }
        if (sumaPesos == 0.0) return null
        return valoresConPeso.sumOf { (valor, peso) -> valor * peso } / sumaPesos
    }
}

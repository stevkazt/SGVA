package com.example.sgva.usecases

import com.example.sgva.domain.DatosInsuficientesException
import com.example.sgva.domain.EstadoCicloAutoevaluacion
import com.example.sgva.domain.FormatoPeriodoInvalidoException
import com.example.sgva.domain.IndicadorCalidad
import com.example.sgva.domain.ReporteEjecutivo
import com.example.sgva.domain.ResultadoAutoevaluacion
import java.time.Clock
import java.time.LocalDate

/**
 * Consolida el reporte ejecutivo (Fase 4 del anteproyecto — integración de
 * reportes): agrupa los indicadores de calidad de los Módulos 1 y 2 con las
 * respuestas de encuesta de percepción del Módulo 3 ya registradas. Por
 * composición sobre los casos de uso de cálculo ya existentes (mismo patrón
 * que [GenerarMatrizRadarUseCase]); no accede al repositorio directamente.
 *
 * La exportación a un formato físico concreto (PDF) es responsabilidad
 * exclusiva de la infraestructura (`GeneradorReportePdf`); este caso de uso
 * solo produce el [ReporteEjecutivo], un objeto de dominio puro.
 */
class GenerarReporteEjecutivoUseCase(
    private val calcularEvolucionMatricula: CalcularEvolucionMatriculaUseCase,
    private val calcularTasaDesercion: CalcularTasaDesercionUseCase,
    private val consolidarSaberPro: ConsolidarPuntajesSaberProUseCase,
    private val calcularDistribucionFormacion: CalcularDistribucionFormacionUseCase,
    private val calcularCapacidadInstalada: CalcularCapacidadInstaladaUseCase,
    private val consultarRespuestasEncuesta: ConsultarRespuestasEncuestaUseCase,
    private val calcularResultadoAutoevaluacion: CalcularResultadoAutoevaluacionUseCase,
    private val consultarEstadoCicloAutoevaluacion: ConsultarEstadoCicloAutoevaluacionUseCase,
    private val reloj: Clock,
) {

    /**
     * @param cohorte cohorte para la tasa de deserción; a diferencia de los
     *   demás indicadores, esa cohorte es obligatoria en su caso de uso
     *   (`DESIGN_SGVA.md` §2) y no tiene un valor por defecto razonable para
     *   un reporte sin cohorte específica — se incluye solo cuando se indica.
     * @param programa programa para el resultado de autoevaluación (Módulo
     *   4); al igual que la cohorte, no tiene un valor por defecto razonable
     *   y se incluye solo cuando se indica junto con [periodo].
     */
    fun ejecutar(
        periodo: String? = null,
        facultad: String? = null,
        cohorte: String? = null,
        programa: String? = null,
    ): ReporteEjecutivo {
        val indicadores = mutableListOf<IndicadorCalidad>()
        indicadores += calcularEvolucionMatricula.ejecutar(facultad)
        indicadores += sinDatosInsuficientes { consolidarSaberPro.ejecutar(facultad) }
        indicadores += sinDatosInsuficientes { calcularDistribucionFormacion.ejecutar(periodo, facultad) }
        indicadores += sinDatosInsuficientes { listOf(calcularCapacidadInstalada.ejecutar(periodo, facultad)) }

        cohorte?.trim()?.takeIf { it.isNotEmpty() }?.let { cohorteIndicada ->
            indicadores += sinDatosInsuficientes { listOf(calcularTasaDesercion.ejecutar(cohorteIndicada, facultad)) }
        }

        return ReporteEjecutivo(
            indicadores = indicadores.toList(),
            respuestasEncuesta = consultarRespuestasEncuesta.listarTodas(),
            resultadoAutoevaluacion = resultadoAutoevaluacion(programa, periodo),
            estadoCiclo = estadoCiclo(programa, periodo),
        )
    }

    /**
     * El resultado de autoevaluación solo se incluye cuando se indican tanto
     * el programa como el periodo, y se omite si el periodo no tiene el
     * formato `AAAAS` esperado o si no hay ningún dato calculable para esa
     * combinación (todos los promedios ausentes), igual que las demás
     * secciones del reporte.
     */
    private fun resultadoAutoevaluacion(programa: String?, periodo: String?): ResultadoAutoevaluacion? {
        val programaIndicado = programa?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        val periodoIndicado = periodo?.trim()?.takeIf { it.isNotEmpty() } ?: return null

        val resultado = try {
            calcularResultadoAutoevaluacion.ejecutar(programaIndicado, periodoIndicado)
        } catch (formatoInvalido: FormatoPeriodoInvalidoException) {
            return null
        } catch (sinPonderaciones: DatosInsuficientesException) {
            // Todavía no se ha cargado el archivo de ponderaciones: la sección se omite.
            return null
        }

        val hayDatos = resultado.promedioGlobal != null ||
            resultado.promediosPorFactor.values.any { it != null } ||
            resultado.promediosPorCaracteristica.values.any { it != null }

        return resultado.takeIf { hayDatos }
    }

    /**
     * El estado del ciclo solo se incluye cuando se indican tanto el
     * programa como el periodo y existe un ciclo registrado con esa clave
     * (Módulo 5, V6); se omite en cualquier otro caso, igual que las demás
     * secciones del reporte. La comparación con el ciclo anterior, si existe,
     * queda embebida en [EstadoCicloAutoevaluacion.comparacionCicloAnterior].
     */
    private fun estadoCiclo(programa: String?, periodo: String?): EstadoCicloAutoevaluacion? {
        val programaIndicado = programa?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        val periodoIndicado = periodo?.trim()?.takeIf { it.isNotEmpty() } ?: return null

        return try {
            consultarEstadoCicloAutoevaluacion.ejecutar(programaIndicado, periodoIndicado, LocalDate.now(reloj))
        } catch (sinPonderaciones: DatosInsuficientesException) {
            // El avance de un ciclo con grupos evaluadores necesita las ponderaciones: la sección se omite.
            null
        }
    }

    /**
     * Un reporte ejecutivo agrega lo que hay disponible: si una sección no
     * tiene datos suficientes para calcularse (ej. sin puntajes Saber Pro
     * aún), se omite en vez de impedir la generación del resto del reporte.
     */
    private fun sinDatosInsuficientes(calculo: () -> List<IndicadorCalidad>): List<IndicadorCalidad> =
        try {
            calculo()
        } catch (sinDatos: DatosInsuficientesException) {
            emptyList()
        }
}

package com.example.sgva.usecases

import com.example.sgva.domain.DatosInsuficientesException
import com.example.sgva.domain.IndicadorCalidad
import com.example.sgva.domain.ReporteEjecutivo

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
) {

    /**
     * @param cohorte cohorte para la tasa de deserción; a diferencia de los
     *   demás indicadores, esa cohorte es obligatoria en su caso de uso
     *   (`DESIGN_SGVA.md` §2) y no tiene un valor por defecto razonable para
     *   un reporte sin cohorte específica — se incluye solo cuando se indica.
     */
    fun ejecutar(periodo: String? = null, facultad: String? = null, cohorte: String? = null): ReporteEjecutivo {
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
        )
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

package com.example.sgva.domain

/**
 * Reporte ejecutivo consolidado: agrupa los indicadores de calidad calculados
 * (Módulos 1 y 2, `DESIGN_SGVA.md` §2) con las respuestas de encuesta de
 * percepción ya registradas (Módulo 3), listas para exportarse. Objeto de
 * valor puro, sin dependencia de ninguna tecnología de generación de
 * documentos: el formato físico (PDF) es responsabilidad exclusiva de la
 * infraestructura (`GeneradorReportePdf`).
 *
 * @property indicadores indicadores de calidad calculados para el reporte.
 * @property respuestasEncuesta respuestas de encuesta de percepción ya registradas en el sistema.
 * @property resultadoAutoevaluacion resultado de autoevaluación del programa indicado (Módulo 4),
 *   o ausente si no se indicó un programa o no hay datos suficientes para calcularlo.
 * @property estadoCiclo estado del ciclo de autoevaluación del programa y periodo indicados
 *   (Módulo 5, V6), o ausente si no se indicaron ambos o no hay ningún ciclo registrado con esa clave.
 */
data class ReporteEjecutivo(
    val indicadores: List<IndicadorCalidad>,
    val respuestasEncuesta: List<RespuestaEncuesta>,
    val resultadoAutoevaluacion: ResultadoAutoevaluacion? = null,
    val estadoCiclo: EstadoCicloAutoevaluacion? = null,
)

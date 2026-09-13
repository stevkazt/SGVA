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
 */
data class ReporteEjecutivo(
    val indicadores: List<IndicadorCalidad>,
    val respuestasEncuesta: List<RespuestaEncuesta>,
)

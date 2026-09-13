package com.example.sgva.infrastructure.web

import com.example.sgva.infrastructure.reporting.GeneradorReportePdf
import com.example.sgva.usecases.GenerarReporteEjecutivoUseCase
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Adaptador de entrada REST (Primary Adapter) de los reportes ejecutivos
 * (Fase 4 del anteproyecto — integración de reportes, `DESIGN_SGVA.md` §4).
 * Delega la consolidación de datos en [GenerarReporteEjecutivoUseCase] y la
 * generación física del PDF en [GeneradorReportePdf]; no reimplementa
 * lógica de negocio ni de renderizado.
 */
@RestController
@RequestMapping("/api/reportes")
class ReporteController(
    private val generarReporteEjecutivo: GenerarReporteEjecutivoUseCase,
    private val generadorReportePdf: GeneradorReportePdf,
) {

    @GetMapping("/ejecutivo", produces = [MediaType.APPLICATION_PDF_VALUE])
    fun obtenerReporteEjecutivo(
        @RequestParam periodo: String?,
        @RequestParam facultad: String?,
        @RequestParam cohorte: String?,
    ): ResponseEntity<ByteArray> {
        val reporte = generarReporteEjecutivo.ejecutar(periodo, facultad, cohorte)
        val pdf = generadorReportePdf.generar(reporte)

        val nombreArchivo = periodo?.trim()?.takeIf { it.isNotEmpty() }
            ?.let { "reporte-ejecutivo-$it.pdf" }
            ?: "reporte-ejecutivo.pdf"

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment().filename(nombreArchivo).build().toString(),
            )
            .body(pdf)
    }
}

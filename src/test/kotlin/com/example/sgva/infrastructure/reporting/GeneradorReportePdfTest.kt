package com.example.sgva.infrastructure.reporting

import com.example.sgva.domain.IndicadorCalidad
import com.example.sgva.domain.ReporteEjecutivo
import com.example.sgva.domain.ResultadoAutoevaluacion
import com.example.sgva.domain.RespuestaEncuesta
import com.example.sgva.domain.TipoEstamento
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeneradorReportePdfTest {

    private val generador = GeneradorReportePdf()

    @Test
    fun `genera bytes de un PDF valido con el contenido del reporte`() {
        val reporte = ReporteEjecutivo(
            indicadores = listOf(
                IndicadorCalidad(IndicadorCalidad.TASA_DESERCION, 12.5, "20231", "Ingeniería"),
            ),
            respuestasEncuesta = listOf(
                RespuestaEncuesta("R001", TipoEstamento.ESTUDIANTE, "Infraestructura", 4, "20261"),
            ),
        )

        val pdf = generador.generar(reporte)

        assertTrue(pdf.isNotEmpty())
        assertEquals("%PDF", String(pdf.copyOfRange(0, 4), Charsets.US_ASCII))

        val texto = extraerTexto(pdf)
        assertTrue(texto.contains("Reporte Ejecutivo SGVA"))
        assertTrue(texto.contains(IndicadorCalidad.TASA_DESERCION))
        assertTrue(texto.contains("R001"))
        assertTrue(texto.contains("Infraestructura"))
    }

    @Test
    fun `las secciones sin datos muestran un mensaje en vez de una tabla vacia`() {
        val reporte = ReporteEjecutivo(indicadores = emptyList(), respuestasEncuesta = emptyList())

        val texto = extraerTexto(generador.generar(reporte))

        assertTrue(texto.contains("No hay indicadores disponibles."))
        assertTrue(texto.contains("No hay respuestas de encuesta registradas."))
    }

    @Test
    fun `incluye el resultado de autoevaluacion con sus promedios por caracteristica, factor y global`() {
        val reporte = ReporteEjecutivo(
            indicadores = emptyList(),
            respuestasEncuesta = emptyList(),
            resultadoAutoevaluacion = ResultadoAutoevaluacion(
                programa = "EGSTI",
                periodo = "20261",
                promediosPorCaracteristica = mapOf("C1" to 4.0, "C2" to null),
                promediosPorFactor = mapOf("F1" to 4.5),
                promedioGlobal = 4.5,
            ),
        )

        val texto = extraerTexto(generador.generar(reporte))

        assertTrue(texto.contains("Resultado de Autoevaluación"))
        assertTrue(texto.contains("EGSTI"))
        assertTrue(texto.contains("20261"))
        assertTrue(texto.contains("C1"))
        assertTrue(texto.contains("4.00"))
        assertTrue(texto.contains("F1"))
        assertTrue(texto.contains("Sin datos"))
    }

    @Test
    fun `omite la seccion de resultado de autoevaluacion cuando no hay datos suficientes`() {
        val reporte = ReporteEjecutivo(indicadores = emptyList(), respuestasEncuesta = emptyList())

        val texto = extraerTexto(generador.generar(reporte))

        assertTrue(!texto.contains("Resultado de Autoevaluación"))
    }

    @Test
    fun `un reporte con muchas filas genera varias paginas sin fallar`() {
        val indicadores = (1..80).map {
            IndicadorCalidad("Indicador $it", it.toDouble(), "20261", "Ingeniería")
        }
        val respuestas = (1..80).map {
            RespuestaEncuesta("R$it", TipoEstamento.ESTUDIANTE, "Factor $it", 3, "20261")
        }
        val reporte = ReporteEjecutivo(indicadores = indicadores, respuestasEncuesta = respuestas)

        val pdf = generador.generar(reporte)

        Loader.loadPDF(pdf).use { documento ->
            assertTrue(documento.numberOfPages > 1)
        }
        val texto = extraerTexto(pdf)
        assertTrue(texto.contains("Indicador 80"))
        assertTrue(texto.contains("R80"))
    }

    private fun extraerTexto(pdf: ByteArray): String =
        Loader.loadPDF(pdf).use { documento -> PDFTextStripper().getText(documento) }
}

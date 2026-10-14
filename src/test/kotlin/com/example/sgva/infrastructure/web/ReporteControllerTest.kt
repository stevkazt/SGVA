package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioAcademicoFalso
import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.EvaluacionDocumental
import com.example.sgva.domain.RespuestaEncuesta
import com.example.sgva.domain.TipoEstamento
import com.example.sgva.usecases.RegistrarCicloAutoevaluacionUseCase
import com.example.sgva.infrastructure.reporting.GeneradorReportePdf
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertTrue

/**
 * Pruebas de integración con `MockMvc` de [ReporteController]: ejercita el
 * controlador, el caso de uso real de consolidación y el generador de PDF
 * real (no un doble), igual que el resto de los controladores REST.
 */
@WebMvcTest(controllers = [ReporteController::class])
@Import(ConfiguracionCasosDeUsoDePrueba::class, GeneradorReportePdf::class)
class ReporteControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repositorio: RepositorioAcademicoFalso

    @BeforeEach
    fun limpiar() {
        repositorio.limpiar()
    }

    @Test
    fun `el reporte ejecutivo devuelve 200 con un PDF adjunto`() {
        repositorio.guardarRespuestaEncuesta(
            RespuestaEncuesta("R1", TipoEstamento.ESTUDIANTE, "Infraestructura", 4, "20261"),
        )

        val resultado = mockMvc.perform(get("/api/reportes/ejecutivo"))
            .andExpect(status().isOk)
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_PDF_VALUE))
            .andExpect(header().string("Content-Disposition", containsString("reporte-ejecutivo.pdf")))
            .andReturn()

        val pdf = resultado.response.contentAsByteArray
        assertTrue(pdf.isNotEmpty())
        assertTrue(String(pdf.copyOfRange(0, 4), Charsets.US_ASCII) == "%PDF")
    }

    @Test
    fun `el nombre del archivo incluye el periodo cuando se indica`() {
        mockMvc.perform(get("/api/reportes/ejecutivo").param("periodo", "20261"))
            .andExpect(status().isOk)
            .andExpect(header().string("Content-Disposition", containsString("reporte-ejecutivo-20261.pdf")))
    }

    @Test
    fun `incluye el resultado de autoevaluacion cuando se indican programa y periodo con datos`() {
        repositorio.guardarEvaluacionDocumental(
            EvaluacionDocumental(programa = "EGSTI", caracteristica = "C1", periodo = "20261", calificacion = 4.0),
        )
        repositorio.guardarEvaluacionDocumental(
            EvaluacionDocumental(programa = "EGSTI", caracteristica = "C2", periodo = "20261", calificacion = 5.0),
        )

        val resultado = mockMvc.perform(
            get("/api/reportes/ejecutivo").param("periodo", "20261").param("programa", "EGSTI"),
        )
            .andExpect(status().isOk)
            .andReturn()

        val texto = extraerTexto(resultado.response.contentAsByteArray)
        assertTrue(texto.contains("Resultado de Autoevaluación"))
        assertTrue(texto.contains("EGSTI"))
    }

    @Test
    fun `omite el resultado de autoevaluacion cuando no se indica un programa`() {
        val resultado = mockMvc.perform(get("/api/reportes/ejecutivo").param("periodo", "20261"))
            .andExpect(status().isOk)
            .andReturn()

        val texto = extraerTexto(resultado.response.contentAsByteArray)
        assertTrue(!texto.contains("Resultado de Autoevaluación"))
    }

    @Test
    fun `incluye el estado del ciclo cuando se indican programa y periodo con un ciclo registrado`() {
        RegistrarCicloAutoevaluacionUseCase(repositorio).ejecutar(
            CicloAutoevaluacion(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261"),
        )

        val resultado = mockMvc.perform(
            get("/api/reportes/ejecutivo").param("periodo", "20261").param("programa", "EGSTI"),
        )
            .andExpect(status().isOk)
            .andReturn()

        val texto = extraerTexto(resultado.response.contentAsByteArray)
        assertTrue(texto.contains("Estado del Ciclo de Autoevaluación"))
    }

    @Test
    fun `omite el estado del ciclo cuando no hay un ciclo registrado para el programa y periodo indicados`() {
        val resultado = mockMvc.perform(
            get("/api/reportes/ejecutivo").param("periodo", "20261").param("programa", "EGSTI"),
        )
            .andExpect(status().isOk)
            .andReturn()

        val texto = extraerTexto(resultado.response.contentAsByteArray)
        assertTrue(!texto.contains("Estado del Ciclo de Autoevaluación"))
    }

    private fun extraerTexto(pdf: ByteArray): String =
        Loader.loadPDF(pdf).use { documento -> PDFTextStripper().getText(documento) }
}

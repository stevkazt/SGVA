package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioAcademicoFalso
import com.example.sgva.domain.RespuestaEncuesta
import com.example.sgva.domain.TipoEstamento
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Pruebas de integración con `MockMvc` de [EncuestaController]: ejercitan el
 * controlador, los casos de uso reales del Módulo 3 y [ManejadorGlobalDeErrores]
 * juntos, igual que [EstudianteControllerTest] e [IndicadorControllerTest].
 */
@WebMvcTest(controllers = [EncuestaController::class])
@Import(ConfiguracionCasosDeUsoDePrueba::class)
class EncuestaControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repositorio: RepositorioAcademicoFalso

    @BeforeEach
    fun limpiar() {
        repositorio.limpiar()
    }

    @Test
    fun `registrar una respuesta valida devuelve 201 con el recurso creado y el header Location`() {
        mockMvc.perform(
            post("/api/encuestas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"id":"R1","estamento":"ESTUDIANTE","factor":"Infraestructura",
                        "calificacion":4,"periodo":"20261"}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/api/encuestas/R1"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("R1"))
            .andExpect(jsonPath("$.calificacion").value(4))

        assertEquals("R1", repositorio.buscarRespuestaEncuestaPorId("R1")?.id)
    }

    @Test
    fun `registrar una respuesta con id ya existente devuelve 409 sin sobrescribir la original`() {
        repositorio.guardarRespuestaEncuesta(respuesta(id = "R1", calificacion = 5))

        mockMvc.perform(
            post("/api/encuestas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"id":"R1","estamento":"PROFESOR","factor":"Infraestructura",
                        "calificacion":1,"periodo":"20261"}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.mensaje").value("Ya existe un registro de respuesta de encuesta con id 'R1'."))

        assertEquals(5, repositorio.buscarRespuestaEncuestaPorId("R1")?.calificacion)
    }

    @Test
    fun `registrar una respuesta con calificacion fuera de rango devuelve 400 con el mensaje de dominio`() {
        mockMvc.perform(
            post("/api/encuestas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"id":"R2","estamento":"ESTUDIANTE","factor":"Infraestructura",
                        "calificacion":6,"periodo":"20261"}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)

        assertTrue(repositorio.listarRespuestasEncuesta().isEmpty())
    }

    @Test
    fun `listar devuelve las respuestas registradas en orden de insercion`() {
        repositorio.guardarRespuestaEncuesta(respuesta(id = "R1"))
        repositorio.guardarRespuestaEncuesta(respuesta(id = "R2"))

        mockMvc.perform(get("/api/encuestas"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value("R1"))
            .andExpect(jsonPath("$[1].id").value("R2"))
    }

    @Test
    fun `buscar por id existente devuelve 200 con la respuesta`() {
        repositorio.guardarRespuestaEncuesta(respuesta(id = "R1"))

        mockMvc.perform(get("/api/encuestas/R1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("R1"))
    }

    @Test
    fun `buscar por id inexistente devuelve 404`() {
        mockMvc.perform(get("/api/encuestas/NOEXISTE"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `cargar un archivo csv valido registra las respuestas y devuelve 201 con el resumen`() {
        val csv = """
            id,estamento,factor,calificacion,periodo
            R100,ESTUDIANTE,Infraestructura,4,20261
            R101,PROFESOR,Plan de Estudios,5,20261
        """.trimIndent()
        val archivo = MockMultipartFile("archivo", "encuestas.csv", "text/csv", csv.toByteArray())

        mockMvc.perform(multipart("/api/encuestas/archivos").file(archivo))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.nombreArchivo").value("encuestas.csv"))
            .andExpect(jsonPath("$.totalRegistrosLeidos").value(2))
            .andExpect(jsonPath("$.registrados").value(2))
            .andExpect(jsonPath("$.omitidos.length()").value(0))

        assertEquals(2, repositorio.listarRespuestasEncuesta().size)
    }

    @Test
    fun `cargar un archivo con formato no soportado devuelve 400 sin registrar nada`() {
        val archivo = MockMultipartFile("archivo", "encuestas.txt", "text/plain", "id\nR1".toByteArray())

        mockMvc.perform(multipart("/api/encuestas/archivos").file(archivo))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.mensaje").value(containsString(".csv")))

        assertTrue(repositorio.listarRespuestasEncuesta().isEmpty())
    }

    @Test
    fun `ponderacion-likert devuelve 200 con el promedio por factor y estamento`() {
        repositorio.guardarRespuestaEncuesta(respuesta(id = "R1", calificacion = 4))
        repositorio.guardarRespuestaEncuesta(respuesta(id = "R2", calificacion = 2))

        mockMvc.perform(get("/api/encuestas/ponderacion-likert"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].factor").value("Infraestructura"))
            .andExpect(jsonPath("$[0].estamento").value("ESTUDIANTE"))
            .andExpect(jsonPath("$[0].promedio").value(3.0))
            .andExpect(jsonPath("$[0].periodo").value("CONSOLIDADO"))
    }

    @Test
    fun `ponderacion-likert sin respuestas registradas devuelve 422 (datos insuficientes)`() {
        mockMvc.perform(get("/api/encuestas/ponderacion-likert"))
            .andExpect(status().isUnprocessableContent)
    }

    @Test
    fun `matriz-radar devuelve 200 con los factores y series organizados por estamento`() {
        repositorio.guardarRespuestaEncuesta(respuesta(id = "R1", calificacion = 4))
        repositorio.guardarRespuestaEncuesta(
            respuesta(id = "R2", estamento = TipoEstamento.PROFESOR, calificacion = 2),
        )

        mockMvc.perform(get("/api/encuestas/matriz-radar"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.factores.length()").value(1))
            .andExpect(jsonPath("$.factores[0]").value("Infraestructura"))
            .andExpect(jsonPath("$.series.length()").value(2))
    }

    @Test
    fun `matriz-radar sin respuestas registradas devuelve 422 (datos insuficientes)`() {
        mockMvc.perform(get("/api/encuestas/matriz-radar"))
            .andExpect(status().isUnprocessableContent)
    }

    private fun respuesta(
        id: String,
        estamento: TipoEstamento = TipoEstamento.ESTUDIANTE,
        calificacion: Int = 4,
    ): RespuestaEncuesta = RespuestaEncuesta(
        id = id,
        estamento = estamento,
        factor = "Infraestructura",
        calificacion = calificacion,
        periodo = "20261",
    )
}

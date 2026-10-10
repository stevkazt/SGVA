package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioAcademicoFalso
import com.example.sgva.domain.EvaluacionDocumental
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Pruebas de integración con `MockMvc` de [EvaluacionDocumentalController]:
 * mismo enfoque que [ResultadoEncuestaPreguntaControllerTest], ejercitando
 * controlador, casos de uso reales y [ManejadorGlobalDeErrores] juntos.
 */
@WebMvcTest(controllers = [EvaluacionDocumentalController::class])
@Import(ConfiguracionCasosDeUsoDePrueba::class)
class EvaluacionDocumentalControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repositorio: RepositorioAcademicoFalso

    @BeforeEach
    fun limpiar() {
        repositorio.limpiar()
    }

    @Test
    fun `registrar una evaluacion valida devuelve 201 con el recurso creado y su valoracion derivada`() {
        mockMvc.perform(
            post("/api/evaluaciones-documentales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"programa":"EGSTI","caracteristica":"1.1","periodo":"20261",
                        "calificacion":4.7,"justificacion":"Evidencia consistente"}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.caracteristica").value("1.1"))
            .andExpect(jsonPath("$.calificacion").value(4.7))
            .andExpect(jsonPath("$.valoracionCualitativa").value("SE_CUMPLE_PLENAMENTE"))

        assertEquals(1, repositorio.listarEvaluacionesDocumentales().size)
    }

    @Test
    fun `registrar una clave natural ya existente devuelve 409 sin sobrescribir el original`() {
        repositorio.guardarEvaluacionDocumental(evaluacion(caracteristica = "1.1", calificacion = 4.5))

        mockMvc.perform(
            post("/api/evaluaciones-documentales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"programa":"EGSTI","caracteristica":"1.1","periodo":"20261","calificacion":1.0}""",
                ),
        )
            .andExpect(status().isConflict)

        assertEquals(4.5, repositorio.buscarEvaluacionDocumental("EGSTI", "20261", "1.1")?.calificacion)
    }

    @Test
    fun `registrar una calificacion de 0 devuelve 400 con el mensaje de dominio`() {
        mockMvc.perform(
            post("/api/evaluaciones-documentales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"programa":"EGSTI","caracteristica":"1.1","periodo":"20261","calificacion":0.0}""",
                ),
        )
            .andExpect(status().isBadRequest)

        assertTrue(repositorio.listarEvaluacionesDocumentales().isEmpty())
    }

    @Test
    fun `listar devuelve las evaluaciones registradas en orden de insercion`() {
        repositorio.guardarEvaluacionDocumental(evaluacion(caracteristica = "1.1"))
        repositorio.guardarEvaluacionDocumental(evaluacion(caracteristica = "1.2"))

        mockMvc.perform(get("/api/evaluaciones-documentales"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].caracteristica").value("1.1"))
            .andExpect(jsonPath("$[1].caracteristica").value("1.2"))
    }

    @Test
    fun `listar filtra por programa y periodo`() {
        repositorio.guardarEvaluacionDocumental(evaluacion(caracteristica = "1.1", programa = "EGSTI"))
        repositorio.guardarEvaluacionDocumental(evaluacion(caracteristica = "1.2", programa = "OTRO"))

        mockMvc.perform(get("/api/evaluaciones-documentales").param("programa", "EGSTI"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].programa").value("EGSTI"))
    }

    @Test
    fun `cargar un archivo csv valido registra las evaluaciones y devuelve 201 con el resumen`() {
        val csv = """
            programa,caracteristica,periodo,calificacion,justificacion,sugerenciasMejora
            EGSTI,1.1,20261,4.7,Evidencia consistente,
            EGSTI,1.2,20261,3.5,,Fortalecer el seguimiento
        """.trimIndent()
        val archivo = MockMultipartFile("archivo", "evaluaciones.csv", "text/csv", csv.toByteArray())

        mockMvc.perform(multipart("/api/evaluaciones-documentales/archivos").file(archivo))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.nombreArchivo").value("evaluaciones.csv"))
            .andExpect(jsonPath("$.totalRegistrosLeidos").value(2))
            .andExpect(jsonPath("$.registrados").value(2))
            .andExpect(jsonPath("$.omitidos.length()").value(0))

        assertEquals(2, repositorio.listarEvaluacionesDocumentales().size)
    }

    @Test
    fun `cargar un archivo con formato no soportado devuelve 400 sin registrar nada`() {
        val archivo = MockMultipartFile(
            "archivo",
            "evaluaciones.txt",
            "text/plain",
            "programa\nEGSTI".toByteArray(),
        )

        mockMvc.perform(multipart("/api/evaluaciones-documentales/archivos").file(archivo))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.mensaje").value(containsString(".csv")))

        assertTrue(repositorio.listarEvaluacionesDocumentales().isEmpty())
    }

    private fun evaluacion(
        caracteristica: String,
        programa: String = "EGSTI",
        calificacion: Double = 4.0,
    ): EvaluacionDocumental = EvaluacionDocumental(
        programa = programa,
        caracteristica = caracteristica,
        periodo = "20261",
        calificacion = calificacion,
    )
}

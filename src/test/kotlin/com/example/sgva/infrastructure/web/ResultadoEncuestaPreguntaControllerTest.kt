package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioAcademicoFalso
import com.example.sgva.domain.ResultadoEncuestaPregunta
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Pruebas de integración con `MockMvc` de [ResultadoEncuestaPreguntaController]:
 * mismo enfoque que [EncuestaControllerTest] y [DocenteControllerTest],
 * ejercitando controlador, casos de uso reales y [ManejadorGlobalDeErrores] juntos.
 */
@WebMvcTest(controllers = [ResultadoEncuestaPreguntaController::class])
@Import(ConfiguracionCasosDeUsoDePrueba::class)
class ResultadoEncuestaPreguntaControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repositorio: RepositorioAcademicoFalso

    @BeforeEach
    fun limpiar() {
        repositorio.limpiar()
    }

    @Test
    fun `registrar un resultado valido devuelve 201 con el recurso creado`() {
        mockMvc.perform(
            post("/api/resultados-encuesta-pregunta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"programa":"EGSTI","periodo":"20261","codigoPregunta":"A1",
                        "caracteristica":"1.1","estamentos":["ESTUDIANTE"],"promedio":4.5}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.codigoPregunta").value("A1"))
            .andExpect(jsonPath("$.promedio").value(4.5))

        assertEquals(1, repositorio.listarResultadosEncuestaPregunta().size)
    }

    @Test
    fun `registrar una clave natural ya existente devuelve 409 sin sobrescribir el original`() {
        repositorio.guardarResultadoEncuestaPregunta(resultado(codigoPregunta = "A1", promedio = 4.5))

        mockMvc.perform(
            post("/api/resultados-encuesta-pregunta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"programa":"EGSTI","periodo":"20261","codigoPregunta":"A1",
                        "caracteristica":"1.1","estamentos":["PROFESOR"],"promedio":1.0}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isConflict)

        assertEquals(4.5, repositorio.buscarResultadoEncuestaPregunta("EGSTI", "20261", "A1")?.promedio)
    }

    @Test
    fun `registrar un promedio igual a 0 devuelve 400 con el mensaje de dominio`() {
        mockMvc.perform(
            post("/api/resultados-encuesta-pregunta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"programa":"EGSTI","periodo":"20261","codigoPregunta":"A1",
                        "caracteristica":"1.1","estamentos":["ESTUDIANTE"],"promedio":0.0}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)

        assertTrue(repositorio.listarResultadosEncuestaPregunta().isEmpty())
    }

    @Test
    fun `listar devuelve los resultados registrados en orden de insercion`() {
        repositorio.guardarResultadoEncuestaPregunta(resultado(codigoPregunta = "A1"))
        repositorio.guardarResultadoEncuestaPregunta(resultado(codigoPregunta = "A2"))

        mockMvc.perform(get("/api/resultados-encuesta-pregunta"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].codigoPregunta").value("A1"))
            .andExpect(jsonPath("$[1].codigoPregunta").value("A2"))
    }

    @Test
    fun `listar filtra por programa y periodo`() {
        repositorio.guardarResultadoEncuestaPregunta(resultado(codigoPregunta = "A1", programa = "EGSTI"))
        repositorio.guardarResultadoEncuestaPregunta(resultado(codigoPregunta = "A2", programa = "OTRO"))

        mockMvc.perform(get("/api/resultados-encuesta-pregunta").param("programa", "EGSTI"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].programa").value("EGSTI"))
    }

    @Test
    fun `cargar un archivo csv valido registra los resultados y devuelve 201 con el resumen`() {
        val csv = """
            programa,periodo,codigoPregunta,caracteristica,estamentos,promedio
            EGSTI,20261,A1,1.1,ESTUDIANTE,4.5
            EGSTI,20261,A2,1.1,PROFESOR,N/A
        """.trimIndent()
        val archivo = MockMultipartFile("archivo", "resultados.csv", "text/csv", csv.toByteArray())

        mockMvc.perform(multipart("/api/resultados-encuesta-pregunta/archivos").file(archivo))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.nombreArchivo").value("resultados.csv"))
            .andExpect(jsonPath("$.totalRegistrosLeidos").value(2))
            .andExpect(jsonPath("$.registrados").value(2))
            .andExpect(jsonPath("$.omitidos.length()").value(0))

        assertEquals(2, repositorio.listarResultadosEncuestaPregunta().size)
    }

    @Test
    fun `cargar un archivo con formato no soportado devuelve 400 sin registrar nada`() {
        val archivo = MockMultipartFile(
            "archivo",
            "resultados.txt",
            "text/plain",
            "programa\nEGSTI".toByteArray(),
        )

        mockMvc.perform(multipart("/api/resultados-encuesta-pregunta/archivos").file(archivo))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.mensaje").value(containsString(".csv")))

        assertTrue(repositorio.listarResultadosEncuestaPregunta().isEmpty())
    }

    private fun resultado(
        codigoPregunta: String,
        programa: String = "EGSTI",
        promedio: Double? = 4.0,
    ): ResultadoEncuestaPregunta = ResultadoEncuestaPregunta(
        programa = programa,
        periodo = "20261",
        codigoPregunta = codigoPregunta,
        caracteristica = "1.1",
        estamentos = setOf(TipoEstamento.ESTUDIANTE),
        promedio = promedio,
    )
}

package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioAcademicoFalso
import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.PreevaluacionEvaluador
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

@WebMvcTest(controllers = [PreevaluacionController::class])
@Import(ConfiguracionCasosDeUsoDePrueba::class)
class PreevaluacionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repositorio: RepositorioAcademicoFalso

    @BeforeEach
    fun prepararCiclo() {
        repositorio.limpiar()
        repositorio.guardarCiclo(CicloAutoevaluacion(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261"))
    }

    @Test
    fun `registrar una preevaluacion valida devuelve 201`() {
        mockMvc.perform(
            post("/api/preevaluaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"ciclo":"EGSTI-20261","caracteristica":"1.1","evaluador":"EVAL-01","calificacion":4.5}"""),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.evaluador").value("EVAL-01"))
    }

    @Test
    fun `listar filtra por ciclo`() {
        repositorio.guardarPreevaluacion(
            PreevaluacionEvaluador(ciclo = "EGSTI-20261", caracteristica = "1.1", evaluador = "EVAL-01", calificacion = 4.0),
        )

        mockMvc.perform(get("/api/preevaluaciones").param("ciclo", "EGSTI-20261"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
    }

    @Test
    fun `cargar un archivo csv valido registra las preevaluaciones y devuelve 201`() {
        val csv = """
            ciclo,caracteristica,evaluador,calificacion
            EGSTI-20261,1.1,EVAL-01,4.5
            EGSTI-20261,1.2,EVAL-01,3.5
        """.trimIndent()
        val archivo = MockMultipartFile("archivo", "preevaluaciones.csv", "text/csv", csv.toByteArray())

        mockMvc.perform(multipart("/api/preevaluaciones/archivos").file(archivo))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.registrados").value(2))

        assertEquals(2, repositorio.listarPreevaluacionesPorCiclo("EGSTI-20261").size)
    }

    @Test
    fun `consolidar un ciclo con preevaluaciones devuelve las evaluaciones documentales generadas`() {
        repositorio.guardarPreevaluacion(
            PreevaluacionEvaluador(ciclo = "EGSTI-20261", caracteristica = "1.1", evaluador = "EVAL-01", calificacion = 4.0),
        )

        mockMvc.perform(post("/api/preevaluaciones/EGSTI-20261/consolidacion"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].caracteristica").value("1.1"))
    }
}

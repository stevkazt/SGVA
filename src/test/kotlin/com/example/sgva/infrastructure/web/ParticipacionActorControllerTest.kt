package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioAcademicoFalso
import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.ParticipacionActor
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

@WebMvcTest(controllers = [ParticipacionActorController::class])
@Import(ConfiguracionCasosDeUsoDePrueba::class)
class ParticipacionActorControllerTest {

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
    fun `registrar una participacion valida devuelve 201 con el porcentaje derivado`() {
        mockMvc.perform(
            post("/api/participacion-actores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"ciclo":"EGSTI-20261","actor":"Estudiantes","poblacion":100,"muestra":50,"respuestas":40}""",
                ),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.porcentajeDiligenciado").value(0.8))
    }

    @Test
    fun `registrar respuestas mayores que la muestra devuelve 400`() {
        mockMvc.perform(
            post("/api/participacion-actores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"ciclo":"EGSTI-20261","actor":"Estudiantes","poblacion":100,"muestra":10,"respuestas":20}""",
                ),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `listar filtra por ciclo`() {
        repositorio.guardarParticipacion(
            ParticipacionActor(ciclo = "EGSTI-20261", actor = "Profesores", poblacion = 40, muestra = 40, respuestas = 38),
        )

        mockMvc.perform(get("/api/participacion-actores").param("ciclo", "EGSTI-20261"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
    }

    @Test
    fun `cargar un archivo csv valido registra las participaciones y devuelve 201`() {
        val csv = """
            ciclo,actor,poblacion,muestra,respuestas
            EGSTI-20261,Estudiantes,500,200,160
            EGSTI-20261,Profesores,40,40,38
        """.trimIndent()
        val archivo = MockMultipartFile("archivo", "participacion.csv", "text/csv", csv.toByteArray())

        mockMvc.perform(multipart("/api/participacion-actores/archivos").file(archivo))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.registrados").value(2))
    }
}

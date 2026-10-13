package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioAcademicoFalso
import com.example.sgva.domain.CicloAutoevaluacion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [GrupoEvaluadorController::class])
@Import(ConfiguracionCasosDeUsoDePrueba::class)
class GrupoEvaluadorControllerTest {

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
    fun `registrar un grupo evaluador valido devuelve 201`() {
        mockMvc.perform(
            post("/api/ciclos/EGSTI-20261/grupos-evaluadores")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"factores":["F5","F9"],"taller":1}"""),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.ciclo").value("EGSTI-20261"))
            .andExpect(jsonPath("$.taller").value(1))
    }

    @Test
    fun `registrar el mismo taller dos veces devuelve 409`() {
        repositorio.guardarGrupoEvaluador(
            com.example.sgva.domain.GrupoEvaluador(ciclo = "EGSTI-20261", factores = setOf("F1"), taller = 1),
        )

        mockMvc.perform(
            post("/api/ciclos/EGSTI-20261/grupos-evaluadores")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"factores":["F2"],"taller":1}"""),
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `listar devuelve los grupos evaluadores del ciclo`() {
        repositorio.guardarGrupoEvaluador(
            com.example.sgva.domain.GrupoEvaluador(ciclo = "EGSTI-20261", factores = setOf("F1"), taller = 1),
        )

        mockMvc.perform(get("/api/ciclos/EGSTI-20261/grupos-evaluadores"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
    }
}

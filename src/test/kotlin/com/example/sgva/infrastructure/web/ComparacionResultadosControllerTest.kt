package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioAcademicoFalso
import com.example.sgva.domain.EvaluacionDocumental
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [ComparacionResultadosController::class])
@Import(ConfiguracionCasosDeUsoDePrueba::class)
class ComparacionResultadosControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repositorio: RepositorioAcademicoFalso

    @BeforeEach
    fun limpiar() {
        repositorio.limpiar()
    }

    @Test
    fun `comparar dos periodos devuelve la diferencia global`() {
        repositorio.guardarEvaluacionDocumental(
            EvaluacionDocumental(programa = "EGSTI", caracteristica = "C1", periodo = "20251", calificacion = 3.0),
        )
        repositorio.guardarEvaluacionDocumental(
            EvaluacionDocumental(programa = "EGSTI", caracteristica = "C1", periodo = "20261", calificacion = 4.0),
        )

        mockMvc.perform(
            get("/api/comparaciones").param("programa", "EGSTI").param("periodos", "20251,20261"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultados.length()").value(2))
            .andExpect(jsonPath("$.diferencias.length()").value(1))
            .andExpect(jsonPath("$.diferencias[0].diferenciaGlobal").value(1.0))
    }
}

package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioAcademicoFalso
import com.example.sgva.domain.EvaluacionDocumental
import com.example.sgva.domain.ResultadoEncuestaPregunta
import com.example.sgva.domain.TipoEstamento
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Pruebas de integración con `MockMvc` de [ResultadoAutoevaluacionController]:
 * mismo enfoque que [IndicadorControllerTest], ejercitando controlador, caso
 * de uso real y [ManejadorGlobalDeErrores] juntos. Usa el mismo catálogo
 * sintético de ponderaciones de [ConfiguracionCasosDeUsoDePrueba] (dos
 * factores F1/F2, mezcla encuestas/documental 40 %/60 %) que
 * `CalcularResultadoAutoevaluacionUseCaseTest`.
 */
@WebMvcTest(controllers = [ResultadoAutoevaluacionController::class])
@Import(ConfiguracionCasosDeUsoDePrueba::class)
class ResultadoAutoevaluacionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repositorio: RepositorioAcademicoFalso

    @BeforeEach
    fun limpiar() {
        repositorio.limpiar()
    }

    @Test
    fun `obtener devuelve 200 con el desglose por caracteristica, por factor y global`() {
        registrarDocumental("C1", 4.0)
        registrarDocumental("C2", 5.0)
        registrarDocumental("C3", 3.0)
        registrarDocumental("C4", 4.0)

        mockMvc.perform(get("/api/resultados-autoevaluacion").param("programa", "EGSTI").param("periodo", "20261"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.programa").value("EGSTI"))
            .andExpect(jsonPath("$.periodo").value("20261"))
            .andExpect(jsonPath("$.promediosPorCaracteristica.C1").value(4.0))
            .andExpect(jsonPath("$.promediosPorCaracteristica.C2").value(5.0))
            .andExpect(jsonPath("$.promediosPorFactor.F1").value(4.5))
            .andExpect(jsonPath("$.promediosPorFactor.F2").value(3.6999999999999997))
            .andExpect(jsonPath("$.promedioGlobal").value(4.18))
    }

    @Test
    fun `obtener mezcla encuesta y evaluacion documental de una caracteristica`() {
        registrarPregunta("A1", "C1", 3.0)
        registrarPregunta("A2", "C1", 5.0)
        registrarDocumental("C1", 4.0)

        mockMvc.perform(get("/api/resultados-autoevaluacion").param("programa", "EGSTI").param("periodo", "20261"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.promediosPorCaracteristica.C1").value(4.0))
    }

    @Test
    fun `obtener conserva el catalogo completo con caracteristicas y factores ausentes como null`() {
        mockMvc.perform(get("/api/resultados-autoevaluacion").param("programa", "EGSTI").param("periodo", "20261"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.promediosPorCaracteristica.length()").value(4))
            .andExpect(jsonPath("$.promediosPorCaracteristica.C1").doesNotExist())
            .andExpect(jsonPath("$.promediosPorFactor.length()").value(2))
            .andExpect(jsonPath("$.promedioGlobal").doesNotExist())
    }

    @Test
    fun `obtener solo usa datos del programa y periodo consultados`() {
        registrarDocumental("C1", 4.0)
        repositorio.guardarEvaluacionDocumental(
            EvaluacionDocumental(programa = "OTRO-PROGRAMA", caracteristica = "C1", periodo = "20261", calificacion = 1.0),
        )
        repositorio.guardarEvaluacionDocumental(
            EvaluacionDocumental(programa = "EGSTI", caracteristica = "C1", periodo = "20252", calificacion = 1.0),
        )

        mockMvc.perform(get("/api/resultados-autoevaluacion").param("programa", "EGSTI").param("periodo", "20261"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.promediosPorCaracteristica.C1").value(4.0))
    }

    @Test
    fun `obtener sin el parametro obligatorio programa devuelve 400`() {
        mockMvc.perform(get("/api/resultados-autoevaluacion").param("periodo", "20261"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `obtener sin el parametro obligatorio periodo devuelve 400`() {
        mockMvc.perform(get("/api/resultados-autoevaluacion").param("programa", "EGSTI"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `obtener con un periodo en formato invalido devuelve 400 con el mensaje de dominio`() {
        mockMvc.perform(get("/api/resultados-autoevaluacion").param("programa", "EGSTI").param("periodo", "2026"))
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.mensaje").value(
                    "El campo 'periodo' debe tener formato AAAAS " +
                        "(año de 4 dígitos + semestre 1 o 2, sin separadores); valor recibido: '2026'.",
                ),
            )
    }

    private fun registrarPregunta(codigoPregunta: String, caracteristica: String, promedio: Double?) {
        repositorio.guardarResultadoEncuestaPregunta(
            ResultadoEncuestaPregunta(
                programa = "EGSTI",
                periodo = "20261",
                codigoPregunta = codigoPregunta,
                caracteristica = caracteristica,
                estamentos = setOf(TipoEstamento.ESTUDIANTE),
                promedio = promedio,
            ),
        )
    }

    private fun registrarDocumental(caracteristica: String, calificacion: Double) {
        repositorio.guardarEvaluacionDocumental(
            EvaluacionDocumental(
                programa = "EGSTI",
                caracteristica = caracteristica,
                periodo = "20261",
                calificacion = calificacion,
            ),
        )
    }
}

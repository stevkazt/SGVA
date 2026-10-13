package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioAcademicoFalso
import com.example.sgva.domain.ActividadCiclo
import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.EstadoActividad
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
import java.time.LocalDate

/**
 * Pruebas de integración con `MockMvc` de [CicloAutoevaluacionController]:
 * mismo enfoque que [EvaluacionDocumentalControllerTest].
 */
@WebMvcTest(controllers = [CicloAutoevaluacionController::class])
@Import(ConfiguracionCasosDeUsoDePrueba::class)
class CicloAutoevaluacionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repositorio: RepositorioAcademicoFalso

    @BeforeEach
    fun limpiar() {
        repositorio.limpiar()
    }

    @Test
    fun `registrar un ciclo valido devuelve 201 con sus cuatro fases`() {
        mockMvc.perform(
            post("/api/ciclos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"id":"EGSTI-20261","programa":"EGSTI","periodo":"20261"}"""),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.ciclo.id").value("EGSTI-20261"))
            .andExpect(jsonPath("$.fases.length()").value(4))
    }

    @Test
    fun `registrar un ciclo con actividades iniciales las devuelve en el detalle`() {
        mockMvc.perform(
            post("/api/ciclos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"id":"EGSTI-20261","programa":"EGSTI","periodo":"20261","actividades":[
                        {"id":"1.1","fase":1,"descripcion":"Sensibilización",
                         "responsable":"Comité de calidad","fechaInicioPlanificada":"2026-01-01",
                         "fechaFinPlanificada":"2026-02-01","estado":"PLANIFICADA"}]}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.actividades.length()").value(1))
            .andExpect(jsonPath("$.actividades[0].id").value("1.1"))
    }

    @Test
    fun `registrar un segundo ciclo del mismo programa y periodo devuelve 409`() {
        repositorio.guardarCiclo(CicloAutoevaluacion(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261"))

        mockMvc.perform(
            post("/api/ciclos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"id":"EGSTI-20261-B","programa":"EGSTI","periodo":"20261"}"""),
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun `listar filtra por programa y periodo`() {
        repositorio.guardarCiclo(CicloAutoevaluacion(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261"))
        repositorio.guardarCiclo(CicloAutoevaluacion(id = "OTRO-20261", programa = "OTRO", periodo = "20261"))

        mockMvc.perform(get("/api/ciclos").param("programa", "EGSTI"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].programa").value("EGSTI"))
    }

    @Test
    fun `buscar el detalle de un ciclo inexistente devuelve 404`() {
        mockMvc.perform(get("/api/ciclos/NO-EXISTE"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `registrar una actividad adicional en un ciclo existente devuelve 201`() {
        repositorio.guardarCiclo(CicloAutoevaluacion(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261"))

        mockMvc.perform(
            post("/api/ciclos/EGSTI-20261/actividades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"id":"2.1","fase":2,"descripcion":"Taller de grupos evaluadores",
                        "responsable":"DAC","fechaInicioPlanificada":"2026-03-01",
                        "fechaFinPlanificada":"2026-03-15","estado":"PLANIFICADA"}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value("2.1"))
    }

    @Test
    fun `consultar el avance de un ciclo devuelve el resumen calculado`() {
        repositorio.guardarCiclo(CicloAutoevaluacion(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261"))
        repositorio.guardarActividad(
            ActividadCiclo(
                id = "1.1",
                ciclo = "EGSTI-20261",
                fase = 1,
                descripcion = "Sensibilización",
                responsable = "Comité de calidad",
                fechaInicioPlanificada = LocalDate.of(2026, 1, 1),
                fechaFinPlanificada = LocalDate.of(2026, 2, 1),
                estado = EstadoActividad.COMPLETADA,
            ),
        )

        mockMvc.perform(get("/api/ciclos/EGSTI-20261/avance").param("fechaReferencia", "2026-06-01"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.avancePorFase.1").value(1.0))
    }
}

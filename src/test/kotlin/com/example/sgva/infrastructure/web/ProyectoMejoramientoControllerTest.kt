package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioAcademicoFalso
import com.example.sgva.domain.EstadoProyecto
import com.example.sgva.domain.OrigenProyecto
import com.example.sgva.domain.ProyectoMejoramiento
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

@WebMvcTest(controllers = [ProyectoMejoramientoController::class])
@Import(ConfiguracionCasosDeUsoDePrueba::class)
class ProyectoMejoramientoControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repositorio: RepositorioAcademicoFalso

    @BeforeEach
    fun limpiar() {
        repositorio.limpiar()
    }

    @Test
    fun `registrar un proyecto valido devuelve 201`() {
        mockMvc.perform(
            post("/api/proyectos-mejoramiento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"id":"PM-001","programa":"EGSTI","nombre":"Fortalecimiento docente",
                        "aspectosMejora":["Formación docente"],"origen":"AUTOEVALUACION",
                        "objetivo":"Aumentar el nivel de formación","actividades":["Convocatoria de becas"],
                        "factores":["F3"],"estado":"PLANIFICADO"}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value("PM-001"))
    }

    @Test
    fun `registrar un proyecto sin factores devuelve 400`() {
        mockMvc.perform(
            post("/api/proyectos-mejoramiento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """{"id":"PM-002","programa":"EGSTI","nombre":"Proyecto sin factor",
                        "aspectosMejora":[],"origen":"RECOMENDACION_CNA",
                        "objetivo":"Objetivo","actividades":[],"factores":[],"estado":"PLANIFICADO"}
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `listar filtra por programa`() {
        repositorio.guardarProyecto(proyecto("PM-001", "EGSTI"))
        repositorio.guardarProyecto(proyecto("PM-002", "OTRO"))

        mockMvc.perform(get("/api/proyectos-mejoramiento").param("programa", "EGSTI"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
    }

    @Test
    fun `buscar un id inexistente devuelve 404`() {
        mockMvc.perform(get("/api/proyectos-mejoramiento/NO-EXISTE"))
            .andExpect(status().isNotFound)
    }

    private fun proyecto(id: String, programa: String): ProyectoMejoramiento = ProyectoMejoramiento(
        id = id,
        programa = programa,
        nombre = "Proyecto de prueba",
        aspectosMejora = listOf("Aspecto 1"),
        origen = OrigenProyecto.AUTOEVALUACION,
        objetivo = "Objetivo de prueba",
        actividades = listOf("Actividad 1"),
        factores = setOf("F1"),
        estado = EstadoProyecto.PLANIFICADO,
    )
}

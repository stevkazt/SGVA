package com.example.sgva.infrastructure.web

import com.example.sgva.application.RepositorioAcademicoFalso
import com.example.sgva.domain.Docente
import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.NivelFormacion
import com.example.sgva.domain.TipoDedicacion
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
 * Pruebas de integración con `MockMvc` de [IndicadorController]: los cinco
 * indicadores de calidad de los Módulos 1 y 2, incluido el caso de datos
 * insuficientes (`DatosInsuficientesException` → 422 vía
 * [ManejadorGlobalDeErrores]). El reloj fijo de [ConfiguracionCasosDeUsoDePrueba]
 * (`2026-06-01T00:00:00Z`) reproduce la ventana de 7 años usada en
 * `CalcularEvolucionMatriculaUseCaseTest` (`20201`..`20262`, 14 periodos).
 */
@WebMvcTest(controllers = [IndicadorController::class])
@Import(ConfiguracionCasosDeUsoDePrueba::class)
class IndicadorControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var repositorio: RepositorioAcademicoFalso

    @BeforeEach
    fun limpiar() {
        repositorio.limpiar()
    }

    @Test
    fun `evolucion-matricula devuelve 200 con los 14 periodos de la ventana y cuenta solo MATRICULADO`() {
        guardarEstudiante(cohorte = "20231", estado = EstadoEstudiante.MATRICULADO)
        guardarEstudiante(cohorte = "20231", estado = EstadoEstudiante.MATRICULADO)
        guardarEstudiante(cohorte = "20231", estado = EstadoEstudiante.DESERTOR)

        mockMvc.perform(get("/api/indicadores/evolucion-matricula"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(14))
            .andExpect(jsonPath("$[0].periodo").value("20201"))
            .andExpect(jsonPath("$[13].periodo").value("20262"))
            .andExpect(jsonPath("$[6].periodo").value("20231"))
            .andExpect(jsonPath("$[6].valor").value(2.0))
    }

    @Test
    fun `tasa-desercion devuelve 200 con el porcentaje de la cohorte solicitada`() {
        guardarEstudiante(cohorte = "20231", estado = EstadoEstudiante.DESERTOR)
        guardarEstudiante(cohorte = "20231", estado = EstadoEstudiante.MATRICULADO)
        guardarEstudiante(cohorte = "20231", estado = EstadoEstudiante.MATRICULADO)
        guardarEstudiante(cohorte = "20231", estado = EstadoEstudiante.GRADUADO)

        mockMvc.perform(get("/api/indicadores/tasa-desercion").param("cohorte", "20231"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nombre").value("Tasa de Deserción"))
            .andExpect(jsonPath("$.valor").value(25.0))
            .andExpect(jsonPath("$.periodo").value("20231"))
    }

    @Test
    fun `tasa-desercion sin el parametro obligatorio cohorte devuelve 400`() {
        mockMvc.perform(get("/api/indicadores/tasa-desercion"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `tasa-desercion para una cohorte sin estudiantes devuelve 422 (datos insuficientes)`() {
        mockMvc.perform(get("/api/indicadores/tasa-desercion").param("cohorte", "20991"))
            .andExpect(status().isUnprocessableContent)
            .andExpect(
                jsonPath("$.mensaje").value(
                    "No hay estudiantes registrados para la cohorte '20991'.",
                ),
            )
    }

    @Test
    fun `saber-pro devuelve 200 con el promedio global y por cohorte`() {
        guardarEstudiante(cohorte = "20231", estado = EstadoEstudiante.GRADUADO, puntajeSaberPro = 200)
        guardarEstudiante(cohorte = "20231", estado = EstadoEstudiante.GRADUADO, puntajeSaberPro = 220)

        mockMvc.perform(get("/api/indicadores/saber-pro"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].periodo").value("CONSOLIDADO"))
            .andExpect(jsonPath("$[0].valor").value(210.0))
            .andExpect(jsonPath("$[1].periodo").value("20231"))
            .andExpect(jsonPath("$[1].valor").value(210.0))
    }

    @Test
    fun `saber-pro sin ningun puntaje registrado devuelve 422 (datos insuficientes)`() {
        guardarEstudiante(cohorte = "20231", estado = EstadoEstudiante.MATRICULADO, puntajeSaberPro = null)

        mockMvc.perform(get("/api/indicadores/saber-pro"))
            .andExpect(status().isUnprocessableContent)
    }

    @Test
    fun `distribucion-formacion devuelve 200 con el porcentaje por nivel`() {
        guardarDocente(NivelFormacion.DOCTORADO)
        guardarDocente(NivelFormacion.MAESTRIA)
        guardarDocente(NivelFormacion.MAESTRIA)
        guardarDocente(NivelFormacion.MAESTRIA)

        mockMvc.perform(get("/api/indicadores/distribucion-formacion"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].nombre").value("Distribución de Formación - ESPECIALIZACION"))
            .andExpect(jsonPath("$[0].valor").value(0.0))
            .andExpect(jsonPath("$[1].nombre").value("Distribución de Formación - MAESTRIA"))
            .andExpect(jsonPath("$[1].valor").value(75.0))
            .andExpect(jsonPath("$[2].nombre").value("Distribución de Formación - DOCTORADO"))
            .andExpect(jsonPath("$[2].valor").value(25.0))
    }

    @Test
    fun `distribucion-formacion sin docentes registrados devuelve 422 (datos insuficientes)`() {
        mockMvc.perform(get("/api/indicadores/distribucion-formacion"))
            .andExpect(status().isUnprocessableContent)
    }

    @Test
    fun `capacidad-instalada devuelve 200 con la relacion estudiante-profesor`() {
        repeat(3) { guardarEstudiante(cohorte = "20231", estado = EstadoEstudiante.MATRICULADO) }
        guardarDocente(NivelFormacion.MAESTRIA, dedicacion = TipoDedicacion.TIEMPO_COMPLETO)

        mockMvc.perform(get("/api/indicadores/capacidad-instalada"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nombre").value("Relación Estudiante/Profesor"))
            .andExpect(jsonPath("$.valor").value(3.0))
    }

    @Test
    fun `capacidad-instalada sin docentes registrados devuelve 422 (datos insuficientes)`() {
        guardarEstudiante(cohorte = "20231", estado = EstadoEstudiante.MATRICULADO)

        mockMvc.perform(get("/api/indicadores/capacidad-instalada"))
            .andExpect(status().isUnprocessableContent)
    }

    private var contador = 1

    private fun guardarEstudiante(
        cohorte: String,
        estado: EstadoEstudiante,
        puntajeSaberPro: Int? = null,
    ) {
        repositorio.guardarEstudiante(
            Estudiante(
                id = "E${contador++}",
                programa = "Ingeniería de Sistemas",
                facultad = "Ingeniería",
                cohorte = cohorte,
                estado = estado,
                puntajeSaberPro = puntajeSaberPro,
            ),
        )
    }

    private fun guardarDocente(
        nivelFormacion: NivelFormacion,
        dedicacion: TipoDedicacion = TipoDedicacion.TIEMPO_COMPLETO,
    ) {
        repositorio.guardarDocente(
            Docente(
                id = "D${contador++}",
                facultad = "Ingeniería",
                nivelFormacion = nivelFormacion,
                dedicacion = dedicacion,
                periodo = "20261",
            ),
        )
    }
}

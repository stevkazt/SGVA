package com.example.sgva.infrastructure.persistence

import com.example.sgva.domain.ActividadCiclo
import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.Docente
import com.example.sgva.domain.EstadoActividad
import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.EstadoProyecto
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.EvaluacionDocumental
import com.example.sgva.domain.FaseCiclo
import com.example.sgva.domain.GrupoEvaluador
import com.example.sgva.domain.NivelFormacion
import com.example.sgva.domain.OrigenProyecto
import com.example.sgva.domain.ParticipacionActor
import com.example.sgva.domain.PreevaluacionEvaluador
import com.example.sgva.domain.ProyectoMejoramiento
import com.example.sgva.domain.ResultadoEncuestaPregunta
import com.example.sgva.domain.RespuestaEncuesta
import com.example.sgva.domain.TipoDedicacion
import com.example.sgva.domain.TipoEstamento
import com.example.sgva.domain.ValoracionCualitativa
import java.time.LocalDate
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Prueba de integración de [AcademicDataRepositoryJpa] contra una instancia
 * real de PostgreSQL (no una base embebida): verifica que el mapeo
 * dominio↔JPA (`MapeadorPersistencia.kt`) y las consultas de Spring Data
 * funcionan de punta a punta. Cada prueba corre en una transacción que
 * `@DataJpaTest` revierte al finalizar, dejando la base limpia entre pruebas
 * sin necesidad de un `TRUNCATE` manual.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AcademicDataRepositoryJpa::class)
@DirtiesContext
class AcademicDataRepositoryJpaTest {

    @Autowired
    private lateinit var repositorio: AcademicDataRepositoryJpa

    @Test
    fun `guarda y recupera un estudiante por id contra PostgreSQL`() {
        val estudiante = Estudiante(
            id = "JPA-E-001",
            programa = "Ingeniería de Sistemas",
            facultad = "Ingeniería",
            cohorte = "20211",
            estado = EstadoEstudiante.MATRICULADO,
            puntajeSaberPro = 250,
        )

        repositorio.guardarEstudiante(estudiante)

        assertEquals(estudiante, repositorio.buscarEstudiantePorId("JPA-E-001"))
        assertNull(repositorio.buscarEstudiantePorId("JPA-E-NOEXISTE"))
    }

    @Test
    fun `lista los estudiantes en orden de insercion`() {
        val primero = estudiante("JPA-E-010")
        val segundo = estudiante("JPA-E-011")

        repositorio.guardarEstudiante(primero)
        repositorio.guardarEstudiante(segundo)

        val listados = repositorio.listarEstudiantes().filter { it.id.startsWith("JPA-E-01") }
        assertEquals(listOf(primero, segundo), listados)
    }

    @Test
    fun `guarda y recupera un docente por id contra PostgreSQL`() {
        val docente = Docente(
            id = "JPA-D-001",
            programa = "EGSTI",
            facultad = "Ingeniería",
            nivelFormacion = NivelFormacion.DOCTORADO,
            dedicacion = TipoDedicacion.TIEMPO_COMPLETO,
            periodo = "20261",
        )

        repositorio.guardarDocente(docente)

        assertEquals(docente, repositorio.buscarDocentePorId("JPA-D-001"))
    }

    @Test
    fun `guarda y recupera una respuesta de encuesta por id contra PostgreSQL`() {
        val respuesta = RespuestaEncuesta(
            id = "JPA-R-001",
            estamento = TipoEstamento.ESTUDIANTE,
            factor = "Infraestructura",
            calificacion = 4,
            periodo = "20261",
        )

        repositorio.guardarRespuestaEncuesta(respuesta)

        assertEquals(respuesta, repositorio.buscarRespuestaEncuestaPorId("JPA-R-001"))
        assertEquals(4, repositorio.listarRespuestasEncuesta().first { it.id == "JPA-R-001" }.calificacion)
    }

    @Test
    fun `guarda y recupera un resultado de encuesta por pregunta por su clave natural contra PostgreSQL`() {
        val resultado = ResultadoEncuestaPregunta(
            programa = "JPA-EGSTI",
            periodo = "20261",
            codigoPregunta = "A2",
            caracteristica = "1.1",
            estamentos = setOf(TipoEstamento.ESTUDIANTE, TipoEstamento.PROFESOR),
            promedio = 4.5,
        )

        repositorio.guardarResultadoEncuestaPregunta(resultado)

        assertEquals(resultado, repositorio.buscarResultadoEncuestaPregunta("JPA-EGSTI", "20261", "A2"))
        assertNull(repositorio.buscarResultadoEncuestaPregunta("JPA-EGSTI", "20261", "NOEXISTE"))
    }

    @Test
    fun `guarda y recupera un resultado de encuesta por pregunta con promedio ausente`() {
        val resultado = ResultadoEncuestaPregunta(
            programa = "JPA-EGSTI",
            periodo = "20262",
            codigoPregunta = "B1",
            caracteristica = "2.1",
            estamentos = setOf(TipoEstamento.ALIADO_ESTRATEGICO),
            promedio = null,
        )

        repositorio.guardarResultadoEncuestaPregunta(resultado)

        val recuperado = repositorio.buscarResultadoEncuestaPregunta("JPA-EGSTI", "20262", "B1")
        assertEquals(resultado, recuperado)
        assertNull(recuperado?.promedio)
    }

    @Test
    fun `guarda y recupera una evaluacion documental por su clave natural contra PostgreSQL`() {
        val evaluacion = EvaluacionDocumental(
            programa = "JPA-EGSTI",
            caracteristica = "1.1",
            periodo = "20261",
            calificacion = 4.7,
            justificacion = "Evidencia documental consistente",
            sugerenciasMejora = "Actualizar el acta",
        )

        repositorio.guardarEvaluacionDocumental(evaluacion)

        val recuperada = repositorio.buscarEvaluacionDocumental("JPA-EGSTI", "20261", "1.1")
        assertEquals(evaluacion, recuperada)
        assertEquals(ValoracionCualitativa.SE_CUMPLE_PLENAMENTE, recuperada?.valoracionCualitativa)
        assertNull(repositorio.buscarEvaluacionDocumental("JPA-EGSTI", "20261", "NOEXISTE"))
    }

    @Test
    fun `guarda y recupera una evaluacion documental sin justificacion ni sugerencias de mejora`() {
        val evaluacion = EvaluacionDocumental(
            programa = "JPA-EGSTI",
            caracteristica = "2.1",
            periodo = "20262",
            calificacion = 3.0,
        )

        repositorio.guardarEvaluacionDocumental(evaluacion)

        val recuperada = repositorio.buscarEvaluacionDocumental("JPA-EGSTI", "20262", "2.1")
        assertEquals(evaluacion, recuperada)
        assertNull(recuperada?.justificacion)
        assertNull(recuperada?.sugerenciasMejora)
    }

    @Test
    fun `guarda y recupera un ciclo de autoevaluacion por su clave natural contra PostgreSQL`() {
        val ciclo = CicloAutoevaluacion(id = "JPA-CICLO-001", programa = "JPA-EGSTI", periodo = "20261")

        repositorio.guardarCiclo(ciclo)

        assertEquals(ciclo, repositorio.buscarCicloPorId("JPA-CICLO-001"))
        assertEquals(ciclo, repositorio.buscarCicloPorProgramaYPeriodo("JPA-EGSTI", "20261"))
        assertNull(repositorio.buscarCicloPorProgramaYPeriodo("JPA-EGSTI", "20262"))
    }

    @Test
    fun `guarda y recupera las fases de un ciclo contra PostgreSQL`() {
        repositorio.guardarCiclo(CicloAutoevaluacion(id = "JPA-CICLO-002", programa = "JPA-EGSTI", periodo = "20262"))
        val fase = FaseCiclo(ciclo = "JPA-CICLO-002", numero = 1, nombre = "Apropiación")

        repositorio.guardarFase(fase)

        assertEquals(listOf(fase), repositorio.listarFasesPorCiclo("JPA-CICLO-002"))
    }

    @Test
    fun `guarda y recupera una actividad de ciclo contra PostgreSQL`() {
        repositorio.guardarCiclo(CicloAutoevaluacion(id = "JPA-CICLO-003", programa = "JPA-EGSTI", periodo = "20251"))
        val actividad = ActividadCiclo(
            id = "1.1",
            ciclo = "JPA-CICLO-003",
            fase = 1,
            descripcion = "Sensibilización del proceso",
            responsable = "Comité de aseguramiento de la calidad",
            fechaInicioPlanificada = LocalDate.of(2026, 1, 1),
            fechaFinPlanificada = LocalDate.of(2026, 2, 1),
            estado = EstadoActividad.EN_CURSO,
        )

        repositorio.guardarActividad(actividad)

        assertEquals(listOf(actividad), repositorio.listarActividadesPorCiclo("JPA-CICLO-003"))
    }

    @Test
    fun `guarda y recupera un grupo evaluador por ciclo y taller contra PostgreSQL`() {
        repositorio.guardarCiclo(CicloAutoevaluacion(id = "JPA-CICLO-004", programa = "JPA-EGSTI", periodo = "20252"))
        val grupo = GrupoEvaluador(
            ciclo = "JPA-CICLO-004",
            factores = setOf("F5", "F9"),
            taller = 1,
            fechaTaller = LocalDate.of(2026, 3, 1),
        )

        repositorio.guardarGrupoEvaluador(grupo)

        assertEquals(grupo, repositorio.buscarGrupoEvaluador("JPA-CICLO-004", 1))
        assertNull(repositorio.buscarGrupoEvaluador("JPA-CICLO-004", 2))
    }

    @Test
    fun `guarda y recupera una preevaluacion por su clave natural contra PostgreSQL`() {
        repositorio.guardarCiclo(CicloAutoevaluacion(id = "JPA-CICLO-005", programa = "JPA-EGSTI", periodo = "20241"))
        val preevaluacion = PreevaluacionEvaluador(
            ciclo = "JPA-CICLO-005",
            caracteristica = "1.1",
            evaluador = "EVAL-01",
            calificacion = 4.5,
            fortaleza = "Buen soporte documental",
        )

        repositorio.guardarPreevaluacion(preevaluacion)

        assertEquals(preevaluacion, repositorio.buscarPreevaluacion("JPA-CICLO-005", "1.1", "EVAL-01"))
    }

    @Test
    fun `guarda y recupera una participacion de actor por su clave natural contra PostgreSQL`() {
        repositorio.guardarCiclo(CicloAutoevaluacion(id = "JPA-CICLO-006", programa = "JPA-EGSTI", periodo = "20242"))
        val participacion = ParticipacionActor(
            ciclo = "JPA-CICLO-006",
            actor = "Estudiantes",
            poblacion = 500,
            muestra = 200,
            respuestas = 160,
        )

        repositorio.guardarParticipacion(participacion)

        val recuperada = repositorio.buscarParticipacion("JPA-CICLO-006", "Estudiantes")
        assertEquals(participacion, recuperada)
        assertEquals(0.8, recuperada?.porcentajeDiligenciado)
    }

    @Test
    fun `guarda y recupera un proyecto de mejoramiento por id contra PostgreSQL`() {
        val proyecto = ProyectoMejoramiento(
            id = "JPA-PM-001",
            programa = "JPA-EGSTI",
            nombre = "Fortalecimiento docente",
            aspectosMejora = listOf("Formación docente, de posgrado", "Producción académica"),
            origen = OrigenProyecto.AUTOEVALUACION,
            objetivo = "Aumentar el nivel de formación de los docentes",
            actividades = listOf("Convocatoria de becas"),
            factores = setOf("F3"),
            estado = EstadoProyecto.EN_EJECUCION,
        )

        repositorio.guardarProyecto(proyecto)

        assertEquals(proyecto, repositorio.buscarProyectoPorId("JPA-PM-001"))
    }

    private fun estudiante(id: String): Estudiante = Estudiante(
        id = id,
        programa = "Ingeniería de Sistemas",
        facultad = "Ingeniería",
        cohorte = "20211",
        estado = EstadoEstudiante.MATRICULADO,
    )
}

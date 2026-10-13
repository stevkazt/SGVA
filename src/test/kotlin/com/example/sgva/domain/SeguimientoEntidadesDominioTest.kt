package com.example.sgva.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Pruebas de las entidades del Módulo 5 — Seguimiento del proceso institucional (V6). */
class SeguimientoEntidadesDominioTest {

    // =========================================================================
    // CicloAutoevaluacion
    // =========================================================================

    @Test
    fun `un ciclo valido se construye correctamente`() {
        val ciclo = CicloAutoevaluacion(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261")

        assertEquals("EGSTI", ciclo.programa)
    }

    @Test
    fun `un periodo de ciclo con formato invalido es rechazado`() {
        assertThrows<FormatoPeriodoInvalidoException> {
            CicloAutoevaluacion(id = "EGSTI-2026", programa = "EGSTI", periodo = "2026")
        }
    }

    // =========================================================================
    // FaseCiclo
    // =========================================================================

    @Test
    fun `una fase con numero fuera de 1 a 4 es rechazada`() {
        assertThrows<FaseInvalidaException> {
            FaseCiclo(ciclo = "EGSTI-20261", numero = 5, nombre = "Fase inexistente")
        }
    }

    @Test
    fun `los nombres canonicos de fase cubren las cuatro fases en orden`() {
        assertEquals(
            listOf(
                "Apropiación",
                "Planeación y desarrollo",
                "Emisión de juicios de valor y plan de mejoramiento",
                "Aprobación de resultados",
            ),
            (1..4).map { FaseCiclo.NOMBRES[it] },
        )
    }

    // =========================================================================
    // ActividadCiclo
    // =========================================================================

    @Test
    fun `una actividad con fecha de inicio posterior a la de fin es rechazada`() {
        assertThrows<FechasActividadInvalidasException> {
            actividad(inicio = LocalDate.of(2026, 3, 10), fin = LocalDate.of(2026, 3, 1))
        }
    }

    @Test
    fun `una actividad con fase fuera de 1 a 4 es rechazada`() {
        assertThrows<FaseInvalidaException> {
            actividad(fase = 0)
        }
    }

    @Test
    fun `una actividad completada nunca esta vencida aunque su fin ya haya pasado`() {
        val completada = actividad(
            fin = LocalDate.of(2026, 1, 1),
            estado = EstadoActividad.COMPLETADA,
        )

        assertFalse(completada.estaVencida(LocalDate.of(2026, 6, 1)))
    }

    @Test
    fun `una actividad no completada con fin anterior a la fecha de referencia esta vencida`() {
        val pendiente = actividad(
            fin = LocalDate.of(2026, 1, 1),
            estado = EstadoActividad.EN_CURSO,
        )

        assertTrue(pendiente.estaVencida(LocalDate.of(2026, 6, 1)))
    }

    @Test
    fun `una actividad cuyo fin cae dentro de la ventana de dias esta proxima a vencer`() {
        val proxima = actividad(
            fin = LocalDate.of(2026, 6, 5),
            estado = EstadoActividad.PLANIFICADA,
        )

        assertTrue(proxima.estaProximaAVencer(LocalDate.of(2026, 6, 1), dias = 7))
        assertFalse(proxima.estaProximaAVencer(LocalDate.of(2026, 6, 1), dias = 3))
    }

    private fun actividad(
        fase: Int = 1,
        inicio: LocalDate = LocalDate.of(2026, 1, 1),
        fin: LocalDate = LocalDate.of(2026, 6, 10),
        estado: EstadoActividad = EstadoActividad.PLANIFICADA,
    ): ActividadCiclo = ActividadCiclo(
        id = "1.1",
        ciclo = "EGSTI-20261",
        fase = fase,
        descripcion = "Sensibilización del proceso",
        responsable = "Comité de aseguramiento de la calidad",
        fechaInicioPlanificada = inicio,
        fechaFinPlanificada = fin,
        estado = estado,
    )

    // =========================================================================
    // GrupoEvaluador
    // =========================================================================

    @Test
    fun `un grupo evaluador sin factores es rechazado`() {
        assertThrows<CampoRequeridoVacioException> {
            GrupoEvaluador(ciclo = "EGSTI-20261", factores = emptySet(), taller = 1)
        }
    }

    @Test
    fun `un grupo evaluador con taller cero o negativo es rechazado`() {
        assertThrows<NumeroTallerInvalidoException> {
            GrupoEvaluador(ciclo = "EGSTI-20261", factores = setOf("F1"), taller = 0)
        }
    }

    @Test
    fun `la fecha del taller es opcional`() {
        val grupo = GrupoEvaluador(ciclo = "EGSTI-20261", factores = setOf("F1", "F2"), taller = 1)

        assertNull(grupo.fechaTaller)
    }

    // =========================================================================
    // PreevaluacionEvaluador
    // =========================================================================

    @Test
    fun `una preevaluacion con calificacion fuera de rango es rechazada`() {
        assertThrows<CalificacionPreevaluacionFueraDeRangoException> {
            PreevaluacionEvaluador(
                ciclo = "EGSTI-20261",
                caracteristica = "1.1",
                evaluador = "EVAL-01",
                calificacion = 5.5,
            )
        }
    }

    @Test
    fun `una preevaluacion con calificacion de 0 es rechazada`() {
        assertThrows<CalificacionPreevaluacionFueraDeRangoException> {
            PreevaluacionEvaluador(
                ciclo = "EGSTI-20261",
                caracteristica = "1.1",
                evaluador = "EVAL-01",
                calificacion = 0.0,
            )
        }
    }

    // =========================================================================
    // ParticipacionActor
    // =========================================================================

    @Test
    fun `una participacion con respuestas mayores que la muestra es rechazada`() {
        assertThrows<ParticipacionInconsistenteException> {
            ParticipacionActor(ciclo = "EGSTI-20261", actor = "Estudiantes", poblacion = 100, muestra = 30, respuestas = 31)
        }
    }

    @Test
    fun `una participacion con muestra mayor que la poblacion es rechazada`() {
        assertThrows<ParticipacionInconsistenteException> {
            ParticipacionActor(ciclo = "EGSTI-20261", actor = "Estudiantes", poblacion = 10, muestra = 30, respuestas = 5)
        }
    }

    @Test
    fun `una cantidad negativa es rechazada`() {
        assertThrows<CantidadNegativaException> {
            ParticipacionActor(ciclo = "EGSTI-20261", actor = "Estudiantes", poblacion = -1, muestra = 0, respuestas = 0)
        }
    }

    @Test
    fun `el porcentaje diligenciado es ausente cuando la muestra es cero`() {
        val participacion =
            ParticipacionActor(ciclo = "EGSTI-20261", actor = "Egresados", poblacion = 0, muestra = 0, respuestas = 0)

        assertNull(participacion.porcentajeDiligenciado)
        assertNull(participacion.semaforo(configuracionSeguimiento()))
    }

    @Test
    fun `el porcentaje diligenciado se calcula como respuestas sobre muestra`() {
        val participacion =
            ParticipacionActor(ciclo = "EGSTI-20261", actor = "Profesores", poblacion = 100, muestra = 50, respuestas = 40)

        assertEquals(0.8, participacion.porcentajeDiligenciado)
    }

    @Test
    fun `el semaforo es rojo por debajo del umbral inferior`() {
        val participacion =
            ParticipacionActor(ciclo = "EGSTI-20261", actor = "Estudiantes", poblacion = 100, muestra = 100, respuestas = 50)

        assertEquals(TipoSemaforo.ROJO, participacion.semaforo(configuracionSeguimiento()))
    }

    @Test
    fun `el semaforo es naranja entre los dos umbrales`() {
        val participacion =
            ParticipacionActor(ciclo = "EGSTI-20261", actor = "Estudiantes", poblacion = 100, muestra = 100, respuestas = 70)

        assertEquals(TipoSemaforo.NARANJA, participacion.semaforo(configuracionSeguimiento()))
    }

    @Test
    fun `el semaforo es verde en o por encima del umbral superior`() {
        val participacion =
            ParticipacionActor(ciclo = "EGSTI-20261", actor = "Estudiantes", poblacion = 100, muestra = 100, respuestas = 80)

        assertEquals(TipoSemaforo.VERDE, participacion.semaforo(configuracionSeguimiento()))
    }

    private fun configuracionSeguimiento(): ConfiguracionSeguimiento =
        ConfiguracionSeguimiento(umbralRojoNaranja = 0.6, umbralNaranjaVerde = 0.8, diasProximaAVencer = 7)

    // =========================================================================
    // ConfiguracionSeguimiento
    // =========================================================================

    @Test
    fun `umbrales invertidos son rechazados`() {
        assertThrows<UmbralesSemaforoInvalidosException> {
            ConfiguracionSeguimiento(umbralRojoNaranja = 0.8, umbralNaranjaVerde = 0.6, diasProximaAVencer = 7)
        }
    }

    @Test
    fun `umbrales iguales son rechazados`() {
        assertThrows<UmbralesSemaforoInvalidosException> {
            ConfiguracionSeguimiento(umbralRojoNaranja = 0.6, umbralNaranjaVerde = 0.6, diasProximaAVencer = 7)
        }
    }

    @Test
    fun `un numero de dias proxima a vencer negativo es rechazado`() {
        assertThrows<DiasProximaAVencerInvalidoException> {
            ConfiguracionSeguimiento(umbralRojoNaranja = 0.6, umbralNaranjaVerde = 0.8, diasProximaAVencer = -1)
        }
    }

    // =========================================================================
    // ProyectoMejoramiento
    // =========================================================================

    @Test
    fun `un proyecto sin factores asociados es rechazado`() {
        assertThrows<CampoRequeridoVacioException> {
            ProyectoMejoramiento(
                id = "PM-001",
                programa = "EGSTI",
                nombre = "Fortalecimiento docente",
                aspectosMejora = listOf("Formación docente"),
                origen = OrigenProyecto.AUTOEVALUACION,
                objetivo = "Aumentar el nivel de formación",
                actividades = listOf("Convocatoria de becas"),
                factores = emptySet(),
                estado = EstadoProyecto.PLANIFICADO,
            )
        }
    }
}

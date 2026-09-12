package com.example.sgva.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EntidadesDominioTest {

    // =========================================================================
    // Estudiante
    // =========================================================================

    @Test
    fun `un estudiante valido se construye correctamente`() {
        val estudiante = Estudiante(
            id = "EST-20211-001",
            programa = "Ingeniería de Telecomunicaciones",
            facultad = "Ingeniería",
            cohorte = "20211",
            estado = EstadoEstudiante.GRADUADO,
            puntajeSaberPro = 210,
        )

        assertEquals("20211", estudiante.cohorte)
        assertEquals(210, estudiante.puntajeSaberPro)
    }

    @Test
    fun `el puntaje Saber Pro es opcional cuando el estudiante no ha presentado la prueba`() {
        val estudiante = Estudiante(
            id = "EST-20221-005",
            programa = "Ingeniería de Sistemas",
            facultad = "Ingeniería",
            cohorte = "20221",
            estado = EstadoEstudiante.MATRICULADO,
        )

        assertNull(estudiante.puntajeSaberPro)
    }

    @Test
    fun `la cohorte con guion es rechazada por no cumplir el formato AAAAS`() {
        assertThrows<FormatoPeriodoInvalidoException> {
            estudianteConCohorte("2021-1")
        }
    }

    @Test
    fun `la cohorte con semestre invalido es rechazada`() {
        assertThrows<FormatoPeriodoInvalidoException> {
            estudianteConCohorte("20213")
        }
    }

    @Test
    fun `la cohorte sin digito de semestre es rechazada`() {
        assertThrows<FormatoPeriodoInvalidoException> {
            estudianteConCohorte("2021")
        }
    }

    @Test
    fun `los limites 0 y 300 del puntaje Saber Pro son validos`() {
        assertEquals(0, estudianteConPuntaje(0).puntajeSaberPro)
        assertEquals(300, estudianteConPuntaje(300).puntajeSaberPro)
    }

    @Test
    fun `un puntaje Saber Pro por encima de 300 es rechazado`() {
        assertThrows<PuntajeSaberProFueraDeRangoException> { estudianteConPuntaje(301) }
    }

    @Test
    fun `un puntaje Saber Pro negativo es rechazado`() {
        assertThrows<PuntajeSaberProFueraDeRangoException> { estudianteConPuntaje(-1) }
    }

    @Test
    fun `un id de estudiante en blanco es rechazado`() {
        assertThrows<CampoRequeridoVacioException> {
            Estudiante(
                id = "  ",
                programa = "Ingeniería de Sistemas",
                facultad = "Ingeniería",
                cohorte = "20211",
                estado = EstadoEstudiante.MATRICULADO,
            )
        }
    }

    // =========================================================================
    // Docente
    // =========================================================================

    @Test
    fun `un docente valido se construye correctamente`() {
        val docente = Docente(
            id = "D001",
            facultad = "Ingeniería",
            nivelFormacion = NivelFormacion.DOCTORADO,
            dedicacion = TipoDedicacion.TIEMPO_COMPLETO,
            periodo = "20261",
        )

        assertEquals("20261", docente.periodo)
        assertEquals(NivelFormacion.DOCTORADO, docente.nivelFormacion)
    }

    @Test
    fun `el periodo del docente con formato invalido es rechazado`() {
        assertThrows<FormatoPeriodoInvalidoException> {
            Docente(
                id = "D001",
                facultad = "Ingeniería",
                nivelFormacion = NivelFormacion.MAESTRIA,
                dedicacion = TipoDedicacion.CATEDRA,
                periodo = "2026-1",
            )
        }
    }

    @Test
    fun `una facultad de docente en blanco es rechazada`() {
        assertThrows<CampoRequeridoVacioException> {
            Docente(
                id = "D001",
                facultad = "",
                nivelFormacion = NivelFormacion.MAESTRIA,
                dedicacion = TipoDedicacion.MEDIO_TIEMPO,
                periodo = "20261",
            )
        }
    }

    // =========================================================================
    // RespuestaEncuesta
    // =========================================================================

    @Test
    fun `una respuesta de encuesta valida se construye correctamente`() {
        val respuesta = RespuestaEncuesta(
            id = "R001",
            estamento = TipoEstamento.ESTUDIANTE,
            factor = "Infraestructura",
            calificacion = 4,
            periodo = "20261",
        )

        assertEquals(4, respuesta.calificacion)
        assertEquals(TipoEstamento.ESTUDIANTE, respuesta.estamento)
    }

    @Test
    fun `los limites 1 y 5 de la calificacion Likert son validos`() {
        assertEquals(1, respuestaConCalificacion(1).calificacion)
        assertEquals(5, respuestaConCalificacion(5).calificacion)
    }

    @Test
    fun `una calificacion por encima de 5 es rechazada`() {
        assertThrows<CalificacionFueraDeRangoException> { respuestaConCalificacion(6) }
    }

    @Test
    fun `una calificacion de 0 es rechazada`() {
        assertThrows<CalificacionFueraDeRangoException> { respuestaConCalificacion(0) }
    }

    @Test
    fun `el periodo de la respuesta con formato invalido es rechazado`() {
        assertThrows<FormatoPeriodoInvalidoException> {
            RespuestaEncuesta(
                id = "R001",
                estamento = TipoEstamento.PROFESOR,
                factor = "Plan de Estudios",
                calificacion = 3,
                periodo = "2026-1",
            )
        }
    }

    @Test
    fun `un factor de respuesta en blanco es rechazado`() {
        assertThrows<CampoRequeridoVacioException> {
            RespuestaEncuesta(
                id = "R001",
                estamento = TipoEstamento.EMPLEADOR,
                factor = "  ",
                calificacion = 3,
                periodo = "20261",
            )
        }
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private fun respuestaConCalificacion(calificacion: Int): RespuestaEncuesta =
        RespuestaEncuesta(
            id = "R001",
            estamento = TipoEstamento.ESTUDIANTE,
            factor = "Infraestructura",
            calificacion = calificacion,
            periodo = "20261",
        )

    private fun estudianteConCohorte(cohorte: String): Estudiante =
        Estudiante(
            id = "EST-001",
            programa = "Ingeniería de Sistemas",
            facultad = "Ingeniería",
            cohorte = cohorte,
            estado = EstadoEstudiante.MATRICULADO,
        )

    private fun estudianteConPuntaje(puntaje: Int): Estudiante =
        Estudiante(
            id = "EST-001",
            programa = "Ingeniería de Sistemas",
            facultad = "Ingeniería",
            cohorte = "20211",
            estado = EstadoEstudiante.GRADUADO,
            puntajeSaberPro = puntaje,
        )
}

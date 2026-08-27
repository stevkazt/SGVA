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
    // Helpers
    // =========================================================================

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

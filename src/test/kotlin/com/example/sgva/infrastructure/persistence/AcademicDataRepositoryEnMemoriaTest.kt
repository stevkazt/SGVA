package com.example.sgva.infrastructure.persistence

import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.NivelFormacion
import com.example.sgva.domain.Docente
import com.example.sgva.domain.RespuestaEncuesta
import com.example.sgva.domain.TipoDedicacion
import com.example.sgva.domain.TipoEstamento
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AcademicDataRepositoryEnMemoriaTest {

    private val repositorio = AcademicDataRepositoryEnMemoria()

    @Test
    fun `guarda y recupera un estudiante por id`() {
        val estudiante = Estudiante(
            id = "EST-001",
            programa = "Ingeniería de Sistemas",
            facultad = "Ingeniería",
            cohorte = "20211",
            estado = EstadoEstudiante.MATRICULADO,
        )

        repositorio.guardarEstudiante(estudiante)

        assertEquals(estudiante, repositorio.buscarEstudiantePorId("EST-001"))
        assertNull(repositorio.buscarEstudiantePorId("EST-999"))
    }

    @Test
    fun `preserva el orden de insercion en el listado de docentes`() {
        val d1 = docente("D-001")
        val d2 = docente("D-002")
        val d3 = docente("D-003")

        repositorio.guardarDocente(d1)
        repositorio.guardarDocente(d2)
        repositorio.guardarDocente(d3)

        assertEquals(listOf(d1, d2, d3), repositorio.listarDocentes())
    }

    @Test
    fun `guardar con un id existente reemplaza la entidad en su posicion original`() {
        val original = docente("D-001", NivelFormacion.MAESTRIA)
        val actualizado = docente("D-001", NivelFormacion.DOCTORADO)
        repositorio.guardarDocente(original)
        repositorio.guardarDocente(docente("D-002"))

        repositorio.guardarDocente(actualizado)

        assertEquals(actualizado, repositorio.buscarDocentePorId("D-001"))
        assertEquals("D-001", repositorio.listarDocentes().first().id)
    }

    @Test
    fun `guarda y recupera una respuesta de encuesta por id`() {
        val respuesta = RespuestaEncuesta(
            id = "R001",
            estamento = TipoEstamento.ESTUDIANTE,
            factor = "Infraestructura",
            calificacion = 4,
            periodo = "20261",
        )

        repositorio.guardarRespuestaEncuesta(respuesta)

        assertEquals(respuesta, repositorio.buscarRespuestaEncuestaPorId("R001"))
        assertNull(repositorio.buscarRespuestaEncuestaPorId("R999"))
    }

    private fun docente(
        id: String,
        nivelFormacion: NivelFormacion = NivelFormacion.MAESTRIA,
    ): Docente = Docente(
        id = id,
        facultad = "Ingeniería",
        nivelFormacion = nivelFormacion,
        dedicacion = TipoDedicacion.TIEMPO_COMPLETO,
        periodo = "20261",
    )
}

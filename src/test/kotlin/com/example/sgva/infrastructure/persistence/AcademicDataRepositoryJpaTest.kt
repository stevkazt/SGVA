package com.example.sgva.infrastructure.persistence

import com.example.sgva.domain.Docente
import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.NivelFormacion
import com.example.sgva.domain.RespuestaEncuesta
import com.example.sgva.domain.TipoDedicacion
import com.example.sgva.domain.TipoEstamento
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

    private fun estudiante(id: String): Estudiante = Estudiante(
        id = id,
        programa = "Ingeniería de Sistemas",
        facultad = "Ingeniería",
        cohorte = "20211",
        estado = EstadoEstudiante.MATRICULADO,
    )
}

package com.example.sgva.application

import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.Estudiante
import com.example.sgva.infrastructure.persistence.AcademicDataRepositoryEnMemoria
import com.example.sgva.usecases.CalcularEvolucionMatriculaUseCase
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals

class CalcularEvolucionMatriculaUseCaseTest {

    private val repositorio = AcademicDataRepositoryEnMemoria()
    private val reloj = Clock.fixed(Instant.parse("2026-06-01T00:00:00Z"), ZoneOffset.UTC)
    private val useCase = CalcularEvolucionMatriculaUseCase(repositorio, reloj)

    @Test
    fun `devuelve un indicador por cada uno de los 14 periodos de la ventana de 7 anios`() {
        val indicadores = useCase.ejecutar()

        assertEquals(14, indicadores.size)
        assertEquals("20201", indicadores.first().periodo)
        assertEquals("20262", indicadores.last().periodo)
    }

    @Test
    fun `cuenta solo los estudiantes MATRICULADO agrupados por cohorte`() {
        guardar("20231", EstadoEstudiante.MATRICULADO)
        guardar("20231", EstadoEstudiante.MATRICULADO)
        guardar("20231", EstadoEstudiante.DESERTOR)
        guardar("20211", EstadoEstudiante.MATRICULADO)

        val porPeriodo = useCase.ejecutar().associate { it.periodo to it.valor }

        assertEquals(2.0, porPeriodo["20231"])
        assertEquals(1.0, porPeriodo["20211"])
        assertEquals(0.0, porPeriodo["20221"])
    }

    @Test
    fun `ignora las cohortes fuera de la ventana historica`() {
        guardar("20182", EstadoEstudiante.MATRICULADO)

        val indicadores = useCase.ejecutar()

        assertEquals(0.0, indicadores.sumOf { it.valor })
        assertEquals(emptyList(), indicadores.map { it.periodo }.filter { it.startsWith("2018") })
    }

    @Test
    fun `el filtro por facultad restringe el conteo (sin distinguir mayusculas) y etiqueta el indicador`() {
        guardar("20231", EstadoEstudiante.MATRICULADO, facultad = "Ingeniería")
        guardar("20231", EstadoEstudiante.MATRICULADO, facultad = "Ciencias")

        val indicadores = useCase.ejecutar(facultad = "  INGENIERÍA ")

        assertEquals(1.0, indicadores.first { it.periodo == "20231" }.valor)
        assertEquals("INGENIERÍA", indicadores.first().facultad)
    }

    private fun guardar(
        cohorte: String,
        estado: EstadoEstudiante,
        facultad: String = "Ingeniería",
    ) {
        repositorio.guardarEstudiante(
            Estudiante(
                id = "E${contador++}",
                programa = "Ingeniería de Sistemas",
                facultad = facultad,
                cohorte = cohorte,
                estado = estado,
            ),
        )
    }

    private var contador = 1
}

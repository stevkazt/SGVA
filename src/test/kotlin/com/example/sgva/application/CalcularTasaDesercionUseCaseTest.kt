package com.example.sgva.application

import com.example.sgva.domain.DatosInsuficientesException
import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.Estudiante
import com.example.sgva.infrastructure.persistence.AcademicDataRepositoryEnMemoria
import com.example.sgva.usecases.CalcularTasaDesercionUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class CalcularTasaDesercionUseCaseTest {

    private val repositorio = AcademicDataRepositoryEnMemoria()
    private val useCase = CalcularTasaDesercionUseCase(repositorio)

    @Test
    fun `calcula el porcentaje de desertores sobre el total de la cohorte`() {
        guardar("20231", EstadoEstudiante.DESERTOR)
        guardar("20231", EstadoEstudiante.MATRICULADO)
        guardar("20231", EstadoEstudiante.MATRICULADO)
        guardar("20231", EstadoEstudiante.GRADUADO)

        val indicador = useCase.ejecutar("20231")

        assertEquals("Tasa de Deserción", indicador.nombre)
        assertEquals(25.0, indicador.valor)
        assertEquals("20231", indicador.periodo)
        assertEquals("TODAS", indicador.facultad)
    }

    @Test
    fun `redondea el porcentaje a dos decimales`() {
        repeat(2) { guardar("20221", EstadoEstudiante.DESERTOR) }
        guardar("20221", EstadoEstudiante.MATRICULADO)

        assertEquals(66.67, useCase.ejecutar("20221").valor)
    }

    @Test
    fun `una cohorte sin estudiantes lanza DatosInsuficientesException`() {
        assertThrows<DatosInsuficientesException> { useCase.ejecutar("20251") }
    }

    @Test
    fun `el filtro por facultad se aplica al numerador y al denominador`() {
        guardar("20231", EstadoEstudiante.DESERTOR, facultad = "Ingeniería")
        guardar("20231", EstadoEstudiante.MATRICULADO, facultad = "Ingeniería")
        guardar("20231", EstadoEstudiante.DESERTOR, facultad = "Ciencias")

        val indicador = useCase.ejecutar("20231", facultad = "Ingeniería")

        assertEquals(50.0, indicador.valor)
        assertEquals("Ingeniería", indicador.facultad)
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

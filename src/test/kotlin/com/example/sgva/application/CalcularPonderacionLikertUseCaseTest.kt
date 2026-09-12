package com.example.sgva.application

import com.example.sgva.domain.DatosInsuficientesException
import com.example.sgva.domain.RespuestaEncuesta
import com.example.sgva.domain.TipoEstamento
import com.example.sgva.infrastructure.persistence.AcademicDataRepositoryEnMemoria
import com.example.sgva.usecases.CalcularPonderacionLikertUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class CalcularPonderacionLikertUseCaseTest {

    private val repositorio = AcademicDataRepositoryEnMemoria()
    private val useCase = CalcularPonderacionLikertUseCase(repositorio)

    @Test
    fun `promedia las calificaciones agrupando por factor y estamento`() {
        guardar(TipoEstamento.ESTUDIANTE, "Infraestructura", 4)
        guardar(TipoEstamento.ESTUDIANTE, "Infraestructura", 2)
        guardar(TipoEstamento.PROFESOR, "Infraestructura", 5)
        guardar(TipoEstamento.ESTUDIANTE, "Plan de Estudios", 3)

        val ponderaciones = useCase.ejecutar()

        assertEquals(3, ponderaciones.size)
        val infraestructuraEstudiante = ponderaciones.first {
            it.factor == "Infraestructura" && it.estamento == TipoEstamento.ESTUDIANTE
        }
        assertEquals(3.0, infraestructuraEstudiante.promedio)
        val infraestructuraProfesor = ponderaciones.first {
            it.factor == "Infraestructura" && it.estamento == TipoEstamento.PROFESOR
        }
        assertEquals(5.0, infraestructuraProfesor.promedio)
    }

    @Test
    fun `el filtro por periodo restringe la poblacion y etiqueta la ponderacion`() {
        guardar(TipoEstamento.ESTUDIANTE, "Infraestructura", 5, periodo = "20261")
        guardar(TipoEstamento.ESTUDIANTE, "Infraestructura", 1, periodo = "20262")

        val ponderaciones = useCase.ejecutar(periodo = "20261")

        assertEquals(1, ponderaciones.size)
        assertEquals(5.0, ponderaciones.first().promedio)
        assertEquals("20261", ponderaciones.first().periodo)
    }

    @Test
    fun `sin filtro de periodo etiqueta con CONSOLIDADO`() {
        guardar(TipoEstamento.ESTUDIANTE, "Infraestructura", 4)

        assertEquals("CONSOLIDADO", useCase.ejecutar().first().periodo)
    }

    @Test
    fun `sin respuestas registradas lanza DatosInsuficientesException`() {
        assertThrows<DatosInsuficientesException> { useCase.ejecutar() }
    }

    private fun guardar(
        estamento: TipoEstamento,
        factor: String,
        calificacion: Int,
        periodo: String = "20261",
    ) {
        repositorio.guardarRespuestaEncuesta(
            RespuestaEncuesta(
                id = "R${contador++}",
                estamento = estamento,
                factor = factor,
                calificacion = calificacion,
                periodo = periodo,
            ),
        )
    }

    private var contador = 1
}

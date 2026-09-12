package com.example.sgva.application

import com.example.sgva.domain.DatosInsuficientesException
import com.example.sgva.domain.RespuestaEncuesta
import com.example.sgva.domain.TipoEstamento
import com.example.sgva.usecases.CalcularPonderacionLikertUseCase
import com.example.sgva.usecases.GenerarMatrizRadarUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class GenerarMatrizRadarUseCaseTest {

    private val repositorio = RepositorioAcademicoFalso()
    private val useCase = GenerarMatrizRadarUseCase(CalcularPonderacionLikertUseCase(repositorio))

    @Test
    fun `organiza los promedios por factor y estamento en la matriz`() {
        guardar(TipoEstamento.ESTUDIANTE, "Infraestructura", 4)
        guardar(TipoEstamento.PROFESOR, "Infraestructura", 2)
        guardar(TipoEstamento.ESTUDIANTE, "Plan de Estudios", 5)

        val matriz = useCase.ejecutar()

        assertEquals(listOf("Infraestructura", "Plan de Estudios"), matriz.factores)
        assertEquals(2, matriz.series.size)

        val serieEstudiante = matriz.series.first { it.estamento == TipoEstamento.ESTUDIANTE }
        assertEquals(4.0, serieEstudiante.promediosPorFactor.getValue("Infraestructura"))
        assertEquals(5.0, serieEstudiante.promediosPorFactor.getValue("Plan de Estudios"))

        val serieProfesor = matriz.series.first { it.estamento == TipoEstamento.PROFESOR }
        assertEquals(2.0, serieProfesor.promediosPorFactor.getValue("Infraestructura"))
        assertEquals(1, serieProfesor.promediosPorFactor.size)
    }

    @Test
    fun `sin respuestas registradas lanza DatosInsuficientesException`() {
        assertThrows<DatosInsuficientesException> { useCase.ejecutar() }
    }

    private fun guardar(estamento: TipoEstamento, factor: String, calificacion: Int) {
        repositorio.guardarRespuestaEncuesta(
            RespuestaEncuesta(
                id = "R${contador++}",
                estamento = estamento,
                factor = factor,
                calificacion = calificacion,
                periodo = "20261",
            ),
        )
    }

    private var contador = 1
}

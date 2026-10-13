package com.example.sgva.application

import com.example.sgva.domain.Caracteristica
import com.example.sgva.domain.ConfiguracionPonderaciones
import com.example.sgva.domain.EvaluacionDocumental
import com.example.sgva.usecases.CalcularResultadoAutoevaluacionUseCase
import com.example.sgva.usecases.CompararResultadosEntreCiclosUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CompararResultadosEntreCiclosUseCaseTest {

    private val repositorio = RepositorioAcademicoFalso()
    private val configuracionPonderaciones = ConfiguracionPonderaciones(
        caracteristicas = listOf(
            Caracteristica(id = "C1", nombre = "Característica 1", factor = "F1", ponderacion = 1.0),
        ),
        pesosFactores = mapOf("F1" to 1.0),
        pesoEncuestas = 0.4,
        pesoDocumental = 0.6,
    )
    private val comparar = CompararResultadosEntreCiclosUseCase(
        CalcularResultadoAutoevaluacionUseCase(repositorio, configuracionPonderaciones),
    )

    @Test
    fun `la diferencia entre dos periodos es el valor del periodo comparado menos el base`() {
        repositorio.guardarEvaluacionDocumental(
            EvaluacionDocumental(programa = "EGSTI", caracteristica = "C1", periodo = "20251", calificacion = 3.0),
        )
        repositorio.guardarEvaluacionDocumental(
            EvaluacionDocumental(programa = "EGSTI", caracteristica = "C1", periodo = "20261", calificacion = 4.0),
        )

        val comparacion = comparar.ejecutar("EGSTI", listOf("20251", "20261"))

        assertEquals(1, comparacion.diferencias.size)
        assertEquals(1.0, comparacion.diferencias.first().diferenciaGlobal)
        assertEquals(1.0, comparacion.diferencias.first().diferenciasPorFactor["F1"])
    }

    @Test
    fun `tres periodos producen dos diferencias consecutivas`() {
        listOf("20241" to 2.0, "20251" to 3.0, "20261" to 5.0).forEach { (periodo, calificacion) ->
            repositorio.guardarEvaluacionDocumental(
                EvaluacionDocumental(programa = "EGSTI", caracteristica = "C1", periodo = periodo, calificacion = calificacion),
            )
        }

        val comparacion = comparar.ejecutar("EGSTI", listOf("20241", "20251", "20261"))

        assertEquals(3, comparacion.resultados.size)
        assertEquals(2, comparacion.diferencias.size)
        assertEquals(1.0, comparacion.diferencias[0].diferenciaGlobal)
        assertEquals(2.0, comparacion.diferencias[1].diferenciaGlobal)
    }

    @Test
    fun `una diferencia con un periodo sin datos es ausente`() {
        repositorio.guardarEvaluacionDocumental(
            EvaluacionDocumental(programa = "EGSTI", caracteristica = "C1", periodo = "20251", calificacion = 3.0),
        )

        val comparacion = comparar.ejecutar("EGSTI", listOf("20251", "20261"))

        assertNull(comparacion.diferencias.first().diferenciaGlobal)
    }
}

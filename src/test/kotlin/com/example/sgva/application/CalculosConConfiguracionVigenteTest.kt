package com.example.sgva.application

import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.ConfiguracionSeguimiento
import com.example.sgva.domain.DatosInsuficientesException
import com.example.sgva.domain.EvaluacionDocumental
import com.example.sgva.domain.GrupoEvaluador
import com.example.sgva.infrastructure.configuracion.CargadorConfiguracionPonderaciones
import com.example.sgva.usecases.CalcularAvanceCicloUseCase
import com.example.sgva.usecases.CalcularResultadoAutoevaluacionUseCase
import com.example.sgva.usecases.CargarConfiguracionPonderacionesUseCase
import com.example.sgva.usecases.ConsultarConfiguracionPonderacionesUseCase
import com.example.sgva.usecases.RegistrarCicloAutoevaluacionUseCase
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/**
 * Verifica que los cálculos consultan la configuración de ponderaciones
 * vigente en cada ejecución (sin reiniciar), y que sin ella responden con un
 * mensaje claro en lugar de usar valores por defecto.
 */
class CalculosConConfiguracionVigenteTest {

    private val repositorio = RepositorioAcademicoFalso()
    private val repositorioConfiguracion = RepositorioConfiguracionPonderacionesFalso()
    private val consultar = ConsultarConfiguracionPonderacionesUseCase(repositorioConfiguracion)
    private val cargar = CargarConfiguracionPonderacionesUseCase(CargadorConfiguracionPonderaciones(), repositorioConfiguracion)

    private val resultado = CalcularResultadoAutoevaluacionUseCase(repositorio, consultar::ejecutar)
    private val avance = CalcularAvanceCicloUseCase(
        repositorio,
        consultar::ejecutar,
        ConfiguracionSeguimiento(0.6, 0.8, 7),
    )

    private fun csv(pesoF1: String, pesoF2: String) = """
        tipo,id,factor,nombre,ponderacion
        FACTOR,F1,,,$pesoF1
        FACTOR,F2,,,$pesoF2
        CARACTERISTICA,C1,F1,Uno,1.0
        CARACTERISTICA,C2,F2,Dos,1.0
        MEZCLA,ENCUESTAS,,,0.4
        MEZCLA,DOCUMENTAL,,,0.6
    """.trimIndent().toByteArray()

    private fun registrarDocumentales() {
        listOf("C1" to 4.0, "C2" to 2.0).forEach { (caracteristica, nota) ->
            repositorio.guardarEvaluacionDocumental(EvaluacionDocumental("EGSTI", caracteristica, "20261", nota))
        }
    }

    @Test
    fun `el resultado sin ponderaciones cargadas falla con un mensaje claro`() {
        registrarDocumentales()

        assertFailsWith<DatosInsuficientesException> { resultado.ejecutar("EGSTI", "20261") }
    }

    @Test
    fun `el resultado se recalcula con las ponderaciones nuevas sin recrear el caso de uso`() {
        registrarDocumentales()

        cargar.ejecutar("a.csv", csv("0.5", "0.5"))
        assertEquals(3.0, resultado.ejecutar("EGSTI", "20261").promedioGlobal)

        cargar.ejecutar("b.csv", csv("0.75", "0.25"))
        assertEquals(3.5, resultado.ejecutar("EGSTI", "20261").promedioGlobal)
    }

    @Test
    fun `el avance de un ciclo sin grupos evaluadores no necesita ponderaciones`() {
        RegistrarCicloAutoevaluacionUseCase(repositorio).ejecutar(
            CicloAutoevaluacion(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261"),
        )

        val resultadoAvance = avance.ejecutar("EGSTI-20261", LocalDate.of(2026, 6, 1))

        assertNull(resultadoAvance.avanceCiclo)
        assertEquals(emptySet(), resultadoAvance.factoresConPreevaluacionPendiente)
    }

    @Test
    fun `el avance de un ciclo con grupos evaluadores exige las ponderaciones`() {
        RegistrarCicloAutoevaluacionUseCase(repositorio).ejecutar(
            CicloAutoevaluacion(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261"),
        )
        repositorio.guardarGrupoEvaluador(GrupoEvaluador(ciclo = "EGSTI-20261", factores = setOf("F1"), taller = 1))

        assertFailsWith<DatosInsuficientesException> { avance.ejecutar("EGSTI-20261", LocalDate.of(2026, 6, 1)) }

        cargar.ejecutar("a.csv", csv("0.5", "0.5"))
        assertEquals(setOf("F1"), avance.ejecutar("EGSTI-20261", LocalDate.of(2026, 6, 1)).factoresConPreevaluacionPendiente)
    }
}

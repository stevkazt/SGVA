package com.example.sgva.application

import com.example.sgva.domain.Caracteristica
import com.example.sgva.domain.ConfiguracionPonderaciones
import com.example.sgva.domain.EvaluacionDocumental
import com.example.sgva.domain.ResultadoEncuestaPregunta
import com.example.sgva.domain.TipoEstamento
import com.example.sgva.usecases.CalcularResultadoAutoevaluacionUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Verifica `CalcularResultadoAutoevaluacionUseCase` contra las reglas de
 * cálculo de `DESIGN_SGVA.md` §0.1, con un catálogo sintético de dos factores
 * (F1 con C1/C2, F2 con C3/C4) y una mezcla encuestas/documental de 40 %/60 %.
 * Los pesos no corresponden al modelo 2025 real: son datos de prueba para
 * aislar cada regla.
 */
class CalcularResultadoAutoevaluacionUseCaseTest {

    private val repositorio = RepositorioAcademicoFalso()

    private val configuracion = ConfiguracionPonderaciones(
        caracteristicas = listOf(
            Caracteristica(id = "C1", nombre = "Característica 1", factor = "F1", ponderacion = 0.5),
            Caracteristica(id = "C2", nombre = "Característica 2", factor = "F1", ponderacion = 0.5),
            Caracteristica(id = "C3", nombre = "Característica 3", factor = "F2", ponderacion = 0.3),
            Caracteristica(id = "C4", nombre = "Característica 4", factor = "F2", ponderacion = 0.7),
        ),
        pesosFactores = mapOf("F1" to 0.6, "F2" to 0.4),
        pesoEncuestas = 0.4,
        pesoDocumental = 0.6,
    )

    private val useCase = CalcularResultadoAutoevaluacionUseCase(repositorio, configuracion)

    private fun registrarPregunta(
        codigoPregunta: String,
        caracteristica: String,
        promedio: Double?,
    ) {
        repositorio.guardarResultadoEncuestaPregunta(
            ResultadoEncuestaPregunta(
                programa = "EGSTI",
                periodo = "20261",
                codigoPregunta = codigoPregunta,
                caracteristica = caracteristica,
                estamentos = setOf(TipoEstamento.ESTUDIANTE),
                promedio = promedio,
            ),
        )
    }

    private fun registrarDocumental(caracteristica: String, calificacion: Double) {
        repositorio.guardarEvaluacionDocumental(
            EvaluacionDocumental(
                programa = "EGSTI",
                caracteristica = caracteristica,
                periodo = "20261",
                calificacion = calificacion,
            ),
        )
    }

    @Test
    fun `el resultado de encuesta de una caracteristica es el promedio de los promedios de sus preguntas`() {
        registrarPregunta("A1", "C1", 3.0)
        registrarPregunta("A2", "C1", 5.0)
        registrarDocumental("C1", 4.0)

        val resultado = useCase.ejecutar("EGSTI", "20261")

        // encuesta = (3.0 + 5.0) / 2 = 4.0; promedio = 4.0*0.6 + 4.0*0.4 = 4.0
        assertEquals(4.0, resultado.promediosPorCaracteristica["C1"]!!, 1e-9)
    }

    @Test
    fun `las preguntas con promedio null no cuentan en el resultado de encuesta`() {
        registrarPregunta("A1", "C1", 4.0)
        registrarPregunta("A2", "C1", null)
        registrarDocumental("C1", 4.0)

        val resultado = useCase.ejecutar("EGSTI", "20261")

        // encuesta = 4.0 (la pregunta ausente no cuenta); promedio = 4.0*0.6 + 4.0*0.4 = 4.0
        assertEquals(4.0, resultado.promediosPorCaracteristica["C1"]!!, 1e-9)
    }

    @Test
    fun `una caracteristica sin ninguna pregunta con valor tiene resultado de encuesta ausente`() {
        registrarPregunta("A1", "C1", null)
        registrarDocumental("C1", 4.7)

        val resultado = useCase.ejecutar("EGSTI", "20261")

        // encuesta ausente -> el promedio es el valor documental completo, sin aplicar su peso de mezcla
        assertEquals(4.7, resultado.promediosPorCaracteristica["C1"]!!, 1e-9)
    }

    @Test
    fun `una caracteristica sin preguntas registradas tiene resultado de encuesta ausente`() {
        registrarDocumental("C1", 4.7)

        val resultado = useCase.ejecutar("EGSTI", "20261")

        assertEquals(4.7, resultado.promediosPorCaracteristica["C1"]!!, 1e-9)
    }

    @Test
    fun `una caracteristica con encuesta y documental mezcla ambas fuentes con los pesos de la configuracion`() {
        registrarPregunta("A1", "C1", 2.0)
        registrarDocumental("C1", 5.0)

        val resultado = useCase.ejecutar("EGSTI", "20261")

        // documental 5.0 * 0.6 + encuesta 2.0 * 0.4 = 3.0 + 0.8 = 3.8
        assertEquals(3.8, resultado.promediosPorCaracteristica["C1"]!!, 1e-9)
    }

    @Test
    fun `una caracteristica sin evaluacion documental es ausente aunque tenga encuesta`() {
        registrarPregunta("A1", "C1", 5.0)

        val resultado = useCase.ejecutar("EGSTI", "20261")

        assertNull(resultado.promediosPorCaracteristica["C1"])
    }

    @Test
    fun `una caracteristica sin ninguna fuente es ausente`() {
        val resultado = useCase.ejecutar("EGSTI", "20261")

        assertNull(resultado.promediosPorCaracteristica["C1"])
    }

    @Test
    fun `el promedio de un factor reponderan los pesos de las caracteristicas presentes`() {
        // C1 (peso 0.5) ausente; solo C2 (peso 0.5) tiene documental.
        registrarDocumental("C2", 4.4)

        val resultado = useCase.ejecutar("EGSTI", "20261")

        assertNull(resultado.promediosPorCaracteristica["C1"])
        // reponderado: solo C2 presente -> su peso se normaliza a 1.0, el promedio del factor es su propio valor
        assertEquals(4.4, resultado.promediosPorFactor["F1"]!!, 1e-9)
    }

    @Test
    fun `el promedio de un factor con todas sus caracteristicas presentes no cambia por la reponderacion`() {
        registrarDocumental("C1", 4.0)
        registrarDocumental("C2", 5.0)

        val resultado = useCase.ejecutar("EGSTI", "20261")

        // 4.0*0.5 + 5.0*0.5 = 4.5 (los pesos ya suman 1.0, reponderar no cambia nada)
        assertEquals(4.5, resultado.promediosPorFactor["F1"]!!, 1e-9)
    }

    @Test
    fun `un factor sin ninguna caracteristica calculable es ausente`() {
        val resultado = useCase.ejecutar("EGSTI", "20261")

        assertNull(resultado.promediosPorFactor["F1"])
    }

    @Test
    fun `el promedio global reponderan los pesos de los factores presentes`() {
        // F1 (peso 0.6) ausente por completo; solo F2 (peso 0.4) tiene datos.
        registrarDocumental("C3", 4.0)
        registrarDocumental("C4", 4.0)

        val resultado = useCase.ejecutar("EGSTI", "20261")

        assertNull(resultado.promediosPorFactor["F1"])
        // reponderado: solo F2 presente -> su peso se normaliza a 1.0
        assertEquals(4.0, resultado.promedioGlobal!!, 1e-9)
    }

    @Test
    fun `el promedio global con todos los factores y caracteristicas presentes no cambia por la reponderacion`() {
        registrarDocumental("C1", 4.0)
        registrarDocumental("C2", 5.0)
        registrarDocumental("C3", 3.0)
        registrarDocumental("C4", 4.0)

        val resultado = useCase.ejecutar("EGSTI", "20261")

        val f1 = 4.0 * 0.5 + 5.0 * 0.5 // 4.5
        val f2 = 3.0 * 0.3 + 4.0 * 0.7 // 3.7
        val global = f1 * 0.6 + f2 * 0.4

        assertEquals(f1, resultado.promediosPorFactor["F1"]!!, 1e-9)
        assertEquals(f2, resultado.promediosPorFactor["F2"]!!, 1e-9)
        assertEquals(global, resultado.promedioGlobal!!, 1e-9)
    }

    @Test
    fun `el promedio global es ausente cuando ningun factor es calculable`() {
        val resultado = useCase.ejecutar("EGSTI", "20261")

        assertNull(resultado.promedioGlobal)
    }

    @Test
    fun `el resultado conserva el catalogo completo de caracteristicas y factores aunque esten ausentes`() {
        registrarDocumental("C1", 4.0)

        val resultado = useCase.ejecutar("EGSTI", "20261")

        assertEquals(setOf("C1", "C2", "C3", "C4"), resultado.promediosPorCaracteristica.keys)
        assertEquals(setOf("F1", "F2"), resultado.promediosPorFactor.keys)
    }

    @Test
    fun `el resultado solo usa datos del programa y periodo consultados`() {
        registrarDocumental("C1", 4.0)
        repositorio.guardarEvaluacionDocumental(
            EvaluacionDocumental(programa = "OTRO-PROGRAMA", caracteristica = "C1", periodo = "20261", calificacion = 1.0),
        )
        repositorio.guardarEvaluacionDocumental(
            EvaluacionDocumental(programa = "EGSTI", caracteristica = "C1", periodo = "20252", calificacion = 1.0),
        )

        val resultado = useCase.ejecutar("EGSTI", "20261")

        assertEquals(4.0, resultado.promediosPorCaracteristica["C1"]!!, 1e-9)
    }

    @Test
    fun `el resultado expone el programa y periodo consultados`() {
        val resultado = useCase.ejecutar("EGSTI", "20261")

        assertEquals("EGSTI", resultado.programa)
        assertEquals("20261", resultado.periodo)
    }
}

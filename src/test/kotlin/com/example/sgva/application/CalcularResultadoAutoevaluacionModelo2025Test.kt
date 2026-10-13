package com.example.sgva.application

import com.example.sgva.domain.ConfiguracionPonderaciones
import com.example.sgva.domain.ResultadoAutoevaluacion
import com.example.sgva.infrastructure.configuracion.CargadorConfiguracionPonderaciones
import com.example.sgva.infrastructure.parsing.CsvDocumentParser
import com.example.sgva.usecases.CalcularResultadoAutoevaluacionUseCase
import de.siegmar.fastcsv.reader.CsvReader
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

/**
 * Valida `CalcularResultadoAutoevaluacionUseCase` contra el modelo de
 * autoevaluación 2025 diligenciado (`DESIGN_SGVA.md` §0.1), usando los
 * fixtures reales de ponderaciones, resultados de encuesta por pregunta y
 * evaluación documental de `src/test/resources/fixtures/modelo2025`.
 *
 * `esperados_autoevaluacion.csv` contiene únicamente códigos y valores
 * numéricos tomados del modelo diligenciado 2025 (no se deriva de este
 * cálculo): es el oráculo contra el que se compara, con una tolerancia de
 * `1e-4` para absorber el redondeo propio de una hoja de cálculo.
 */
class CalcularResultadoAutoevaluacionModelo2025Test {

    private val programa = "EGSTI"
    private val periodo = "20252"
    private val tolerancia = 1e-4

    private val resultado: ResultadoAutoevaluacion = calcularResultado()
    private val esperadosPorCaracteristica = esperadosDelNivel("caracteristica")
    private val esperadosPorFactor = esperadosDelNivel("factor")
    private val esperadoGlobal = esperadosDelNivel("global").getValue("EGSTI")

    @Test
    fun `el promedio por caracteristica reproduce el modelo 2025 diligenciado`() {
        val diferencias = esperadosPorCaracteristica.mapNotNull { (id, esperado) ->
            diferenciaFueraDeTolerancia(id, esperado, resultado.promediosPorCaracteristica[id])
        }

        assertTrue(diferencias.isEmpty(), diferencias.joinToString("\n", prefix = "\n"))
    }

    @Test
    fun `el promedio por factor reproduce el modelo 2025 diligenciado`() {
        val diferencias = esperadosPorFactor.mapNotNull { (id, esperado) ->
            diferenciaFueraDeTolerancia(id, esperado, resultado.promediosPorFactor[id])
        }

        assertTrue(diferencias.isEmpty(), diferencias.joinToString("\n", prefix = "\n"))
    }

    @Test
    fun `el promedio global reproduce el modelo 2025 diligenciado`() {
        val diferencia = diferenciaFueraDeTolerancia("EGSTI", esperadoGlobal, resultado.promedioGlobal)

        assertTrue(diferencia == null, diferencia ?: "")
    }

    private fun diferenciaFueraDeTolerancia(id: String, esperado: Double, calculado: Double?): String? {
        if (calculado == null) return "$id: esperado $esperado, calculado ausente (null)"
        val diferencia = calculado - esperado
        if (Math.abs(diferencia) <= tolerancia) return null
        return "$id: esperado $esperado, calculado $calculado (diferencia ${"%+.6f".format(diferencia)})"
    }

    private fun calcularResultado(): ResultadoAutoevaluacion {
        val repositorio = RepositorioAcademicoFalso()
        val parser = CsvDocumentParser()

        parser.extraerResultadosEncuestaPregunta(
            "resultados_encuesta_pregunta.csv",
            fixture("resultados_encuesta_pregunta.csv"),
        ).forEach { repositorio.guardarResultadoEncuestaPregunta(it) }

        parser.extraerEvaluacionesDocumentales(
            "evaluacion_documental.csv",
            fixture("evaluacion_documental.csv"),
        ).forEach { repositorio.guardarEvaluacionDocumental(it) }

        val configuracion: ConfiguracionPonderaciones = CargadorConfiguracionPonderaciones().parsear(
            contenido = String(fixture("ponderaciones_modelo_2025.csv")),
            origen = "ponderaciones_modelo_2025.csv",
        )

        return CalcularResultadoAutoevaluacionUseCase(repositorio, configuracion).ejecutar(programa, periodo)
    }

    /** Lee `esperados_autoevaluacion.csv` (columnas `nivel,id,valor`) y filtra por [nivel]. */
    private fun esperadosDelNivel(nivel: String): Map<String, Double> {
        val contenido = String(fixture("esperados_autoevaluacion.csv"))
        val filas = CsvReader.builder().ofCsvRecord(contenido).use { lector -> lector.map { it.fields } }
        return filas.drop(1)
            .filter { (filaNivel, _, _) -> filaNivel == nivel }
            .associate { (_, id, valor) -> id to valor.toDouble() }
    }

    private fun fixture(nombre: String): ByteArray =
        checkNotNull(javaClass.getResourceAsStream("/fixtures/modelo2025/$nombre")) { "falta el fixture $nombre" }
            .use { it.readBytes() }
}

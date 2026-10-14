package com.example.sgva.infrastructure.reporting

import com.example.sgva.domain.AvanceCiclo
import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.ComparacionCicloAnterior
import com.example.sgva.domain.DiferenciaAvanceCiclo
import com.example.sgva.domain.EstadoCicloAutoevaluacion
import com.example.sgva.domain.EstadoProyecto
import com.example.sgva.domain.IndicadorCalidad
import com.example.sgva.domain.OrigenProyecto
import com.example.sgva.domain.ParticipacionActor
import com.example.sgva.domain.ParticipacionConSemaforo
import com.example.sgva.domain.ProyectoMejoramiento
import com.example.sgva.domain.ReporteEjecutivo
import com.example.sgva.domain.ResultadoAutoevaluacion
import com.example.sgva.domain.RespuestaEncuesta
import com.example.sgva.domain.TipoEstamento
import com.example.sgva.domain.TipoSemaforo
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeneradorReportePdfTest {

    private val generador = GeneradorReportePdf()

    @Test
    fun `genera bytes de un PDF valido con el contenido del reporte`() {
        val reporte = ReporteEjecutivo(
            indicadores = listOf(
                IndicadorCalidad(IndicadorCalidad.TASA_DESERCION, 12.5, "20231", "Ingeniería"),
            ),
            respuestasEncuesta = listOf(
                RespuestaEncuesta("R001", TipoEstamento.ESTUDIANTE, "Infraestructura", 4, "20261"),
            ),
        )

        val pdf = generador.generar(reporte)

        assertTrue(pdf.isNotEmpty())
        assertEquals("%PDF", String(pdf.copyOfRange(0, 4), Charsets.US_ASCII))

        val texto = extraerTexto(pdf)
        assertTrue(texto.contains("Reporte Ejecutivo SGVA"))
        assertTrue(texto.contains(IndicadorCalidad.TASA_DESERCION))
        assertTrue(texto.contains("R001"))
        assertTrue(texto.contains("Infraestructura"))
    }

    @Test
    fun `las secciones sin datos muestran un mensaje en vez de una tabla vacia`() {
        val reporte = ReporteEjecutivo(indicadores = emptyList(), respuestasEncuesta = emptyList())

        val texto = extraerTexto(generador.generar(reporte))

        assertTrue(texto.contains("No hay indicadores disponibles."))
        assertTrue(texto.contains("No hay respuestas de encuesta registradas."))
    }

    @Test
    fun `incluye el resultado de autoevaluacion con sus promedios por caracteristica, factor y global`() {
        val reporte = ReporteEjecutivo(
            indicadores = emptyList(),
            respuestasEncuesta = emptyList(),
            resultadoAutoevaluacion = ResultadoAutoevaluacion(
                programa = "EGSTI",
                periodo = "20261",
                promediosPorCaracteristica = mapOf("C1" to 4.0, "C2" to null),
                promediosPorFactor = mapOf("F1" to 4.5),
                promedioGlobal = 4.5,
            ),
        )

        val texto = extraerTexto(generador.generar(reporte))

        assertTrue(texto.contains("Resultado de Autoevaluación"))
        assertTrue(texto.contains("EGSTI"))
        assertTrue(texto.contains("20261"))
        assertTrue(texto.contains("C1"))
        assertTrue(texto.contains("4.00"))
        assertTrue(texto.contains("F1"))
        assertTrue(texto.contains("Sin datos"))
    }

    @Test
    fun `omite la seccion de resultado de autoevaluacion cuando no hay datos suficientes`() {
        val reporte = ReporteEjecutivo(indicadores = emptyList(), respuestasEncuesta = emptyList())

        val texto = extraerTexto(generador.generar(reporte))

        assertTrue(!texto.contains("Resultado de Autoevaluación"))
    }

    @Test
    fun `un reporte con muchas filas genera varias paginas sin fallar`() {
        val indicadores = (1..80).map {
            IndicadorCalidad("Indicador $it", it.toDouble(), "20261", "Ingeniería")
        }
        val respuestas = (1..80).map {
            RespuestaEncuesta("R$it", TipoEstamento.ESTUDIANTE, "Factor $it", 3, "20261")
        }
        val reporte = ReporteEjecutivo(indicadores = indicadores, respuestasEncuesta = respuestas)

        val pdf = generador.generar(reporte)

        Loader.loadPDF(pdf).use { documento ->
            assertTrue(documento.numberOfPages > 1)
        }
        val texto = extraerTexto(pdf)
        assertTrue(texto.contains("Indicador 80"))
        assertTrue(texto.contains("R80"))
    }

    @Test
    fun `incluye el estado del ciclo con avance por fase, participacion y plan de mejoramiento`() {
        val reporte = ReporteEjecutivo(
            indicadores = emptyList(),
            respuestasEncuesta = emptyList(),
            estadoCiclo = EstadoCicloAutoevaluacion(
                ciclo = CicloAutoevaluacion(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261"),
                avance = AvanceCiclo(
                    ciclo = "EGSTI-20261",
                    avancePorFase = mapOf(1 to 1.0, 2 to null, 3 to null, 4 to null),
                    avanceCiclo = 1.0,
                    actividadesVencidas = emptyList(),
                    actividadesProximasAVencer = emptyList(),
                    factoresConPreevaluacionPendiente = emptySet(),
                    participacionActores = listOf(
                        ParticipacionConSemaforo(
                            participacion = ParticipacionActor(
                                ciclo = "EGSTI-20261",
                                actor = "Estudiantes",
                                poblacion = 100,
                                muestra = 100,
                                respuestas = 90,
                            ),
                            semaforo = TipoSemaforo.VERDE,
                        ),
                    ),
                ),
                proyectosMejoramiento = listOf(
                    ProyectoMejoramiento(
                        id = "PM1",
                        programa = "EGSTI",
                        nombre = "Proyecto de prueba",
                        aspectosMejora = listOf("Aspecto 1"),
                        origen = OrigenProyecto.AUTOEVALUACION,
                        objetivo = "Objetivo de prueba",
                        actividades = listOf("Actividad 1"),
                        factores = setOf("F1"),
                        estado = EstadoProyecto.EN_EJECUCION,
                    ),
                ),
                comparacionCicloAnterior = null,
            ),
        )

        val texto = extraerTexto(generador.generar(reporte))

        assertTrue(texto.contains("Estado del Ciclo de Autoevaluación"))
        assertTrue(texto.contains("EGSTI"))
        assertTrue(texto.contains("Apropiación"))
        assertTrue(texto.contains("Estudiantes"))
        assertTrue(texto.contains("VERDE"))
        assertTrue(texto.contains("Proyecto de prueba"))
        assertTrue(texto.contains("EN_EJECUCION"))
    }

    @Test
    fun `incluye la comparacion con el ciclo anterior cuando esta presente`() {
        val reporte = ReporteEjecutivo(
            indicadores = emptyList(),
            respuestasEncuesta = emptyList(),
            estadoCiclo = EstadoCicloAutoevaluacion(
                ciclo = CicloAutoevaluacion(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261"),
                avance = avanceVacio("EGSTI-20261"),
                proyectosMejoramiento = emptyList(),
                comparacionCicloAnterior = ComparacionCicloAnterior(
                    cicloAnterior = CicloAutoevaluacion(id = "EGSTI-20251", programa = "EGSTI", periodo = "20251"),
                    avanceAnterior = avanceVacio("EGSTI-20251").copy(avanceCiclo = 0.5),
                    diferencia = DiferenciaAvanceCiclo(
                        periodoBase = "20251",
                        periodoComparado = "20261",
                        diferenciasPorFase = mapOf(1 to 0.5),
                        diferenciaAvanceCiclo = 0.5,
                    ),
                ),
            ),
        )

        val texto = extraerTexto(generador.generar(reporte))

        assertTrue(texto.contains("Comparación con el ciclo 20251"))
    }

    @Test
    fun `omite la seccion de estado del ciclo cuando no hay datos suficientes`() {
        val reporte = ReporteEjecutivo(indicadores = emptyList(), respuestasEncuesta = emptyList())

        val texto = extraerTexto(generador.generar(reporte))

        assertTrue(!texto.contains("Estado del Ciclo de Autoevaluación"))
    }

    private fun avanceVacio(ciclo: String): AvanceCiclo = AvanceCiclo(
        ciclo = ciclo,
        avancePorFase = emptyMap(),
        avanceCiclo = null,
        actividadesVencidas = emptyList(),
        actividadesProximasAVencer = emptyList(),
        factoresConPreevaluacionPendiente = emptySet(),
        participacionActores = emptyList(),
    )

    private fun extraerTexto(pdf: ByteArray): String =
        Loader.loadPDF(pdf).use { documento -> PDFTextStripper().getText(documento) }
}

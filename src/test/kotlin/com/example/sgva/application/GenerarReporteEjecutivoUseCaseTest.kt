package com.example.sgva.application

import com.example.sgva.domain.Caracteristica
import com.example.sgva.domain.ConfiguracionPonderaciones
import com.example.sgva.domain.Docente
import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.EvaluacionDocumental
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.IndicadorCalidad
import com.example.sgva.domain.NivelFormacion
import com.example.sgva.domain.RespuestaEncuesta
import com.example.sgva.domain.TipoDedicacion
import com.example.sgva.domain.TipoEstamento
import com.example.sgva.usecases.CalcularCapacidadInstaladaUseCase
import com.example.sgva.usecases.CalcularDistribucionFormacionUseCase
import com.example.sgva.usecases.CalcularEvolucionMatriculaUseCase
import com.example.sgva.usecases.CalcularResultadoAutoevaluacionUseCase
import com.example.sgva.usecases.CalcularTasaDesercionUseCase
import com.example.sgva.usecases.ConsolidarPuntajesSaberProUseCase
import com.example.sgva.usecases.ConsultarRespuestasEncuestaUseCase
import com.example.sgva.usecases.GenerarReporteEjecutivoUseCase
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GenerarReporteEjecutivoUseCaseTest {

    private val repositorio = RepositorioAcademicoFalso()
    private val reloj = Clock.fixed(Instant.parse("2026-06-01T00:00:00Z"), ZoneOffset.UTC)

    /** Mismo catálogo sintético usado en `CalcularResultadoAutoevaluacionUseCaseTest`. */
    private val configuracionPonderaciones = ConfiguracionPonderaciones(
        caracteristicas = listOf(
            Caracteristica(id = "C1", nombre = "Característica 1", factor = "F1", ponderacion = 0.5),
            Caracteristica(id = "C2", nombre = "Característica 2", factor = "F1", ponderacion = 0.5),
        ),
        pesosFactores = mapOf("F1" to 1.0),
        pesoEncuestas = 0.4,
        pesoDocumental = 0.6,
    )

    private val useCase = GenerarReporteEjecutivoUseCase(
        CalcularEvolucionMatriculaUseCase(repositorio, reloj),
        CalcularTasaDesercionUseCase(repositorio),
        ConsolidarPuntajesSaberProUseCase(repositorio),
        CalcularDistribucionFormacionUseCase(repositorio),
        CalcularCapacidadInstaladaUseCase(repositorio),
        ConsultarRespuestasEncuestaUseCase(repositorio),
        CalcularResultadoAutoevaluacionUseCase(repositorio, configuracionPonderaciones),
    )

    @Test
    fun `un sistema vacio produce un reporte solo con la evolucion de matricula en ceros y sin respuestas`() {
        val reporte = useCase.ejecutar()

        assertTrue(reporte.indicadores.all { it.nombre == IndicadorCalidad.EVOLUCION_MATRICULA })
        assertTrue(reporte.indicadores.all { it.valor == 0.0 })
        assertTrue(reporte.respuestasEncuesta.isEmpty())
    }

    @Test
    fun `incluye los indicadores calculables sin cohorte cuando hay datos`() {
        repositorio.guardarEstudiante(estudiante(id = "E1", puntajeSaberPro = 250))
        repositorio.guardarDocente(docente(id = "D1"))

        val reporte = useCase.ejecutar()

        val nombres = reporte.indicadores.map { it.nombre }.toSet()
        assertTrue(nombres.contains(IndicadorCalidad.EVOLUCION_MATRICULA))
        assertTrue(nombres.contains(IndicadorCalidad.PROMEDIO_SABER_PRO))
        assertTrue(nombres.contains(IndicadorCalidad.RELACION_ESTUDIANTE_PROFESOR))
        assertTrue(nombres.any { it.startsWith(IndicadorCalidad.DISTRIBUCION_FORMACION) })
        assertTrue(nombres.none { it == IndicadorCalidad.TASA_DESERCION })
    }

    @Test
    fun `incluye la tasa de desercion solo cuando se indica una cohorte`() {
        repositorio.guardarEstudiante(estudiante(id = "E1", cohorte = "20231", estado = EstadoEstudiante.DESERTOR))

        val reporte = useCase.ejecutar(cohorte = "20231")

        assertTrue(reporte.indicadores.any { it.nombre == IndicadorCalidad.TASA_DESERCION })
    }

    @Test
    fun `incluye todas las respuestas de encuesta registradas`() {
        repositorio.guardarRespuestaEncuesta(respuesta(id = "R1"))
        repositorio.guardarRespuestaEncuesta(respuesta(id = "R2"))

        val reporte = useCase.ejecutar()

        assertEquals(2, reporte.respuestasEncuesta.size)
    }

    @Test
    fun `omite el resultado de autoevaluacion cuando no se indica un programa`() {
        registrarDocumental(caracteristica = "C1", calificacion = 4.0)

        val reporte = useCase.ejecutar(periodo = "20261")

        assertNull(reporte.resultadoAutoevaluacion)
    }

    @Test
    fun `omite el resultado de autoevaluacion cuando no hay datos para el programa y periodo indicados`() {
        val reporte = useCase.ejecutar(periodo = "20261", programa = "EGSTI")

        assertNull(reporte.resultadoAutoevaluacion)
    }

    @Test
    fun `incluye el resultado de autoevaluacion cuando hay datos para el programa y periodo indicados`() {
        registrarDocumental(caracteristica = "C1", calificacion = 4.0)
        registrarDocumental(caracteristica = "C2", calificacion = 5.0)

        val reporte = useCase.ejecutar(periodo = "20261", programa = "EGSTI")

        val resultado = reporte.resultadoAutoevaluacion
        assertTrue(resultado != null)
        assertEquals("EGSTI", resultado.programa)
        assertEquals("20261", resultado.periodo)
        assertEquals(4.5, resultado.promediosPorFactor["F1"]!!, 1e-9)
        assertEquals(4.5, resultado.promedioGlobal!!, 1e-9)
    }

    private fun registrarDocumental(caracteristica: String, calificacion: Double) {
        repositorio.guardarEvaluacionDocumental(
            EvaluacionDocumental(programa = "EGSTI", caracteristica = caracteristica, periodo = "20261", calificacion = calificacion),
        )
    }

    private fun estudiante(
        id: String,
        cohorte: String = "20231",
        estado: EstadoEstudiante = EstadoEstudiante.MATRICULADO,
        puntajeSaberPro: Int? = null,
    ): Estudiante = Estudiante(
        id = id,
        programa = "Ingeniería de Sistemas",
        facultad = "Ingeniería",
        cohorte = cohorte,
        estado = estado,
        puntajeSaberPro = puntajeSaberPro,
    )

    private fun docente(id: String): Docente = Docente(
        id = id,
        programa = "EGSTI",
        facultad = "Ingeniería",
        nivelFormacion = NivelFormacion.MAESTRIA,
        dedicacion = TipoDedicacion.TIEMPO_COMPLETO,
        periodo = "20261",
    )

    private fun respuesta(id: String): RespuestaEncuesta = RespuestaEncuesta(
        id = id,
        estamento = TipoEstamento.ESTUDIANTE,
        factor = "Infraestructura",
        calificacion = 4,
        periodo = "20261",
    )
}

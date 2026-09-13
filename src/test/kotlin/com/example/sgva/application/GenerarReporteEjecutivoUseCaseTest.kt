package com.example.sgva.application

import com.example.sgva.domain.Docente
import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.IndicadorCalidad
import com.example.sgva.domain.NivelFormacion
import com.example.sgva.domain.RespuestaEncuesta
import com.example.sgva.domain.TipoDedicacion
import com.example.sgva.domain.TipoEstamento
import com.example.sgva.usecases.CalcularCapacidadInstaladaUseCase
import com.example.sgva.usecases.CalcularDistribucionFormacionUseCase
import com.example.sgva.usecases.CalcularEvolucionMatriculaUseCase
import com.example.sgva.usecases.CalcularTasaDesercionUseCase
import com.example.sgva.usecases.ConsolidarPuntajesSaberProUseCase
import com.example.sgva.usecases.ConsultarRespuestasEncuestaUseCase
import com.example.sgva.usecases.GenerarReporteEjecutivoUseCase
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GenerarReporteEjecutivoUseCaseTest {

    private val repositorio = RepositorioAcademicoFalso()
    private val reloj = Clock.fixed(Instant.parse("2026-06-01T00:00:00Z"), ZoneOffset.UTC)

    private val useCase = GenerarReporteEjecutivoUseCase(
        CalcularEvolucionMatriculaUseCase(repositorio, reloj),
        CalcularTasaDesercionUseCase(repositorio),
        ConsolidarPuntajesSaberProUseCase(repositorio),
        CalcularDistribucionFormacionUseCase(repositorio),
        CalcularCapacidadInstaladaUseCase(repositorio),
        ConsultarRespuestasEncuestaUseCase(repositorio),
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

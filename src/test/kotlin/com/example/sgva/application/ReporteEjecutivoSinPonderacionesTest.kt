package com.example.sgva.application

import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.ConfiguracionSeguimiento
import com.example.sgva.domain.Docente
import com.example.sgva.domain.EvaluacionDocumental
import com.example.sgva.domain.GrupoEvaluador
import com.example.sgva.domain.IndicadorCalidad
import com.example.sgva.domain.NivelFormacion
import com.example.sgva.domain.TipoDedicacion
import com.example.sgva.usecases.CalcularAvanceCicloUseCase
import com.example.sgva.usecases.CalcularCapacidadInstaladaUseCase
import com.example.sgva.usecases.CalcularDistribucionFormacionUseCase
import com.example.sgva.usecases.CalcularEvolucionMatriculaUseCase
import com.example.sgva.usecases.CalcularResultadoAutoevaluacionUseCase
import com.example.sgva.usecases.CalcularTasaDesercionUseCase
import com.example.sgva.usecases.ConsolidarPuntajesSaberProUseCase
import com.example.sgva.usecases.ConsultarCicloAutoevaluacionUseCase
import com.example.sgva.usecases.ConsultarConfiguracionPonderacionesUseCase
import com.example.sgva.usecases.ConsultarEstadoCicloAutoevaluacionUseCase
import com.example.sgva.usecases.ConsultarProyectoMejoramientoUseCase
import com.example.sgva.usecases.ConsultarRespuestasEncuestaUseCase
import com.example.sgva.usecases.GenerarReporteEjecutivoUseCase
import com.example.sgva.usecases.RegistrarCicloAutoevaluacionUseCase
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Mientras no se haya cargado el archivo de ponderaciones, el reporte
 * ejecutivo se genera igual y omite las secciones que dependen de ellas.
 */
class ReporteEjecutivoSinPonderacionesTest {

    private val repositorio = RepositorioAcademicoFalso()
    private val consultarPonderaciones =
        ConsultarConfiguracionPonderacionesUseCase(RepositorioConfiguracionPonderacionesFalso())
    private val reloj = Clock.fixed(Instant.parse("2026-06-01T00:00:00Z"), ZoneOffset.UTC)

    private val useCase = GenerarReporteEjecutivoUseCase(
        CalcularEvolucionMatriculaUseCase(repositorio, reloj),
        CalcularTasaDesercionUseCase(repositorio),
        ConsolidarPuntajesSaberProUseCase(repositorio),
        CalcularDistribucionFormacionUseCase(repositorio),
        CalcularCapacidadInstaladaUseCase(repositorio),
        ConsultarRespuestasEncuestaUseCase(repositorio),
        CalcularResultadoAutoevaluacionUseCase(repositorio, consultarPonderaciones::ejecutar),
        ConsultarEstadoCicloAutoevaluacionUseCase(
            ConsultarCicloAutoevaluacionUseCase(repositorio),
            CalcularAvanceCicloUseCase(repositorio, consultarPonderaciones::ejecutar, ConfiguracionSeguimiento(0.6, 0.8, 7)),
            ConsultarProyectoMejoramientoUseCase(repositorio),
        ),
        reloj,
    )

    @Test
    fun `el reporte omite el resultado y el estado del ciclo y conserva los indicadores`() {
        repositorio.guardarDocente(
            Docente("D1", "EGSTI", "Ingeniería", NivelFormacion.MAESTRIA, TipoDedicacion.TIEMPO_COMPLETO, "20261"),
        )
        repositorio.guardarEvaluacionDocumental(EvaluacionDocumental("EGSTI", "C1", "20261", 4.0))
        RegistrarCicloAutoevaluacionUseCase(repositorio).ejecutar(
            CicloAutoevaluacion(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261"),
        )
        repositorio.guardarGrupoEvaluador(GrupoEvaluador(ciclo = "EGSTI-20261", factores = setOf("F1"), taller = 1))

        val reporte = useCase.ejecutar(periodo = "20261", programa = "EGSTI")

        assertNull(reporte.resultadoAutoevaluacion)
        assertNull(reporte.estadoCiclo)
        assertTrue(reporte.indicadores.any { it.nombre == IndicadorCalidad.DISTRIBUCION_FORMACION + " - MAESTRIA" || it.nombre.startsWith(IndicadorCalidad.DISTRIBUCION_FORMACION) })
    }

    @Test
    fun `un ciclo sin grupos evaluadores conserva su estado aunque no haya ponderaciones`() {
        RegistrarCicloAutoevaluacionUseCase(repositorio).ejecutar(
            CicloAutoevaluacion(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261"),
        )

        val reporte = useCase.ejecutar(periodo = "20261", programa = "EGSTI")

        assertNotNull(reporte.estadoCiclo)
    }
}

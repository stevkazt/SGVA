package com.example.sgva.application

import com.example.sgva.domain.ActividadCiclo
import com.example.sgva.domain.Caracteristica
import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.ConfiguracionPonderaciones
import com.example.sgva.domain.ConfiguracionSeguimiento
import com.example.sgva.domain.EstadoActividad
import com.example.sgva.domain.EstadoProyecto
import com.example.sgva.domain.OrigenProyecto
import com.example.sgva.domain.ParticipacionActor
import com.example.sgva.domain.ProyectoMejoramiento
import com.example.sgva.usecases.CalcularAvanceCicloUseCase
import com.example.sgva.usecases.ConsultarCicloAutoevaluacionUseCase
import com.example.sgva.usecases.ConsultarEstadoCicloAutoevaluacionUseCase
import com.example.sgva.usecases.ConsultarProyectoMejoramientoUseCase
import com.example.sgva.usecases.RegistrarCicloAutoevaluacionUseCase
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Pruebas del estado consolidado del ciclo para el reporte ejecutivo (Módulo
 * 5, V6, `DESIGN_SGVA.md` §4): avance, participación, plan de mejoramiento y
 * comparación con el ciclo inmediatamente anterior del mismo programa.
 */
class ConsultarEstadoCicloAutoevaluacionUseCaseTest {

    private val repositorio = RepositorioAcademicoFalso()
    private val configuracionPonderaciones = ConfiguracionPonderaciones(
        caracteristicas = listOf(
            Caracteristica(id = "C1", nombre = "Característica 1", factor = "F1", ponderacion = 1.0),
        ),
        pesosFactores = mapOf("F1" to 1.0),
        pesoEncuestas = 0.4,
        pesoDocumental = 0.6,
    )
    private val configuracionSeguimiento = ConfiguracionSeguimiento(0.6, 0.8, 7)
    private val fechaReferencia = LocalDate.of(2026, 6, 1)

    private val useCase = ConsultarEstadoCicloAutoevaluacionUseCase(
        ConsultarCicloAutoevaluacionUseCase(repositorio),
        CalcularAvanceCicloUseCase(repositorio, configuracionPonderaciones, configuracionSeguimiento),
        ConsultarProyectoMejoramientoUseCase(repositorio),
    )

    @Test
    fun `devuelve null cuando no hay ningun ciclo registrado para el programa y periodo indicados`() {
        val estado = useCase.ejecutar("EGSTI", "20261", fechaReferencia)

        assertNull(estado)
    }

    @Test
    fun `incluye el avance, la participacion y los proyectos del programa del ciclo`() {
        registrarCiclo(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261")
        repositorio.guardarActividad(actividad(ciclo = "EGSTI-20261", id = "1.1", fase = 1))
        repositorio.guardarParticipacion(
            ParticipacionActor(ciclo = "EGSTI-20261", actor = "Estudiantes", poblacion = 50, muestra = 50, respuestas = 45),
        )
        repositorio.guardarProyecto(proyecto(id = "PM1", programa = "EGSTI"))
        repositorio.guardarProyecto(proyecto(id = "PM2", programa = "OTRO"))

        val estado = useCase.ejecutar("EGSTI", "20261", fechaReferencia)

        assertTrue(estado != null)
        assertEquals(1.0, estado.avance.avancePorFase[1])
        assertEquals(1, estado.avance.participacionActores.size)
        assertEquals(listOf("PM1"), estado.proyectosMejoramiento.map { it.id })
    }

    @Test
    fun `omite la comparacion cuando el programa no tiene un ciclo anterior registrado`() {
        registrarCiclo(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261")

        val estado = useCase.ejecutar("EGSTI", "20261", fechaReferencia)

        assertTrue(estado != null)
        assertNull(estado.comparacionCicloAnterior)
    }

    @Test
    fun `compara con el ciclo inmediatamente anterior del mismo programa`() {
        registrarCiclo(id = "EGSTI-20251", programa = "EGSTI", periodo = "20251")
        repositorio.guardarActividad(actividad(ciclo = "EGSTI-20251", id = "1.1", fase = 1, estado = EstadoActividad.PLANIFICADA))
        registrarCiclo(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261")
        repositorio.guardarActividad(actividad(ciclo = "EGSTI-20261", id = "1.1", fase = 1, estado = EstadoActividad.COMPLETADA))

        val estado = useCase.ejecutar("EGSTI", "20261", fechaReferencia)

        val comparacion = estado?.comparacionCicloAnterior
        assertTrue(comparacion != null)
        assertEquals("EGSTI-20251", comparacion.cicloAnterior.id)
        assertEquals(0.0, comparacion.avanceAnterior.avancePorFase[1])
        assertEquals("20251", comparacion.diferencia.periodoBase)
        assertEquals("20261", comparacion.diferencia.periodoComparado)
        assertEquals(1.0, comparacion.diferencia.diferenciaAvanceCiclo)
    }

    @Test
    fun `ignora ciclos de otros programas al buscar el ciclo anterior`() {
        registrarCiclo(id = "OTRO-20251", programa = "OTRO", periodo = "20251")
        registrarCiclo(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261")

        val estado = useCase.ejecutar("EGSTI", "20261", fechaReferencia)

        assertTrue(estado != null)
        assertNull(estado.comparacionCicloAnterior)
    }

    private fun registrarCiclo(id: String, programa: String, periodo: String) {
        RegistrarCicloAutoevaluacionUseCase(repositorio).ejecutar(
            CicloAutoevaluacion(id = id, programa = programa, periodo = periodo),
        )
    }

    private fun actividad(
        ciclo: String,
        id: String,
        fase: Int,
        estado: EstadoActividad = EstadoActividad.COMPLETADA,
    ): ActividadCiclo = ActividadCiclo(
        id = id,
        ciclo = ciclo,
        fase = fase,
        descripcion = "Actividad de prueba",
        responsable = "Comité de aseguramiento de la calidad",
        fechaInicioPlanificada = LocalDate.of(2026, 1, 1),
        fechaFinPlanificada = LocalDate.of(2026, 12, 1),
        estado = estado,
    )

    private fun proyecto(id: String, programa: String): ProyectoMejoramiento = ProyectoMejoramiento(
        id = id,
        programa = programa,
        nombre = "Proyecto de prueba",
        aspectosMejora = listOf("Aspecto 1"),
        origen = OrigenProyecto.AUTOEVALUACION,
        objetivo = "Objetivo de prueba",
        actividades = listOf("Actividad 1"),
        factores = setOf("F1"),
        estado = EstadoProyecto.PLANIFICADO,
    )
}

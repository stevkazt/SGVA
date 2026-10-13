package com.example.sgva.application

import com.example.sgva.domain.ActividadCiclo
import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.EstadoActividad
import com.example.sgva.domain.FaseCiclo
import com.example.sgva.domain.ReferenciaInconsistenteException
import com.example.sgva.usecases.ConsultarCicloAutoevaluacionUseCase
import com.example.sgva.usecases.RegistrarActividadCicloUseCase
import com.example.sgva.usecases.RegistrarCicloAutoevaluacionUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GestionCicloAutoevaluacionUseCaseTest {

    private val repositorio = RepositorioAcademicoFalso()
    private val registrarCiclo = RegistrarCicloAutoevaluacionUseCase(repositorio)
    private val registrarActividad = RegistrarActividadCicloUseCase(repositorio)
    private val consultarCiclo = ConsultarCicloAutoevaluacionUseCase(repositorio)

    @Test
    fun `registrar un ciclo crea automaticamente sus cuatro fases`() {
        registrarCiclo.ejecutar(ciclo())

        val detalle = consultarCiclo.buscarDetallePorId("EGSTI-20261")

        assertEquals(4, detalle?.fases?.size)
        assertEquals((1..4).toList(), detalle?.fases?.map { it.numero })
        assertEquals(FaseCiclo.NOMBRES[1], detalle?.fases?.first { it.numero == 1 }?.nombre)
    }

    @Test
    fun `registrar un ciclo con actividades iniciales las deja disponibles en el detalle`() {
        registrarCiclo.ejecutar(ciclo(), listOf(actividad()))

        val detalle = consultarCiclo.buscarDetallePorId("EGSTI-20261")

        assertEquals(1, detalle?.actividades?.size)
        assertEquals("1.1", detalle?.actividades?.first()?.id)
    }

    @Test
    fun `un programa no puede tener dos ciclos en el mismo periodo`() {
        registrarCiclo.ejecutar(ciclo())

        assertThrows<EntidadDuplicadaException> {
            registrarCiclo.ejecutar(CicloAutoevaluacion(id = "EGSTI-20261-B", programa = "EGSTI", periodo = "20261"))
        }
    }

    @Test
    fun `un mismo programa puede tener ciclos en periodos distintos`() {
        registrarCiclo.ejecutar(ciclo())

        registrarCiclo.ejecutar(CicloAutoevaluacion(id = "EGSTI-20271", programa = "EGSTI", periodo = "20271"))

        assertEquals(2, consultarCiclo.listarTodos().size)
    }

    @Test
    fun `una actividad inicial que no pertenece al ciclo que se registra es rechazada`() {
        val actividadDeOtroCiclo = actividad(ciclo = "OTRO-CICLO")

        assertThrows<ReferenciaInconsistenteException> {
            registrarCiclo.ejecutar(ciclo(), listOf(actividadDeOtroCiclo))
        }
    }

    @Test
    fun `registrar una actividad adicional en un ciclo existente la deja disponible en el detalle`() {
        registrarCiclo.ejecutar(ciclo())

        registrarActividad.ejecutar(actividad(id = "2.1", fase = 2))

        val detalle = consultarCiclo.buscarDetallePorId("EGSTI-20261")
        assertEquals(1, detalle?.actividades?.size)
    }

    @Test
    fun `registrar la misma actividad dos veces en el mismo ciclo es rechazado`() {
        registrarCiclo.ejecutar(ciclo(), listOf(actividad()))

        assertThrows<EntidadDuplicadaException> {
            registrarActividad.ejecutar(actividad())
        }
    }

    @Test
    fun `consultar el detalle de un ciclo inexistente devuelve null`() {
        assertNull(consultarCiclo.buscarDetallePorId("NO-EXISTE"))
    }

    private fun ciclo(): CicloAutoevaluacion = CicloAutoevaluacion(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261")

    private fun actividad(
        id: String = "1.1",
        ciclo: String = "EGSTI-20261",
        fase: Int = 1,
    ): ActividadCiclo = ActividadCiclo(
        id = id,
        ciclo = ciclo,
        fase = fase,
        descripcion = "Sensibilización del proceso",
        responsable = "Comité de aseguramiento de la calidad",
        fechaInicioPlanificada = LocalDate.of(2026, 1, 1),
        fechaFinPlanificada = LocalDate.of(2026, 2, 1),
        estado = EstadoActividad.PLANIFICADA,
    )
}

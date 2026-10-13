package com.example.sgva.application

import com.example.sgva.domain.ActividadCiclo
import com.example.sgva.domain.Caracteristica
import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.CicloNoEncontradoException
import com.example.sgva.domain.ConfiguracionPonderaciones
import com.example.sgva.domain.ConfiguracionSeguimiento
import com.example.sgva.domain.EstadoActividad
import com.example.sgva.domain.GrupoEvaluador
import com.example.sgva.domain.ParticipacionActor
import com.example.sgva.domain.PreevaluacionEvaluador
import com.example.sgva.domain.TipoSemaforo
import com.example.sgva.usecases.CalcularAvanceCicloUseCase
import com.example.sgva.usecases.RegistrarCicloAutoevaluacionUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CalcularAvanceCicloUseCaseTest {

    private val repositorio = RepositorioAcademicoFalso()
    private val configuracionPonderaciones = ConfiguracionPonderaciones(
        caracteristicas = listOf(
            Caracteristica(id = "C1", nombre = "Característica 1", factor = "F1", ponderacion = 0.5),
            Caracteristica(id = "C2", nombre = "Característica 2", factor = "F1", ponderacion = 0.5),
            Caracteristica(id = "C3", nombre = "Característica 3", factor = "F2", ponderacion = 1.0),
        ),
        pesosFactores = mapOf("F1" to 0.6, "F2" to 0.4),
        pesoEncuestas = 0.4,
        pesoDocumental = 0.6,
    )
    private val configuracionSeguimiento = ConfiguracionSeguimiento(0.6, 0.8, 7)
    private val calcularAvance =
        CalcularAvanceCicloUseCase(repositorio, configuracionPonderaciones, configuracionSeguimiento)

    @BeforeEach
    fun registrarCicloBase() {
        RegistrarCicloAutoevaluacionUseCase(repositorio).ejecutar(
            CicloAutoevaluacion(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261"),
        )
    }

    @Test
    fun `el avance de una fase sin actividades es ausente`() {
        val avance = calcularAvance.ejecutar("EGSTI-20261", LocalDate.of(2026, 6, 1))

        assertNull(avance.avancePorFase[1])
        assertNull(avance.avanceCiclo)
    }

    @Test
    fun `el avance de una fase es completadas sobre total`() {
        repositorio.guardarActividad(actividad(id = "1.1", fase = 1, estado = EstadoActividad.COMPLETADA))
        repositorio.guardarActividad(actividad(id = "1.2", fase = 1, estado = EstadoActividad.EN_CURSO))

        val avance = calcularAvance.ejecutar("EGSTI-20261", LocalDate.of(2026, 6, 1))

        assertEquals(0.5, avance.avancePorFase[1])
    }

    @Test
    fun `el avance del ciclo es el promedio de las fases con avance calculable`() {
        repositorio.guardarActividad(actividad(id = "1.1", fase = 1, estado = EstadoActividad.COMPLETADA))
        repositorio.guardarActividad(actividad(id = "2.1", fase = 2, estado = EstadoActividad.PLANIFICADA))

        val avance = calcularAvance.ejecutar("EGSTI-20261", LocalDate.of(2026, 6, 1))

        assertEquals(0.5, avance.avanceCiclo)
    }

    @Test
    fun `una actividad vencida no completada aparece en actividadesVencidas`() {
        repositorio.guardarActividad(
            actividad(id = "1.1", fase = 1, fin = LocalDate.of(2026, 1, 1), estado = EstadoActividad.EN_CURSO),
        )

        val avance = calcularAvance.ejecutar("EGSTI-20261", LocalDate.of(2026, 6, 1))

        assertEquals(listOf("1.1"), avance.actividadesVencidas.map { it.id })
        assertTrue(avance.actividadesProximasAVencer.isEmpty())
    }

    @Test
    fun `una actividad que vence dentro de los dias configurados aparece en proximasAVencer`() {
        repositorio.guardarActividad(
            actividad(id = "1.1", fase = 1, fin = LocalDate.of(2026, 6, 5), estado = EstadoActividad.PLANIFICADA),
        )

        val avance = calcularAvance.ejecutar("EGSTI-20261", LocalDate.of(2026, 6, 1))

        assertEquals(listOf("1.1"), avance.actividadesProximasAVencer.map { it.id })
        assertTrue(avance.actividadesVencidas.isEmpty())
    }

    @Test
    fun `un factor con alguna caracteristica sin preevaluacion queda pendiente`() {
        repositorio.guardarGrupoEvaluador(GrupoEvaluador(ciclo = "EGSTI-20261", factores = setOf("F1"), taller = 1))
        repositorio.guardarPreevaluacion(
            PreevaluacionEvaluador(ciclo = "EGSTI-20261", caracteristica = "C1", evaluador = "EVAL-01", calificacion = 4.0),
        )

        val avance = calcularAvance.ejecutar("EGSTI-20261", LocalDate.of(2026, 6, 1))

        assertEquals(setOf("F1"), avance.factoresConPreevaluacionPendiente)
    }

    @Test
    fun `un factor con todas sus caracteristicas preevaluadas no queda pendiente`() {
        repositorio.guardarGrupoEvaluador(GrupoEvaluador(ciclo = "EGSTI-20261", factores = setOf("F1"), taller = 1))
        repositorio.guardarPreevaluacion(
            PreevaluacionEvaluador(ciclo = "EGSTI-20261", caracteristica = "C1", evaluador = "EVAL-01", calificacion = 4.0),
        )
        repositorio.guardarPreevaluacion(
            PreevaluacionEvaluador(ciclo = "EGSTI-20261", caracteristica = "C2", evaluador = "EVAL-01", calificacion = 4.0),
        )

        val avance = calcularAvance.ejecutar("EGSTI-20261", LocalDate.of(2026, 6, 1))

        assertTrue(avance.factoresConPreevaluacionPendiente.isEmpty())
    }

    @Test
    fun `la participacion de actores se devuelve con su semaforo ya resuelto`() {
        repositorio.guardarParticipacion(
            ParticipacionActor(ciclo = "EGSTI-20261", actor = "Estudiantes", poblacion = 100, muestra = 100, respuestas = 90),
        )

        val avance = calcularAvance.ejecutar("EGSTI-20261", LocalDate.of(2026, 6, 1))

        assertEquals(1, avance.participacionActores.size)
        assertEquals(TipoSemaforo.VERDE, avance.participacionActores.first().semaforo)
    }

    @Test
    fun `calcular el avance de un ciclo inexistente es rechazado`() {
        assertThrows<CicloNoEncontradoException> {
            calcularAvance.ejecutar("NO-EXISTE", LocalDate.of(2026, 6, 1))
        }
    }

    private fun actividad(
        id: String,
        fase: Int,
        inicio: LocalDate = LocalDate.of(2026, 1, 1),
        fin: LocalDate = LocalDate.of(2026, 12, 1),
        estado: EstadoActividad,
    ): ActividadCiclo = ActividadCiclo(
        id = id,
        ciclo = "EGSTI-20261",
        fase = fase,
        descripcion = "Actividad de prueba",
        responsable = "Comité de aseguramiento de la calidad",
        fechaInicioPlanificada = inicio,
        fechaFinPlanificada = fin,
        estado = estado,
    )
}

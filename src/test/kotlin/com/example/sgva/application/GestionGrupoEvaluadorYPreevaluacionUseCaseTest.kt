package com.example.sgva.application

import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.CicloNoEncontradoException
import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.GrupoEvaluador
import com.example.sgva.domain.PreevaluacionEvaluador
import com.example.sgva.usecases.ConsolidarPreevaluacionesUseCase
import com.example.sgva.usecases.ConsultarGrupoEvaluadorUseCase
import com.example.sgva.usecases.ConsultarPreevaluacionUseCase
import com.example.sgva.usecases.RegistrarCicloAutoevaluacionUseCase
import com.example.sgva.usecases.RegistrarGrupoEvaluadorUseCase
import com.example.sgva.usecases.RegistrarPreevaluacionUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GestionGrupoEvaluadorYPreevaluacionUseCaseTest {

    private val repositorio = RepositorioAcademicoFalso()
    private val registrarGrupoEvaluador = RegistrarGrupoEvaluadorUseCase(repositorio)
    private val consultarGrupoEvaluador = ConsultarGrupoEvaluadorUseCase(repositorio)
    private val registrarPreevaluacion = RegistrarPreevaluacionUseCase(repositorio)
    private val consultarPreevaluacion = ConsultarPreevaluacionUseCase(repositorio)
    private val consolidarPreevaluaciones = ConsolidarPreevaluacionesUseCase(repositorio)

    @BeforeEach
    fun registrarCicloBase() {
        RegistrarCicloAutoevaluacionUseCase(repositorio).ejecutar(
            CicloAutoevaluacion(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261"),
        )
    }

    @Test
    fun `un factor puede aparecer en mas de un taller del mismo ciclo`() {
        registrarGrupoEvaluador.ejecutar(GrupoEvaluador(ciclo = "EGSTI-20261", factores = setOf("F5"), taller = 1))
        registrarGrupoEvaluador.ejecutar(GrupoEvaluador(ciclo = "EGSTI-20261", factores = setOf("F5"), taller = 2))

        assertEquals(2, consultarGrupoEvaluador.listarPorCiclo("EGSTI-20261").size)
    }

    @Test
    fun `dos grupos con el mismo ciclo y taller son rechazados como duplicados`() {
        registrarGrupoEvaluador.ejecutar(GrupoEvaluador(ciclo = "EGSTI-20261", factores = setOf("F1"), taller = 1))

        assertThrows<EntidadDuplicadaException> {
            registrarGrupoEvaluador.ejecutar(GrupoEvaluador(ciclo = "EGSTI-20261", factores = setOf("F2"), taller = 1))
        }
    }

    @Test
    fun `registrar un grupo evaluador de un ciclo inexistente es rechazado`() {
        assertThrows<CicloNoEncontradoException> {
            registrarGrupoEvaluador.ejecutar(GrupoEvaluador(ciclo = "NO-EXISTE", factores = setOf("F1"), taller = 1))
        }
    }

    @Test
    fun `registrar una preevaluacion la hace recuperable por ciclo`() {
        registrarPreevaluacion.ejecutar(preevaluacion(caracteristica = "1.1", evaluador = "EVAL-01"))

        assertEquals(1, consultarPreevaluacion.listarPorCiclo("EGSTI-20261").size)
    }

    @Test
    fun `la misma clave natural de preevaluacion no puede registrarse dos veces`() {
        registrarPreevaluacion.ejecutar(preevaluacion(caracteristica = "1.1", evaluador = "EVAL-01"))

        assertThrows<EntidadDuplicadaException> {
            registrarPreevaluacion.ejecutar(preevaluacion(caracteristica = "1.1", evaluador = "EVAL-01"))
        }
    }

    @Test
    fun `evaluadores distintos pueden preevaluar la misma caracteristica`() {
        registrarPreevaluacion.ejecutar(preevaluacion(caracteristica = "1.1", evaluador = "EVAL-01"))
        registrarPreevaluacion.ejecutar(preevaluacion(caracteristica = "1.1", evaluador = "EVAL-02"))

        assertEquals(2, consultarPreevaluacion.listarPorCiclo("EGSTI-20261").size)
    }

    @Test
    fun `consolidar promedia las calificaciones de la fuente sin redondear`() {
        registrarPreevaluacion.ejecutar(preevaluacion(caracteristica = "1.1", evaluador = "EVAL-01", calificacion = 5.0))
        registrarPreevaluacion.ejecutar(preevaluacion(caracteristica = "1.1", evaluador = "EVAL-02", calificacion = 4.4))
        registrarPreevaluacion.ejecutar(preevaluacion(caracteristica = "1.1", evaluador = "EVAL-03", calificacion = 4.0))

        val consolidadas = consolidarPreevaluaciones.ejecutar("EGSTI-20261")

        assertEquals(1, consolidadas.size)
        assertEquals(4.4667, consolidadas.first().calificacion, 0.0001)
        assertEquals("EGSTI", consolidadas.first().programa)
        assertEquals("20261", consolidadas.first().periodo)
    }

    @Test
    fun `consolidar vuelve a calcular y sobrescribe la evaluacion documental de ejecuciones previas`() {
        registrarPreevaluacion.ejecutar(preevaluacion(caracteristica = "1.1", evaluador = "EVAL-01", calificacion = 3.0))
        consolidarPreevaluaciones.ejecutar("EGSTI-20261")

        registrarPreevaluacion.ejecutar(preevaluacion(caracteristica = "1.1", evaluador = "EVAL-02", calificacion = 5.0))
        val consolidadas = consolidarPreevaluaciones.ejecutar("EGSTI-20261")

        assertEquals(4.0, consolidadas.first().calificacion)
        assertEquals(1, repositorio.listarEvaluacionesDocumentales().size)
    }

    @Test
    fun `consolidar un ciclo sin preevaluaciones no genera ninguna evaluacion documental`() {
        val consolidadas = consolidarPreevaluaciones.ejecutar("EGSTI-20261")

        assertTrue(consolidadas.isEmpty())
    }

    @Test
    fun `consolidar un ciclo inexistente es rechazado`() {
        assertThrows<CicloNoEncontradoException> {
            consolidarPreevaluaciones.ejecutar("NO-EXISTE")
        }
    }

    private fun preevaluacion(
        caracteristica: String,
        evaluador: String,
        calificacion: Double = 4.0,
    ): PreevaluacionEvaluador = PreevaluacionEvaluador(
        ciclo = "EGSTI-20261",
        caracteristica = caracteristica,
        evaluador = evaluador,
        calificacion = calificacion,
    )
}

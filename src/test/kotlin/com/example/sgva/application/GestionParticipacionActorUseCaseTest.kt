package com.example.sgva.application

import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.CicloNoEncontradoException
import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.ParticipacionActor
import com.example.sgva.usecases.ConsultarParticipacionActorUseCase
import com.example.sgva.usecases.RegistrarCicloAutoevaluacionUseCase
import com.example.sgva.usecases.RegistrarParticipacionActorUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class GestionParticipacionActorUseCaseTest {

    private val repositorio = RepositorioAcademicoFalso()
    private val registrarParticipacion = RegistrarParticipacionActorUseCase(repositorio)
    private val consultarParticipacion = ConsultarParticipacionActorUseCase(repositorio)

    @BeforeEach
    fun registrarCicloBase() {
        RegistrarCicloAutoevaluacionUseCase(repositorio).ejecutar(
            CicloAutoevaluacion(id = "EGSTI-20261", programa = "EGSTI", periodo = "20261"),
        )
    }

    @Test
    fun `registrar una participacion la hace recuperable por ciclo`() {
        registrarParticipacion.ejecutar(participacion(actor = "Estudiantes"))

        assertEquals(1, consultarParticipacion.listarPorCiclo("EGSTI-20261").size)
    }

    @Test
    fun `la misma clave natural ciclo mas actor no puede registrarse dos veces`() {
        registrarParticipacion.ejecutar(participacion(actor = "Estudiantes"))

        assertThrows<EntidadDuplicadaException> {
            registrarParticipacion.ejecutar(participacion(actor = "Estudiantes"))
        }
    }

    @Test
    fun `actores distintos del mismo ciclo no son duplicados`() {
        registrarParticipacion.ejecutar(participacion(actor = "Estudiantes"))
        registrarParticipacion.ejecutar(participacion(actor = "Profesores"))

        assertEquals(2, consultarParticipacion.listarPorCiclo("EGSTI-20261").size)
    }

    @Test
    fun `registrar una participacion de un ciclo inexistente es rechazado`() {
        assertThrows<CicloNoEncontradoException> {
            registrarParticipacion.ejecutar(
                ParticipacionActor(ciclo = "NO-EXISTE", actor = "Estudiantes", poblacion = 10, muestra = 5, respuestas = 2),
            )
        }
    }

    private fun participacion(actor: String): ParticipacionActor =
        ParticipacionActor(ciclo = "EGSTI-20261", actor = actor, poblacion = 100, muestra = 50, respuestas = 40)
}

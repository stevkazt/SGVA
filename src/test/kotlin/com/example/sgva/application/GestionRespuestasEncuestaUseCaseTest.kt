package com.example.sgva.application

import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.RespuestaEncuesta
import com.example.sgva.domain.TipoEstamento
import com.example.sgva.usecases.ConsultarRespuestasEncuestaUseCase
import com.example.sgva.usecases.RegistrarRespuestaEncuestaUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GestionRespuestasEncuestaUseCaseTest {

    private val repositorio = RepositorioAcademicoFalso()
    private val registrar = RegistrarRespuestaEncuestaUseCase(repositorio)
    private val consultar = ConsultarRespuestasEncuestaUseCase(repositorio)

    @Test
    fun `registrar una respuesta la hace recuperable por id`() {
        val respuesta = respuesta(id = "R001")

        registrar.ejecutar(respuesta)

        assertEquals(respuesta, consultar.buscarPorId("R001"))
    }

    @Test
    fun `el listado devuelve las respuestas en orden de registro`() {
        val primera = respuesta(id = "R001")
        val segunda = respuesta(id = "R002")

        registrar.ejecutar(primera)
        registrar.ejecutar(segunda)

        assertEquals(listOf(primera, segunda), consultar.listarTodas())
    }

    @Test
    fun `registrar un id ya existente es rechazado sin sobrescribir el registro previo`() {
        val original = respuesta(id = "R001", calificacion = 5)
        registrar.ejecutar(original)

        assertThrows<EntidadDuplicadaException> {
            registrar.ejecutar(respuesta(id = "R001", calificacion = 1))
        }
        assertEquals(original, consultar.buscarPorId("R001"))
    }

    @Test
    fun `consultar un id inexistente devuelve null`() {
        assertNull(consultar.buscarPorId("NO-EXISTE"))
    }

    @Test
    fun `el listado esta vacio cuando no hay respuestas registradas`() {
        assertEquals(emptyList(), consultar.listarTodas())
    }

    private fun respuesta(
        id: String,
        calificacion: Int = 4,
    ): RespuestaEncuesta = RespuestaEncuesta(
        id = id,
        estamento = TipoEstamento.ESTUDIANTE,
        factor = "Infraestructura",
        calificacion = calificacion,
        periodo = "20261",
    )
}

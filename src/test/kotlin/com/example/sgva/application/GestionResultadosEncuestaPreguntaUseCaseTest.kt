package com.example.sgva.application

import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.ResultadoEncuestaPregunta
import com.example.sgva.domain.TipoEstamento
import com.example.sgva.usecases.ConsultarResultadoEncuestaPreguntaUseCase
import com.example.sgva.usecases.RegistrarResultadoEncuestaPreguntaUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GestionResultadosEncuestaPreguntaUseCaseTest {

    private val repositorio = RepositorioAcademicoFalso()
    private val registrar = RegistrarResultadoEncuestaPreguntaUseCase(repositorio)
    private val consultar = ConsultarResultadoEncuestaPreguntaUseCase(repositorio)

    @Test
    fun `registrar un resultado lo hace recuperable por su clave natural`() {
        val resultado = resultado(codigoPregunta = "A1")

        registrar.ejecutar(resultado)

        assertEquals(resultado, consultar.buscarPorClave("EGSTI", "20261", "A1"))
    }

    @Test
    fun `el listado devuelve los resultados en orden de registro`() {
        val primero = resultado(codigoPregunta = "A1")
        val segundo = resultado(codigoPregunta = "A2")

        registrar.ejecutar(primero)
        registrar.ejecutar(segundo)

        assertEquals(listOf(primero, segundo), consultar.listarTodos())
    }

    @Test
    fun `registrar una clave natural ya existente es rechazado sin sobrescribir el registro previo`() {
        val original = resultado(codigoPregunta = "A1", promedio = 4.5)
        registrar.ejecutar(original)

        assertThrows<EntidadDuplicadaException> {
            registrar.ejecutar(resultado(codigoPregunta = "A1", promedio = 2.0))
        }
        assertEquals(original, consultar.buscarPorClave("EGSTI", "20261", "A1"))
    }

    @Test
    fun `dos resultados del mismo periodo y programa con distinto codigo de pregunta no son duplicados`() {
        registrar.ejecutar(resultado(codigoPregunta = "A1"))

        registrar.ejecutar(resultado(codigoPregunta = "A2"))

        assertEquals(2, consultar.listarTodos().size)
    }

    @Test
    fun `consultar una clave inexistente devuelve null`() {
        assertNull(consultar.buscarPorClave("NO-EXISTE", "20261", "A1"))
    }

    @Test
    fun `el listado esta vacio cuando no hay resultados registrados`() {
        assertEquals(emptyList(), consultar.listarTodos())
    }

    private fun resultado(
        codigoPregunta: String,
        promedio: Double? = 4.0,
    ): ResultadoEncuestaPregunta = ResultadoEncuestaPregunta(
        programa = "EGSTI",
        periodo = "20261",
        codigoPregunta = codigoPregunta,
        caracteristica = "1.1",
        estamentos = setOf(TipoEstamento.ESTUDIANTE),
        promedio = promedio,
    )
}

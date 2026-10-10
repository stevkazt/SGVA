package com.example.sgva.application

import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.EvaluacionDocumental
import com.example.sgva.domain.ValoracionCualitativa
import com.example.sgva.usecases.ConsultarEvaluacionDocumentalUseCase
import com.example.sgva.usecases.RegistrarEvaluacionDocumentalUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GestionEvaluacionesDocumentalesUseCaseTest {

    private val repositorio = RepositorioAcademicoFalso()
    private val registrar = RegistrarEvaluacionDocumentalUseCase(repositorio)
    private val consultar = ConsultarEvaluacionDocumentalUseCase(repositorio)

    @Test
    fun `registrar una evaluacion la hace recuperable por su clave natural`() {
        val evaluacion = evaluacion(caracteristica = "1.1")

        registrar.ejecutar(evaluacion)

        assertEquals(evaluacion, consultar.buscarPorClave("EGSTI", "20261", "1.1"))
    }

    @Test
    fun `el listado devuelve las evaluaciones en orden de registro`() {
        val primera = evaluacion(caracteristica = "1.1")
        val segunda = evaluacion(caracteristica = "1.2")

        registrar.ejecutar(primera)
        registrar.ejecutar(segunda)

        assertEquals(listOf(primera, segunda), consultar.listarTodas())
    }

    @Test
    fun `registrar una clave natural ya existente es rechazado sin sobrescribir el registro previo`() {
        val original = evaluacion(caracteristica = "1.1", calificacion = 4.5)
        registrar.ejecutar(original)

        assertThrows<EntidadDuplicadaException> {
            registrar.ejecutar(evaluacion(caracteristica = "1.1", calificacion = 2.0))
        }
        assertEquals(original, consultar.buscarPorClave("EGSTI", "20261", "1.1"))
    }

    @Test
    fun `dos evaluaciones del mismo periodo y programa con distinta caracteristica no son duplicadas`() {
        registrar.ejecutar(evaluacion(caracteristica = "1.1"))

        registrar.ejecutar(evaluacion(caracteristica = "1.2"))

        assertEquals(2, consultar.listarTodas().size)
    }

    @Test
    fun `consultar una clave inexistente devuelve null`() {
        assertNull(consultar.buscarPorClave("NO-EXISTE", "20261", "1.1"))
    }

    @Test
    fun `el listado esta vacio cuando no hay evaluaciones registradas`() {
        assertEquals(emptyList(), consultar.listarTodas())
    }

    @Test
    fun `la valoracion cualitativa de la evaluacion recuperada se deriva de su calificacion`() {
        registrar.ejecutar(evaluacion(caracteristica = "1.1", calificacion = 4.8))

        val recuperada = consultar.buscarPorClave("EGSTI", "20261", "1.1")

        assertEquals(ValoracionCualitativa.SE_CUMPLE_PLENAMENTE, recuperada?.valoracionCualitativa)
    }

    private fun evaluacion(
        caracteristica: String,
        calificacion: Double = 4.0,
    ): EvaluacionDocumental = EvaluacionDocumental(
        programa = "EGSTI",
        caracteristica = caracteristica,
        periodo = "20261",
        calificacion = calificacion,
    )
}

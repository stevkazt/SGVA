package com.example.sgva.application

import com.example.sgva.domain.Docente
import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.NivelFormacion
import com.example.sgva.domain.TipoDedicacion
import com.example.sgva.usecases.ConsultarDocentesUseCase
import com.example.sgva.usecases.RegistrarDocenteUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GestionDocentesUseCaseTest {

    private val repositorio = RepositorioAcademicoFalso()
    private val registrar = RegistrarDocenteUseCase(repositorio)
    private val consultar = ConsultarDocentesUseCase(repositorio)

    @Test
    fun `registrar un docente lo hace recuperable por id`() {
        val docente = docente(id = "D-001")

        registrar.ejecutar(docente)

        assertEquals(docente, consultar.buscarPorId("D-001"))
    }

    @Test
    fun `el listado devuelve los docentes en orden de registro`() {
        val primero = docente(id = "D-001")
        val segundo = docente(id = "D-002")

        registrar.ejecutar(primero)
        registrar.ejecutar(segundo)

        assertEquals(listOf(primero, segundo), consultar.listarTodos())
    }

    @Test
    fun `registrar un id ya existente es rechazado sin sobrescribir el registro previo`() {
        val original = docente(id = "D-001", nivelFormacion = NivelFormacion.DOCTORADO)
        registrar.ejecutar(original)

        assertThrows<EntidadDuplicadaException> {
            registrar.ejecutar(docente(id = "D-001", nivelFormacion = NivelFormacion.MAESTRIA))
        }
        assertEquals(original, consultar.buscarPorId("D-001"))
    }

    @Test
    fun `consultar un id inexistente devuelve null`() {
        assertNull(consultar.buscarPorId("NO-EXISTE"))
    }

    private fun docente(
        id: String,
        nivelFormacion: NivelFormacion = NivelFormacion.MAESTRIA,
    ): Docente = Docente(
        id = id,
        facultad = "Ingeniería",
        nivelFormacion = nivelFormacion,
        dedicacion = TipoDedicacion.TIEMPO_COMPLETO,
        periodo = "20261",
    )
}

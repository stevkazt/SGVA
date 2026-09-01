package com.example.sgva.application

import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.Estudiante
import com.example.sgva.usecases.ConsultarEstudiantesUseCase
import com.example.sgva.usecases.RegistrarEstudianteUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GestionEstudiantesUseCaseTest {

    private val repositorio = RepositorioAcademicoFalso()
    private val registrar = RegistrarEstudianteUseCase(repositorio)
    private val consultar = ConsultarEstudiantesUseCase(repositorio)

    @Test
    fun `registrar un estudiante lo hace recuperable por id`() {
        val estudiante = estudiante(id = "EST-001")

        registrar.ejecutar(estudiante)

        assertEquals(estudiante, consultar.buscarPorId("EST-001"))
    }

    @Test
    fun `el listado devuelve los estudiantes en orden de registro`() {
        val primero = estudiante(id = "EST-001")
        val segundo = estudiante(id = "EST-002")

        registrar.ejecutar(primero)
        registrar.ejecutar(segundo)

        assertEquals(listOf(primero, segundo), consultar.listarTodos())
    }

    @Test
    fun `registrar un id ya existente es rechazado sin sobrescribir el registro previo`() {
        val original = estudiante(id = "EST-001", programa = "Ingeniería de Sistemas")
        registrar.ejecutar(original)

        assertThrows<EntidadDuplicadaException> {
            registrar.ejecutar(estudiante(id = "EST-001", programa = "Otro programa"))
        }
        assertEquals(original, consultar.buscarPorId("EST-001"))
    }

    @Test
    fun `consultar un id inexistente devuelve null`() {
        assertNull(consultar.buscarPorId("NO-EXISTE"))
    }

    @Test
    fun `el listado esta vacio cuando no hay estudiantes registrados`() {
        assertEquals(emptyList(), consultar.listarTodos())
    }

    private fun estudiante(
        id: String,
        programa: String = "Ingeniería de Sistemas",
    ): Estudiante = Estudiante(
        id = id,
        programa = programa,
        facultad = "Ingeniería",
        cohorte = "20211",
        estado = EstadoEstudiante.MATRICULADO,
    )
}

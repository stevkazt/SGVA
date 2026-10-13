package com.example.sgva.application

import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.EstadoProyecto
import com.example.sgva.domain.OrigenProyecto
import com.example.sgva.domain.ProyectoMejoramiento
import com.example.sgva.usecases.ConsultarProyectoMejoramientoUseCase
import com.example.sgva.usecases.RegistrarProyectoMejoramientoUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GestionProyectoMejoramientoUseCaseTest {

    private val repositorio = RepositorioAcademicoFalso()
    private val registrar = RegistrarProyectoMejoramientoUseCase(repositorio)
    private val consultar = ConsultarProyectoMejoramientoUseCase(repositorio)

    @Test
    fun `registrar un proyecto lo hace recuperable por id`() {
        registrar.ejecutar(proyecto("PM-001"))

        assertEquals(proyecto("PM-001"), consultar.buscarPorId("PM-001"))
    }

    @Test
    fun `el listado filtrado por programa excluye proyectos de otros programas`() {
        registrar.ejecutar(proyecto("PM-001", programa = "EGSTI"))
        registrar.ejecutar(proyecto("PM-002", programa = "OTRO"))

        assertEquals(1, consultar.listarTodos().count { it.programa == "EGSTI" })
    }

    @Test
    fun `un id ya existente es rechazado como duplicado`() {
        registrar.ejecutar(proyecto("PM-001"))

        assertThrows<EntidadDuplicadaException> {
            registrar.ejecutar(proyecto("PM-001"))
        }
    }

    @Test
    fun `consultar un id inexistente devuelve null`() {
        assertNull(consultar.buscarPorId("NO-EXISTE"))
    }

    private fun proyecto(id: String, programa: String = "EGSTI"): ProyectoMejoramiento = ProyectoMejoramiento(
        id = id,
        programa = programa,
        nombre = "Fortalecimiento docente",
        aspectosMejora = listOf("Formación docente", "Producción académica"),
        origen = OrigenProyecto.AUTOEVALUACION,
        objetivo = "Aumentar el nivel de formación de los docentes",
        actividades = listOf("Convocatoria de becas", "Convenios con universidades"),
        factores = setOf("F3"),
        estado = EstadoProyecto.PLANIFICADO,
    )
}

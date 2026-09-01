package com.example.sgva.application

import com.example.sgva.domain.DatosInsuficientesException
import com.example.sgva.domain.Docente
import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.NivelFormacion
import com.example.sgva.domain.TipoDedicacion
import com.example.sgva.infrastructure.persistence.AcademicDataRepositoryEnMemoria
import com.example.sgva.usecases.CalcularCapacidadInstaladaUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class CalcularCapacidadInstaladaUseCaseTest {

    private val repositorio = AcademicDataRepositoryEnMemoria()
    private val useCase = CalcularCapacidadInstaladaUseCase(repositorio)

    @Test
    fun `divide los matriculados entre los profesores de tiempo completo equivalente`() {
        repeat(30) { guardarEstudiante(EstadoEstudiante.MATRICULADO) }
        guardarEstudiante(EstadoEstudiante.DESERTOR) // no cuenta en el numerador

        guardarDocente(TipoDedicacion.TIEMPO_COMPLETO) // 1.0
        guardarDocente(TipoDedicacion.TIEMPO_COMPLETO) // 1.0
        guardarDocente(TipoDedicacion.MEDIO_TIEMPO) //    0.5
        guardarDocente(TipoDedicacion.CATEDRA) //         0.25  -> total 2.75 TCE

        val indicador = useCase.ejecutar()

        assertEquals("Relación Estudiante/Profesor", indicador.nombre)
        assertEquals(10.91, indicador.valor) // 30 / 2.75 = 10.9090...
        assertEquals("CONSOLIDADO", indicador.periodo)
        assertEquals("TODAS", indicador.facultad)
    }

    @Test
    fun `los filtros de periodo y facultad se aplican al numerador y al denominador`() {
        guardarEstudiante(EstadoEstudiante.MATRICULADO, facultad = "Ingeniería")
        guardarEstudiante(EstadoEstudiante.MATRICULADO, facultad = "Ingeniería")
        guardarEstudiante(EstadoEstudiante.MATRICULADO, facultad = "Ciencias")

        guardarDocente(TipoDedicacion.TIEMPO_COMPLETO, periodo = "20261", facultad = "Ingeniería")
        guardarDocente(TipoDedicacion.TIEMPO_COMPLETO, periodo = "20262", facultad = "Ingeniería")

        val indicador = useCase.ejecutar(periodo = "20261", facultad = "Ingeniería")

        assertEquals(2.0, indicador.valor) // 2 matriculados de Ingeniería / 1.0 TCE del periodo 20261
        assertEquals("20261", indicador.periodo)
        assertEquals("Ingeniería", indicador.facultad)
    }

    @Test
    fun `sin docentes lanza DatosInsuficientesException`() {
        repeat(5) { guardarEstudiante(EstadoEstudiante.MATRICULADO) }

        assertThrows<DatosInsuficientesException> { useCase.ejecutar() }
    }

    private fun guardarEstudiante(estado: EstadoEstudiante, facultad: String = "Ingeniería") {
        repositorio.guardarEstudiante(
            Estudiante(
                id = "E${contador++}",
                programa = "Ingeniería de Sistemas",
                facultad = facultad,
                cohorte = "20231",
                estado = estado,
            ),
        )
    }

    private fun guardarDocente(
        dedicacion: TipoDedicacion,
        periodo: String = "20261",
        facultad: String = "Ingeniería",
    ) {
        repositorio.guardarDocente(
            Docente(
                id = "D${contador++}",
                facultad = facultad,
                nivelFormacion = NivelFormacion.MAESTRIA,
                dedicacion = dedicacion,
                periodo = periodo,
            ),
        )
    }

    private var contador = 1
}

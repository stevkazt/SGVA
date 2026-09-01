package com.example.sgva.application

import com.example.sgva.domain.DatosInsuficientesException
import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.IndicadorCalidad
import com.example.sgva.infrastructure.persistence.AcademicDataRepositoryEnMemoria
import com.example.sgva.usecases.ConsolidarPuntajesSaberProUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ConsolidarPuntajesSaberProUseCaseTest {

    private val repositorio = AcademicDataRepositoryEnMemoria()
    private val useCase = ConsolidarPuntajesSaberProUseCase(repositorio)

    @Test
    fun `consolida el promedio global y por cohorte, excluyendo a quienes no presentaron la prueba`() {
        guardar("20211", 200)
        guardar("20211", 220)
        guardar("20221", 180)
        guardar("20221", null)

        val indicadores = useCase.ejecutar()

        assertEquals(3, indicadores.size)
        val global = indicadores.first()
        assertEquals(IndicadorCalidad.CONSOLIDADO, global.periodo)
        assertEquals(200.0, global.valor)
        assertEquals(210.0, indicadores.first { it.periodo == "20211" }.valor)
        assertEquals(180.0, indicadores.first { it.periodo == "20221" }.valor)
    }

    @Test
    fun `los promedios por cohorte van en orden ascendente`() {
        guardar("20231", 150)
        guardar("20201", 250)

        val periodos = useCase.ejecutar().map { it.periodo }

        assertEquals(listOf(IndicadorCalidad.CONSOLIDADO, "20201", "20231"), periodos)
    }

    @Test
    fun `sin ningun puntaje registrado lanza DatosInsuficientesException`() {
        guardar("20211", null)

        assertThrows<DatosInsuficientesException> { useCase.ejecutar() }
    }

    private fun guardar(cohorte: String, puntaje: Int?) {
        repositorio.guardarEstudiante(
            Estudiante(
                id = "E${contador++}",
                programa = "Ingeniería de Sistemas",
                facultad = "Ingeniería",
                cohorte = cohorte,
                estado = EstadoEstudiante.GRADUADO,
                puntajeSaberPro = puntaje,
            ),
        )
    }

    private var contador = 1
}

package com.example.sgva.application

import com.example.sgva.domain.DatosInsuficientesException
import com.example.sgva.domain.Docente
import com.example.sgva.domain.NivelFormacion
import com.example.sgva.domain.TipoDedicacion
import com.example.sgva.infrastructure.persistence.AcademicDataRepositoryEnMemoria
import com.example.sgva.usecases.CalcularDistribucionFormacionUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class CalcularDistribucionFormacionUseCaseTest {

    private val repositorio = AcademicDataRepositoryEnMemoria()
    private val useCase = CalcularDistribucionFormacionUseCase(repositorio)

    @Test
    fun `calcula el porcentaje de cada nivel de formacion sobre el total`() {
        guardar(NivelFormacion.DOCTORADO)
        guardar(NivelFormacion.DOCTORADO)
        guardar(NivelFormacion.MAESTRIA)
        guardar(NivelFormacion.ESPECIALIZACION)

        val porNivel = useCase.ejecutar().associateBy { it.nombre }

        assertEquals(3, porNivel.size)
        assertEquals(50.0, porNivel.getValue("Distribución de Formación - DOCTORADO").valor)
        assertEquals(25.0, porNivel.getValue("Distribución de Formación - MAESTRIA").valor)
        assertEquals(25.0, porNivel.getValue("Distribución de Formación - ESPECIALIZACION").valor)
    }

    @Test
    fun `devuelve un indicador por nivel incluso con porcentaje cero, en el orden del enum`() {
        guardar(NivelFormacion.MAESTRIA)

        val indicadores = useCase.ejecutar()

        assertEquals(NivelFormacion.entries.map { it.name }, indicadores.map { it.nombre.substringAfterLast("- ") })
        assertEquals(0.0, indicadores.first { it.nombre.endsWith("DOCTORADO") }.valor)
    }

    @Test
    fun `el filtro por periodo y facultad restringe la poblacion y etiqueta el indicador`() {
        guardar(NivelFormacion.DOCTORADO, periodo = "20261", facultad = "Ingeniería")
        guardar(NivelFormacion.MAESTRIA, periodo = "20262", facultad = "Ingeniería")
        guardar(NivelFormacion.MAESTRIA, periodo = "20261", facultad = "Ciencias")

        val indicadores = useCase.ejecutar(periodo = "20261", facultad = "Ingeniería")

        assertEquals(100.0, indicadores.first { it.nombre.endsWith("DOCTORADO") }.valor)
        assertEquals("20261", indicadores.first().periodo)
        assertEquals("Ingeniería", indicadores.first().facultad)
    }

    @Test
    fun `sin docentes lanza DatosInsuficientesException`() {
        assertThrows<DatosInsuficientesException> { useCase.ejecutar(periodo = "20261") }
    }

    private fun guardar(
        nivel: NivelFormacion,
        periodo: String = "20261",
        facultad: String = "Ingeniería",
    ) {
        repositorio.guardarDocente(
            Docente(
                id = "D${contador++}",
                facultad = facultad,
                nivelFormacion = nivel,
                dedicacion = TipoDedicacion.TIEMPO_COMPLETO,
                periodo = periodo,
            ),
        )
    }

    private var contador = 1
}

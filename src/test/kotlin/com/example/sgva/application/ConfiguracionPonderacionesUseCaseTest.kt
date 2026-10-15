package com.example.sgva.application

import com.example.sgva.domain.DatosInsuficientesException
import com.example.sgva.domain.FormatoArchivoInvalidoException
import com.example.sgva.domain.SumaPonderacionesInvalidaException
import com.example.sgva.infrastructure.configuracion.CargadorConfiguracionPonderaciones
import com.example.sgva.usecases.CargarConfiguracionPonderacionesUseCase
import com.example.sgva.usecases.ConsultarConfiguracionPonderacionesUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ConfiguracionPonderacionesUseCaseTest {

    private val repositorio = RepositorioConfiguracionPonderacionesFalso()
    private val consultar = ConsultarConfiguracionPonderacionesUseCase(repositorio)
    private val cargar = CargarConfiguracionPonderacionesUseCase(CargadorConfiguracionPonderaciones(), repositorio)

    private fun csv(pesoF1: String = "0.6", pesoF2: String = "0.4") = """
        tipo,id,factor,nombre,ponderacion
        FACTOR,F1,,,$pesoF1
        FACTOR,F2,,,$pesoF2
        CARACTERISTICA,C1,F1,Uno,1.0
        CARACTERISTICA,C2,F2,Dos,1.0
        MEZCLA,ENCUESTAS,,,0.4
        MEZCLA,DOCUMENTAL,,,0.6
    """.trimIndent().toByteArray()

    @Test
    fun `consultar sin configuracion cargada falla con un mensaje claro`() {
        val error = assertFailsWith<DatosInsuficientesException> { consultar.ejecutar() }
        assertTrue(error.message!!.contains("No hay ponderaciones configuradas"))
    }

    @Test
    fun `cargar un archivo valido deja la configuracion vigente`() {
        cargar.ejecutar("ponderaciones.csv", csv())

        val vigente = consultar.ejecutar()
        assertEquals(mapOf("F1" to 0.6, "F2" to 0.4), vigente.pesosFactores)
        assertEquals(listOf("C1", "C2"), vigente.caracteristicas.map { it.id })
        assertEquals(0.4, vigente.pesoEncuestas)
        assertEquals(0.6, vigente.pesoDocumental)
    }

    @Test
    fun `cargar otro archivo reemplaza por completo la configuracion anterior`() {
        cargar.ejecutar("a.csv", csv())
        cargar.ejecutar("b.csv", csv(pesoF1 = "0.3", pesoF2 = "0.7"))

        assertEquals(mapOf("F1" to 0.3, "F2" to 0.7), consultar.ejecutar().pesosFactores)
    }

    @Test
    fun `un archivo invalido no reemplaza la configuracion vigente`() {
        cargar.ejecutar("a.csv", csv())

        assertFailsWith<SumaPonderacionesInvalidaException> {
            cargar.ejecutar("mala.csv", csv(pesoF1 = "0.9", pesoF2 = "0.4"))
        }
        assertEquals(mapOf("F1" to 0.6, "F2" to 0.4), consultar.ejecutar().pesosFactores)
    }

    @Test
    fun `un archivo que no es csv se rechaza`() {
        assertFailsWith<FormatoArchivoInvalidoException> { cargar.ejecutar("ponderaciones.xlsx", csv()) }
    }

    @Test
    fun `un archivo csv con BOM se lee igual`() {
        val conBom = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()) + csv()
        cargar.ejecutar("ponderaciones.csv", conBom)

        assertEquals(2, consultar.ejecutar().pesosFactores.size)
    }
}

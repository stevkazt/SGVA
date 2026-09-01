package com.example.sgva.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class IndicadorCalidadTest {

    @Test
    fun `un indicador valido se construye correctamente`() {
        val indicador = IndicadorCalidad("Tasa de Deserción", 12.5, "20231", "Ingeniería")

        assertEquals(12.5, indicador.valor)
        assertEquals("20231", indicador.periodo)
    }

    @Test
    fun `admite los sentinels para agregados sin periodo ni facultad concretos`() {
        val indicador = IndicadorCalidad(
            nombre = "Promedio Saber Pro",
            valor = 205.0,
            periodo = IndicadorCalidad.CONSOLIDADO,
            facultad = IndicadorCalidad.TODAS_LAS_FACULTADES,
        )

        assertEquals("CONSOLIDADO", indicador.periodo)
        assertEquals("TODAS", indicador.facultad)
    }

    @Test
    fun `un nombre en blanco es rechazado`() {
        assertThrows<CampoRequeridoVacioException> {
            IndicadorCalidad("  ", 1.0, "20231", "Ingeniería")
        }
    }

    @Test
    fun `un valor no finito es rechazado`() {
        assertThrows<ValorIndicadorInvalidoException> {
            IndicadorCalidad("Tasa de Deserción", Double.NaN, "20231", "Ingeniería")
        }
        assertThrows<ValorIndicadorInvalidoException> {
            IndicadorCalidad("Tasa de Deserción", Double.POSITIVE_INFINITY, "20231", "Ingeniería")
        }
    }
}

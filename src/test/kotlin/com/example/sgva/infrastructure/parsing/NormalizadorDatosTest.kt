package com.example.sgva.infrastructure.parsing

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NormalizadorDatosTest {

    @Test
    fun `el periodo academico conserva solo los digitos`() {
        assertEquals("20261", NormalizadorDatos.periodoAcademico("2026-1"))
        assertEquals("20211", NormalizadorDatos.periodoAcademico("2021_1"))
        assertEquals("20221", NormalizadorDatos.periodoAcademico("  2022 / 1 "))
        assertEquals("20231", NormalizadorDatos.periodoAcademico("20231"))
    }

    @Test
    fun `la clave de enum se normaliza sin acentos, en mayusculas y con separadores unificados`() {
        assertEquals("TIEMPO_COMPLETO", NormalizadorDatos.claveEnum("tiempo completo"))
        assertEquals("CATEDRA", NormalizadorDatos.claveEnum("Cátedra"))
        assertEquals("MEDIO_TIEMPO", NormalizadorDatos.claveEnum(" medio-tiempo "))
        assertEquals("DOCTORADO", NormalizadorDatos.claveEnum("DOCTORADO"))
    }

    @Test
    fun `el entero opcional distingue vacio de cero y admite decimales enteros`() {
        assertNull(NormalizadorDatos.enteroOpcional(""))
        assertNull(NormalizadorDatos.enteroOpcional("   "))
        assertNull(NormalizadorDatos.enteroOpcional(null))
        assertEquals(0, NormalizadorDatos.enteroOpcional("0"))
        assertEquals(210, NormalizadorDatos.enteroOpcional("210"))
        assertEquals(210, NormalizadorDatos.enteroOpcional("210.0"))
    }

    @Test
    fun `el entero opcional rechaza un valor no numerico`() {
        assertThrows<NumberFormatException> { NormalizadorDatos.enteroOpcional("N/A") }
        assertThrows<NumberFormatException> { NormalizadorDatos.enteroOpcional("210.5") }
    }

    @Test
    fun `el entero obligatorio admite un valor valido pero rechaza el vacio`() {
        assertEquals(4, NormalizadorDatos.entero("4"))
        assertThrows<NumberFormatException> { NormalizadorDatos.entero("") }
        assertThrows<NumberFormatException> { NormalizadorDatos.entero(null) }
    }
}

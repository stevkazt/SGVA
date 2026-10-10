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

    @Test
    fun `el decimal opcional trata vacio y N A como ausente, sin distinguir mayusculas`() {
        assertNull(NormalizadorDatos.decimalOpcional(""))
        assertNull(NormalizadorDatos.decimalOpcional("   "))
        assertNull(NormalizadorDatos.decimalOpcional(null))
        assertNull(NormalizadorDatos.decimalOpcional("N/A"))
        assertNull(NormalizadorDatos.decimalOpcional("n/a"))
    }

    @Test
    fun `el decimal opcional admite coma o punto como separador decimal`() {
        assertEquals(4.5, NormalizadorDatos.decimalOpcional("4.5"))
        assertEquals(4.5, NormalizadorDatos.decimalOpcional("4,5"))
        assertEquals(0.0, NormalizadorDatos.decimalOpcional("0"))
    }

    @Test
    fun `el decimal opcional rechaza un valor no numerico`() {
        assertThrows<NumberFormatException> { NormalizadorDatos.decimalOpcional("cuatro") }
    }

    @Test
    fun `el decimal obligatorio admite coma o punto decimal pero rechaza vacio y N A`() {
        assertEquals(4.5, NormalizadorDatos.decimal("4.5"))
        assertEquals(4.5, NormalizadorDatos.decimal("4,5"))
        assertThrows<NumberFormatException> { NormalizadorDatos.decimal("") }
        assertThrows<NumberFormatException> { NormalizadorDatos.decimal(null) }
        assertThrows<NumberFormatException> { NormalizadorDatos.decimal("N/A") }
    }

    @Test
    fun `el texto opcional trata la cadena en blanco como ausente`() {
        assertNull(NormalizadorDatos.textoOpcional(""))
        assertNull(NormalizadorDatos.textoOpcional("   "))
        assertNull(NormalizadorDatos.textoOpcional(null))
        assertEquals("Observación relevante", NormalizadorDatos.textoOpcional("  Observación relevante  "))
    }

    @Test
    fun `la lista de claves de enum se separa por coma, punto y coma o barra vertical`() {
        assertEquals(listOf("ESTUDIANTE", "PROFESOR"), NormalizadorDatos.listaClavesEnum("Estudiante;Profesor"))
        assertEquals(listOf("ESTUDIANTE", "PROFESOR"), NormalizadorDatos.listaClavesEnum("estudiante, profesor"))
        assertEquals(listOf("ESTUDIANTE", "PROFESOR"), NormalizadorDatos.listaClavesEnum("ESTUDIANTE|PROFESOR"))
        assertEquals(listOf("ALIADO_ESTRATEGICO"), NormalizadorDatos.listaClavesEnum("Aliado Estratégico"))
    }

    @Test
    fun `la lista de claves de enum de un valor vacio es una lista vacia`() {
        assertEquals(emptyList(), NormalizadorDatos.listaClavesEnum(""))
        assertEquals(emptyList(), NormalizadorDatos.listaClavesEnum(null))
    }
}

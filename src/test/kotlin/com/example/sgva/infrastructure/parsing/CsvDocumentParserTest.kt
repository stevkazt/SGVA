package com.example.sgva.infrastructure.parsing

import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.FormatoArchivoInvalidoException
import com.example.sgva.domain.NivelFormacion
import com.example.sgva.domain.TipoDedicacion
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CsvDocumentParserTest {

    private val parser = CsvDocumentParser()

    @Test
    fun `soporta solo archivos con extension csv`() {
        assertTrue(parser.soporta("datos_estudiantes.csv"))
        assertTrue(parser.soporta("RUTA/Datos.CSV"))
        assertTrue(!parser.soporta("datos.xlsx"))
    }

    @Test
    fun `extrae los estudiantes del fixture y trata la celda vacia de puntaje como null`() {
        val estudiantes = parser.extraerEstudiantes("datos_estudiantes_test.csv", fixture("datos_estudiantes_test.csv"))

        assertEquals(10, estudiantes.size)
        val primero = estudiantes.first()
        assertEquals("2021101", primero.id)
        assertEquals("20211", primero.cohorte)
        assertEquals(EstadoEstudiante.GRADUADO, primero.estado)
        assertEquals(210, primero.puntajeSaberPro)
        assertNull(estudiantes[2].puntajeSaberPro)
    }

    @Test
    fun `normaliza el periodo con guion de los docentes al formato AAAAS`() {
        val docentes = parser.extraerDocentes("datos_docentes_test.csv", fixture("datos_docentes_test.csv"))

        assertEquals(4, docentes.size)
        assertTrue(docentes.all { it.periodo == "20261" })
        assertEquals(NivelFormacion.DOCTORADO, docentes.first().nivelFormacion)
        assertEquals(TipoDedicacion.MEDIO_TIEMPO, docentes.last().dedicacion)
    }

    @Test
    fun `rechaza un csv al que le falta una columna obligatoria`() {
        val csv = "id,programa,facultad,estado\nE1,Sistemas,Ingeniería,MATRICULADO\n".toByteArray()

        val error = assertThrows<FormatoArchivoInvalidoException> {
            parser.extraerEstudiantes("incompleto.csv", csv)
        }
        assertTrue(error.message!!.contains("cohorte"))
    }

    @Test
    fun `rechaza un csv con un enum no reconocible senalando la fila`() {
        val csv = buildString {
            append("id,programa,facultad,cohorte,estado,puntajeSaberPro\n")
            append("E1,Sistemas,Ingeniería,20231,MATRICULADO,\n")
            append("E2,Sistemas,Ingeniería,20231,EGRESADO,\n")
        }.toByteArray()

        val error = assertThrows<FormatoArchivoInvalidoException> {
            parser.extraerEstudiantes("datos.csv", csv)
        }
        assertTrue(error.message!!.contains("fila 3"))
        assertTrue(error.message!!.contains("estado"))
    }

    private fun fixture(nombre: String): ByteArray =
        checkNotNull(javaClass.getResourceAsStream("/fixtures/$nombre")) { "falta el fixture $nombre" }
            .use { it.readBytes() }
}

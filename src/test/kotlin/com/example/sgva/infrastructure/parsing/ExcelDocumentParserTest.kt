package com.example.sgva.infrastructure.parsing

import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.FormatoArchivoInvalidoException
import com.example.sgva.domain.TipoEstamento
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExcelDocumentParserTest {

    private val parser = ExcelDocumentParser()

    @Test
    fun `soporta solo archivos con extension xlsx`() {
        assertTrue(parser.soporta("planta_docente.xlsx"))
        assertTrue(!parser.soporta("planta_docente.csv"))
    }

    @Test
    fun `extrae estudiantes de un libro con celdas numericas y de texto`() {
        val libro = xlsx(
            listOf("id", "programa", "facultad", "cohorte", "estado", "puntajeSaberPro"),
            listOf("2023106", "Ingeniería de Sistemas", "Ingeniería", "2023-1", "matriculado", ""),
            listOf("2021101", "Ingeniería de Sistemas", "Ingeniería", 20211.0, "GRADUADO", 210.0),
        )

        val estudiantes = parser.extraerEstudiantes("estudiantes.xlsx", libro)

        assertEquals(2, estudiantes.size)
        assertEquals("20231", estudiantes[0].cohorte)
        assertEquals(EstadoEstudiante.MATRICULADO, estudiantes[0].estado)
        assertNull(estudiantes[0].puntajeSaberPro)
        assertEquals("2021101", estudiantes[1].id)
        assertEquals(210, estudiantes[1].puntajeSaberPro)
    }

    @Test
    fun `ignora las filas totalmente vacias`() {
        val libro = xlsx(
            listOf("id", "facultad", "nivelFormacion", "dedicacion", "periodo"),
            listOf("D001", "Ingenieria", "DOCTORADO", "TIEMPO_COMPLETO", "2026-1"),
            listOf("", "", "", "", ""),
        )

        val docentes = parser.extraerDocentes("docentes.xlsx", libro)

        assertEquals(1, docentes.size)
        assertEquals("20261", docentes.first().periodo)
    }

    @Test
    fun `extrae respuestas de encuesta de un libro con celdas numericas y de texto`() {
        val libro = xlsx(
            listOf("id", "estamento", "factor", "calificacion", "periodo"),
            listOf("R001", "estudiante", "Infraestructura", 4.0, "2026-1"),
            listOf("R002", "PROFESOR", "Plan de Estudios", "5", "20261"),
        )

        val respuestas = parser.extraerRespuestasEncuestas("encuestas.xlsx", libro)

        assertEquals(2, respuestas.size)
        assertEquals(TipoEstamento.ESTUDIANTE, respuestas[0].estamento)
        assertEquals(4, respuestas[0].calificacion)
        assertEquals("20261", respuestas[0].periodo)
        assertEquals(TipoEstamento.PROFESOR, respuestas[1].estamento)
    }

    @Test
    fun `rechaza un libro corrupto con una excepcion controlada`() {
        val error = assertThrows<FormatoArchivoInvalidoException> {
            parser.extraerEstudiantes("corrupto.xlsx", "esto no es un xlsx".toByteArray())
        }
        assertTrue(error.message!!.contains("corrupto.xlsx"))
    }

    private fun xlsx(encabezados: List<String>, vararg filas: List<Any>): ByteArray =
        XSSFWorkbook().use { libro ->
            val hoja = libro.createSheet("datos")
            hoja.createRow(0).also { fila ->
                encabezados.forEachIndexed { i, titulo -> fila.createCell(i).setCellValue(titulo) }
            }
            filas.forEachIndexed { indiceFila, valores ->
                val fila = hoja.createRow(indiceFila + 1)
                valores.forEachIndexed { i, valor ->
                    val celda = fila.createCell(i)
                    when (valor) {
                        is Number -> celda.setCellValue(valor.toDouble())
                        else -> celda.setCellValue(valor.toString())
                    }
                }
            }
            ByteArrayOutputStream().use { salida ->
                libro.write(salida)
                salida.toByteArray()
            }
        }
}

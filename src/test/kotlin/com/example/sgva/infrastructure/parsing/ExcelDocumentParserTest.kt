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
            listOf("id", "programa", "facultad", "nivelFormacion", "dedicacion", "periodo"),
            listOf("D001", "EGSTI", "Ingenieria", "DOCTORADO", "TIEMPO_COMPLETO", "2026-1"),
            listOf("", "", "", "", "", ""),
        )

        val docentes = parser.extraerDocentes("docentes.xlsx", libro)

        assertEquals(1, docentes.size)
        assertEquals("20261", docentes.first().periodo)
        assertEquals("EGSTI", docentes.first().programa)
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
    fun `extrae resultados de encuesta por pregunta de un libro con celdas numericas y de texto`() {
        val libro = xlsx(
            listOf("programa", "periodo", "codigoPregunta", "caracteristica", "estamentos", "promedio"),
            listOf("EGSTI", "2026-1", "A1", "1.1", "ESTUDIANTE;PROFESOR", 4.5),
            listOf("EGSTI", "2026-1", "A2", "1.2", "DIRECTIVO", "N/A"),
        )

        val resultados = parser.extraerResultadosEncuestaPregunta("resultados.xlsx", libro)

        assertEquals(2, resultados.size)
        assertEquals("20261", resultados[0].periodo)
        assertEquals(setOf(TipoEstamento.ESTUDIANTE, TipoEstamento.PROFESOR), resultados[0].estamentos)
        assertEquals(4.5, resultados[0].promedio)
        assertNull(resultados[1].promedio)
    }

    @Test
    fun `extrae evaluaciones documentales de un libro con celdas numericas y de texto`() {
        val libro = xlsx(
            listOf("programa", "caracteristica", "periodo", "calificacion", "justificacion"),
            listOf("EGSTI", "1.1", "2026-1", 4.7, "Evidencia documental consistente"),
            listOf("EGSTI", "1.2", "20261", "3,5", ""),
        )

        val evaluaciones = parser.extraerEvaluacionesDocumentales("evaluaciones.xlsx", libro)

        assertEquals(2, evaluaciones.size)
        assertEquals("20261", evaluaciones[0].periodo)
        assertEquals(4.7, evaluaciones[0].calificacion)
        assertEquals("Evidencia documental consistente", evaluaciones[0].justificacion)
        assertEquals(3.5, evaluaciones[1].calificacion)
        assertNull(evaluaciones[1].justificacion)
    }

    @Test
    fun `omite una o mas filas de titulo antes del encabezado`() {
        val libro = XSSFWorkbook().use { libro ->
            val hoja = libro.createSheet("datos")
            hoja.createRow(0).also { it.createCell(0).setCellValue("UNIVERSIDAD EJEMPLO") }
            hoja.createRow(1).also { it.createCell(0).setCellValue("INFORME DE ESTUDIANTES - 2026-1") }
            hoja.createRow(2).also { fila ->
                listOf("id", "programa", "facultad", "cohorte", "estado", "puntajeSaberPro")
                    .forEachIndexed { i, titulo -> fila.createCell(i).setCellValue(titulo) }
            }
            hoja.createRow(3).also { fila ->
                listOf("2021101", "Ingeniería de Sistemas", "Ingeniería", "2023-1", "MATRICULADO", "210")
                    .forEachIndexed { i, valor -> fila.createCell(i).setCellValue(valor) }
            }
            ByteArrayOutputStream().use { salida ->
                libro.write(salida)
                salida.toByteArray()
            }
        }

        val estudiantes = parser.extraerEstudiantes("estudiantes.xlsx", libro)

        assertEquals(1, estudiantes.size)
        assertEquals("2021101", estudiantes[0].id)
        assertEquals("20231", estudiantes[0].cohorte)
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

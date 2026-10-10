package com.example.sgva.infrastructure.parsing

import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.FormatoArchivoInvalidoException
import com.example.sgva.domain.NivelFormacion
import com.example.sgva.domain.TipoDedicacion
import com.example.sgva.domain.TipoEstamento
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
        assertTrue(docentes.all { it.programa == "EGSTI" })
        assertEquals(NivelFormacion.DOCTORADO, docentes.first().nivelFormacion)
        assertEquals(TipoDedicacion.MEDIO_TIEMPO, docentes.last().dedicacion)
    }

    @Test
    fun `extrae las respuestas de encuesta del fixture y normaliza el periodo con guion`() {
        val respuestas =
            parser.extraerRespuestasEncuestas("datos_encuestas_test.csv", fixture("datos_encuestas_test.csv"))

        assertEquals(5, respuestas.size)
        assertTrue(respuestas.all { it.periodo == "20261" })
        val primera = respuestas.first()
        assertEquals("R001", primera.id)
        assertEquals(TipoEstamento.ESTUDIANTE, primera.estamento)
        assertEquals("Plan de Estudios", primera.factor)
        assertEquals(5, primera.calificacion)
        assertEquals(TipoEstamento.EMPLEADOR, respuestas.last().estamento)
    }

    @Test
    fun `extrae los resultados de encuesta por pregunta del fixture, normaliza el periodo y trata N A como promedio ausente`() {
        val resultados = parser.extraerResultadosEncuestaPregunta(
            "datos_resultados_encuesta_pregunta_test.csv",
            fixture("datos_resultados_encuesta_pregunta_test.csv"),
        )

        assertEquals(4, resultados.size)
        assertTrue(resultados.all { it.periodo == "20261" })
        assertTrue(resultados.all { it.programa == "EGSTI" })

        val primero = resultados[0]
        assertEquals(setOf(TipoEstamento.ESTUDIANTE), primero.estamentos)
        assertEquals(4.5, primero.promedio)

        val segundo = resultados[1]
        assertEquals(setOf(TipoEstamento.ESTUDIANTE, TipoEstamento.PROFESOR), segundo.estamentos)

        val tercero = resultados[2]
        assertNull(tercero.promedio)

        val cuarto = resultados[3]
        assertEquals(
            setOf(TipoEstamento.DIRECTIVO, TipoEstamento.ADMINISTRATIVO, TipoEstamento.ALIADO_ESTRATEGICO),
            cuarto.estamentos,
        )
    }

    @Test
    fun `rechaza un resultado de encuesta por pregunta con promedio igual a 0`() {
        val csv = """
            programa,periodo,codigoPregunta,caracteristica,estamentos,promedio
            EGSTI,20261,A1,1.1,ESTUDIANTE,0
        """.trimIndent().toByteArray()

        val error = assertThrows<FormatoArchivoInvalidoException> {
            parser.extraerResultadosEncuestaPregunta("datos.csv", csv)
        }
        assertTrue(error.message!!.contains("fila 2"))
    }

    @Test
    fun `rechaza un resultado de encuesta por pregunta con un estamento no reconocible`() {
        val csv = """
            programa,periodo,codigoPregunta,caracteristica,estamentos,promedio
            EGSTI,20261,A1,1.1,NO_EXISTE,4.5
        """.trimIndent().toByteArray()

        val error = assertThrows<FormatoArchivoInvalidoException> {
            parser.extraerResultadosEncuestaPregunta("datos.csv", csv)
        }
        assertTrue(error.message!!.contains("estamentos"))
    }

    @Test
    fun `extrae las evaluaciones documentales del fixture, normaliza el periodo y trata las columnas opcionales vacias como null`() {
        val evaluaciones = parser.extraerEvaluacionesDocumentales(
            "datos_evaluaciones_documentales_test.csv",
            fixture("datos_evaluaciones_documentales_test.csv"),
        )

        assertEquals(4, evaluaciones.size)
        assertTrue(evaluaciones.all { it.periodo == "20261" })
        assertTrue(evaluaciones.all { it.programa == "EGSTI" })

        val primera = evaluaciones[0]
        assertEquals(4.7, primera.calificacion)
        assertEquals("Evidencia documental consistente con el PEP", primera.justificacion)
        assertEquals("Actualizar el acta de autoevaluación", primera.sugerenciasMejora)

        val segunda = evaluaciones[1]
        assertNull(segunda.justificacion)
        assertEquals("Fortalecer el seguimiento estadístico", segunda.sugerenciasMejora)

        val tercera = evaluaciones[2]
        assertNull(tercera.sugerenciasMejora)
    }

    @Test
    fun `rechaza una evaluacion documental con calificacion N A, a diferencia del promedio de encuesta`() {
        val csv = """
            programa,caracteristica,periodo,calificacion
            EGSTI,1.1,20261,N/A
        """.trimIndent().toByteArray()

        val error = assertThrows<FormatoArchivoInvalidoException> {
            parser.extraerEvaluacionesDocumentales("datos.csv", csv)
        }
        assertTrue(error.message!!.contains("fila 2"))
    }

    @Test
    fun `rechaza una evaluacion documental con calificacion fuera de rango`() {
        val csv = """
            programa,caracteristica,periodo,calificacion
            EGSTI,1.1,20261,5.5
        """.trimIndent().toByteArray()

        val error = assertThrows<FormatoArchivoInvalidoException> {
            parser.extraerEvaluacionesDocumentales("datos.csv", csv)
        }
        assertTrue(error.message!!.contains("calificación documental"))
    }

    @Test
    fun `rechaza un csv de evaluaciones documentales al que le falta la columna calificacion`() {
        val csv = "programa,caracteristica,periodo\nEGSTI,1.1,20261\n".toByteArray()

        val error = assertThrows<FormatoArchivoInvalidoException> {
            parser.extraerEvaluacionesDocumentales("incompleto.csv", csv)
        }
        assertTrue(error.message!!.contains("calificacion"))
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

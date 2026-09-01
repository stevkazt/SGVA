package com.example.sgva.application

import com.example.sgva.domain.FormatoArchivoInvalidoException
import com.example.sgva.infrastructure.parsing.CsvDocumentParser
import com.example.sgva.infrastructure.persistence.AcademicDataRepositoryEnMemoria
import com.example.sgva.usecases.ProcesarDatosDocentesUseCase
import com.example.sgva.usecases.ProcesarDatosEstudiantesUseCase
import com.example.sgva.usecases.RegistrarDocenteUseCase
import com.example.sgva.usecases.RegistrarEstudianteUseCase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProcesarDatosArchivoUseCaseTest {

    private val repositorio = AcademicDataRepositoryEnMemoria()
    private val parsers = listOf(CsvDocumentParser())
    private val procesarEstudiantes =
        ProcesarDatosEstudiantesUseCase(parsers, RegistrarEstudianteUseCase(repositorio))
    private val procesarDocentes =
        ProcesarDatosDocentesUseCase(parsers, RegistrarDocenteUseCase(repositorio))

    @Test
    fun `carga el archivo de estudiantes y registra cada fila mediante el caso de uso de registro`() {
        val resultado = procesarEstudiantes.ejecutar(
            "datos_estudiantes_test.csv",
            fixture("datos_estudiantes_test.csv"),
        )

        assertEquals(10, resultado.totalRegistrosLeidos)
        assertEquals(10, resultado.registrados)
        assertTrue(resultado.omitidos.isEmpty())
        assertEquals(10, repositorio.listarEstudiantes().size)
    }

    @Test
    fun `una segunda carga del mismo archivo omite los ids ya registrados`() {
        procesarDocentes.ejecutar("datos_docentes_test.csv", fixture("datos_docentes_test.csv"))

        val resultado = procesarDocentes.ejecutar(
            "datos_docentes_test.csv",
            fixture("datos_docentes_test.csv"),
        )

        assertEquals(0, resultado.registrados)
        assertEquals(4, resultado.omitidos.size)
        assertEquals(4, repositorio.listarDocentes().size)
        assertTrue(resultado.omitidos.all { it.motivo.contains("Ya existe") })
    }

    @Test
    fun `un formato de archivo no soportado se rechaza antes de registrar nada`() {
        val error = assertThrows<FormatoArchivoInvalidoException> {
            procesarEstudiantes.ejecutar("datos.txt", "id\nX".toByteArray())
        }

        assertTrue(error.message!!.contains(".csv"))
        assertTrue(repositorio.listarEstudiantes().isEmpty())
    }

    private fun fixture(nombre: String): ByteArray =
        checkNotNull(javaClass.getResourceAsStream("/fixtures/$nombre")) { "falta el fixture $nombre" }
            .use { it.readBytes() }
}

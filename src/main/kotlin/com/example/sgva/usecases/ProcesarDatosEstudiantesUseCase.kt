package com.example.sgva.usecases

import com.example.sgva.domain.DocumentParserPort
import com.example.sgva.domain.Estudiante

/**
 * Módulo 1 — carga masiva de estudiantes desde un archivo CSV o Excel
 * (`DESIGN_SGVA.md` §2). Registra cada estudiante reutilizando
 * [RegistrarEstudianteUseCase].
 */
class ProcesarDatosEstudiantesUseCase(
    parsers: List<DocumentParserPort>,
    private val registrarEstudiante: RegistrarEstudianteUseCase,
) : ProcesarArchivoUseCase<Estudiante>(parsers) {

    override fun extraer(
        parser: DocumentParserPort,
        nombreArchivo: String,
        contenido: ByteArray,
    ): List<Estudiante> = parser.extraerEstudiantes(nombreArchivo, contenido)

    override fun idDe(entidad: Estudiante): String = entidad.id

    override fun registrar(entidad: Estudiante) {
        registrarEstudiante.ejecutar(entidad)
    }
}

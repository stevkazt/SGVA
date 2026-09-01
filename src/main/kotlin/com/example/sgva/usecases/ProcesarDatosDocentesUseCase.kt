package com.example.sgva.usecases

import com.example.sgva.domain.Docente
import com.example.sgva.domain.DocumentParserPort

/**
 * Módulo 2 — carga masiva de la planta profesoral de un periodo desde un archivo
 * CSV o Excel (`DESIGN_SGVA.md` §2). Registra cada docente reutilizando
 * [RegistrarDocenteUseCase].
 */
class ProcesarDatosDocentesUseCase(
    parsers: List<DocumentParserPort>,
    private val registrarDocente: RegistrarDocenteUseCase,
) : ProcesarArchivoUseCase<Docente>(parsers) {

    override fun extraer(
        parser: DocumentParserPort,
        nombreArchivo: String,
        contenido: ByteArray,
    ): List<Docente> = parser.extraerDocentes(nombreArchivo, contenido)

    override fun idDe(entidad: Docente): String = entidad.id

    override fun registrar(entidad: Docente) {
        registrarDocente.ejecutar(entidad)
    }
}

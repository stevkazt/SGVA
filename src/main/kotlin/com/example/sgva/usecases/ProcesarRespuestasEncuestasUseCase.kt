package com.example.sgva.usecases

import com.example.sgva.domain.DocumentParserPort
import com.example.sgva.domain.RespuestaEncuesta

/**
 * Módulo 3 — tabulación automatizada de los formularios de encuesta de
 * percepción cargados desde un archivo CSV o Excel (`DESIGN_SGVA.md` §2).
 * Registra cada respuesta reutilizando [RegistrarRespuestaEncuestaUseCase].
 */
class ProcesarRespuestasEncuestasUseCase(
    parsers: List<DocumentParserPort>,
    private val registrarRespuestaEncuesta: RegistrarRespuestaEncuestaUseCase,
) : ProcesarArchivoUseCase<RespuestaEncuesta>(parsers) {

    override fun extraer(
        parser: DocumentParserPort,
        nombreArchivo: String,
        contenido: ByteArray,
    ): List<RespuestaEncuesta> = parser.extraerRespuestasEncuestas(nombreArchivo, contenido)

    override fun idDe(entidad: RespuestaEncuesta): String = entidad.id

    override fun registrar(entidad: RespuestaEncuesta) {
        registrarRespuestaEncuesta.ejecutar(entidad)
    }
}

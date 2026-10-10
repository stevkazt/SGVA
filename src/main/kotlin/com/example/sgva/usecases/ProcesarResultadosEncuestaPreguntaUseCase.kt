package com.example.sgva.usecases

import com.example.sgva.domain.DocumentParserPort
import com.example.sgva.domain.ResultadoEncuestaPregunta

/**
 * Módulo 4 — carga masiva de resultados de encuesta por pregunta desde un
 * archivo CSV o Excel (`DESIGN_SGVA.md` §2), reutilizando la normalización y
 * el protocolo de validación en origen ya existentes para las demás
 * entidades (`DESIGN_SGVA.md` §5). Registra cada resultado reutilizando
 * [RegistrarResultadoEncuestaPreguntaUseCase].
 */
class ProcesarResultadosEncuestaPreguntaUseCase(
    parsers: List<DocumentParserPort>,
    private val registrarResultadoEncuestaPregunta: RegistrarResultadoEncuestaPreguntaUseCase,
) : ProcesarArchivoUseCase<ResultadoEncuestaPregunta>(parsers) {

    override fun extraer(
        parser: DocumentParserPort,
        nombreArchivo: String,
        contenido: ByteArray,
    ): List<ResultadoEncuestaPregunta> = parser.extraerResultadosEncuestaPregunta(nombreArchivo, contenido)

    override fun idDe(entidad: ResultadoEncuestaPregunta): String =
        "${entidad.programa}/${entidad.periodo}/${entidad.codigoPregunta}"

    override fun registrar(entidad: ResultadoEncuestaPregunta) {
        registrarResultadoEncuestaPregunta.ejecutar(entidad)
    }
}

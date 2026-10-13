package com.example.sgva.usecases

import com.example.sgva.domain.DocumentParserPort
import com.example.sgva.domain.PreevaluacionEvaluador

/**
 * Módulo 5 — carga masiva de preevaluaciones desde un archivo CSV o Excel
 * (`DESIGN_SGVA.md` §2 y §5.5, V6). Registra cada preevaluación reutilizando
 * [RegistrarPreevaluacionUseCase].
 */
class ProcesarPreevaluacionesUseCase(
    parsers: List<DocumentParserPort>,
    private val registrarPreevaluacion: RegistrarPreevaluacionUseCase,
) : ProcesarArchivoUseCase<PreevaluacionEvaluador>(parsers) {

    override fun extraer(
        parser: DocumentParserPort,
        nombreArchivo: String,
        contenido: ByteArray,
    ): List<PreevaluacionEvaluador> = parser.extraerPreevaluaciones(nombreArchivo, contenido)

    override fun idDe(entidad: PreevaluacionEvaluador): String =
        "${entidad.ciclo}/${entidad.caracteristica}/${entidad.evaluador}"

    override fun registrar(entidad: PreevaluacionEvaluador) {
        registrarPreevaluacion.ejecutar(entidad)
    }
}

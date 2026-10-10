package com.example.sgva.usecases

import com.example.sgva.domain.DocumentParserPort
import com.example.sgva.domain.EvaluacionDocumental

/**
 * Módulo 4 — carga masiva de evaluaciones documentales desde un archivo CSV o
 * Excel (`DESIGN_SGVA.md` §2), reutilizando la normalización y el protocolo
 * de validación en origen ya existentes para las demás entidades
 * (`DESIGN_SGVA.md` §5). Registra cada evaluación reutilizando
 * [RegistrarEvaluacionDocumentalUseCase].
 */
class ProcesarEvaluacionesDocumentalesUseCase(
    parsers: List<DocumentParserPort>,
    private val registrarEvaluacionDocumental: RegistrarEvaluacionDocumentalUseCase,
) : ProcesarArchivoUseCase<EvaluacionDocumental>(parsers) {

    override fun extraer(
        parser: DocumentParserPort,
        nombreArchivo: String,
        contenido: ByteArray,
    ): List<EvaluacionDocumental> = parser.extraerEvaluacionesDocumentales(nombreArchivo, contenido)

    override fun idDe(entidad: EvaluacionDocumental): String =
        "${entidad.programa}/${entidad.periodo}/${entidad.caracteristica}"

    override fun registrar(entidad: EvaluacionDocumental) {
        registrarEvaluacionDocumental.ejecutar(entidad)
    }
}

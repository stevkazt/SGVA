package com.example.sgva.usecases

import com.example.sgva.domain.DocumentParserPort
import com.example.sgva.domain.ParticipacionActor

/**
 * Módulo 5 — carga masiva de participación de actores desde un archivo CSV o
 * Excel (`DESIGN_SGVA.md` §2 y §5.5, V6). Registra cada participación
 * reutilizando [RegistrarParticipacionActorUseCase].
 */
class ProcesarParticipacionActorUseCase(
    parsers: List<DocumentParserPort>,
    private val registrarParticipacionActor: RegistrarParticipacionActorUseCase,
) : ProcesarArchivoUseCase<ParticipacionActor>(parsers) {

    override fun extraer(
        parser: DocumentParserPort,
        nombreArchivo: String,
        contenido: ByteArray,
    ): List<ParticipacionActor> = parser.extraerParticipacionActores(nombreArchivo, contenido)

    override fun idDe(entidad: ParticipacionActor): String = "${entidad.ciclo}/${entidad.actor}"

    override fun registrar(entidad: ParticipacionActor) {
        registrarParticipacionActor.ejecutar(entidad)
    }
}

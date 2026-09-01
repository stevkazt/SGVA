package com.example.sgva.usecases

import com.example.sgva.domain.DocumentParserPort
import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.FormatoArchivoInvalidoException
import com.example.sgva.domain.RegistroOmitido
import com.example.sgva.domain.ResultadoIngesta

/**
 * Orquestación común a las cargas masivas de archivo (`DESIGN_SGVA.md` §2):
 * elige el adaptador de parseo que soporta el archivo, le pide las entidades ya
 * normalizadas y válidas, y da de alta cada una. Un `id` ya existente —en el
 * repositorio o repetido dentro del propio archivo— se reporta como omitido en
 * lugar de abortar la carga completa.
 *
 * Las subclases solo aportan el detalle específico de la entidad: cómo pedírsela
 * al parser, cuál es su `id` y con qué caso de uso registrarla.
 */
abstract class ProcesarArchivoUseCase<T>(
    private val parsers: List<DocumentParserPort>,
) : IngestarArchivoUseCase {

    protected abstract fun extraer(
        parser: DocumentParserPort,
        nombreArchivo: String,
        contenido: ByteArray,
    ): List<T>

    protected abstract fun idDe(entidad: T): String

    protected abstract fun registrar(entidad: T)

    final override fun ejecutar(nombreArchivo: String, contenido: ByteArray): ResultadoIngesta {
        val parser = parsers.firstOrNull { it.soporta(nombreArchivo) }
            ?: throw FormatoArchivoInvalidoException(
                "El archivo '$nombreArchivo' no tiene un formato soportado (.csv, .xlsx).",
            )

        val entidades = extraer(parser, nombreArchivo, contenido)
        val omitidos = mutableListOf<RegistroOmitido>()
        var registrados = 0

        for (entidad in entidades) {
            try {
                registrar(entidad)
                registrados++
            } catch (duplicado: EntidadDuplicadaException) {
                omitidos += RegistroOmitido(idDe(entidad), duplicado.message ?: "registro duplicado")
            }
        }

        return ResultadoIngesta(
            nombreArchivo = nombreArchivo,
            totalRegistrosLeidos = entidades.size,
            registrados = registrados,
            omitidos = omitidos.toList(),
        )
    }
}

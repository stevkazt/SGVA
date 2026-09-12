package com.example.sgva.infrastructure.parsing

import com.example.sgva.domain.Docente
import com.example.sgva.domain.DocumentParserPort
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.FormatoArchivoInvalidoException
import com.example.sgva.domain.RespuestaEncuesta

/**
 * Base común de los adaptadores de [DocumentParserPort]. Concentra el
 * *Protocolo de Validación en Origen* (`DESIGN_SGVA.md` §5):
 *
 *  1. Cada adaptador concreto solo se encarga de leer el archivo a una
 *     [TablaCruda] (encabezados + filas de texto) según su formato físico.
 *  2. Esta base verifica que estén todas las columnas obligatorias de la entidad.
 *  3. Mapea y normaliza fila por fila; cualquier fallo se envuelve en
 *     [FormatoArchivoInvalidoException] indicando el archivo y la fila, sin dejar
 *     escapar excepciones técnicas de la librería de parseo.
 */
internal abstract class ParserDocumentalBase : DocumentParserPort {

    /**
     * Lee el archivo a una tabla de texto. Puede lanzar excepciones técnicas de
     * la librería subyacente: [transformar] las traduce a excepción controlada.
     */
    protected abstract fun leerTabla(contenido: ByteArray): TablaCruda

    final override fun extraerEstudiantes(nombreArchivo: String, contenido: ByteArray): List<Estudiante> =
        transformar(nombreArchivo, contenido, MapeadorEstudiante)

    final override fun extraerDocentes(nombreArchivo: String, contenido: ByteArray): List<Docente> =
        transformar(nombreArchivo, contenido, MapeadorDocente)

    final override fun extraerRespuestasEncuestas(nombreArchivo: String, contenido: ByteArray): List<RespuestaEncuesta> =
        transformar(nombreArchivo, contenido, MapeadorRespuestaEncuesta)

    private fun <T> transformar(
        nombreArchivo: String,
        contenido: ByteArray,
        mapeador: MapeadorEntidad<T>,
    ): List<T> {
        val tabla = leerTablaControlada(nombreArchivo, contenido)
        exigirColumnas(nombreArchivo, tabla.encabezados, mapeador.columnasObligatorias)

        return tabla.filas.mapIndexed { indice, valores ->
            val numeroFila = indice + 2 // +1 por el encabezado, +1 para numeración en base 1
            val fila = tabla.encabezados.zip(valores).toMap()
            try {
                mapeador.mapear(fila)
            } catch (datoInvalido: IllegalArgumentException) {
                // Cubre también NumberFormatException (subtipo) y las excepciones de dominio.
                throw FormatoArchivoInvalidoException(
                    "Archivo '$nombreArchivo', fila $numeroFila: ${datoInvalido.message}",
                    datoInvalido,
                )
            }
        }
    }

    private fun leerTablaControlada(nombreArchivo: String, contenido: ByteArray): TablaCruda =
        try {
            leerTabla(contenido)
        } catch (formatoInvalido: FormatoArchivoInvalidoException) {
            throw formatoInvalido
        } catch (falloTecnico: Exception) {
            throw FormatoArchivoInvalidoException(
                "No se pudo leer el archivo '$nombreArchivo': ${falloTecnico.message}",
                falloTecnico,
            )
        }

    private fun exigirColumnas(
        nombreArchivo: String,
        encabezados: List<String>,
        obligatorias: Set<String>,
    ) {
        val presentes = encabezados.map { it.trim() }.toSet()
        val faltantes = (obligatorias - presentes).sorted()
        if (faltantes.isNotEmpty()) {
            throw FormatoArchivoInvalidoException(
                "Al archivo '$nombreArchivo' le faltan columnas obligatorias: ${faltantes.joinToString(", ")}.",
            )
        }
    }
}

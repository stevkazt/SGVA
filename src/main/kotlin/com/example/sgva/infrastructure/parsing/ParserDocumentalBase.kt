package com.example.sgva.infrastructure.parsing

import com.example.sgva.domain.Docente
import com.example.sgva.domain.DocumentParserPort
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.EvaluacionDocumental
import com.example.sgva.domain.FormatoArchivoInvalidoException
import com.example.sgva.domain.ParticipacionActor
import com.example.sgva.domain.PreevaluacionEvaluador
import com.example.sgva.domain.ResultadoEncuestaPregunta
import com.example.sgva.domain.RespuestaEncuesta

/**
 * Base común de los adaptadores de [DocumentParserPort]. Concentra el
 * *Protocolo de Validación en Origen* (`DESIGN_SGVA.md` §5):
 *
 *  1. Cada adaptador concreto solo se encarga de leer el archivo a una
 *     [TablaCruda] (todas sus filas de texto, sin distinguir encabezado) según
 *     su formato físico.
 *  2. Esta base localiza la fila de encabezado y verifica que estén todas las
 *     columnas obligatorias de la entidad, descartando las filas de título
 *     que la preceden.
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

    final override fun extraerResultadosEncuestaPregunta(
        nombreArchivo: String,
        contenido: ByteArray,
    ): List<ResultadoEncuestaPregunta> = transformar(nombreArchivo, contenido, MapeadorResultadoEncuestaPregunta)

    final override fun extraerEvaluacionesDocumentales(
        nombreArchivo: String,
        contenido: ByteArray,
    ): List<EvaluacionDocumental> = transformar(nombreArchivo, contenido, MapeadorEvaluacionDocumental)

    final override fun extraerPreevaluaciones(
        nombreArchivo: String,
        contenido: ByteArray,
    ): List<PreevaluacionEvaluador> = transformar(nombreArchivo, contenido, MapeadorPreevaluacion)

    final override fun extraerParticipacionActores(
        nombreArchivo: String,
        contenido: ByteArray,
    ): List<ParticipacionActor> = transformar(nombreArchivo, contenido, MapeadorParticipacionActor)

    private fun <T> transformar(
        nombreArchivo: String,
        contenido: ByteArray,
        mapeador: MapeadorEntidad<T>,
    ): List<T> {
        val tabla = leerTablaControlada(nombreArchivo, contenido)
        val indiceEncabezado = localizarEncabezado(nombreArchivo, tabla.filas, mapeador.columnasObligatorias)
        val encabezados = tabla.filas[indiceEncabezado].map(String::trim)

        return tabla.filas.drop(indiceEncabezado + 1).mapIndexed { posicion, valores ->
            // +1 por el encabezado, +1 para numeración en base 1; indiceEncabezado desplaza
            // la cuenta cuando el archivo trae filas de título antes del encabezado.
            val numeroFila = indiceEncabezado + posicion + 2
            val fila = encabezados.zip(valores).toMap()
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

    /**
     * Localiza la fila de encabezado como la primera que contiene todas las
     * columnas obligatorias de la entidad; las filas anteriores son títulos
     * del área y se descartan (`DESIGN_SGVA.md` §4, "Robustez de lectura V5").
     * Si ninguna fila las contiene todas, se señalan como faltantes las que
     * no aparecen en la fila con más coincidencias.
     */
    private fun localizarEncabezado(
        nombreArchivo: String,
        filas: List<List<String>>,
        obligatorias: Set<String>,
    ): Int {
        val coincidencias = filas.map { fila -> fila.map(String::trim).toSet().intersect(obligatorias) }
        val indiceCandidato = coincidencias.indices.maxBy { coincidencias[it].size }

        if (coincidencias[indiceCandidato].size < obligatorias.size) {
            val faltantes = (obligatorias - coincidencias[indiceCandidato]).sorted()
            throw FormatoArchivoInvalidoException(
                "Al archivo '$nombreArchivo' le faltan columnas obligatorias: ${faltantes.joinToString(", ")}.",
            )
        }
        return indiceCandidato
    }
}

package com.example.sgva.infrastructure.parsing

import com.example.sgva.domain.FormatoArchivoInvalidoException
import de.siegmar.fastcsv.reader.CsvReader
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

/**
 * Adaptador de [com.example.sgva.domain.DocumentParserPort] para archivos `.csv`,
 * implementado sobre FastCSV (`DESIGN_SGVA.md` §4, adaptador «OpenCsvParser» del
 * diseño; se usa FastCSV por ser una dependencia sin transitivas y gestionable
 * dentro del stack de Spring Boot 4).
 *
 * Se lee la primera fila no vacía como encabezado; la normalización de los
 * valores la aplica [ParserDocumentalBase] a través de los mapeadores.
 */
@Component
internal class CsvDocumentParser : ParserDocumentalBase() {

    override fun soporta(nombreArchivo: String): Boolean = extension(nombreArchivo) == "csv"

    override fun leerTabla(contenido: ByteArray): TablaCruda {
        val registros = CsvReader.builder()
            .skipEmptyLines(true)
            .ofCsvRecord(ByteArrayInputStream(contenido), StandardCharsets.UTF_8)
            .use { lector -> lector.map { it.fields } }

        if (registros.isEmpty()) {
            throw FormatoArchivoInvalidoException("El archivo CSV no contiene ninguna fila.")
        }

        return TablaCruda(
            encabezados = registros.first().map(String::trim),
            filas = registros.drop(1),
        )
    }

    private fun extension(nombreArchivo: String): String =
        nombreArchivo.substringAfterLast('.', "").lowercase()
}

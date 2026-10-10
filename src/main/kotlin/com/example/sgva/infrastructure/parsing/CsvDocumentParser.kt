package com.example.sgva.infrastructure.parsing

import com.example.sgva.domain.FormatoArchivoInvalidoException
import de.siegmar.fastcsv.reader.CsvReader
import org.springframework.stereotype.Component
import java.nio.ByteBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets

/**
 * Adaptador de [com.example.sgva.domain.DocumentParserPort] para archivos `.csv`,
 * implementado sobre FastCSV (`DESIGN_SGVA.md` §4, adaptador «OpenCsvParser» del
 * diseño; se usa FastCSV por ser una dependencia sin transitivas y gestionable
 * dentro del stack de Spring Boot 4).
 *
 * Soporta tanto los archivos que ya se cargan hoy (UTF-8, separador `,`) como
 * el formato real de las áreas (`DESIGN_SGVA.md` §4, "Robustez de lectura V5"):
 * separador `;`, coma decimal, `N/A` como dato faltante, codificación
 * ISO-8859-1 (Latin-1) y filas de título antes del encabezado. La detección
 * de encabezado y la normalización de valores las aplica [ParserDocumentalBase]
 * a través de los mapeadores.
 */
@Component
internal class CsvDocumentParser : ParserDocumentalBase() {

    override fun soporta(nombreArchivo: String): Boolean = extension(nombreArchivo) == "csv"

    override fun leerTabla(contenido: ByteArray): TablaCruda {
        val texto = decodificar(contenido)
        val separador = detectarSeparador(texto)

        val registros = CsvReader.builder()
            .fieldSeparator(separador)
            .skipEmptyLines(true)
            .ofCsvRecord(texto)
            .use { lector -> lector.map { it.fields } }
            .filter { fila -> fila.any { it.isNotBlank() } }

        if (registros.isEmpty()) {
            throw FormatoArchivoInvalidoException("El archivo CSV no contiene ninguna fila.")
        }

        return TablaCruda(filas = registros)
    }

    /**
     * Los archivos que ya se cargan hoy están en UTF-8; las áreas entregan los
     * suyos en ISO-8859-1 (Latin-1). Se intenta UTF-8 estricto primero porque
     * Latin-1 nunca falla al decodificar (cubre los 256 valores de byte) y por
     * tanto no sirve para detectar el formato por sí solo.
     */
    private fun decodificar(contenido: ByteArray): String {
        val decodificadorUtf8 = StandardCharsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT)
        return try {
            decodificadorUtf8.decode(ByteBuffer.wrap(contenido)).toString()
        } catch (_: CharacterCodingException) {
            String(contenido, StandardCharsets.ISO_8859_1)
        }
    }

    /**
     * Los archivos que ya se cargan hoy separan columnas con `,`; los de las
     * áreas usan `;` como separador de columnas y `,` como separador decimal.
     * Se elige el símbolo que aparece más veces en el archivo.
     */
    private fun detectarSeparador(texto: String): Char =
        if (texto.count { it == ';' } > texto.count { it == ',' }) ';' else ','

    private fun extension(nombreArchivo: String): String =
        nombreArchivo.substringAfterLast('.', "").lowercase()
}

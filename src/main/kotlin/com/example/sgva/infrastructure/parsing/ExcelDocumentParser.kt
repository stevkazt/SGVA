package com.example.sgva.infrastructure.parsing

import com.example.sgva.domain.FormatoArchivoInvalidoException
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream

/**
 * Adaptador de [com.example.sgva.domain.DocumentParserPort] para archivos `.xlsx`,
 * implementado sobre Apache POI (`DESIGN_SGVA.md` §4, adaptador «ApachePoiExcelParser»).
 *
 * Se procesa la primera hoja del libro: la primera fila es el encabezado y el
 * resto son datos. Las celdas numéricas se convierten a texto sin el `.0` que
 * añade Excel a los enteros; la normalización final la aplica [ParserDocumentalBase].
 */
@Component
internal class ExcelDocumentParser : ParserDocumentalBase() {

    override fun soporta(nombreArchivo: String): Boolean =
        nombreArchivo.substringAfterLast('.', "").lowercase() == "xlsx"

    override fun leerTabla(contenido: ByteArray): TablaCruda =
        XSSFWorkbook(ByteArrayInputStream(contenido)).use { libro ->
            if (libro.numberOfSheets == 0) {
                throw FormatoArchivoInvalidoException("El archivo Excel no contiene ninguna hoja.")
            }

            val filas = libro.getSheetAt(0).iterator().asSequence().toList()
            if (filas.isEmpty()) {
                throw FormatoArchivoInvalidoException("La primera hoja del archivo Excel está vacía.")
            }

            val encabezados = leerFila(filas.first()).map(String::trim)
            TablaCruda(
                encabezados = encabezados,
                filas = filas.drop(1)
                    .map { leerFila(it, encabezados.size) }
                    .filter { valores -> valores.any { it.isNotBlank() } },
            )
        }

    private fun leerFila(fila: Row, columnasMinimas: Int = 0): List<String> {
        val columnas = maxOf(fila.lastCellNum.toInt(), columnasMinimas)
        return (0 until columnas).map { indice ->
            valorComoTexto(fila.getCell(indice, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
        }
    }

    private fun valorComoTexto(celda: Cell?): String = when {
        celda == null -> ""
        celda.cellType == CellType.STRING -> celda.stringCellValue.trim()
        celda.cellType == CellType.BOOLEAN -> celda.booleanCellValue.toString()
        celda.cellType == CellType.NUMERIC -> numeroComoTexto(celda.numericCellValue)
        celda.cellType == CellType.FORMULA -> when (celda.cachedFormulaResultType) {
            CellType.STRING -> celda.stringCellValue.trim()
            CellType.NUMERIC -> numeroComoTexto(celda.numericCellValue)
            CellType.BOOLEAN -> celda.booleanCellValue.toString()
            else -> ""
        }
        else -> ""
    }

    private fun numeroComoTexto(valor: Double): String =
        if (valor % 1.0 == 0.0) valor.toLong().toString() else valor.toString()
}

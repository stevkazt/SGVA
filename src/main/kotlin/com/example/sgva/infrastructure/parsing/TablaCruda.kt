package com.example.sgva.infrastructure.parsing

/**
 * Representación tabular en texto de un archivo, independiente de su formato
 * físico. La produce cada adaptador concreto y la consume [ParserDocumentalBase].
 *
 * Incluye todas las filas no vacías del archivo en su orden original, sin
 * distinguir todavía cuál es el encabezado: los archivos reales de las áreas
 * traen una o más filas de título antes del encabezado (`DESIGN_SGVA.md` §4,
 * "Robustez de lectura V5"), y es [ParserDocumentalBase] quien localiza la
 * fila de encabezado buscando las columnas obligatorias de cada entidad.
 *
 * @property filas todas las filas del archivo (títulos, encabezado y datos);
 *   cada una es la lista de valores en bruto de esa fila.
 */
internal data class TablaCruda(
    val filas: List<List<String>>,
)

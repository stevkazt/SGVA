package com.example.sgva.infrastructure.parsing

/**
 * Representación tabular en texto de un archivo, independiente de su formato
 * físico. La produce cada adaptador concreto y la consume [ParserDocumentalBase].
 *
 * @property encabezados nombres de columna de la primera fila, ya recortados.
 * @property filas filas de datos; cada una es la lista de valores en bruto, en el
 *   mismo orden que [encabezados].
 */
internal data class TablaCruda(
    val encabezados: List<String>,
    val filas: List<List<String>>,
)

package com.example.sgva.domain

/**
 * Resumen del resultado de incorporar los registros académicos contenidos en un
 * archivo cargado desde el exterior.
 *
 * Es un objeto de valor puro (sin dependencias de framework); vive en el dominio
 * porque describe el desenlace de una operación de negocio —cuántos registros se
 * integraron y cuáles quedaron fuera— y lo devuelven los casos de uso de carga.
 *
 * @property nombreArchivo archivo procesado.
 * @property totalRegistrosLeidos filas de datos extraídas y normalizadas correctamente.
 * @property registrados entidades dadas de alta en esta ejecución.
 * @property omitidos entidades que no se registraron (p. ej. `id` ya existente),
 *   con el motivo de cada omisión.
 */
data class ResultadoIngesta(
    val nombreArchivo: String,
    val totalRegistrosLeidos: Int,
    val registrados: Int,
    val omitidos: List<RegistroOmitido>,
)

/** Entidad de un archivo que no llegó a registrarse, junto con la razón. */
data class RegistroOmitido(val id: String, val motivo: String)

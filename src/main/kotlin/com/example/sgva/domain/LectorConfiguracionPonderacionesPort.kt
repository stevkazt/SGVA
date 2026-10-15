package com.example.sgva.domain

/**
 * Puerto de salida que delega en infraestructura la lectura del archivo de
 * ponderaciones: convierte su contenido en una [ConfiguracionPonderaciones]
 * válida, o falla con [FormatoArchivoInvalidoException] o con la excepción de
 * dominio que corresponda a la regla que incumple (pesos negativos, sumas
 * distintas de `1,0`, filas de mezcla ausentes).
 */
interface LectorConfiguracionPonderacionesPort {

    fun leer(nombreArchivo: String, contenido: ByteArray): ConfiguracionPonderaciones
}

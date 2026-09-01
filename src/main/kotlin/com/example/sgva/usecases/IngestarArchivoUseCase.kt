package com.example.sgva.usecases

import com.example.sgva.domain.ResultadoIngesta

/**
 * Puerto de entrada (Inbound/Driving Port): dispara la carga de un archivo de
 * datos —CSV o Excel— y el registro de las entidades que contiene
 * (`DESIGN_SGVA.md` §3). Cada implementación cubre un tipo de entidad
 * (estudiantes, docentes).
 */
interface IngestarArchivoUseCase {

    /**
     * Procesa el archivo [nombreArchivo] (cuyos bytes son [contenido]): delega la
     * extracción y normalización al adaptador de parseo correspondiente y
     * registra cada entidad mediante el caso de uso de registro individual.
     *
     * @throws com.example.sgva.domain.FormatoArchivoInvalidoException si el formato
     *   del archivo no está soportado, le faltan columnas o sus datos son irrecuperables.
     */
    fun ejecutar(nombreArchivo: String, contenido: ByteArray): ResultadoIngesta
}

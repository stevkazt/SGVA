package com.example.sgva.usecases

import com.example.sgva.domain.ConfiguracionPonderaciones
import com.example.sgva.domain.ConfiguracionPonderacionesRepository
import com.example.sgva.domain.LectorConfiguracionPonderacionesPort

/**
 * Módulo 4 — carga del archivo de ponderaciones (`DESIGN_SGVA.md` §0.1 y §4).
 * Lee y valida el archivo y, solo si es válido, reemplaza la configuración
 * vigente; si el archivo es inválido, la configuración anterior se conserva.
 */
class CargarConfiguracionPonderacionesUseCase(
    private val lector: LectorConfiguracionPonderacionesPort,
    private val repositorio: ConfiguracionPonderacionesRepository,
) {

    fun ejecutar(nombreArchivo: String, contenido: ByteArray): ConfiguracionPonderaciones =
        repositorio.reemplazar(lector.leer(nombreArchivo, contenido))
}

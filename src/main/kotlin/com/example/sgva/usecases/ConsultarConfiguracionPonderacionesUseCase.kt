package com.example.sgva.usecases

import com.example.sgva.domain.ConfiguracionPonderaciones
import com.example.sgva.domain.ConfiguracionPonderacionesRepository
import com.example.sgva.domain.DatosInsuficientesException

/**
 * Módulo 4 — consulta de la configuración de ponderaciones vigente
 * (`DESIGN_SGVA.md` §0.1). No existe una configuración por defecto: mientras
 * nadie haya cargado el archivo de ponderaciones, los cálculos que dependen de
 * ella no pueden ejecutarse.
 */
class ConsultarConfiguracionPonderacionesUseCase(
    private val repositorio: ConfiguracionPonderacionesRepository,
) {

    /** @throws DatosInsuficientesException si todavía no se ha cargado ninguna configuración. */
    fun ejecutar(): ConfiguracionPonderaciones =
        repositorio.obtener() ?: throw DatosInsuficientesException(
            "No hay ponderaciones configuradas. Carga el archivo de ponderaciones del modelo " +
                "(sección «Carga de datos») antes de calcular resultados.",
        )
}

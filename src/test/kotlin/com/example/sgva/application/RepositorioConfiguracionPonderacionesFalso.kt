package com.example.sgva.application

import com.example.sgva.domain.ConfiguracionPonderaciones
import com.example.sgva.domain.ConfiguracionPonderacionesRepository

/**
 * Doble de prueba en memoria del puerto [ConfiguracionPonderacionesRepository].
 * Pública para poder exponerse como bean en las pruebas de `infrastructure.web`.
 */
class RepositorioConfiguracionPonderacionesFalso : ConfiguracionPonderacionesRepository {

    private var vigente: ConfiguracionPonderaciones? = null

    override fun obtener(): ConfiguracionPonderaciones? = vigente

    override fun reemplazar(configuracion: ConfiguracionPonderaciones): ConfiguracionPonderaciones {
        vigente = configuracion
        return configuracion
    }

    fun limpiar() {
        vigente = null
    }
}

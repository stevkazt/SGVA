package com.example.sgva.domain

/**
 * Puerto de salida de la configuración de ponderaciones del modelo
 * institucional (`DESIGN_SGVA.md` §0.1 y §4): conserva la configuración vigente
 * para que se pueda cambiar sin reiniciar la aplicación. Hay una sola
 * configuración vigente; cargar otra reemplaza por completo a la anterior.
 */
interface ConfiguracionPonderacionesRepository {

    /** La configuración vigente, o `null` si todavía no se ha cargado ninguna. */
    fun obtener(): ConfiguracionPonderaciones?

    /** Reemplaza por completo la configuración vigente y devuelve la nueva. */
    fun reemplazar(configuracion: ConfiguracionPonderaciones): ConfiguracionPonderaciones
}

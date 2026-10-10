package com.example.sgva.domain

/**
 * Elemento del modelo institucional de autoevaluación de 12 factores y 48
 * características (Sección 0 de `DESIGN_SGVA.md`, V5). No depende de
 * frameworks, bases de datos ni sistemas de archivos.
 *
 * La `ponderacion` proviene siempre de una fuente de configuración externa
 * (hoja «1. Ponderación» del modelo 2025); nunca se hardcodea un valor por
 * característica en el dominio (`DESIGN_SGVA.md` §0.1).
 *
 * @property id identificador único de la característica (ej.: "1.1").
 * @property nombre nombre descriptivo de la característica.
 * @property factor identificador del factor del modelo al que pertenece (ej.: "F1").
 * @property ponderacion peso de la característica dentro de su factor, en el
 *   rango `0.0..1.0`. La suma de las ponderaciones de las características de
 *   un mismo factor se valida en [ConfiguracionPonderaciones], no aquí: esta
 *   entidad solo conoce su propio peso, no el de sus pares.
 */
data class Caracteristica(
    val id: String,
    val nombre: String,
    val factor: String,
    val ponderacion: Double,
) {
    init {
        ReglasDeFormato.exigirNoVacio(id, "id")
        ReglasDeFormato.exigirNoVacio(nombre, "nombre")
        ReglasDeFormato.exigirNoVacio(factor, "factor")
        ReglasDeFormato.exigirPonderacionNoNegativa(ponderacion, "ponderacion de la característica '$id'")
    }
}

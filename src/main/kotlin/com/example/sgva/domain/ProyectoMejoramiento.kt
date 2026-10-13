package com.example.sgva.domain

/**
 * Proyecto del plan de mejoramiento de un programa (Módulo 5,
 * `DESIGN_SGVA.md` §0.1 y §1, V6). No depende de frameworks, bases de datos
 * ni sistemas de archivos.
 *
 * @property id identificador único del proyecto.
 * @property programa identificador corto y estable del programa (ver §0.1 de `DESIGN_SGVA.md`).
 * @property nombre nombre del proyecto.
 * @property aspectosMejora aspectos de mejora que atiende el proyecto.
 * @property origen origen del proyecto.
 * @property objetivo objetivo del proyecto.
 * @property actividades actividades previstas del proyecto.
 * @property factores identificadores de los factores asociados; un proyecto
 *   está asociado a uno o más factores.
 * @property estado estado actual del proyecto.
 */
data class ProyectoMejoramiento(
    val id: String,
    val programa: String,
    val nombre: String,
    val aspectosMejora: List<String>,
    val origen: OrigenProyecto,
    val objetivo: String,
    val actividades: List<String>,
    val factores: Set<String>,
    val estado: EstadoProyecto,
) {
    init {
        ReglasDeFormato.exigirNoVacio(id, "id")
        ReglasDeFormato.exigirNoVacio(programa, "programa")
        ReglasDeFormato.exigirNoVacio(nombre, "nombre")
        ReglasDeFormato.exigirNoVacio(objetivo, "objetivo")
        ReglasDeFormato.exigirNoVacio(factores, "factores")
    }
}

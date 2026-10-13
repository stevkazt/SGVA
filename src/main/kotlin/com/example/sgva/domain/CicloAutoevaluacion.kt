package com.example.sgva.domain

/**
 * Edición anual del proceso de autoevaluación de un programa en un periodo
 * (Módulo 5, `DESIGN_SGVA.md` §0, §0.1 y §1, V6). No depende de frameworks,
 * bases de datos ni sistemas de archivos.
 *
 * Un programa tiene como máximo un ciclo por periodo (`DESIGN_SGVA.md`
 * §0.1); esa unicidad es una regla de aplicación que verifica
 * `RegistrarCicloAutoevaluacionUseCase`, no esta entidad.
 *
 * @property id identificador único del ciclo.
 * @property programa identificador corto y estable del programa (ver §0.1 de `DESIGN_SGVA.md`).
 * @property periodo periodo académico del ciclo en formato `AAAAS`.
 */
data class CicloAutoevaluacion(
    val id: String,
    val programa: String,
    val periodo: String,
) {
    init {
        ReglasDeFormato.exigirNoVacio(id, "id")
        ReglasDeFormato.exigirNoVacio(programa, "programa")
        ReglasDeFormato.exigirPeriodoAcademico(periodo, "periodo")
    }
}

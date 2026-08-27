package com.example.sgva.domain

/**
 * Entidad de negocio pura que representa a un docente de la planta profesoral
 * para un periodo académico dado (Factor 3). No depende de frameworks, bases de
 * datos ni sistemas de archivos.
 *
 * Las invariantes se verifican en el bloque `init`: cualquier registro que las
 * incumpla no puede existir como instancia de `Docente`.
 *
 * @property id identificador único del docente.
 * @property facultad facultad a la que está adscrito.
 * @property nivelFormacion máximo nivel de formación académica alcanzado.
 * @property dedicacion tipo de dedicación contractual con la institución.
 * @property periodo periodo académico del registro en formato `AAAAS` (ej.: "20261"), sin separadores.
 */
data class Docente(
    val id: String,
    val facultad: String,
    val nivelFormacion: NivelFormacion,
    val dedicacion: TipoDedicacion,
    val periodo: String,
) {
    init {
        ReglasDeFormato.exigirNoVacio(id, "id")
        ReglasDeFormato.exigirNoVacio(facultad, "facultad")
        ReglasDeFormato.exigirPeriodoAcademico(periodo, "periodo")
    }
}

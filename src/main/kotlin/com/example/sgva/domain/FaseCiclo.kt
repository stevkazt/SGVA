package com.example.sgva.domain

/**
 * Una de las cuatro fases fijas del proceso institucional de autoevaluación
 * (Módulo 5, `DESIGN_SGVA.md` §0 y §1, V6): (1) apropiación; (2) planeación y
 * desarrollo; (3) emisión de juicios de valor y plan de mejoramiento; (4)
 * aprobación de resultados. No depende de frameworks, bases de datos ni
 * sistemas de archivos.
 *
 * @property ciclo identificador del [CicloAutoevaluacion] al que pertenece.
 * @property numero número de la fase, de 1 a 4 inclusive.
 * @property nombre nombre descriptivo de la fase (ver [NOMBRES]).
 */
data class FaseCiclo(
    val ciclo: String,
    val numero: Int,
    val nombre: String,
) {
    init {
        ReglasDeFormato.exigirNoVacio(ciclo, "ciclo")
        if (numero !in RANGO_NUMERO) {
            throw FaseInvalidaException(numero)
        }
        ReglasDeFormato.exigirNoVacio(nombre, "nombre")
    }

    companion object {
        /** Rango válido del número de fase (`DESIGN_SGVA.md` §0, V6). */
        val RANGO_NUMERO: IntRange = 1..4

        /**
         * Nombre canónico de cada una de las cuatro fases del proceso
         * institucional, en el orden fijado por `DESIGN_SGVA.md` §0 (V6).
         * `RegistrarCicloAutoevaluacionUseCase` las crea automáticamente para
         * cada ciclo nuevo: no varían entre ediciones.
         */
        val NOMBRES: Map<Int, String> = mapOf(
            1 to "Apropiación",
            2 to "Planeación y desarrollo",
            3 to "Emisión de juicios de valor y plan de mejoramiento",
            4 to "Aprobación de resultados",
        )
    }
}

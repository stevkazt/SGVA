package com.example.sgva.domain

import java.time.LocalDate

/**
 * Grupo evaluador de un conjunto de factores dentro de un taller de un
 * [CicloAutoevaluacion] (Módulo 5, `DESIGN_SGVA.md` §0.1 y §1, V6). No
 * depende de frameworks, bases de datos ni sistemas de archivos.
 *
 * Un factor puede aparecer en más de un taller del mismo ciclo
 * (`DESIGN_SGVA.md` §0.1).
 *
 * @property ciclo identificador del [CicloAutoevaluacion] al que pertenece.
 * @property factores identificadores de los factores que evalúa el grupo.
 * @property taller número del taller dentro del ciclo.
 * @property fechaTaller fecha del taller; opcional, porque la fuente
 *   institucional tiene un taller sin fecha registrada.
 */
data class GrupoEvaluador(
    val ciclo: String,
    val factores: Set<String>,
    val taller: Int,
    val fechaTaller: LocalDate? = null,
) {
    init {
        ReglasDeFormato.exigirNoVacio(ciclo, "ciclo")
        ReglasDeFormato.exigirNoVacio(factores, "factores")
        if (taller <= 0) {
            throw NumeroTallerInvalidoException(taller)
        }
    }
}

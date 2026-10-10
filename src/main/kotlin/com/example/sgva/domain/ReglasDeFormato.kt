package com.example.sgva.domain

/**
 * Reglas de formato compartidas por las entidades del dominio. Centraliza la
 * verificación del formato `AAAAS` para que `Estudiante.cohorte` y
 * `Docente.periodo` apliquen exactamente el mismo criterio.
 */
internal object ReglasDeFormato {

    /** Regex de referencia definida en `DESIGN_SGVA.md`, Sección 0.1. */
    private val PATRON_PERIODO_ACADEMICO = Regex("""^\d{4}[12]$""")

    /**
     * Verifica que [valor] cumpla el formato `AAAAS` y lo devuelve sin cambios.
     *
     * @throws FormatoPeriodoInvalidoException si el formato no es válido.
     */
    fun exigirPeriodoAcademico(valor: String, campo: String): String {
        if (!PATRON_PERIODO_ACADEMICO.matches(valor)) {
            throw FormatoPeriodoInvalidoException(campo, valor)
        }
        return valor
    }

    /**
     * Verifica que [valor] no esté vacío ni en blanco y lo devuelve sin cambios.
     *
     * @throws CampoRequeridoVacioException si el valor está en blanco.
     */
    fun exigirNoVacio(valor: String, campo: String): String {
        if (valor.isBlank()) {
            throw CampoRequeridoVacioException(campo)
        }
        return valor
    }

    /**
     * Verifica que [valor] contenga al menos un elemento y lo devuelve sin
     * cambios. Aplica a los conjuntos obligatorios de la entidad (ej.
     * `ResultadoEncuestaPregunta.estamentos`).
     *
     * @throws CampoRequeridoVacioException si el conjunto está vacío.
     */
    fun <T> exigirNoVacio(valor: Set<T>, campo: String): Set<T> {
        if (valor.isEmpty()) {
            throw CampoRequeridoVacioException(campo)
        }
        return valor
    }

    /** Tolerancia numérica para las sumas de ponderaciones (`DESIGN_SGVA.md` §0.1). */
    const val TOLERANCIA_SUMA_PONDERACIONES: Double = 0.001

    /**
     * Verifica que [valor] no sea negativo y lo devuelve sin cambios. Aplica a
     * todo peso del modelo de ponderaciones (factores, características y
     * mezcla encuestas/documental; `DESIGN_SGVA.md` §0.1).
     *
     * @throws PonderacionNegativaException si [valor] es negativo.
     */
    fun exigirPonderacionNoNegativa(valor: Double, contexto: String): Double {
        if (valor < 0.0) {
            throw PonderacionNegativaException(contexto, valor)
        }
        return valor
    }

    /**
     * Verifica que [suma] sea igual a `1.0` dentro de
     * [TOLERANCIA_SUMA_PONDERACIONES] y la devuelve sin cambios.
     *
     * @throws SumaPonderacionesInvalidaException si [suma] se aleja de `1.0` más que la tolerancia.
     */
    fun exigirSumaUno(suma: Double, contexto: String): Double {
        if (kotlin.math.abs(suma - 1.0) > TOLERANCIA_SUMA_PONDERACIONES) {
            throw SumaPonderacionesInvalidaException(contexto, suma)
        }
        return suma
    }
}

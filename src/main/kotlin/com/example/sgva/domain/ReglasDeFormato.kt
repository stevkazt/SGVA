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
}

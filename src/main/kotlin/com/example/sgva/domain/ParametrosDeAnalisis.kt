package com.example.sgva.domain

/**
 * Parámetros de negocio fijados por `DESIGN_SGVA.md` para el cálculo de
 * indicadores. Se centralizan aquí, en el dominio, porque son reglas del modelo
 * de autoevaluación y no decisiones de infraestructura.
 */
object ParametrosDeAnalisis {

    /**
     * Amplitud, en años calendario, de la ventana histórica de matrícula
     * (`DESIGN_SGVA.md` §0.1). Se cuenta hacia atrás desde la fecha actual del
     * sistema en tiempo de ejecución.
     */
    const val ANIOS_VENTANA_MATRICULA: Int = 7
}

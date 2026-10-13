package com.example.sgva.domain

/**
 * Objeto de valor puro que agrupa la configuración del seguimiento del
 * proceso institucional de autoevaluación (Módulo 5, `DESIGN_SGVA.md` §0.1,
 * V6): los umbrales del semáforo de participación y el número de días para
 * considerar una actividad "próxima a vencer". No depende de frameworks,
 * bases de datos ni sistemas de archivos: ningún umbral se hardcodea en el
 * dominio ni en los casos de uso.
 *
 * @property umbralRojoNaranja porcentaje diligenciado por debajo del cual el
 *   semáforo es [TipoSemaforo.ROJO] (en el criterio vigente, `0.6`).
 * @property umbralNaranjaVerde porcentaje diligenciado a partir del cual el
 *   semáforo es [TipoSemaforo.VERDE] (en el criterio vigente, `0.8`).
 * @property diasProximaAVencer número de días a partir de la fecha de
 *   referencia dentro de los cuales una actividad no completada se considera
 *   próxima a vencer (en el criterio vigente, `7`).
 */
data class ConfiguracionSeguimiento(
    val umbralRojoNaranja: Double,
    val umbralNaranjaVerde: Double,
    val diasProximaAVencer: Int,
) {
    init {
        if (umbralRojoNaranja < 0.0 || umbralRojoNaranja >= umbralNaranjaVerde || umbralNaranjaVerde > 1.0) {
            throw UmbralesSemaforoInvalidosException(
                "Los umbrales del semáforo deben cumplir 0 <= umbralRojoNaranja < umbralNaranjaVerde <= 1; " +
                    "valores recibidos: umbralRojoNaranja=$umbralRojoNaranja, umbralNaranjaVerde=$umbralNaranjaVerde.",
            )
        }
        if (diasProximaAVencer < 0) {
            throw DiasProximaAVencerInvalidoException(diasProximaAVencer)
        }
    }

    /**
     * Deriva el semáforo de un [porcentaje] diligenciado (`DESIGN_SGVA.md`
     * §0.1): ausente si [porcentaje] es ausente.
     */
    fun semaforoDe(porcentaje: Double?): TipoSemaforo? = when {
        porcentaje == null -> null
        porcentaje < umbralRojoNaranja -> TipoSemaforo.ROJO
        porcentaje < umbralNaranjaVerde -> TipoSemaforo.NARANJA
        else -> TipoSemaforo.VERDE
    }
}

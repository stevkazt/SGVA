package com.example.sgva.domain

/**
 * Participación de un actor en las encuestas de un [CicloAutoevaluacion]
 * (Módulo 5, `DESIGN_SGVA.md` §0.1 y §1, V6). No depende de frameworks,
 * bases de datos ni sistemas de archivos.
 *
 * Su clave natural es la combinación de [ciclo] y [actor]: no tiene un `id`
 * propio. [actor] es un texto abierto, no uno de los siete [TipoEstamento]:
 * la fuente institucional agrupa actores de forma distinta (ej. empleadores
 * y aliados estratégicos juntos).
 *
 * @property ciclo identificador del [CicloAutoevaluacion] al que pertenece.
 * @property actor texto abierto que identifica al actor.
 * @property poblacion tamaño de la población del actor; entero no negativo.
 * @property muestra tamaño de la muestra; entero no negativo, `muestra <= poblacion`.
 * @property respuestas respuestas recibidas; entero no negativo, `respuestas <= muestra`.
 */
data class ParticipacionActor(
    val ciclo: String,
    val actor: String,
    val poblacion: Int,
    val muestra: Int,
    val respuestas: Int,
) {
    init {
        ReglasDeFormato.exigirNoVacio(ciclo, "ciclo")
        ReglasDeFormato.exigirNoVacio(actor, "actor")
        if (poblacion < 0) throw CantidadNegativaException("poblacion", poblacion)
        if (muestra < 0) throw CantidadNegativaException("muestra", muestra)
        if (respuestas < 0) throw CantidadNegativaException("respuestas", respuestas)
        if (muestra > poblacion) {
            throw ParticipacionInconsistenteException(
                "La muestra ($muestra) no puede ser mayor que la población ($poblacion).",
            )
        }
        if (respuestas > muestra) {
            throw ParticipacionInconsistenteException(
                "Las respuestas ($respuestas) no pueden ser mayores que la muestra ($muestra).",
            )
        }
    }

    /**
     * Porcentaje diligenciado (`DESIGN_SGVA.md` §0.1): respuestas / muestra,
     * ausente si la muestra es `0`.
     */
    val porcentajeDiligenciado: Double?
        get() = if (muestra == 0) null else respuestas.toDouble() / muestra

    /**
     * Semáforo de participación derivado de [porcentajeDiligenciado] según
     * los umbrales de [configuracion] (`DESIGN_SGVA.md` §0.1). No es una
     * propiedad almacenada: los umbrales siempre provienen de una fuente de
     * configuración externa, nunca se hardcodean en el dominio.
     */
    fun semaforo(configuracion: ConfiguracionSeguimiento): TipoSemaforo? =
        configuracion.semaforoDe(porcentajeDiligenciado)
}

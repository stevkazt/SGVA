package com.example.sgva.domain

/**
 * Semáforo de participación de un actor en las encuestas de un ciclo (Módulo
 * 5, `DESIGN_SGVA.md` §0.1, V6). Se deriva del porcentaje diligenciado de
 * [ParticipacionActor] mediante los umbrales de [ConfiguracionSeguimiento];
 * no es un criterio de la fuente institucional, sino del investigador.
 */
enum class TipoSemaforo {
    ROJO,
    NARANJA,
    VERDE,
}

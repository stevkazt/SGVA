package com.example.sgva.domain

/**
 * Estamento institucional que responde una [RespuestaEncuesta] de percepción
 * (Módulo 3, `DESIGN_SGVA.md` §2).
 *
 * `EGRESADO` cubre también al egresado del Factor 4 (Sección 0 de
 * `DESIGN_SGVA.md`): no existe una entidad `Egresado` independiente, su
 * percepción se captura únicamente a través de este estamento.
 */
enum class TipoEstamento {
    ESTUDIANTE,
    PROFESOR,
    EGRESADO,
    EMPLEADOR,
}

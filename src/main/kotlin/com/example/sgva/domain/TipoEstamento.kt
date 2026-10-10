package com.example.sgva.domain

/**
 * Estamento institucional que responde una [RespuestaEncuesta] de percepción
 * o un [ResultadoEncuestaPregunta] (Módulo 3 y Módulo 4, `DESIGN_SGVA.md` §2).
 *
 * Desde la V5 cubre los siete actores del proceso institucional de
 * autoevaluación (`DESIGN_SGVA.md` §0): estudiantes, profesores, directivos,
 * administrativos, egresados, empleadores y aliados estratégicos.
 *
 * `EGRESADO` cubre también al egresado del Factor 4 (Sección 0 de
 * `DESIGN_SGVA.md`): no existe una entidad `Egresado` independiente, su
 * percepción se captura únicamente a través de este estamento.
 */
enum class TipoEstamento {
    ESTUDIANTE,
    PROFESOR,
    DIRECTIVO,
    ADMINISTRATIVO,
    EGRESADO,
    EMPLEADOR,
    ALIADO_ESTRATEGICO,
}

package com.example.sgva.domain

/**
 * Vista consolidada de un [CicloAutoevaluacion] con sus fases y actividades
 * (Módulo 5, V6). Es el resultado de `ConsultarCicloAutoevaluacionUseCase`;
 * no se persiste como tal, se compone a partir de las tres entidades.
 */
data class DetalleCiclo(
    val ciclo: CicloAutoevaluacion,
    val fases: List<FaseCiclo>,
    val actividades: List<ActividadCiclo>,
)

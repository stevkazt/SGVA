package com.example.sgva.infrastructure.persistence.jpa

import com.example.sgva.domain.Docente
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.EvaluacionDocumental
import com.example.sgva.domain.ResultadoEncuestaPregunta
import com.example.sgva.domain.RespuestaEncuesta
import com.example.sgva.domain.TipoEstamento

/**
 * Traducción entre las entidades de dominio y las entidades JPA de
 * `infrastructure.persistence.jpa`. Vive enteramente en infraestructura: el
 * dominio no conoce estas clases ni esta conversión (`DESIGN_SGVA.md` §3–§4).
 *
 * Reconstruir la entidad de dominio desde la fila leída reaplica su bloque
 * `init` (formato de periodo, rango de calificación, etc.); es una
 * revalidación redundante pero inofensiva, ya que solo se persisten entidades
 * válidas por construcción.
 */
internal fun Estudiante.aEntidad(): EstudianteEntity =
    EstudianteEntity(
        id = id,
        programa = programa,
        facultad = facultad,
        cohorte = cohorte,
        estado = estado,
        puntajeSaberPro = puntajeSaberPro,
    )

internal fun EstudianteEntity.aDominio(): Estudiante =
    Estudiante(
        id = id,
        programa = programa,
        facultad = facultad,
        cohorte = cohorte,
        estado = estado,
        puntajeSaberPro = puntajeSaberPro,
    )

internal fun Docente.aEntidad(): DocenteEntity =
    DocenteEntity(
        id = id,
        programa = programa,
        facultad = facultad,
        nivelFormacion = nivelFormacion,
        dedicacion = dedicacion,
        periodo = periodo,
    )

internal fun DocenteEntity.aDominio(): Docente =
    Docente(
        id = id,
        programa = programa,
        facultad = facultad,
        nivelFormacion = nivelFormacion,
        dedicacion = dedicacion,
        periodo = periodo,
    )

internal fun RespuestaEncuesta.aEntidad(): RespuestaEncuestaEntity =
    RespuestaEncuestaEntity(
        id = id,
        estamento = estamento,
        factor = factor,
        calificacion = calificacion,
        periodo = periodo,
    )

internal fun RespuestaEncuestaEntity.aDominio(): RespuestaEncuesta =
    RespuestaEncuesta(
        id = id,
        estamento = estamento,
        factor = factor,
        calificacion = calificacion,
        periodo = periodo,
    )

/** Clave natural de [ResultadoEncuestaPregunta] usada como `id` técnico de [ResultadoEncuestaPreguntaEntity]. */
internal fun claveResultadoEncuestaPregunta(programa: String, periodo: String, codigoPregunta: String): String =
    "$programa|$periodo|$codigoPregunta"

internal fun ResultadoEncuestaPregunta.aEntidad(): ResultadoEncuestaPreguntaEntity =
    ResultadoEncuestaPreguntaEntity(
        id = claveResultadoEncuestaPregunta(programa, periodo, codigoPregunta),
        programa = programa,
        periodo = periodo,
        codigoPregunta = codigoPregunta,
        caracteristica = caracteristica,
        estamentos = estamentos.joinToString(",") { it.name },
        promedio = promedio,
    )

internal fun ResultadoEncuestaPreguntaEntity.aDominio(): ResultadoEncuestaPregunta =
    ResultadoEncuestaPregunta(
        programa = programa,
        periodo = periodo,
        codigoPregunta = codigoPregunta,
        caracteristica = caracteristica,
        estamentos = estamentos.split(",").filter(String::isNotBlank).map { TipoEstamento.valueOf(it) }.toSet(),
        promedio = promedio,
    )

/** Clave natural de [EvaluacionDocumental] usada como `id` técnico de [EvaluacionDocumentalEntity]. */
internal fun claveEvaluacionDocumental(programa: String, periodo: String, caracteristica: String): String =
    "$programa|$periodo|$caracteristica"

internal fun EvaluacionDocumental.aEntidad(): EvaluacionDocumentalEntity =
    EvaluacionDocumentalEntity(
        id = claveEvaluacionDocumental(programa, periodo, caracteristica),
        programa = programa,
        caracteristica = caracteristica,
        periodo = periodo,
        calificacion = calificacion,
        justificacion = justificacion,
        sugerenciasMejora = sugerenciasMejora,
    )

internal fun EvaluacionDocumentalEntity.aDominio(): EvaluacionDocumental =
    EvaluacionDocumental(
        programa = programa,
        caracteristica = caracteristica,
        periodo = periodo,
        calificacion = calificacion,
        justificacion = justificacion,
        sugerenciasMejora = sugerenciasMejora,
    )

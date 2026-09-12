package com.example.sgva.infrastructure.persistence.jpa

import com.example.sgva.domain.Docente
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.RespuestaEncuesta

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
        facultad = facultad,
        nivelFormacion = nivelFormacion,
        dedicacion = dedicacion,
        periodo = periodo,
    )

internal fun DocenteEntity.aDominio(): Docente =
    Docente(
        id = id,
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

package com.example.sgva.infrastructure.persistence.jpa

import com.example.sgva.domain.ActividadCiclo
import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.Docente
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.EvaluacionDocumental
import com.example.sgva.domain.FaseCiclo
import com.example.sgva.domain.GrupoEvaluador
import com.example.sgva.domain.ParticipacionActor
import com.example.sgva.domain.PreevaluacionEvaluador
import com.example.sgva.domain.ProyectoMejoramiento
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

// ===== Módulo 5 — Seguimiento del proceso institucional (V6) =====

internal fun CicloAutoevaluacion.aEntidad(): CicloAutoevaluacionEntity =
    CicloAutoevaluacionEntity(id = id, programa = programa, periodo = periodo)

internal fun CicloAutoevaluacionEntity.aDominio(): CicloAutoevaluacion =
    CicloAutoevaluacion(id = id, programa = programa, periodo = periodo)

/** Clave natural de [FaseCiclo] usada como `id` técnico de [FaseCicloEntity]. */
internal fun claveFaseCiclo(ciclo: String, numero: Int): String = "$ciclo|$numero"

internal fun FaseCiclo.aEntidad(): FaseCicloEntity =
    FaseCicloEntity(id = claveFaseCiclo(ciclo, numero), ciclo = ciclo, numero = numero, nombre = nombre)

internal fun FaseCicloEntity.aDominio(): FaseCiclo = FaseCiclo(ciclo = ciclo, numero = numero, nombre = nombre)

/** Clave natural de [ActividadCiclo] usada como `id` técnico de [ActividadCicloEntity]. */
internal fun claveActividadCiclo(ciclo: String, idActividad: String): String = "$ciclo|$idActividad"

internal fun ActividadCiclo.aEntidad(): ActividadCicloEntity =
    ActividadCicloEntity(
        id = claveActividadCiclo(ciclo, id),
        idActividad = id,
        ciclo = ciclo,
        fase = fase,
        descripcion = descripcion,
        responsable = responsable,
        fechaInicioPlanificada = fechaInicioPlanificada,
        fechaFinPlanificada = fechaFinPlanificada,
        estado = estado,
    )

internal fun ActividadCicloEntity.aDominio(): ActividadCiclo =
    ActividadCiclo(
        id = idActividad,
        ciclo = ciclo,
        fase = fase,
        descripcion = descripcion,
        responsable = responsable,
        fechaInicioPlanificada = fechaInicioPlanificada,
        fechaFinPlanificada = fechaFinPlanificada,
        estado = estado,
    )

/** Clave natural de [GrupoEvaluador] usada como `id` técnico de [GrupoEvaluadorEntity]. */
internal fun claveGrupoEvaluador(ciclo: String, taller: Int): String = "$ciclo|$taller"

internal fun GrupoEvaluador.aEntidad(): GrupoEvaluadorEntity =
    GrupoEvaluadorEntity(
        id = claveGrupoEvaluador(ciclo, taller),
        ciclo = ciclo,
        factores = factores.joinToString(","),
        taller = taller,
        fechaTaller = fechaTaller,
    )

internal fun GrupoEvaluadorEntity.aDominio(): GrupoEvaluador =
    GrupoEvaluador(
        ciclo = ciclo,
        factores = factores.split(",").filter(String::isNotBlank).toSet(),
        taller = taller,
        fechaTaller = fechaTaller,
    )

/** Clave natural de [PreevaluacionEvaluador] usada como `id` técnico de [PreevaluacionEvaluadorEntity]. */
internal fun clavePreevaluacion(ciclo: String, caracteristica: String, evaluador: String): String =
    "$ciclo|$caracteristica|$evaluador"

internal fun PreevaluacionEvaluador.aEntidad(): PreevaluacionEvaluadorEntity =
    PreevaluacionEvaluadorEntity(
        id = clavePreevaluacion(ciclo, caracteristica, evaluador),
        ciclo = ciclo,
        caracteristica = caracteristica,
        evaluador = evaluador,
        calificacion = calificacion,
        justificacion = justificacion,
        fortaleza = fortaleza,
        oportunidadMejora = oportunidadMejora,
    )

internal fun PreevaluacionEvaluadorEntity.aDominio(): PreevaluacionEvaluador =
    PreevaluacionEvaluador(
        ciclo = ciclo,
        caracteristica = caracteristica,
        evaluador = evaluador,
        calificacion = calificacion,
        justificacion = justificacion,
        fortaleza = fortaleza,
        oportunidadMejora = oportunidadMejora,
    )

/** Clave natural de [ParticipacionActor] usada como `id` técnico de [ParticipacionActorEntity]. */
internal fun claveParticipacionActor(ciclo: String, actor: String): String = "$ciclo|$actor"

internal fun ParticipacionActor.aEntidad(): ParticipacionActorEntity =
    ParticipacionActorEntity(
        id = claveParticipacionActor(ciclo, actor),
        ciclo = ciclo,
        actor = actor,
        poblacion = poblacion,
        muestra = muestra,
        respuestas = respuestas,
    )

internal fun ParticipacionActorEntity.aDominio(): ParticipacionActor =
    ParticipacionActor(ciclo = ciclo, actor = actor, poblacion = poblacion, muestra = muestra, respuestas = respuestas)

/** Separador de listas de texto libre que sí podrían contener comas (`DESIGN_SGVA.md` §1, V6). */
private const val SEPARADOR_LISTA_TEXTO = "\u001E"

internal fun ProyectoMejoramiento.aEntidad(): ProyectoMejoramientoEntity =
    ProyectoMejoramientoEntity(
        id = id,
        programa = programa,
        nombre = nombre,
        aspectosMejora = aspectosMejora.joinToString(SEPARADOR_LISTA_TEXTO),
        origen = origen,
        objetivo = objetivo,
        actividades = actividades.joinToString(SEPARADOR_LISTA_TEXTO),
        factores = factores.joinToString(","),
        estado = estado,
    )

internal fun ProyectoMejoramientoEntity.aDominio(): ProyectoMejoramiento =
    ProyectoMejoramiento(
        id = id,
        programa = programa,
        nombre = nombre,
        aspectosMejora = aspectosMejora.split(SEPARADOR_LISTA_TEXTO).filter(String::isNotBlank),
        origen = origen,
        objetivo = objetivo,
        actividades = actividades.split(SEPARADOR_LISTA_TEXTO).filter(String::isNotBlank),
        factores = factores.split(",").filter(String::isNotBlank).toSet(),
        estado = estado,
    )

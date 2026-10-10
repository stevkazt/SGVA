package com.example.sgva.infrastructure.persistence.jpa

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

/**
 * Entidad JPA de persistencia para
 * [com.example.sgva.domain.ResultadoEncuestaPregunta] (adaptador de salida
 * sobre PostgreSQL, Módulo 4). Ver [EstudianteEntity] para el razonamiento de
 * `@field:` y `creadoEn`.
 *
 * A diferencia de [EstudianteEntity] o [DocenteEntity], el dominio no define
 * un `id` propio para esta entidad: su clave natural es la combinación de
 * `programa`, `periodo` y `codigoPregunta` (`DESIGN_SGVA.md` §1). El `id` de
 * esta tabla es un detalle de infraestructura —la concatenación de esa clave—
 * que el dominio no conoce.
 *
 * `estamentos` se almacena como texto separado por comas (ej.:
 * `"ESTUDIANTE,PROFESOR"`); la conversión desde/hacia el `Set<TipoEstamento>`
 * del dominio vive en `MapeadorPersistencia.kt`.
 */
@Entity
@Table(name = "resultados_encuesta_pregunta")
internal class ResultadoEncuestaPreguntaEntity(
    @field:Id
    val id: String,
    val programa: String,
    val periodo: String,
    val codigoPregunta: String,
    val caracteristica: String,
    val estamentos: String,
    val promedio: Double?,
    val creadoEn: Instant = Instant.now(),
)

internal interface ResultadoEncuestaPreguntaJpaRepository : JpaRepository<ResultadoEncuestaPreguntaEntity, String> {
    fun findAllByOrderByCreadoEnAsc(): List<ResultadoEncuestaPreguntaEntity>
    fun findByProgramaAndPeriodoAndCodigoPregunta(
        programa: String,
        periodo: String,
        codigoPregunta: String,
    ): ResultadoEncuestaPreguntaEntity?
}

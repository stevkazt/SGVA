package com.example.sgva.infrastructure.persistence.jpa

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

/**
 * Entidad JPA de persistencia para
 * [com.example.sgva.domain.EvaluacionDocumental] (adaptador de salida sobre
 * PostgreSQL, Módulo 4). Ver [EstudianteEntity] para el razonamiento de
 * `@field:` y `creadoEn`.
 *
 * A diferencia de [EstudianteEntity] o [DocenteEntity], el dominio no define
 * un `id` propio para esta entidad: su clave natural es la combinación de
 * `programa`, `periodo` y `caracteristica` (`DESIGN_SGVA.md` §1). El `id` de
 * esta tabla es un detalle de infraestructura —la concatenación de esa
 * clave— que el dominio no conoce.
 *
 * `valoracionCualitativa` no se persiste: se deriva siempre de `calificacion`
 * en el dominio ([com.example.sgva.domain.EvaluacionDocumental]), nunca se
 * almacena por separado.
 */
@Entity
@Table(name = "evaluaciones_documentales")
internal class EvaluacionDocumentalEntity(
    @field:Id
    val id: String,
    val programa: String,
    val caracteristica: String,
    val periodo: String,
    val calificacion: Double,
    val justificacion: String?,
    val sugerenciasMejora: String?,
    val creadoEn: Instant = Instant.now(),
)

internal interface EvaluacionDocumentalJpaRepository : JpaRepository<EvaluacionDocumentalEntity, String> {
    fun findAllByOrderByCreadoEnAsc(): List<EvaluacionDocumentalEntity>
    fun findByProgramaAndPeriodoAndCaracteristica(
        programa: String,
        periodo: String,
        caracteristica: String,
    ): EvaluacionDocumentalEntity?
}

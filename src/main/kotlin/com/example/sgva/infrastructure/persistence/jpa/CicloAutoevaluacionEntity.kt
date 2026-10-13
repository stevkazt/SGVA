package com.example.sgva.infrastructure.persistence.jpa

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

/**
 * Entidad JPA de persistencia para
 * [com.example.sgva.domain.CicloAutoevaluacion] (adaptador de salida sobre
 * PostgreSQL, Módulo 5, V6). Ver [EstudianteEntity] para el razonamiento de
 * `@field:` y `creadoEn`.
 */
@Entity
@Table(name = "ciclos_autoevaluacion")
internal class CicloAutoevaluacionEntity(
    @field:Id
    val id: String,
    val programa: String,
    val periodo: String,
    val creadoEn: Instant = Instant.now(),
)

internal interface CicloAutoevaluacionJpaRepository : JpaRepository<CicloAutoevaluacionEntity, String> {
    fun findAllByOrderByCreadoEnAsc(): List<CicloAutoevaluacionEntity>
    fun findByProgramaAndPeriodo(programa: String, periodo: String): CicloAutoevaluacionEntity?
}

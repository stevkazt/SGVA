package com.example.sgva.infrastructure.persistence.jpa

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

/**
 * Entidad JPA de persistencia para
 * [com.example.sgva.domain.PreevaluacionEvaluador] (adaptador de salida
 * sobre PostgreSQL, Módulo 5, V6). Su clave natural es la combinación de
 * `ciclo`, `caracteristica` y `evaluador`; el `id` técnico es su
 * concatenación.
 */
@Entity
@Table(name = "preevaluaciones")
internal class PreevaluacionEvaluadorEntity(
    @field:Id
    val id: String,
    val ciclo: String,
    val caracteristica: String,
    val evaluador: String,
    val calificacion: Double,
    val justificacion: String?,
    val fortaleza: String?,
    val oportunidadMejora: String?,
    val creadoEn: Instant = Instant.now(),
)

internal interface PreevaluacionEvaluadorJpaRepository : JpaRepository<PreevaluacionEvaluadorEntity, String> {
    fun findByCicloOrderByCreadoEnAsc(ciclo: String): List<PreevaluacionEvaluadorEntity>
    fun findByCicloAndCaracteristicaAndEvaluador(
        ciclo: String,
        caracteristica: String,
        evaluador: String,
    ): PreevaluacionEvaluadorEntity?
}

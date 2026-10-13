package com.example.sgva.infrastructure.persistence.jpa

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.time.LocalDate

/**
 * Entidad JPA de persistencia para [com.example.sgva.domain.GrupoEvaluador]
 * (adaptador de salida sobre PostgreSQL, Módulo 5, V6). Su clave natural es
 * la combinación de `ciclo` y `taller`; `factores` se serializa como texto
 * separado por comas, igual que `estamentos` en [ResultadoEncuestaPreguntaEntity].
 */
@Entity
@Table(name = "grupos_evaluadores")
internal class GrupoEvaluadorEntity(
    @field:Id
    val id: String,
    val ciclo: String,
    val factores: String,
    val taller: Int,
    val fechaTaller: LocalDate?,
    val creadoEn: Instant = Instant.now(),
)

internal interface GrupoEvaluadorJpaRepository : JpaRepository<GrupoEvaluadorEntity, String> {
    fun findByCicloOrderByCreadoEnAsc(ciclo: String): List<GrupoEvaluadorEntity>
    fun findByCicloAndTaller(ciclo: String, taller: Int): GrupoEvaluadorEntity?
}

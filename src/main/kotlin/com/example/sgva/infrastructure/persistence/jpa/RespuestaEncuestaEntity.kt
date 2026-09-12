package com.example.sgva.infrastructure.persistence.jpa

import com.example.sgva.domain.TipoEstamento
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

/**
 * Entidad JPA de persistencia para [com.example.sgva.domain.RespuestaEncuesta]
 * (adaptador de salida sobre PostgreSQL, Módulo 3). Ver [EstudianteEntity]
 * para el razonamiento de `@field:` y `creadoEn`.
 */
@Entity
@Table(name = "respuestas_encuesta")
internal class RespuestaEncuestaEntity(
    @field:Id
    val id: String,
    @field:Enumerated(EnumType.STRING)
    val estamento: TipoEstamento,
    val factor: String,
    val calificacion: Int,
    val periodo: String,
    val creadoEn: Instant = Instant.now(),
)

internal interface RespuestaEncuestaJpaRepository : JpaRepository<RespuestaEncuestaEntity, String> {
    fun findAllByOrderByCreadoEnAsc(): List<RespuestaEncuestaEntity>
}

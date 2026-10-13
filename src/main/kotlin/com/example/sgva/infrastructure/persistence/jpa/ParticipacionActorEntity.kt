package com.example.sgva.infrastructure.persistence.jpa

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

/**
 * Entidad JPA de persistencia para [com.example.sgva.domain.ParticipacionActor]
 * (adaptador de salida sobre PostgreSQL, Módulo 5, V6). Su clave natural es
 * la combinación de `ciclo` y `actor`; el `id` técnico es su concatenación.
 * `porcentajeDiligenciado` y el semáforo no se persisten: se derivan siempre
 * en el dominio, nunca se almacenan por separado.
 */
@Entity
@Table(name = "participacion_actores")
internal class ParticipacionActorEntity(
    @field:Id
    val id: String,
    val ciclo: String,
    val actor: String,
    val poblacion: Int,
    val muestra: Int,
    val respuestas: Int,
    val creadoEn: Instant = Instant.now(),
)

internal interface ParticipacionActorJpaRepository : JpaRepository<ParticipacionActorEntity, String> {
    fun findByCicloOrderByCreadoEnAsc(ciclo: String): List<ParticipacionActorEntity>
    fun findByCicloAndActor(ciclo: String, actor: String): ParticipacionActorEntity?
}

package com.example.sgva.infrastructure.persistence

import com.example.sgva.domain.Caracteristica
import com.example.sgva.domain.ConfiguracionPonderaciones
import com.example.sgva.domain.ConfiguracionPonderacionesRepository
import com.example.sgva.infrastructure.persistence.jpa.PonderacionEntity
import com.example.sgva.infrastructure.persistence.jpa.PonderacionJpaRepository
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * Adaptador de salida de [ConfiguracionPonderacionesRepository] sobre
 * PostgreSQL (`DESIGN_SGVA.md` §4). Guarda la configuración como filas con la
 * misma forma del archivo de ponderaciones y la reconstruye con el
 * constructor del dominio, de modo que se vuelven a aplicar sus validaciones.
 */
@Repository
internal class ConfiguracionPonderacionesRepositoryJpa(
    private val ponderaciones: PonderacionJpaRepository,
    private val entityManager: EntityManager,
) : ConfiguracionPonderacionesRepository {

    @Transactional(readOnly = true)
    override fun obtener(): ConfiguracionPonderaciones? {
        val filas = ponderaciones.findAllByOrderByOrdenAsc()
        if (filas.isEmpty()) return null

        return ConfiguracionPonderaciones(
            caracteristicas = filas.filter { it.tipo == TIPO_CARACTERISTICA }.map {
                Caracteristica(
                    id = it.clave,
                    nombre = it.nombre.orEmpty(),
                    factor = it.factor.orEmpty(),
                    ponderacion = it.ponderacion,
                )
            },
            pesosFactores = filas.filter { it.tipo == TIPO_FACTOR }.associate { it.clave to it.ponderacion },
            pesoEncuestas = pesoDeMezcla(filas, CLAVE_ENCUESTAS),
            pesoDocumental = pesoDeMezcla(filas, CLAVE_DOCUMENTAL),
        )
    }

    @Transactional
    override fun reemplazar(configuracion: ConfiguracionPonderaciones): ConfiguracionPonderaciones {
        var orden = 0
        val filas = buildList {
            configuracion.pesosFactores.forEach { (factor, peso) ->
                add(PonderacionEntity("$TIPO_FACTOR:$factor", TIPO_FACTOR, factor, null, null, peso, orden++))
            }
            configuracion.caracteristicas.forEach {
                add(
                    PonderacionEntity(
                        "$TIPO_CARACTERISTICA:${it.id}", TIPO_CARACTERISTICA, it.id, it.factor, it.nombre,
                        it.ponderacion, orden++,
                    ),
                )
            }
            add(PonderacionEntity("$TIPO_MEZCLA:$CLAVE_ENCUESTAS", TIPO_MEZCLA, CLAVE_ENCUESTAS, null, null, configuracion.pesoEncuestas, orden++))
            add(PonderacionEntity("$TIPO_MEZCLA:$CLAVE_DOCUMENTAL", TIPO_MEZCLA, CLAVE_DOCUMENTAL, null, null, configuracion.pesoDocumental, orden))
        }
        // El borrado en lote no actualiza el contexto de persistencia: se limpia para poder
        // volver a insertar filas con las mismas claves dentro de la misma transacción.
        ponderaciones.deleteAllInBatch()
        entityManager.flush()
        entityManager.clear()
        ponderaciones.saveAll(filas)
        return configuracion
    }

    private fun pesoDeMezcla(filas: List<PonderacionEntity>, clave: String): Double =
        filas.first { it.tipo == TIPO_MEZCLA && it.clave == clave }.ponderacion

    private companion object {
        const val TIPO_FACTOR = "FACTOR"
        const val TIPO_CARACTERISTICA = "CARACTERISTICA"
        const val TIPO_MEZCLA = "MEZCLA"
        const val CLAVE_ENCUESTAS = "ENCUESTAS"
        const val CLAVE_DOCUMENTAL = "DOCUMENTAL"
    }
}

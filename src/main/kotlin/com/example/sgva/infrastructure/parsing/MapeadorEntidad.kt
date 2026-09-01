package com.example.sgva.infrastructure.parsing

import com.example.sgva.domain.Docente
import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.NivelFormacion
import com.example.sgva.domain.TipoDedicacion

/**
 * Convierte una fila del archivo (columna -> valor en bruto) en una entidad de
 * dominio, aplicando la normalización de [NormalizadorDatos]. Es independiente
 * del formato físico del archivo (CSV o Excel).
 */
internal interface MapeadorEntidad<out T> {

    /** Columnas cuya presencia en el encabezado es obligatoria (`DESIGN_SGVA.md` §5.1). */
    val columnasObligatorias: Set<String>

    /**
     * @throws IllegalArgumentException si algún valor no puede normalizarse a un
     *   dato válido (lo recoge [ParserDocumentalBase] para señalar la fila).
     */
    fun mapear(fila: Map<String, String>): T
}

internal object MapeadorEstudiante : MapeadorEntidad<Estudiante> {

    override val columnasObligatorias =
        setOf("id", "programa", "facultad", "cohorte", "estado")

    override fun mapear(fila: Map<String, String>): Estudiante = Estudiante(
        id = NormalizadorDatos.texto(fila["id"]),
        programa = NormalizadorDatos.texto(fila["programa"]),
        facultad = NormalizadorDatos.texto(fila["facultad"]),
        cohorte = NormalizadorDatos.periodoAcademico(fila["cohorte"]),
        estado = aEnum<EstadoEstudiante>(fila["estado"], "estado"),
        puntajeSaberPro = NormalizadorDatos.enteroOpcional(fila["puntajeSaberPro"]),
    )
}

internal object MapeadorDocente : MapeadorEntidad<Docente> {

    override val columnasObligatorias =
        setOf("id", "facultad", "nivelFormacion", "dedicacion", "periodo")

    override fun mapear(fila: Map<String, String>): Docente = Docente(
        id = NormalizadorDatos.texto(fila["id"]),
        facultad = NormalizadorDatos.texto(fila["facultad"]),
        nivelFormacion = aEnum<NivelFormacion>(fila["nivelFormacion"], "nivelFormacion"),
        dedicacion = aEnum<TipoDedicacion>(fila["dedicacion"], "dedicacion"),
        periodo = NormalizadorDatos.periodoAcademico(fila["periodo"]),
    )
}

private inline fun <reified E : Enum<E>> aEnum(valor: String?, campo: String): E {
    val clave = NormalizadorDatos.claveEnum(valor)
    val valores = enumValues<E>()
    return valores.firstOrNull { it.name == clave }
        ?: throw IllegalArgumentException(
            "el valor '${NormalizadorDatos.texto(valor)}' no corresponde a ningún $campo válido " +
                "(${valores.joinToString(", ") { it.name }})",
        )
}

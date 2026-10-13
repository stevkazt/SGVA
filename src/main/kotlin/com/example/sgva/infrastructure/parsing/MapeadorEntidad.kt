package com.example.sgva.infrastructure.parsing

import com.example.sgva.domain.Docente
import com.example.sgva.domain.EstadoEstudiante
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.EvaluacionDocumental
import com.example.sgva.domain.NivelFormacion
import com.example.sgva.domain.ParticipacionActor
import com.example.sgva.domain.PreevaluacionEvaluador
import com.example.sgva.domain.ResultadoEncuestaPregunta
import com.example.sgva.domain.RespuestaEncuesta
import com.example.sgva.domain.TipoDedicacion
import com.example.sgva.domain.TipoEstamento

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
        setOf("id", "programa", "facultad", "nivelFormacion", "dedicacion", "periodo")

    override fun mapear(fila: Map<String, String>): Docente = Docente(
        id = NormalizadorDatos.texto(fila["id"]),
        programa = NormalizadorDatos.texto(fila["programa"]),
        facultad = NormalizadorDatos.texto(fila["facultad"]),
        nivelFormacion = aEnum<NivelFormacion>(fila["nivelFormacion"], "nivelFormacion"),
        dedicacion = aEnum<TipoDedicacion>(fila["dedicacion"], "dedicacion"),
        periodo = NormalizadorDatos.periodoAcademico(fila["periodo"]),
    )
}

internal object MapeadorRespuestaEncuesta : MapeadorEntidad<RespuestaEncuesta> {

    override val columnasObligatorias =
        setOf("id", "estamento", "factor", "calificacion", "periodo")

    override fun mapear(fila: Map<String, String>): RespuestaEncuesta = RespuestaEncuesta(
        id = NormalizadorDatos.texto(fila["id"]),
        estamento = aEnum<TipoEstamento>(fila["estamento"], "estamento"),
        factor = NormalizadorDatos.texto(fila["factor"]),
        calificacion = NormalizadorDatos.entero(fila["calificacion"]),
        periodo = NormalizadorDatos.periodoAcademico(fila["periodo"]),
    )
}

internal object MapeadorResultadoEncuestaPregunta : MapeadorEntidad<ResultadoEncuestaPregunta> {

    override val columnasObligatorias =
        setOf("programa", "periodo", "codigoPregunta", "caracteristica", "estamentos", "promedio")

    override fun mapear(fila: Map<String, String>): ResultadoEncuestaPregunta = ResultadoEncuestaPregunta(
        programa = NormalizadorDatos.texto(fila["programa"]),
        periodo = NormalizadorDatos.periodoAcademico(fila["periodo"]),
        codigoPregunta = NormalizadorDatos.texto(fila["codigoPregunta"]),
        caracteristica = NormalizadorDatos.texto(fila["caracteristica"]),
        estamentos = aConjuntoEnum<TipoEstamento>(fila["estamentos"], "estamentos"),
        promedio = NormalizadorDatos.decimalOpcional(fila["promedio"]),
    )
}

internal object MapeadorEvaluacionDocumental : MapeadorEntidad<EvaluacionDocumental> {

    override val columnasObligatorias =
        setOf("programa", "caracteristica", "periodo", "calificacion")

    override fun mapear(fila: Map<String, String>): EvaluacionDocumental = EvaluacionDocumental(
        programa = NormalizadorDatos.texto(fila["programa"]),
        caracteristica = NormalizadorDatos.texto(fila["caracteristica"]),
        periodo = NormalizadorDatos.periodoAcademico(fila["periodo"]),
        calificacion = NormalizadorDatos.decimal(fila["calificacion"]),
        justificacion = NormalizadorDatos.textoOpcional(fila["justificacion"]),
        sugerenciasMejora = NormalizadorDatos.textoOpcional(fila["sugerenciasMejora"]),
    )
}

/** Columnas obligatorias de la carga masiva de preevaluaciones (`DESIGN_SGVA.md` §5.5, V6). */
internal object MapeadorPreevaluacion : MapeadorEntidad<PreevaluacionEvaluador> {

    override val columnasObligatorias =
        setOf("ciclo", "caracteristica", "evaluador", "calificacion")

    override fun mapear(fila: Map<String, String>): PreevaluacionEvaluador = PreevaluacionEvaluador(
        ciclo = NormalizadorDatos.texto(fila["ciclo"]),
        caracteristica = NormalizadorDatos.texto(fila["caracteristica"]),
        evaluador = NormalizadorDatos.texto(fila["evaluador"]),
        calificacion = NormalizadorDatos.decimal(fila["calificacion"]),
        justificacion = NormalizadorDatos.textoOpcional(fila["justificacion"]),
        fortaleza = NormalizadorDatos.textoOpcional(fila["fortaleza"]),
        oportunidadMejora = NormalizadorDatos.textoOpcional(fila["oportunidadMejora"]),
    )
}

/** Columnas obligatorias de la carga masiva de participación de actores (`DESIGN_SGVA.md` §5.5, V6). */
internal object MapeadorParticipacionActor : MapeadorEntidad<ParticipacionActor> {

    override val columnasObligatorias =
        setOf("ciclo", "actor", "poblacion", "muestra", "respuestas")

    override fun mapear(fila: Map<String, String>): ParticipacionActor = ParticipacionActor(
        ciclo = NormalizadorDatos.texto(fila["ciclo"]),
        actor = NormalizadorDatos.texto(fila["actor"]),
        poblacion = NormalizadorDatos.entero(fila["poblacion"]),
        muestra = NormalizadorDatos.entero(fila["muestra"]),
        respuestas = NormalizadorDatos.entero(fila["respuestas"]),
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

private inline fun <reified E : Enum<E>> aConjuntoEnum(valor: String?, campo: String): Set<E> {
    val claves = NormalizadorDatos.listaClavesEnum(valor)
    val valores = enumValues<E>()

    return claves.map { clave ->
        valores.firstOrNull { it.name == clave }
            ?: throw IllegalArgumentException(
                "el valor '$clave' no corresponde a ningún $campo válido " +
                    "(${valores.joinToString(", ") { it.name }})",
            )
    }.toSet()
}

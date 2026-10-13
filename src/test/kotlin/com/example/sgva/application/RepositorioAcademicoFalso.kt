package com.example.sgva.application

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.ActividadCiclo
import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.Docente
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.EvaluacionDocumental
import com.example.sgva.domain.FaseCiclo
import com.example.sgva.domain.GrupoEvaluador
import com.example.sgva.domain.ParticipacionActor
import com.example.sgva.domain.PreevaluacionEvaluador
import com.example.sgva.domain.ProyectoMejoramiento
import com.example.sgva.domain.ResultadoEncuestaPregunta
import com.example.sgva.domain.RespuestaEncuesta

/**
 * Doble de prueba en memoria del puerto [AcademicDataRepository]. Permite
 * ejercitar los casos de uso sin depender del adaptador de infraestructura.
 * Pública (no `internal`) para poder exponerse como bean de Spring en las
 * pruebas de integración de `infrastructure.web` (`ConfiguracionCasosDeUsoDePrueba`).
 */
class RepositorioAcademicoFalso : AcademicDataRepository {

    private val estudiantes = LinkedHashMap<String, Estudiante>()
    private val docentes = LinkedHashMap<String, Docente>()
    private val respuestasEncuesta = LinkedHashMap<String, RespuestaEncuesta>()
    private val resultadosEncuestaPregunta = LinkedHashMap<String, ResultadoEncuestaPregunta>()
    private val evaluacionesDocumentales = LinkedHashMap<String, EvaluacionDocumental>()

    override fun guardarEstudiante(estudiante: Estudiante): Estudiante {
        estudiantes[estudiante.id] = estudiante
        return estudiante
    }

    override fun buscarEstudiantePorId(id: String): Estudiante? = estudiantes[id]

    override fun listarEstudiantes(): List<Estudiante> = estudiantes.values.toList()

    override fun guardarDocente(docente: Docente): Docente {
        docentes[docente.id] = docente
        return docente
    }

    override fun buscarDocentePorId(id: String): Docente? = docentes[id]

    override fun listarDocentes(): List<Docente> = docentes.values.toList()

    override fun guardarRespuestaEncuesta(respuesta: RespuestaEncuesta): RespuestaEncuesta {
        respuestasEncuesta[respuesta.id] = respuesta
        return respuesta
    }

    override fun buscarRespuestaEncuestaPorId(id: String): RespuestaEncuesta? = respuestasEncuesta[id]

    override fun listarRespuestasEncuesta(): List<RespuestaEncuesta> = respuestasEncuesta.values.toList()

    override fun guardarResultadoEncuestaPregunta(resultado: ResultadoEncuestaPregunta): ResultadoEncuestaPregunta {
        resultadosEncuestaPregunta[claveDe(resultado.programa, resultado.periodo, resultado.codigoPregunta)] =
            resultado
        return resultado
    }

    override fun buscarResultadoEncuestaPregunta(
        programa: String,
        periodo: String,
        codigoPregunta: String,
    ): ResultadoEncuestaPregunta? = resultadosEncuestaPregunta[claveDe(programa, periodo, codigoPregunta)]

    override fun listarResultadosEncuestaPregunta(): List<ResultadoEncuestaPregunta> =
        resultadosEncuestaPregunta.values.toList()

    private fun claveDe(programa: String, periodo: String, codigoPregunta: String): String =
        "$programa|$periodo|$codigoPregunta"

    override fun guardarEvaluacionDocumental(evaluacion: EvaluacionDocumental): EvaluacionDocumental {
        evaluacionesDocumentales[claveDe(evaluacion.programa, evaluacion.periodo, evaluacion.caracteristica)] =
            evaluacion
        return evaluacion
    }

    override fun buscarEvaluacionDocumental(
        programa: String,
        periodo: String,
        caracteristica: String,
    ): EvaluacionDocumental? = evaluacionesDocumentales[claveDe(programa, periodo, caracteristica)]

    override fun listarEvaluacionesDocumentales(): List<EvaluacionDocumental> =
        evaluacionesDocumentales.values.toList()

    // ===== Módulo 5 — Seguimiento del proceso institucional (V6) =====

    private val ciclos = LinkedHashMap<String, CicloAutoevaluacion>()
    private val fases = LinkedHashMap<String, FaseCiclo>()
    private val actividades = LinkedHashMap<String, ActividadCiclo>()
    private val gruposEvaluadores = LinkedHashMap<String, GrupoEvaluador>()
    private val preevaluaciones = LinkedHashMap<String, PreevaluacionEvaluador>()
    private val participaciones = LinkedHashMap<String, ParticipacionActor>()
    private val proyectos = LinkedHashMap<String, ProyectoMejoramiento>()

    override fun guardarCiclo(ciclo: CicloAutoevaluacion): CicloAutoevaluacion {
        ciclos[ciclo.id] = ciclo
        return ciclo
    }

    override fun buscarCicloPorId(id: String): CicloAutoevaluacion? = ciclos[id]

    override fun buscarCicloPorProgramaYPeriodo(programa: String, periodo: String): CicloAutoevaluacion? =
        ciclos.values.firstOrNull { it.programa == programa && it.periodo == periodo }

    override fun listarCiclos(): List<CicloAutoevaluacion> = ciclos.values.toList()

    override fun guardarFase(fase: FaseCiclo): FaseCiclo {
        fases["${fase.ciclo}|${fase.numero}"] = fase
        return fase
    }

    override fun listarFasesPorCiclo(ciclo: String): List<FaseCiclo> = fases.values.filter { it.ciclo == ciclo }

    override fun guardarActividad(actividad: ActividadCiclo): ActividadCiclo {
        actividades["${actividad.ciclo}|${actividad.id}"] = actividad
        return actividad
    }

    override fun listarActividadesPorCiclo(ciclo: String): List<ActividadCiclo> =
        actividades.values.filter { it.ciclo == ciclo }

    override fun guardarGrupoEvaluador(grupo: GrupoEvaluador): GrupoEvaluador {
        gruposEvaluadores["${grupo.ciclo}|${grupo.taller}"] = grupo
        return grupo
    }

    override fun buscarGrupoEvaluador(ciclo: String, taller: Int): GrupoEvaluador? =
        gruposEvaluadores["$ciclo|$taller"]

    override fun listarGruposEvaluadorPorCiclo(ciclo: String): List<GrupoEvaluador> =
        gruposEvaluadores.values.filter { it.ciclo == ciclo }

    override fun guardarPreevaluacion(preevaluacion: PreevaluacionEvaluador): PreevaluacionEvaluador {
        preevaluaciones["${preevaluacion.ciclo}|${preevaluacion.caracteristica}|${preevaluacion.evaluador}"] =
            preevaluacion
        return preevaluacion
    }

    override fun buscarPreevaluacion(ciclo: String, caracteristica: String, evaluador: String): PreevaluacionEvaluador? =
        preevaluaciones["$ciclo|$caracteristica|$evaluador"]

    override fun listarPreevaluacionesPorCiclo(ciclo: String): List<PreevaluacionEvaluador> =
        preevaluaciones.values.filter { it.ciclo == ciclo }

    override fun guardarParticipacion(participacion: ParticipacionActor): ParticipacionActor {
        participaciones["${participacion.ciclo}|${participacion.actor}"] = participacion
        return participacion
    }

    override fun buscarParticipacion(ciclo: String, actor: String): ParticipacionActor? =
        participaciones["$ciclo|$actor"]

    override fun listarParticipacionPorCiclo(ciclo: String): List<ParticipacionActor> =
        participaciones.values.filter { it.ciclo == ciclo }

    override fun guardarProyecto(proyecto: ProyectoMejoramiento): ProyectoMejoramiento {
        proyectos[proyecto.id] = proyecto
        return proyecto
    }

    override fun buscarProyectoPorId(id: String): ProyectoMejoramiento? = proyectos[id]

    override fun listarProyectos(): List<ProyectoMejoramiento> = proyectos.values.toList()

    /** Vacía todos los almacenes; útil para reutilizar la misma instancia entre pruebas. */
    fun limpiar() {
        estudiantes.clear()
        docentes.clear()
        respuestasEncuesta.clear()
        resultadosEncuestaPregunta.clear()
        evaluacionesDocumentales.clear()
        ciclos.clear()
        fases.clear()
        actividades.clear()
        gruposEvaluadores.clear()
        preevaluaciones.clear()
        participaciones.clear()
        proyectos.clear()
    }
}

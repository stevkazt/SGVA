package com.example.sgva.infrastructure.persistence

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
import com.example.sgva.infrastructure.persistence.jpa.ActividadCicloJpaRepository
import com.example.sgva.infrastructure.persistence.jpa.CicloAutoevaluacionJpaRepository
import com.example.sgva.infrastructure.persistence.jpa.DocenteJpaRepository
import com.example.sgva.infrastructure.persistence.jpa.EstudianteJpaRepository
import com.example.sgva.infrastructure.persistence.jpa.EvaluacionDocumentalJpaRepository
import com.example.sgva.infrastructure.persistence.jpa.FaseCicloJpaRepository
import com.example.sgva.infrastructure.persistence.jpa.GrupoEvaluadorJpaRepository
import com.example.sgva.infrastructure.persistence.jpa.ParticipacionActorJpaRepository
import com.example.sgva.infrastructure.persistence.jpa.PreevaluacionEvaluadorJpaRepository
import com.example.sgva.infrastructure.persistence.jpa.ProyectoMejoramientoJpaRepository
import com.example.sgva.infrastructure.persistence.jpa.ResultadoEncuestaPreguntaJpaRepository
import com.example.sgva.infrastructure.persistence.jpa.RespuestaEncuestaJpaRepository
import com.example.sgva.infrastructure.persistence.jpa.aDominio
import com.example.sgva.infrastructure.persistence.jpa.aEntidad
import org.springframework.stereotype.Repository

/**
 * Adaptador de salida sobre PostgreSQL (Spring Data JPA) para
 * [AcademicDataRepository] (`DESIGN_SGVA.md` §3–§4). Sustituye al adaptador en
 * memoria de V1: delega en los repositorios de Spring Data de
 * `infrastructure.persistence.jpa` y traduce entre las entidades de dominio y
 * las entidades JPA mediante `MapeadorPersistencia.kt`. El dominio y los
 * casos de uso no dependen de JPA ni conocen esta clase, solo el puerto
 * [AcademicDataRepository] (Inversión de Dependencia).
 */
@Repository
internal class AcademicDataRepositoryJpa(
    private val estudiantes: EstudianteJpaRepository,
    private val docentes: DocenteJpaRepository,
    private val respuestasEncuesta: RespuestaEncuestaJpaRepository,
    private val resultadosEncuestaPregunta: ResultadoEncuestaPreguntaJpaRepository,
    private val evaluacionesDocumentales: EvaluacionDocumentalJpaRepository,
    private val ciclos: CicloAutoevaluacionJpaRepository,
    private val fases: FaseCicloJpaRepository,
    private val actividades: ActividadCicloJpaRepository,
    private val gruposEvaluadores: GrupoEvaluadorJpaRepository,
    private val preevaluaciones: PreevaluacionEvaluadorJpaRepository,
    private val participaciones: ParticipacionActorJpaRepository,
    private val proyectos: ProyectoMejoramientoJpaRepository,
) : AcademicDataRepository {

    override fun guardarEstudiante(estudiante: Estudiante): Estudiante =
        estudiantes.save(estudiante.aEntidad()).aDominio()

    override fun buscarEstudiantePorId(id: String): Estudiante? =
        estudiantes.findById(id).orElse(null)?.aDominio()

    override fun listarEstudiantes(): List<Estudiante> =
        estudiantes.findAllByOrderByCreadoEnAsc().map { it.aDominio() }

    override fun guardarDocente(docente: Docente): Docente =
        docentes.save(docente.aEntidad()).aDominio()

    override fun buscarDocentePorId(id: String): Docente? =
        docentes.findById(id).orElse(null)?.aDominio()

    override fun listarDocentes(): List<Docente> =
        docentes.findAllByOrderByCreadoEnAsc().map { it.aDominio() }

    override fun guardarRespuestaEncuesta(respuesta: RespuestaEncuesta): RespuestaEncuesta =
        respuestasEncuesta.save(respuesta.aEntidad()).aDominio()

    override fun buscarRespuestaEncuestaPorId(id: String): RespuestaEncuesta? =
        respuestasEncuesta.findById(id).orElse(null)?.aDominio()

    override fun listarRespuestasEncuesta(): List<RespuestaEncuesta> =
        respuestasEncuesta.findAllByOrderByCreadoEnAsc().map { it.aDominio() }

    override fun guardarResultadoEncuestaPregunta(resultado: ResultadoEncuestaPregunta): ResultadoEncuestaPregunta =
        resultadosEncuestaPregunta.save(resultado.aEntidad()).aDominio()

    override fun buscarResultadoEncuestaPregunta(
        programa: String,
        periodo: String,
        codigoPregunta: String,
    ): ResultadoEncuestaPregunta? =
        resultadosEncuestaPregunta.findByProgramaAndPeriodoAndCodigoPregunta(programa, periodo, codigoPregunta)
            ?.aDominio()

    override fun listarResultadosEncuestaPregunta(): List<ResultadoEncuestaPregunta> =
        resultadosEncuestaPregunta.findAllByOrderByCreadoEnAsc().map { it.aDominio() }

    override fun guardarEvaluacionDocumental(evaluacion: EvaluacionDocumental): EvaluacionDocumental =
        evaluacionesDocumentales.save(evaluacion.aEntidad()).aDominio()

    override fun buscarEvaluacionDocumental(
        programa: String,
        periodo: String,
        caracteristica: String,
    ): EvaluacionDocumental? =
        evaluacionesDocumentales.findByProgramaAndPeriodoAndCaracteristica(programa, periodo, caracteristica)
            ?.aDominio()

    override fun listarEvaluacionesDocumentales(): List<EvaluacionDocumental> =
        evaluacionesDocumentales.findAllByOrderByCreadoEnAsc().map { it.aDominio() }

    // ===== Módulo 5 — Seguimiento del proceso institucional (V6) =====

    override fun guardarCiclo(ciclo: CicloAutoevaluacion): CicloAutoevaluacion =
        ciclos.save(ciclo.aEntidad()).aDominio()

    override fun buscarCicloPorId(id: String): CicloAutoevaluacion? = ciclos.findById(id).orElse(null)?.aDominio()

    override fun buscarCicloPorProgramaYPeriodo(programa: String, periodo: String): CicloAutoevaluacion? =
        ciclos.findByProgramaAndPeriodo(programa, periodo)?.aDominio()

    override fun listarCiclos(): List<CicloAutoevaluacion> =
        ciclos.findAllByOrderByCreadoEnAsc().map { it.aDominio() }

    override fun guardarFase(fase: FaseCiclo): FaseCiclo = fases.save(fase.aEntidad()).aDominio()

    override fun listarFasesPorCiclo(ciclo: String): List<FaseCiclo> =
        fases.findByCicloOrderByCreadoEnAsc(ciclo).map { it.aDominio() }

    override fun guardarActividad(actividad: ActividadCiclo): ActividadCiclo =
        actividades.save(actividad.aEntidad()).aDominio()

    override fun listarActividadesPorCiclo(ciclo: String): List<ActividadCiclo> =
        actividades.findByCicloOrderByCreadoEnAsc(ciclo).map { it.aDominio() }

    override fun guardarGrupoEvaluador(grupo: GrupoEvaluador): GrupoEvaluador =
        gruposEvaluadores.save(grupo.aEntidad()).aDominio()

    override fun buscarGrupoEvaluador(ciclo: String, taller: Int): GrupoEvaluador? =
        gruposEvaluadores.findByCicloAndTaller(ciclo, taller)?.aDominio()

    override fun listarGruposEvaluadorPorCiclo(ciclo: String): List<GrupoEvaluador> =
        gruposEvaluadores.findByCicloOrderByCreadoEnAsc(ciclo).map { it.aDominio() }

    override fun guardarPreevaluacion(preevaluacion: PreevaluacionEvaluador): PreevaluacionEvaluador =
        preevaluaciones.save(preevaluacion.aEntidad()).aDominio()

    override fun buscarPreevaluacion(
        ciclo: String,
        caracteristica: String,
        evaluador: String,
    ): PreevaluacionEvaluador? =
        preevaluaciones.findByCicloAndCaracteristicaAndEvaluador(ciclo, caracteristica, evaluador)?.aDominio()

    override fun listarPreevaluacionesPorCiclo(ciclo: String): List<PreevaluacionEvaluador> =
        preevaluaciones.findByCicloOrderByCreadoEnAsc(ciclo).map { it.aDominio() }

    override fun guardarParticipacion(participacion: ParticipacionActor): ParticipacionActor =
        participaciones.save(participacion.aEntidad()).aDominio()

    override fun buscarParticipacion(ciclo: String, actor: String): ParticipacionActor? =
        participaciones.findByCicloAndActor(ciclo, actor)?.aDominio()

    override fun listarParticipacionPorCiclo(ciclo: String): List<ParticipacionActor> =
        participaciones.findByCicloOrderByCreadoEnAsc(ciclo).map { it.aDominio() }

    override fun guardarProyecto(proyecto: ProyectoMejoramiento): ProyectoMejoramiento =
        proyectos.save(proyecto.aEntidad()).aDominio()

    override fun buscarProyectoPorId(id: String): ProyectoMejoramiento? =
        proyectos.findById(id).orElse(null)?.aDominio()

    override fun listarProyectos(): List<ProyectoMejoramiento> =
        proyectos.findAllByOrderByCreadoEnAsc().map { it.aDominio() }
}

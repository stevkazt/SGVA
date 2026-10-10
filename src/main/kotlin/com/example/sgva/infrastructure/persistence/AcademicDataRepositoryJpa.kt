package com.example.sgva.infrastructure.persistence

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.Docente
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.ResultadoEncuestaPregunta
import com.example.sgva.domain.RespuestaEncuesta
import com.example.sgva.infrastructure.persistence.jpa.DocenteJpaRepository
import com.example.sgva.infrastructure.persistence.jpa.EstudianteJpaRepository
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
}

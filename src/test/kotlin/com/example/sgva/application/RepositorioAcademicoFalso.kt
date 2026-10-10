package com.example.sgva.application

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.Docente
import com.example.sgva.domain.Estudiante
import com.example.sgva.domain.EvaluacionDocumental
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

    /** Vacía los cinco almacenes; útil para reutilizar la misma instancia entre pruebas. */
    fun limpiar() {
        estudiantes.clear()
        docentes.clear()
        respuestasEncuesta.clear()
        resultadosEncuestaPregunta.clear()
        evaluacionesDocumentales.clear()
    }
}

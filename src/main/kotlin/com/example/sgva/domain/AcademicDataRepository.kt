package com.example.sgva.domain

/**
 * Puerto de salida (Outbound/Driven Port) para persistir y recuperar las
 * entidades de dominio ya validadas (`DESIGN_SGVA.md` §3).
 *
 * El dominio declara la interfaz; la tecnología concreta vive en
 * `infrastructure` y nunca al revés (Inversión de Dependencia). En esta versión
 * el único adaptador es en memoria; un futuro adaptador Spring Data debe poder
 * sustituirlo sin tocar el dominio ni los casos de uso.
 *
 * Toda entidad que entra o sale de este puerto es válida por construcción: sus
 * invariantes se verifican en el bloque `init` de [Estudiante] y [Docente], no
 * aquí ni en los adaptadores (`DESIGN_SGVA.md` §0.1).
 *
 * El identificador (`id`) es la clave de identidad de cada entidad. Este puerto
 * solo almacena y consulta; la regla de aplicación "un `id` no puede
 * registrarse dos veces" la aplican los casos de uso de registro.
 */
interface AcademicDataRepository {

    /** Persiste [estudiante] indexado por su `id` y lo devuelve sin cambios. */
    fun guardarEstudiante(estudiante: Estudiante): Estudiante

    /** Devuelve el estudiante con [id], o `null` si no hay ninguno registrado. */
    fun buscarEstudiantePorId(id: String): Estudiante?

    /** Devuelve todos los estudiantes registrados, en orden de inserción. */
    fun listarEstudiantes(): List<Estudiante>

    /** Persiste [docente] indexado por su `id` y lo devuelve sin cambios. */
    fun guardarDocente(docente: Docente): Docente

    /** Devuelve el docente con [id], o `null` si no hay ninguno registrado. */
    fun buscarDocentePorId(id: String): Docente?

    /** Devuelve todos los docentes registrados, en orden de inserción. */
    fun listarDocentes(): List<Docente>

    /** Persiste [respuesta] indexada por su `id` y la devuelve sin cambios. */
    fun guardarRespuestaEncuesta(respuesta: RespuestaEncuesta): RespuestaEncuesta

    /** Devuelve la respuesta de encuesta con [id], o `null` si no hay ninguna registrada. */
    fun buscarRespuestaEncuestaPorId(id: String): RespuestaEncuesta?

    /** Devuelve todas las respuestas de encuesta registradas, en orden de inserción. */
    fun listarRespuestasEncuesta(): List<RespuestaEncuesta>

    /** Persiste [resultado] y lo devuelve sin cambios. */
    fun guardarResultadoEncuestaPregunta(resultado: ResultadoEncuestaPregunta): ResultadoEncuestaPregunta

    /**
     * Devuelve el resultado de encuesta por pregunta cuya clave natural es
     * ([programa], [periodo], [codigoPregunta]), o `null` si no hay ninguno registrado.
     */
    fun buscarResultadoEncuestaPregunta(
        programa: String,
        periodo: String,
        codigoPregunta: String,
    ): ResultadoEncuestaPregunta?

    /** Devuelve todos los resultados de encuesta por pregunta registrados, en orden de inserción. */
    fun listarResultadosEncuestaPregunta(): List<ResultadoEncuestaPregunta>

    /** Persiste [evaluacion] y la devuelve sin cambios. */
    fun guardarEvaluacionDocumental(evaluacion: EvaluacionDocumental): EvaluacionDocumental

    /**
     * Devuelve la evaluación documental cuya clave natural es ([programa],
     * [periodo], [caracteristica]), o `null` si no hay ninguna registrada.
     */
    fun buscarEvaluacionDocumental(
        programa: String,
        periodo: String,
        caracteristica: String,
    ): EvaluacionDocumental?

    /** Devuelve todas las evaluaciones documentales registradas, en orden de inserción. */
    fun listarEvaluacionesDocumentales(): List<EvaluacionDocumental>

    // ===== Módulo 5 — Seguimiento del proceso institucional (V6) =====

    /** Persiste [ciclo] y lo devuelve sin cambios. */
    fun guardarCiclo(ciclo: CicloAutoevaluacion): CicloAutoevaluacion

    /** Devuelve el ciclo con [id], o `null` si no hay ninguno registrado. */
    fun buscarCicloPorId(id: String): CicloAutoevaluacion?

    /** Devuelve el ciclo cuya clave natural es ([programa], [periodo]), o `null` si no hay ninguno registrado. */
    fun buscarCicloPorProgramaYPeriodo(programa: String, periodo: String): CicloAutoevaluacion?

    /** Devuelve todos los ciclos registrados, en orden de inserción. */
    fun listarCiclos(): List<CicloAutoevaluacion>

    /** Persiste [fase] y la devuelve sin cambios. */
    fun guardarFase(fase: FaseCiclo): FaseCiclo

    /** Devuelve las fases del [ciclo], en orden de inserción. */
    fun listarFasesPorCiclo(ciclo: String): List<FaseCiclo>

    /** Persiste [actividad] y la devuelve sin cambios. */
    fun guardarActividad(actividad: ActividadCiclo): ActividadCiclo

    /** Devuelve las actividades del [ciclo], en orden de inserción. */
    fun listarActividadesPorCiclo(ciclo: String): List<ActividadCiclo>

    /** Persiste [grupo] y lo devuelve sin cambios. */
    fun guardarGrupoEvaluador(grupo: GrupoEvaluador): GrupoEvaluador

    /**
     * Devuelve el grupo evaluador cuya clave natural es ([ciclo], [taller]),
     * o `null` si no hay ninguno registrado.
     */
    fun buscarGrupoEvaluador(ciclo: String, taller: Int): GrupoEvaluador?

    /** Devuelve los grupos evaluadores del [ciclo], en orden de inserción. */
    fun listarGruposEvaluadorPorCiclo(ciclo: String): List<GrupoEvaluador>

    /** Persiste [preevaluacion] y la devuelve sin cambios. */
    fun guardarPreevaluacion(preevaluacion: PreevaluacionEvaluador): PreevaluacionEvaluador

    /**
     * Devuelve la preevaluación cuya clave natural es ([ciclo],
     * [caracteristica], [evaluador]), o `null` si no hay ninguna registrada.
     */
    fun buscarPreevaluacion(ciclo: String, caracteristica: String, evaluador: String): PreevaluacionEvaluador?

    /** Devuelve las preevaluaciones del [ciclo], en orden de inserción. */
    fun listarPreevaluacionesPorCiclo(ciclo: String): List<PreevaluacionEvaluador>

    /** Persiste [participacion] y la devuelve sin cambios. */
    fun guardarParticipacion(participacion: ParticipacionActor): ParticipacionActor

    /**
     * Devuelve la participación cuya clave natural es ([ciclo], [actor]), o
     * `null` si no hay ninguna registrada.
     */
    fun buscarParticipacion(ciclo: String, actor: String): ParticipacionActor?

    /** Devuelve las participaciones del [ciclo], en orden de inserción. */
    fun listarParticipacionPorCiclo(ciclo: String): List<ParticipacionActor>

    /** Persiste [proyecto] y lo devuelve sin cambios. */
    fun guardarProyecto(proyecto: ProyectoMejoramiento): ProyectoMejoramiento

    /** Devuelve el proyecto con [id], o `null` si no hay ninguno registrado. */
    fun buscarProyectoPorId(id: String): ProyectoMejoramiento?

    /** Devuelve todos los proyectos de mejoramiento registrados, en orden de inserción. */
    fun listarProyectos(): List<ProyectoMejoramiento>
}

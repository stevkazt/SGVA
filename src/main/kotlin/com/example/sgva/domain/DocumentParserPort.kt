package com.example.sgva.domain

/**
 * Puerto de salida (Outbound/Driven Port) para la extracción física de datos de
 * un archivo cargado desde el exterior (`DESIGN_SGVA.md` §3).
 *
 * Cada adaptador cubre un formato concreto (CSV, Excel). El adaptador es el
 * responsable de:
 *  1. Verificar que el archivo contiene las columnas obligatorias de la entidad
 *     antes de mapear cualquier fila (`DESIGN_SGVA.md` §5.1).
 *  2. Normalizar en la capa de infraestructura toda inconsistencia de formato de
 *     los datos (separadores en el periodo, mayúsculas/acentos en los enums,
 *     celdas en blanco) antes de construir la entidad de dominio.
 *  3. Emitir [FormatoArchivoInvalidoException] —sin propagar excepciones técnicas
 *     de la librería de parseo— si la estructura o los tipos de dato son
 *     irrecuperables (`DESIGN_SGVA.md` §5.2).
 *
 * Las entidades devueltas ya son válidas por construcción: sus invariantes se
 * verificaron en el bloque `init` de [Estudiante] y [Docente].
 */
interface DocumentParserPort {

    /** `true` si este adaptador puede procesar un archivo con el nombre dado (por su extensión). */
    fun soporta(nombreArchivo: String): Boolean

    /**
     * Extrae y normaliza los estudiantes contenidos en [contenido].
     *
     * @throws FormatoArchivoInvalidoException si faltan columnas obligatorias o
     *   alguna fila contiene datos que no pueden normalizarse a una entidad válida.
     */
    fun extraerEstudiantes(nombreArchivo: String, contenido: ByteArray): List<Estudiante>

    /**
     * Extrae y normaliza los docentes contenidos en [contenido].
     *
     * @throws FormatoArchivoInvalidoException si faltan columnas obligatorias o
     *   alguna fila contiene datos que no pueden normalizarse a una entidad válida.
     */
    fun extraerDocentes(nombreArchivo: String, contenido: ByteArray): List<Docente>

    /**
     * Extrae y normaliza las respuestas de encuesta de percepción contenidas en [contenido].
     *
     * @throws FormatoArchivoInvalidoException si faltan columnas obligatorias o
     *   alguna fila contiene datos que no pueden normalizarse a una entidad válida.
     */
    fun extraerRespuestasEncuestas(nombreArchivo: String, contenido: ByteArray): List<RespuestaEncuesta>

    /**
     * Extrae y normaliza los resultados de encuesta por pregunta contenidos en [contenido].
     *
     * @throws FormatoArchivoInvalidoException si faltan columnas obligatorias o
     *   alguna fila contiene datos que no pueden normalizarse a una entidad válida.
     */
    fun extraerResultadosEncuestaPregunta(nombreArchivo: String, contenido: ByteArray): List<ResultadoEncuestaPregunta>

    /**
     * Extrae y normaliza las evaluaciones documentales contenidas en [contenido].
     *
     * @throws FormatoArchivoInvalidoException si faltan columnas obligatorias o
     *   alguna fila contiene datos que no pueden normalizarse a una entidad válida.
     */
    fun extraerEvaluacionesDocumentales(nombreArchivo: String, contenido: ByteArray): List<EvaluacionDocumental>

    /**
     * Extrae y normaliza las preevaluaciones por evaluador contenidas en
     * [contenido] (`DESIGN_SGVA.md` §5.5, V6).
     *
     * @throws FormatoArchivoInvalidoException si faltan columnas obligatorias o
     *   alguna fila contiene datos que no pueden normalizarse a una entidad válida.
     */
    fun extraerPreevaluaciones(nombreArchivo: String, contenido: ByteArray): List<PreevaluacionEvaluador>

    /**
     * Extrae y normaliza la participación de actores contenida en
     * [contenido] (`DESIGN_SGVA.md` §5.5, V6).
     *
     * @throws FormatoArchivoInvalidoException si faltan columnas obligatorias o
     *   alguna fila contiene datos que no pueden normalizarse a una entidad válida.
     */
    fun extraerParticipacionActores(nombreArchivo: String, contenido: ByteArray): List<ParticipacionActor>
}

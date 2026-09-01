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
}

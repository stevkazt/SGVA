package com.example.sgva.infrastructure.persistence

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.Docente
import com.example.sgva.domain.Estudiante
import org.springframework.stereotype.Repository
import java.util.Collections

/**
 * Adaptador de salida en memoria para [AcademicDataRepository].
 *
 * Es la implementación de persistencia de esta versión: mantiene las entidades
 * en mapas dentro del heap de la JVM, sin durabilidad entre ejecuciones. Se
 * puede reemplazar por un adaptador Spring Data sin tocar el dominio ni los
 * casos de uso (`DESIGN_SGVA.md` §3–§4).
 *
 * Se usa un [LinkedHashMap] sincronizado para preservar el orden de inserción,
 * de modo que los listados sean deterministas para las pruebas y para los
 * gráficos del Dashboard.
 */
@Repository
class AcademicDataRepositoryEnMemoria : AcademicDataRepository {

    private val estudiantesPorId: MutableMap<String, Estudiante> =
        Collections.synchronizedMap(LinkedHashMap())

    private val docentesPorId: MutableMap<String, Docente> =
        Collections.synchronizedMap(LinkedHashMap())

    override fun guardarEstudiante(estudiante: Estudiante): Estudiante {
        estudiantesPorId[estudiante.id] = estudiante
        return estudiante
    }

    override fun buscarEstudiantePorId(id: String): Estudiante? = estudiantesPorId[id]

    override fun listarEstudiantes(): List<Estudiante> =
        synchronized(estudiantesPorId) { estudiantesPorId.values.toList() }

    override fun guardarDocente(docente: Docente): Docente {
        docentesPorId[docente.id] = docente
        return docente
    }

    override fun buscarDocentePorId(id: String): Docente? = docentesPorId[id]

    override fun listarDocentes(): List<Docente> =
        synchronized(docentesPorId) { docentesPorId.values.toList() }
}

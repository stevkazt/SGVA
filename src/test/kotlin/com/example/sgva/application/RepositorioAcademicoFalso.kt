package com.example.sgva.application

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.Docente
import com.example.sgva.domain.Estudiante

/**
 * Doble de prueba en memoria del puerto [AcademicDataRepository]. Permite
 * ejercitar los casos de uso sin depender del adaptador de infraestructura.
 */
internal class RepositorioAcademicoFalso : AcademicDataRepository {

    private val estudiantes = LinkedHashMap<String, Estudiante>()
    private val docentes = LinkedHashMap<String, Docente>()

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
}

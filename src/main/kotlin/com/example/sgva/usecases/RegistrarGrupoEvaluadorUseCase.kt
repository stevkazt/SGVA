package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.CicloNoEncontradoException
import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.GrupoEvaluador

/**
 * Registra un nuevo [GrupoEvaluador] de un ciclo (Módulo 5, `DESIGN_SGVA.md`
 * §0.1 y §2, V6). La clave natural es la combinación de ciclo y número de
 * taller: un mismo taller agrupa un único conjunto de factores.
 */
class RegistrarGrupoEvaluadorUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /**
     * @throws CicloNoEncontradoException si no existe el ciclo referenciado por [grupo].
     * @throws EntidadDuplicadaException si ya existe un grupo con el mismo ciclo y taller.
     */
    fun ejecutar(grupo: GrupoEvaluador): GrupoEvaluador {
        repositorio.buscarCicloPorId(grupo.ciclo) ?: throw CicloNoEncontradoException(grupo.ciclo)

        val existente = repositorio.buscarGrupoEvaluador(grupo.ciclo, grupo.taller)
        if (existente != null) {
            throw EntidadDuplicadaException("grupo evaluador", "${grupo.ciclo}/${grupo.taller}")
        }

        return repositorio.guardarGrupoEvaluador(grupo)
    }
}

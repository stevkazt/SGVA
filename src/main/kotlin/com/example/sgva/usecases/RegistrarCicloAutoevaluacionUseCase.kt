package com.example.sgva.usecases

import com.example.sgva.domain.AcademicDataRepository
import com.example.sgva.domain.ActividadCiclo
import com.example.sgva.domain.CicloAutoevaluacion
import com.example.sgva.domain.DetalleCiclo
import com.example.sgva.domain.EntidadDuplicadaException
import com.example.sgva.domain.FaseCiclo
import com.example.sgva.domain.ReferenciaInconsistenteException

/**
 * Registra un nuevo [CicloAutoevaluacion] (Módulo 5, `DESIGN_SGVA.md` §0.1 y
 * §2, V6): crea automáticamente sus cuatro fases fijas ([FaseCiclo.NOMBRES])
 * y, si se entregan, sus actividades iniciales.
 *
 * La validación de formato de cada entidad ya está garantizada por su propio
 * bloque `init`. Este caso de uso aporta las reglas de aplicación restantes:
 * un programa tiene como máximo un ciclo por periodo, y cada actividad
 * entregada debe pertenecer al ciclo que se está registrando.
 */
class RegistrarCicloAutoevaluacionUseCase(
    private val repositorio: AcademicDataRepository,
) {

    /**
     * Da de alta [ciclo], sus cuatro fases y las [actividades] entregadas, y
     * devuelve el detalle resultante.
     *
     * @throws EntidadDuplicadaException si el programa ya tiene un ciclo registrado en ese periodo.
     * @throws ReferenciaInconsistenteException si alguna actividad no pertenece a [ciclo].
     */
    fun ejecutar(ciclo: CicloAutoevaluacion, actividades: List<ActividadCiclo> = emptyList()): DetalleCiclo {
        val existente = repositorio.buscarCicloPorProgramaYPeriodo(ciclo.programa, ciclo.periodo)
        if (existente != null) {
            throw EntidadDuplicadaException("ciclo de autoevaluación", "${ciclo.programa}/${ciclo.periodo}")
        }

        actividades.forEach { actividad ->
            if (actividad.ciclo != ciclo.id) {
                throw ReferenciaInconsistenteException(
                    "La actividad '${actividad.id}' pertenece al ciclo '${actividad.ciclo}', " +
                        "no al ciclo '${ciclo.id}' que se está registrando.",
                )
            }
        }

        repositorio.guardarCiclo(ciclo)
        val fases = FaseCiclo.NOMBRES.map { (numero, nombre) ->
            repositorio.guardarFase(FaseCiclo(ciclo = ciclo.id, numero = numero, nombre = nombre))
        }
        val actividadesRegistradas = actividades.map { repositorio.guardarActividad(it) }

        return DetalleCiclo(ciclo = ciclo, fases = fases, actividades = actividadesRegistradas)
    }
}

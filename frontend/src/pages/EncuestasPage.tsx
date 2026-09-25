import { useEffect, useState } from 'react'
import { encuestas } from '../api/client'
import { mensajeDeError } from '../api/errores'
import type { MatrizRadar, PonderacionLikert } from '../api/types'
import { LikertChart } from '../components/charts/LikertChart'
import { RadarFactorChart } from '../components/charts/RadarFactorChart'
import { ErrorBanner } from '../components/ErrorBanner'

export function EncuestasPage() {
  const [periodo, setPeriodo] = useState('')
  const [periodoAplicado, setPeriodoAplicado] = useState('')

  const [likert, setLikert] = useState<{ datos: PonderacionLikert[] | null; cargando: boolean; error: string | null }>({
    datos: null,
    cargando: true,
    error: null,
  })
  const [radar, setRadar] = useState<{ datos: MatrizRadar | null; cargando: boolean; error: string | null }>({
    datos: null,
    cargando: true,
    error: null,
  })

  useEffect(() => {
    setLikert({ datos: null, cargando: true, error: null })
    encuestas
      .ponderacionLikert(periodoAplicado)
      .then((datos) => setLikert({ datos, cargando: false, error: null }))
      .catch((error) => setLikert({ datos: null, cargando: false, error: mensajeDeError(error) }))

    setRadar({ datos: null, cargando: true, error: null })
    encuestas
      .matrizRadar(periodoAplicado)
      .then((datos) => setRadar({ datos, cargando: false, error: null }))
      .catch((error) => setRadar({ datos: null, cargando: false, error: mensajeDeError(error) }))
  }, [periodoAplicado])

  return (
    <div>
      <h1>Encuestas de percepción</h1>
      <p className="page-subtitle">
        Módulo 3 — ponderación Likert (escala 1 a 5) y matriz radar de percepción por factor y estamento.
      </p>

      <div className="filter-bar">
        <div className="field">
          <label htmlFor="periodo-encuesta">Periodo</label>
          <input
            id="periodo-encuesta"
            placeholder="ej. 20261 (vacío = todos)"
            value={periodo}
            onChange={(e) => setPeriodo(e.target.value)}
          />
        </div>
        <button type="button" onClick={() => setPeriodoAplicado(periodo)}>
          Aplicar filtro
        </button>
      </div>

      <div className="card">
        <h2>Ponderación Likert por factor</h2>
        {likert.error && <ErrorBanner mensaje={likert.error} />}
        {likert.cargando && <p className="loading-text">Cargando…</p>}
        {likert.datos &&
          (likert.datos.length > 0 ? (
            <LikertChart datos={likert.datos} />
          ) : (
            <p className="empty-state">No hay respuestas de encuesta para este periodo.</p>
          ))}
      </div>

      <div className="card">
        <h2>Matriz radar por factor y estamento</h2>
        {radar.error && <ErrorBanner mensaje={radar.error} />}
        {radar.cargando && <p className="loading-text">Cargando…</p>}
        {radar.datos &&
          (radar.datos.factores.length > 0 ? (
            <RadarFactorChart matriz={radar.datos} />
          ) : (
            <p className="empty-state">No hay respuestas de encuesta para este periodo.</p>
          ))}
      </div>
    </div>
  )
}

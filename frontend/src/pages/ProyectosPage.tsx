import { useState } from 'react'
import { proyectosMejoramiento } from '../api/client'
import { ErrorBanner } from '../components/ErrorBanner'
import { ETIQUETA_ESTADO_PROYECTO, ETIQUETA_ORIGEN } from '../components/etiquetas'
import { Insignia } from '../components/Insignia'
import { useConsulta } from '../hooks/useConsulta'

export function ProyectosPage() {
  const [programa, setPrograma] = useState('')
  const [aplicado, setAplicado] = useState('')
  const lista = useConsulta(aplicado, () => proyectosMejoramiento.listar(aplicado))

  return (
    <div>
      <h1>Proyectos de mejoramiento</h1>
      <p className="page-subtitle">
        Plan de mejoramiento derivado de la autoevaluación o de recomendaciones del CNA.
      </p>

      <form
        className="filter-bar"
        onSubmit={(e) => {
          e.preventDefault()
          setAplicado(programa)
        }}
      >
        <div className="field">
          <label htmlFor="programa-proyectos">Programa</label>
          <input
            id="programa-proyectos"
            placeholder="vacío = todos"
            value={programa}
            onChange={(e) => setPrograma(e.target.value)}
          />
        </div>
        <button type="submit">Aplicar filtro</button>
      </form>

      {lista.error && <ErrorBanner mensaje={lista.error} />}
      {lista.cargando && <p className="loading-text">Cargando…</p>}
      {lista.datos?.length === 0 && <p className="empty-state">No hay proyectos de mejoramiento registrados.</p>}

      {lista.datos?.map((proyecto) => (
        <div className="card" key={proyecto.id}>
          <h2>
            {proyecto.nombre} <Insignia texto={ETIQUETA_ESTADO_PROYECTO[proyecto.estado]} tono="neutro" />
          </h2>
          <div className="stack-card">
            <span>
              Programa {proyecto.programa} · origen: {ETIQUETA_ORIGEN[proyecto.origen]} · factores:{' '}
              {proyecto.factores.join(', ') || '—'}
            </span>
            <span>
              <strong>Objetivo:</strong> {proyecto.objetivo}
            </span>
            <strong>Aspectos de mejora</strong>
            <ul>
              {proyecto.aspectosMejora.map((aspecto) => (
                <li key={aspecto}>{aspecto}</li>
              ))}
            </ul>
            <strong>Actividades</strong>
            <ul>
              {proyecto.actividades.map((actividad) => (
                <li key={actividad}>{actividad}</li>
              ))}
            </ul>
          </div>
        </div>
      ))}
    </div>
  )
}

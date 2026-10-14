import { useState, type FormEvent } from 'react'
import { resultadosAutoevaluacion } from '../api/client'
import { mensajeDeError } from '../api/errores'
import type { ResultadoAutoevaluacion } from '../api/types'
import { BarraSimpleChart } from '../components/charts/BarraSimpleChart'
import { ErrorBanner } from '../components/ErrorBanner'
import { StatTile } from '../components/StatTile'

/** Misma escala que `ValoracionCualitativa.desdeCalificacion` del backend (redondeo a un decimal). */
function valoracionCualitativa(calificacion: number): string {
  const redondeada = Math.round(calificacion * 10) / 10
  if (redondeada >= 4.6) return 'Se cumple plenamente'
  if (redondeada >= 4.0) return 'Se cumple en alto grado'
  if (redondeada >= 3.0) return 'Se cumple aceptablemente'
  if (redondeada >= 2.0) return 'Se cumple insatisfactoriamente'
  return 'No se cumple'
}

function formatear(valor: number | null): string {
  return valor === null ? 'Sin datos' : valor.toFixed(2)
}

/** Ordena códigos como F2 antes de F10 y C9 antes de C10. */
function ordenNatural(a: string, b: string): number {
  return a.localeCompare(b, 'es', { numeric: true })
}

export function ResultadoAutoevaluacionPage() {
  const [programa, setPrograma] = useState('')
  const [periodo, setPeriodo] = useState('')
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [resultado, setResultado] = useState<ResultadoAutoevaluacion | null>(null)

  async function consultar(evento: FormEvent) {
    evento.preventDefault()
    setCargando(true)
    setError(null)
    setResultado(null)
    try {
      setResultado(await resultadosAutoevaluacion.obtener(programa, periodo))
    } catch (err) {
      setError(mensajeDeError(err))
    } finally {
      setCargando(false)
    }
  }

  const factores = resultado
    ? Object.entries(resultado.promediosPorFactor).sort(([a], [b]) => ordenNatural(a, b))
    : []
  const caracteristicas = resultado
    ? Object.entries(resultado.promediosPorCaracteristica).sort(([a], [b]) => ordenNatural(a, b))
    : []
  const datosGrafica = factores.flatMap(([factor, valor]) => (valor === null ? [] : [{ etiqueta: factor, valor }]))

  return (
    <div>
      <h1>Resultado de autoevaluación</h1>
      <p className="page-subtitle">
        Módulo 4 — calificación global, por factor y por característica de un programa en un periodo, mezclando
        encuesta y evaluación documental según las ponderaciones configuradas.
      </p>

      <form className="filter-bar" onSubmit={consultar}>
        <div className="field">
          <label htmlFor="programa-autoevaluacion">Programa</label>
          <input
            id="programa-autoevaluacion"
            placeholder="ej. EGSTI"
            value={programa}
            onChange={(e) => setPrograma(e.target.value)}
            required
          />
        </div>
        <div className="field">
          <label htmlFor="periodo-autoevaluacion">Periodo</label>
          <input
            id="periodo-autoevaluacion"
            placeholder="ej. 20252"
            value={periodo}
            onChange={(e) => setPeriodo(e.target.value)}
            required
          />
        </div>
        <button type="submit" disabled={cargando}>
          {cargando ? 'Consultando…' : 'Consultar'}
        </button>
      </form>

      {error && <ErrorBanner mensaje={error} />}

      {resultado && (
        <>
          <div className="card">
            <h2>
              {resultado.programa} — {resultado.periodo}
            </h2>
            <div className="result-summary">
              <StatTile
                etiqueta="Promedio global"
                valor={formatear(resultado.promedioGlobal)}
                meta={
                  resultado.promedioGlobal === null
                    ? undefined
                    : valoracionCualitativa(resultado.promedioGlobal)
                }
              />
              <StatTile etiqueta="Factores" valor={factores.length.toString()} />
              <StatTile etiqueta="Características" valor={caracteristicas.length.toString()} />
            </div>
          </div>

          <div className="card">
            <h2>Promedio por factor</h2>
            {datosGrafica.length > 0 ? (
              <BarraSimpleChart datos={datosGrafica} />
            ) : (
              <p className="empty-state">No hay datos suficientes para calcular los factores.</p>
            )}
          </div>

          <div className="card">
            <h2>Detalle por característica</h2>
            {caracteristicas.length > 0 ? (
              <table>
                <thead>
                  <tr>
                    <th>Característica</th>
                    <th>Promedio</th>
                    <th>Valoración</th>
                  </tr>
                </thead>
                <tbody>
                  {caracteristicas.map(([codigo, valor]) => (
                    <tr key={codigo}>
                      <td>{codigo}</td>
                      <td>{formatear(valor)}</td>
                      <td>{valor === null ? '—' : valoracionCualitativa(valor)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <p className="empty-state">No hay características con datos para este programa y periodo.</p>
            )}
          </div>
        </>
      )}
    </div>
  )
}

import { useState, type FormEvent } from 'react'
import { comparaciones } from '../api/client'
import { mensajeDeError } from '../api/errores'
import type { ComparacionResultados } from '../api/types'
import { ErrorBanner } from '../components/ErrorBanner'

function formatear(valor: number | null, conSigno = false): string {
  if (valor === null) return 'Sin datos'
  const texto = valor.toFixed(2)
  return conSigno && valor > 0 ? `+${texto}` : texto
}

export function ComparacionPage() {
  const [programa, setPrograma] = useState('')
  const [periodos, setPeriodos] = useState('')
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [comparacion, setComparacion] = useState<ComparacionResultados | null>(null)

  async function comparar(evento: FormEvent) {
    evento.preventDefault()
    setCargando(true)
    setError(null)
    setComparacion(null)
    try {
      const lista = periodos.split(',').map((p) => p.trim()).filter((p) => p !== '')
      setComparacion(await comparaciones.comparar(programa.trim(), lista))
    } catch (err) {
      setError(mensajeDeError(err))
    } finally {
      setCargando(false)
    }
  }

  const factores = comparacion
    ? [...new Set(comparacion.resultados.flatMap((r) => Object.keys(r.promediosPorFactor)))].sort((a, b) =>
        a.localeCompare(b, 'es', { numeric: true }),
      )
    : []

  return (
    <div>
      <h1>Comparación entre ciclos</h1>
      <p className="page-subtitle">
        Resultado de autoevaluación de un programa en varios periodos, con la diferencia entre periodos consecutivos.
      </p>

      <form className="filter-bar" onSubmit={comparar}>
        <div className="field">
          <label htmlFor="programa-comparacion">Programa</label>
          <input
            id="programa-comparacion"
            placeholder="ej. EGSTI"
            value={programa}
            onChange={(e) => setPrograma(e.target.value)}
            required
          />
        </div>
        <div className="field">
          <label htmlFor="periodos-comparacion">Periodos (separados por coma)</label>
          <input
            id="periodos-comparacion"
            placeholder="ej. 20242, 20252"
            value={periodos}
            onChange={(e) => setPeriodos(e.target.value)}
            required
          />
        </div>
        <button type="submit" disabled={cargando}>
          {cargando ? 'Comparando…' : 'Comparar'}
        </button>
      </form>

      {error && <ErrorBanner mensaje={error} />}

      {comparacion && (
        <>
          <div className="card">
            <h2>Promedios por periodo</h2>
            <table>
              <thead>
                <tr>
                  <th>Factor</th>
                  {comparacion.resultados.map((r) => (
                    <th key={r.periodo}>{r.periodo}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {factores.map((factor) => (
                  <tr key={factor}>
                    <td>{factor}</td>
                    {comparacion.resultados.map((r) => (
                      <td key={r.periodo}>{formatear(r.promediosPorFactor[factor] ?? null)}</td>
                    ))}
                  </tr>
                ))}
                <tr>
                  <td>
                    <strong>Global</strong>
                  </td>
                  {comparacion.resultados.map((r) => (
                    <td key={r.periodo}>
                      <strong>{formatear(r.promedioGlobal)}</strong>
                    </td>
                  ))}
                </tr>
              </tbody>
            </table>
          </div>

          {comparacion.diferencias.map((d) => (
            <div className="card" key={`${d.periodoBase}-${d.periodoComparado}`}>
              <h2>
                Diferencia {d.periodoBase} → {d.periodoComparado}
              </h2>
              <table>
                <thead>
                  <tr>
                    <th>Factor</th>
                    <th>Diferencia</th>
                  </tr>
                </thead>
                <tbody>
                  {Object.entries(d.diferenciasPorFactor)
                    .sort(([a], [b]) => a.localeCompare(b, 'es', { numeric: true }))
                    .map(([factor, valor]) => (
                      <tr key={factor}>
                        <td>{factor}</td>
                        <td>{formatear(valor, true)}</td>
                      </tr>
                    ))}
                  <tr>
                    <td>
                      <strong>Global</strong>
                    </td>
                    <td>
                      <strong>{formatear(d.diferenciaGlobal, true)}</strong>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          ))}
        </>
      )}
    </div>
  )
}

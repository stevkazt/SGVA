import { Link } from 'react-router-dom'
import { ponderaciones } from '../api/client'
import { ErrorBanner } from '../components/ErrorBanner'
import { StatTile } from '../components/StatTile'
import { useConsulta } from '../hooks/useConsulta'

function porcentaje(peso: number): string {
  return `${(peso * 100).toLocaleString('es-CO', { maximumFractionDigits: 2 })} %`
}

export function PonderacionesPage() {
  const configuracion = useConsulta('ponderaciones', ponderaciones.obtener)

  const factores = configuracion.datos
    ? Object.entries(configuracion.datos.pesosFactores).sort(([a], [b]) => a.localeCompare(b, 'es', { numeric: true }))
    : []

  return (
    <div>
      <h1>Ponderaciones del modelo</h1>
      <p className="page-subtitle">
        Pesos de los factores, de las características dentro de cada factor y de la mezcla entre encuestas y
        evaluación documental. Definen cómo se calcula el resultado de autoevaluación. Para cambiarlos, carga un
        archivo nuevo en <Link to="/carga">Carga de datos</Link> (tipo «Ponderaciones del modelo»): reemplaza por
        completo a los vigentes.
      </p>

      {configuracion.error && <ErrorBanner mensaje={configuracion.error} />}
      {configuracion.cargando && <p className="loading-text">Cargando…</p>}

      {configuracion.datos && (
        <>
          <div className="card">
            <h2>Resumen</h2>
            <div className="result-summary">
              <StatTile etiqueta="Factores" valor={factores.length.toString()} />
              <StatTile etiqueta="Características" valor={configuracion.datos.caracteristicas.length.toString()} />
              <StatTile etiqueta="Peso de las encuestas" valor={porcentaje(configuracion.datos.pesoEncuestas)} />
              <StatTile etiqueta="Peso de la evaluación documental" valor={porcentaje(configuracion.datos.pesoDocumental)} />
            </div>
          </div>

          {factores.map(([factor, peso]) => (
            <div className="card" key={factor}>
              <h2>
                Factor {factor} — {porcentaje(peso)} del global
              </h2>
              <table>
                <thead>
                  <tr>
                    <th>Característica</th>
                    <th>Nombre</th>
                    <th>Peso dentro del factor</th>
                  </tr>
                </thead>
                <tbody>
                  {configuracion.datos?.caracteristicas
                    .filter((c) => c.factor === factor)
                    .map((c) => (
                      <tr key={c.id}>
                        <td>{c.id}</td>
                        <td>{c.nombre}</td>
                        <td>{porcentaje(c.ponderacion)}</td>
                      </tr>
                    ))}
                </tbody>
              </table>
            </div>
          ))}
        </>
      )}
    </div>
  )
}

import { useState, type FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { ciclos } from '../api/client'
import { mensajeDeError } from '../api/errores'
import { ErrorBanner } from '../components/ErrorBanner'
import { useConsulta } from '../hooks/useConsulta'

export function CiclosPage() {
  const [programa, setPrograma] = useState('')
  const [periodo, setPeriodo] = useState('')
  const [filtros, setFiltros] = useState({ programa: '', periodo: '' })
  const [version, setVersion] = useState(0)

  const [nuevo, setNuevo] = useState({ id: '', programa: '', periodo: '' })
  const [registrando, setRegistrando] = useState(false)
  const [errorRegistro, setErrorRegistro] = useState<string | null>(null)

  const lista = useConsulta(`${filtros.programa}|${filtros.periodo}|${version}`, () =>
    ciclos.listar(filtros.programa, filtros.periodo),
  )

  async function registrar(evento: FormEvent) {
    evento.preventDefault()
    setRegistrando(true)
    setErrorRegistro(null)
    try {
      await ciclos.registrar(nuevo)
      setNuevo({ id: '', programa: '', periodo: '' })
      setVersion((v) => v + 1)
    } catch (error) {
      setErrorRegistro(mensajeDeError(error))
    } finally {
      setRegistrando(false)
    }
  }

  return (
    <div>
      <h1>Ciclos de autoevaluación</h1>
      <p className="page-subtitle">
        Módulo 5 — seguimiento del proceso anual: fases, actividades, grupos evaluadores y participación.
      </p>

      <form
        className="filter-bar"
        onSubmit={(e) => {
          e.preventDefault()
          setFiltros({ programa, periodo })
        }}
      >
        <div className="field">
          <label htmlFor="filtro-programa-ciclo">Programa</label>
          <input
            id="filtro-programa-ciclo"
            placeholder="vacío = todos"
            value={programa}
            onChange={(e) => setPrograma(e.target.value)}
          />
        </div>
        <div className="field">
          <label htmlFor="filtro-periodo-ciclo">Periodo</label>
          <input
            id="filtro-periodo-ciclo"
            placeholder="ej. 20252"
            value={periodo}
            onChange={(e) => setPeriodo(e.target.value)}
          />
        </div>
        <button type="submit">Aplicar filtro</button>
      </form>

      <div className="card">
        <h2>Ciclos registrados</h2>
        {lista.error && <ErrorBanner mensaje={lista.error} />}
        {lista.cargando && <p className="loading-text">Cargando…</p>}
        {lista.datos &&
          (lista.datos.length > 0 ? (
            <table className="lista-enlazada">
              <thead>
                <tr>
                  <th>Identificador</th>
                  <th>Programa</th>
                  <th>Periodo</th>
                </tr>
              </thead>
              <tbody>
                {lista.datos.map((ciclo) => (
                  <tr key={ciclo.id}>
                    <td>
                      <Link to={`/ciclos/${encodeURIComponent(ciclo.id)}`}>{ciclo.id}</Link>
                    </td>
                    <td>{ciclo.programa}</td>
                    <td>{ciclo.periodo}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <p className="empty-state">No hay ciclos registrados con ese filtro.</p>
          ))}
      </div>

      <div className="card">
        <h2>Registrar ciclo</h2>
        <form className="upload-form" onSubmit={registrar}>
          <div className="field">
            <label htmlFor="ciclo-id">Identificador</label>
            <input
              id="ciclo-id"
              placeholder="ej. EGSTI-2025"
              value={nuevo.id}
              onChange={(e) => setNuevo({ ...nuevo, id: e.target.value })}
              required
            />
          </div>
          <div className="field">
            <label htmlFor="ciclo-programa">Programa</label>
            <input
              id="ciclo-programa"
              placeholder="ej. EGSTI"
              value={nuevo.programa}
              onChange={(e) => setNuevo({ ...nuevo, programa: e.target.value })}
              required
            />
          </div>
          <div className="field">
            <label htmlFor="ciclo-periodo">Periodo</label>
            <input
              id="ciclo-periodo"
              placeholder="ej. 20252"
              value={nuevo.periodo}
              onChange={(e) => setNuevo({ ...nuevo, periodo: e.target.value })}
              required
            />
          </div>
          <button type="submit" disabled={registrando}>
            {registrando ? 'Registrando…' : 'Registrar'}
          </button>
        </form>
        {errorRegistro && <ErrorBanner mensaje={errorRegistro} />}
      </div>
    </div>
  )
}

import { useState, type FormEvent } from 'react'
import { cargarArchivo } from '../api/client'
import { mensajeDeError } from '../api/errores'
import type { ResultadoIngesta, TipoCarga } from '../api/types'
import { ErrorBanner } from '../components/ErrorBanner'
import { StatTile } from '../components/StatTile'

export function UploadPage() {
  const [tipo, setTipo] = useState<TipoCarga>('estudiantes')
  const [archivo, setArchivo] = useState<File | null>(null)
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [resultado, setResultado] = useState<ResultadoIngesta | null>(null)

  async function enviar(evento: FormEvent) {
    evento.preventDefault()
    if (!archivo) return

    setCargando(true)
    setError(null)
    setResultado(null)
    try {
      const respuesta = await cargarArchivo(tipo, archivo)
      setResultado(respuesta)
    } catch (err) {
      setError(mensajeDeError(err))
    } finally {
      setCargando(false)
    }
  }

  return (
    <div>
      <h1>Carga de datos</h1>
      <p className="page-subtitle">
        Sube un archivo CSV o Excel con registros de estudiantes o docentes. El backend valida cada fila y
        reporta cuáles se registraron y cuáles se omitieron.
      </p>

      <div className="card">
        <form className="upload-form" onSubmit={enviar}>
          <div className="field">
            <label htmlFor="tipo-carga">Tipo de datos</label>
            <select
              id="tipo-carga"
              value={tipo}
              onChange={(e) => setTipo(e.target.value as TipoCarga)}
            >
              <option value="estudiantes">Estudiantes</option>
              <option value="docentes">Docentes</option>
            </select>
          </div>

          <div className="field">
            <label htmlFor="archivo">Archivo (.csv, .xlsx)</label>
            <input
              id="archivo"
              type="file"
              accept=".csv,.xlsx,.xls"
              onChange={(e) => setArchivo(e.target.files?.[0] ?? null)}
            />
          </div>

          <button type="submit" disabled={!archivo || cargando}>
            {cargando ? 'Cargando…' : 'Cargar archivo'}
          </button>
        </form>
      </div>

      {error && <ErrorBanner mensaje={error} />}

      {resultado && (
        <div className="card">
          <h2>Resultado de la carga — {resultado.nombreArchivo}</h2>
          <div className="result-summary">
            <StatTile etiqueta="Filas leídas" valor={resultado.totalRegistrosLeidos.toString()} />
            <StatTile etiqueta="Registrados" valor={resultado.registrados.toString()} />
            <StatTile etiqueta="Omitidos" valor={resultado.omitidos.length.toString()} />
          </div>

          {resultado.omitidos.length > 0 ? (
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Motivo</th>
                </tr>
              </thead>
              <tbody>
                {resultado.omitidos.map((omitido) => (
                  <tr key={omitido.id}>
                    <td>{omitido.id}</td>
                    <td>{omitido.motivo}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <p className="loading-text">Todos los registros se cargaron sin errores.</p>
          )}
        </div>
      )}
    </div>
  )
}

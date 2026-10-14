import { useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { ciclos, participacionActores, preevaluaciones, reportes } from '../api/client'
import { mensajeDeError } from '../api/errores'
import type { ActividadCiclo, EvaluacionDocumental } from '../api/types'
import { BarraSimpleChart } from '../components/charts/BarraSimpleChart'
import { ErrorBanner } from '../components/ErrorBanner'
import {
  ETIQUETA_ESTADO_ACTIVIDAD,
  ETIQUETA_SEMAFORO,
  TONO_ESTADO_ACTIVIDAD,
  TONO_SEMAFORO,
} from '../components/etiquetas'
import { Insignia } from '../components/Insignia'
import { StatTile } from '../components/StatTile'
import { descargarBlob, hoyIso, porcentaje } from '../descargas'
import { useConsulta } from '../hooks/useConsulta'

function TablaActividades({ actividades, vencidas }: { actividades: ActividadCiclo[]; vencidas?: Set<string> }) {
  if (actividades.length === 0) return <p className="empty-state">Sin actividades.</p>
  return (
    <table>
      <thead>
        <tr>
          <th>Fase</th>
          <th>Actividad</th>
          <th>Responsable</th>
          <th>Inicio</th>
          <th>Fin</th>
          <th>Estado</th>
        </tr>
      </thead>
      <tbody>
        {actividades.map((a) => (
          <tr key={a.id} className={vencidas?.has(a.id) ? 'fila-vencida' : undefined}>
            <td>{a.fase}</td>
            <td>{a.descripcion}</td>
            <td>{a.responsable}</td>
            <td>{a.fechaInicioPlanificada}</td>
            <td>{a.fechaFinPlanificada}</td>
            <td>
              <Insignia texto={ETIQUETA_ESTADO_ACTIVIDAD[a.estado]} tono={TONO_ESTADO_ACTIVIDAD[a.estado]} />
              {vencidas?.has(a.id) && <> <Insignia texto="Vencida" tono="critico" /></>}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

export function CicloDetallePage() {
  const { id = '' } = useParams()
  const [fechaEntrada, setFechaEntrada] = useState(hoyIso())
  const [fechaReferencia, setFechaReferencia] = useState(fechaEntrada)
  const [version, setVersion] = useState(0)

  const detalle = useConsulta(`detalle|${id}|${version}`, () => ciclos.detalle(id))
  const avance = useConsulta(`avance|${id}|${fechaReferencia}|${version}`, () => ciclos.avance(id, fechaReferencia))
  const grupos = useConsulta(`grupos|${id}|${version}`, () => ciclos.gruposEvaluadores(id))
  const preevals = useConsulta(`preevaluaciones|${id}|${version}`, () => preevaluaciones.listar(id))
  const participacion = useConsulta(`participacion|${id}|${version}`, () => participacionActores.listar(id))

  const [consolidando, setConsolidando] = useState(false)
  const [consolidadas, setConsolidadas] = useState<EvaluacionDocumental[] | null>(null)
  const [errorAccion, setErrorAccion] = useState<string | null>(null)
  const [descargando, setDescargando] = useState(false)

  async function consolidar() {
    setConsolidando(true)
    setErrorAccion(null)
    try {
      setConsolidadas(await preevaluaciones.consolidar(id))
      setVersion((v) => v + 1)
    } catch (error) {
      setErrorAccion(mensajeDeError(error))
    } finally {
      setConsolidando(false)
    }
  }

  async function descargarReporte() {
    if (!detalle.datos) return
    setDescargando(true)
    setErrorAccion(null)
    try {
      const { programa, periodo } = detalle.datos.ciclo
      const { blob, nombreArchivo } = await reportes.ejecutivo({ programa, periodo })
      descargarBlob(blob, nombreArchivo)
    } catch (error) {
      setErrorAccion(mensajeDeError(error))
    } finally {
      setDescargando(false)
    }
  }

  const vencidas = new Set((avance.datos?.actividadesVencidas ?? []).map((a) => a.id))
  const datosFases = detalle.datos
    ? detalle.datos.fases.map((fase) => ({
        etiqueta: `Fase ${fase.numero}`,
        valor: Math.round((avance.datos?.avancePorFase[String(fase.numero)] ?? 0) * 1000) / 10,
      }))
    : []

  return (
    <div>
      <p className="page-subtitle">
        <Link to="/ciclos">← Ciclos</Link>
      </p>
      <h1>Ciclo {id}</h1>
      {detalle.error && <ErrorBanner mensaje={detalle.error} />}
      {detalle.cargando && <p className="loading-text">Cargando…</p>}

      {detalle.datos && (
        <>
          <p className="page-subtitle">
            Programa {detalle.datos.ciclo.programa} · periodo {detalle.datos.ciclo.periodo}
          </p>

          <div className="acciones">
            <button type="button" onClick={descargarReporte} disabled={descargando}>
              {descargando ? 'Generando…' : 'Descargar reporte ejecutivo (PDF)'}
            </button>
            <button type="button" onClick={consolidar} disabled={consolidando}>
              {consolidando ? 'Consolidando…' : 'Consolidar preevaluaciones'}
            </button>
          </div>
          {errorAccion && <ErrorBanner mensaje={errorAccion} />}
          {consolidadas && (
            <p className="loading-text">
              Consolidación terminada: {consolidadas.length} característica(s) pasaron a la evaluación documental.
            </p>
          )}

          <div className="card">
            <h2>Avance</h2>
            <form
              className="filter-bar"
              onSubmit={(e) => {
                e.preventDefault()
                setFechaReferencia(fechaEntrada)
              }}
            >
              <div className="field">
                <label htmlFor="fecha-referencia">Fecha de referencia</label>
                <input
                  id="fecha-referencia"
                  type="date"
                  value={fechaEntrada}
                  onChange={(e) => setFechaEntrada(e.target.value)}
                  required
                />
              </div>
              <button type="submit">Recalcular</button>
            </form>
            {avance.error && <ErrorBanner mensaje={avance.error} />}
            {avance.cargando && <p className="loading-text">Cargando…</p>}
            {avance.datos && (
              <>
                <div className="result-summary">
                  <StatTile etiqueta="Avance del ciclo" valor={porcentaje(avance.datos.avanceCiclo)} />
                  <StatTile etiqueta="Vencidas" valor={avance.datos.actividadesVencidas.length.toString()} />
                  <StatTile
                    etiqueta="Próximas a vencer"
                    valor={avance.datos.actividadesProximasAVencer.length.toString()}
                  />
                  <StatTile
                    etiqueta="Factores sin preevaluar"
                    valor={avance.datos.factoresConPreevaluacionPendiente.length.toString()}
                    meta={avance.datos.factoresConPreevaluacionPendiente.join(', ') || undefined}
                  />
                </div>
                <h3>Avance por fase (%)</h3>
                <BarraSimpleChart datos={datosFases} />
                <ul>
                  {detalle.datos.fases.map((fase) => (
                    <li key={fase.numero}>
                      Fase {fase.numero}: {fase.nombre} —{' '}
                      {porcentaje(avance.datos?.avancePorFase[String(fase.numero)] ?? null)}
                    </li>
                  ))}
                </ul>
              </>
            )}
          </div>

          <div className="card">
            <h2>Actividades</h2>
            <TablaActividades actividades={detalle.datos.actividades} vencidas={vencidas} />
          </div>

          <div className="card">
            <h2>Participación de actores</h2>
            {avance.datos && avance.datos.participacionActores.length > 0 ? (
              <table>
                <thead>
                  <tr>
                    <th>Actor</th>
                    <th>Población</th>
                    <th>Muestra</th>
                    <th>Respuestas</th>
                    <th>Diligenciado</th>
                    <th>Semáforo</th>
                  </tr>
                </thead>
                <tbody>
                  {avance.datos.participacionActores.map(({ participacion: p, semaforo }) => (
                    <tr key={p.actor}>
                      <td>{p.actor}</td>
                      <td>{p.poblacion}</td>
                      <td>{p.muestra}</td>
                      <td>{p.respuestas}</td>
                      <td>{porcentaje(p.muestra === 0 ? null : p.respuestas / p.muestra)}</td>
                      <td>
                        {semaforo ? (
                          <Insignia texto={ETIQUETA_SEMAFORO[semaforo]} tono={TONO_SEMAFORO[semaforo]} />
                        ) : (
                          '—'
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <p className="empty-state">
                {participacion.datos?.length === 0
                  ? 'No hay participación registrada para este ciclo.'
                  : 'Cargando…'}
              </p>
            )}
          </div>

          <div className="card">
            <h2>Grupos evaluadores</h2>
            {grupos.error && <ErrorBanner mensaje={grupos.error} />}
            {grupos.datos &&
              (grupos.datos.length > 0 ? (
                <table>
                  <thead>
                    <tr>
                      <th>Taller</th>
                      <th>Factores</th>
                      <th>Fecha</th>
                    </tr>
                  </thead>
                  <tbody>
                    {grupos.datos.map((g) => (
                      <tr key={g.taller}>
                        <td>{g.taller}</td>
                        <td>{g.factores.join(', ')}</td>
                        <td>{g.fechaTaller ?? 'Sin fecha'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <p className="empty-state">No hay grupos evaluadores registrados.</p>
              ))}
          </div>

          <div className="card">
            <h2>Preevaluaciones</h2>
            {preevals.error && <ErrorBanner mensaje={preevals.error} />}
            {preevals.datos &&
              (preevals.datos.length > 0 ? (
                <table>
                  <thead>
                    <tr>
                      <th>Característica</th>
                      <th>Evaluador</th>
                      <th>Calificación</th>
                    </tr>
                  </thead>
                  <tbody>
                    {preevals.datos.map((p) => (
                      <tr key={`${p.caracteristica}|${p.evaluador}`}>
                        <td>{p.caracteristica}</td>
                        <td>{p.evaluador}</td>
                        <td>{p.calificacion.toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <p className="empty-state">No hay preevaluaciones registradas.</p>
              ))}
          </div>
        </>
      )}
    </div>
  )
}

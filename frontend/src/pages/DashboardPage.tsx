import { useEffect, useState } from 'react'
import { indicadores, reportes } from '../api/client'
import { mensajeDeError } from '../api/errores'
import type { IndicadorCalidad } from '../api/types'
import { BarraSimpleChart } from '../components/charts/BarraSimpleChart'
import { SerieUnicaChart } from '../components/charts/SerieUnicaChart'
import { ErrorBanner } from '../components/ErrorBanner'
import { StatTile } from '../components/StatTile'

interface EstadoIndicador<T> {
  datos: T | null
  cargando: boolean
  error: string | null
}

function estadoInicial<T>(): EstadoIndicador<T> {
  return { datos: null, cargando: true, error: null }
}

export function DashboardPage() {
  const [facultad, setFacultad] = useState('')
  const [periodo, setPeriodo] = useState('')
  const [cohorte, setCohorte] = useState('')
  const [filtrosAplicados, setFiltrosAplicados] = useState({ facultad: '', periodo: '', cohorte: '' })

  const [evolucionMatricula, setEvolucionMatricula] = useState(estadoInicial<IndicadorCalidad[]>())
  const [saberPro, setSaberPro] = useState(estadoInicial<IndicadorCalidad[]>())
  const [distribucionFormacion, setDistribucionFormacion] = useState(estadoInicial<IndicadorCalidad[]>())
  const [capacidadInstalada, setCapacidadInstalada] = useState(estadoInicial<IndicadorCalidad>())
  const [tasaDesercion, setTasaDesercion] = useState<EstadoIndicador<IndicadorCalidad>>({
    datos: null,
    cargando: false,
    error: null,
  })

  useEffect(() => {
    const { facultad, periodo, cohorte } = filtrosAplicados

    setEvolucionMatricula(estadoInicial())
    indicadores
      .evolucionMatricula({ facultad })
      .then((datos) => setEvolucionMatricula({ datos, cargando: false, error: null }))
      .catch((error) => setEvolucionMatricula({ datos: null, cargando: false, error: mensajeDeError(error) }))

    setSaberPro(estadoInicial())
    indicadores
      .saberPro({ facultad })
      .then((datos) => setSaberPro({ datos, cargando: false, error: null }))
      .catch((error) => setSaberPro({ datos: null, cargando: false, error: mensajeDeError(error) }))

    setDistribucionFormacion(estadoInicial())
    indicadores
      .distribucionFormacion({ facultad, periodo })
      .then((datos) => setDistribucionFormacion({ datos, cargando: false, error: null }))
      .catch((error) => setDistribucionFormacion({ datos: null, cargando: false, error: mensajeDeError(error) }))

    setCapacidadInstalada(estadoInicial())
    indicadores
      .capacidadInstalada({ facultad, periodo })
      .then((datos) => setCapacidadInstalada({ datos, cargando: false, error: null }))
      .catch((error) => setCapacidadInstalada({ datos: null, cargando: false, error: mensajeDeError(error) }))

    if (cohorte.trim() === '') {
      setTasaDesercion({ datos: null, cargando: false, error: null })
    } else {
      setTasaDesercion({ datos: null, cargando: true, error: null })
      indicadores
        .tasaDesercion({ cohorte, facultad })
        .then((datos) => setTasaDesercion({ datos, cargando: false, error: null }))
        .catch((error) => setTasaDesercion({ datos: null, cargando: false, error: mensajeDeError(error) }))
    }
  }, [filtrosAplicados])

  const [descargandoReporte, setDescargandoReporte] = useState(false)
  const [errorReporte, setErrorReporte] = useState<string | null>(null)

  function aplicarFiltros() {
    setFiltrosAplicados({ facultad, periodo, cohorte })
  }

  async function descargarReporte() {
    setDescargandoReporte(true)
    setErrorReporte(null)
    try {
      const { blob, nombreArchivo } = await reportes.ejecutivo(filtrosAplicados)
      const url = URL.createObjectURL(blob)
      const enlace = document.createElement('a')
      enlace.href = url
      enlace.download = nombreArchivo
      enlace.click()
      URL.revokeObjectURL(url)
    } catch (error) {
      setErrorReporte(mensajeDeError(error))
    } finally {
      setDescargandoReporte(false)
    }
  }

  const distribucionParaGrafica = (distribucionFormacion.datos ?? []).map((i) => ({
    // El nombre viene como "Distribución de Formación - MAESTRIA"; solo mostramos el nivel.
    etiqueta: i.nombre.includes(' - ') ? i.nombre.split(' - ')[1] : i.nombre,
    valor: i.valor,
  }))

  // El primer elemento es el promedio global (periodo "CONSOLIDADO"); el resto son promedios por cohorte.
  const saberProGlobal = (saberPro.datos ?? []).find((i) => i.periodo === 'CONSOLIDADO')
  const saberProParaGrafica = (saberPro.datos ?? [])
    .filter((i) => i.periodo !== 'CONSOLIDADO')
    .slice()
    .sort((a, b) => a.periodo.localeCompare(b.periodo))
    .map((i) => ({ etiqueta: i.periodo, valor: i.valor }))

  return (
    <div>
      <h1>Indicadores de calidad</h1>
      <p className="page-subtitle">Módulos 1 y 2 — resultados calculados a partir de los datos cargados.</p>

      <div className="filter-bar">
        <div className="field">
          <label htmlFor="facultad">Facultad</label>
          <input id="facultad" placeholder="ej. Ingeniería" value={facultad} onChange={(e) => setFacultad(e.target.value)} />
        </div>
        <div className="field">
          <label htmlFor="periodo">Periodo</label>
          <input id="periodo" placeholder="ej. 20261" value={periodo} onChange={(e) => setPeriodo(e.target.value)} />
        </div>
        <div className="field">
          <label htmlFor="cohorte">Cohorte (para deserción)</label>
          <input id="cohorte" placeholder="ej. 20211" value={cohorte} onChange={(e) => setCohorte(e.target.value)} />
        </div>
        <button type="button" onClick={aplicarFiltros}>
          Aplicar filtros
        </button>
        <button type="button" onClick={descargarReporte} disabled={descargandoReporte}>
          {descargandoReporte ? 'Generando…' : 'Descargar reporte ejecutivo'}
        </button>
      </div>

      {errorReporte && <ErrorBanner mensaje={errorReporte} />}

      <div className="card-grid">
        <div className="card">
          <h2>Evolución de matrícula</h2>
          {evolucionMatricula.error && <ErrorBanner mensaje={evolucionMatricula.error} />}
          {evolucionMatricula.cargando && <p className="loading-text">Cargando…</p>}
          {evolucionMatricula.datos &&
            (evolucionMatricula.datos.length > 0 ? (
              <SerieUnicaChart datos={evolucionMatricula.datos} />
            ) : (
              <p className="empty-state">No hay datos de matrícula para este filtro.</p>
            ))}
        </div>

        <div className="card">
          <h2>Consolidado Saber Pro</h2>
          {saberPro.error && <ErrorBanner mensaje={saberPro.error} />}
          {saberPro.cargando && <p className="loading-text">Cargando…</p>}
          {saberPro.datos && saberPro.datos.length > 0 && saberProGlobal && (
            <StatTile
              etiqueta="Promedio global"
              valor={saberProGlobal.valor.toLocaleString('es-CO', { maximumFractionDigits: 1 })}
              meta={`sobre ${300} puntos · ${saberProGlobal.facultad}`}
            />
          )}
          {saberPro.datos &&
            (saberProParaGrafica.length > 0 ? (
              <BarraSimpleChart datos={saberProParaGrafica} colorBarra="var(--series-2)" />
            ) : (
              <p className="empty-state">No hay puntajes Saber Pro por cohorte para este filtro.</p>
            ))}
        </div>

        <div className="card">
          <h2>Distribución de formación docente (%)</h2>
          {distribucionFormacion.error && <ErrorBanner mensaje={distribucionFormacion.error} />}
          {distribucionFormacion.cargando && <p className="loading-text">Cargando…</p>}
          {distribucionFormacion.datos &&
            (distribucionFormacion.datos.length > 0 ? (
              <BarraSimpleChart datos={distribucionParaGrafica} colorBarra="var(--series-3)" layout="vertical" />
            ) : (
              <p className="empty-state">No hay datos de docentes para este filtro.</p>
            ))}
        </div>

        <div className="card">
          <h2>Capacidad instalada</h2>
          {capacidadInstalada.error && <ErrorBanner mensaje={capacidadInstalada.error} />}
          {capacidadInstalada.cargando && <p className="loading-text">Cargando…</p>}
          {capacidadInstalada.datos && (
            <StatTile
              etiqueta="Relación estudiante / profesor"
              valor={capacidadInstalada.datos.valor.toLocaleString('es-CO', { maximumFractionDigits: 2 })}
              meta={`Periodo ${capacidadInstalada.datos.periodo} · ${capacidadInstalada.datos.facultad}`}
            />
          )}
        </div>

        <div className="card">
          <h2>Tasa de deserción</h2>
          {cohorte.trim() === '' && filtrosAplicados.cohorte.trim() === '' && (
            <p className="empty-state">Ingresa una cohorte arriba y aplica filtros para calcularla.</p>
          )}
          {tasaDesercion.error && <ErrorBanner mensaje={tasaDesercion.error} />}
          {tasaDesercion.cargando && <p className="loading-text">Cargando…</p>}
          {tasaDesercion.datos && (
            <StatTile
              etiqueta="Deserción"
              valor={`${tasaDesercion.datos.valor.toLocaleString('es-CO', { maximumFractionDigits: 1 })}%`}
              meta={`Cohorte ${tasaDesercion.datos.periodo} · ${tasaDesercion.datos.facultad}`}
            />
          )}
        </div>
      </div>
    </div>
  )
}

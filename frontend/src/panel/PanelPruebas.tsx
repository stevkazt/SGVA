import { useEffect, useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { archivosEjemplo, ayudaDe, RUTA_DOCUMENTO, recorridoDemo, recorridoLimpia, referenciasDocumento } from './contenido'
import { cambiarModo, modoActual, prepararEntorno, restaurarDemo, vaciarLimpia } from './modo'

type Aviso = { tipo: 'ok' | 'error'; texto: string }
type Entorno = { estado: 'preparando' } | { estado: 'listo'; usuario: string } | { estado: 'error'; texto: string }

/** Panel lateral de la demo pública: elegir datos, restaurarlos y entender cada pantalla. */
export function PanelPruebas() {
  const modo = modoActual()
  const { pathname } = useLocation()
  const ayuda = ayudaDe(pathname)
  const [abierto, setAbierto] = useState(() => localStorage.getItem('sgva.panel') !== 'cerrado')
  const [confirmando, setConfirmando] = useState(false)
  const [trabajando, setTrabajando] = useState(false)
  const [aviso, setAviso] = useState<Aviso | null>(null)
  const [entorno, setEntorno] = useState<Entorno>({ estado: 'preparando' })

  useEffect(() => {
    localStorage.setItem('sgva.panel', abierto ? 'abierto' : 'cerrado')
  }, [abierto])

  // Cada usuario tiene su propio backend; la primera vez tarda unos segundos en arrancar.
  useEffect(() => {
    prepararEntorno()
      .then((usuario) => setEntorno({ estado: 'listo', usuario }))
      .catch((error: unknown) =>
        setEntorno({ estado: 'error', texto: error instanceof Error ? error.message : 'No se pudo preparar tu entorno.' }),
      )
  }, [])

  async function ejecutarAccion() {
    setTrabajando(true)
    setAviso(null)
    try {
      const mensaje = await (modo === 'demo' ? restaurarDemo() : vaciarLimpia())
      setAviso({ tipo: 'ok', texto: mensaje })
      setConfirmando(false)
      setTimeout(() => window.location.reload(), 900)
    } catch (error) {
      setAviso({ tipo: 'error', texto: error instanceof Error ? error.message : 'No se pudo completar la acción.' })
    } finally {
      setTrabajando(false)
    }
  }

  if (!abierto) {
    return (
      <button type="button" className="panel-pruebas__abrir" onClick={() => setAbierto(true)}>
        Panel de pruebas
      </button>
    )
  }

  const recorrido = modo === 'demo' ? recorridoDemo : recorridoLimpia
  const enlaceDocumento = (ancla: string) => `${import.meta.env.BASE_URL}${RUTA_DOCUMENTO}${ancla ? `#${ancla}` : ''}`

  return (
    <aside className="panel-pruebas" aria-label="Panel de pruebas">
      <header className="panel-pruebas__cabecera">
        <h2>Panel de pruebas</h2>
        <button type="button" className="secondary" onClick={() => setAbierto(false)} aria-label="Ocultar el panel">
          Ocultar
        </button>
      </header>

      <section>
        <h3>Datos con los que trabajas</h3>
        {entorno.estado === 'preparando' && (
          <p className="panel-pruebas__nota" role="status">
            Preparando tu entorno… la primera vez tarda unos segundos.
          </p>
        )}
        {entorno.estado === 'listo' && entorno.usuario && (
          <p className="panel-pruebas__nota">
            Entorno de <strong>{entorno.usuario}</strong>.
          </p>
        )}
        {entorno.estado === 'error' && (
          <p className="panel-pruebas__aviso panel-pruebas__aviso--error" role="alert">
            {entorno.texto}
          </p>
        )}
        <div className="panel-pruebas__modos" role="group" aria-label="Modo de datos">
          <button type="button" className={modo === 'demo' ? '' : 'secondary'} onClick={() => modo !== 'demo' && cambiarModo('demo')}>
            Datos demo
          </button>
          <button type="button" className={modo === 'limpia' ? '' : 'secondary'} onClick={() => modo !== 'limpia' && cambiarModo('limpia')}>
            Base limpia
          </button>
        </div>
        <p className="panel-pruebas__nota">
          {modo === 'demo'
            ? 'Programa de ejemplo EGSTI con el modelo y el proceso 2025 ya cargados. Los datos de estudiantes, docentes, encuestas individuales y el periodo 20251 son sintéticos. Es tu copia: lo que cambies no lo ve el otro usuario.'
            : 'Base vacía y solo tuya: sube tus propios archivos desde «Carga de datos». Tus datos se conservan aunque cierres el navegador.'}
        </p>
        {!confirmando ? (
          <button type="button" className="secondary" onClick={() => setConfirmando(true)} disabled={trabajando}>
            {modo === 'demo' ? 'Restaurar datos demo' : 'Vaciar base limpia'}
          </button>
        ) : (
          <div className="panel-pruebas__confirmar">
            <span>{modo === 'demo' ? 'Se borra lo que hayas cambiado y vuelve la demo original.' : 'Se borran todos los datos de la base limpia.'}</span>
            <button type="button" onClick={ejecutarAccion} disabled={trabajando}>
              {trabajando ? 'Trabajando…' : 'Confirmar'}
            </button>
            <button type="button" className="secondary" onClick={() => setConfirmando(false)} disabled={trabajando}>
              Cancelar
            </button>
          </div>
        )}
        {aviso && (
          <p className={`panel-pruebas__aviso panel-pruebas__aviso--${aviso.tipo}`} role="status">
            {aviso.texto}
          </p>
        )}
      </section>

      <section>
        <h3>Esta pantalla: {ayuda.titulo}</h3>
        <p>{ayuda.que}</p>
        <p className="panel-pruebas__nota">{ayuda.necesita}</p>
        <a href={enlaceDocumento(ayuda.ancla)} target="_blank" rel="noreferrer">
          Ver esta pantalla en el documento
        </a>
      </section>

      <section>
        <h3>Recorrido sugerido</h3>
        <ol className="panel-pruebas__lista">
          {recorrido.map((paso) => (
            <li key={paso.texto}>
              <Link to={paso.ruta}>{paso.texto}</Link>
            </li>
          ))}
        </ol>
      </section>

      <section>
        <h3>Archivos de ejemplo</h3>
        <p className="panel-pruebas__nota">
          Descárgalos y súbelos en «Carga de datos» con el tipo indicado, en este orden. Sirven también como modelo del formato.
        </p>
        <ul className="panel-pruebas__lista panel-pruebas__lista--archivos">
          {archivosEjemplo.map((ejemplo) => (
            <li key={ejemplo.archivo}>
              <a href={`${import.meta.env.BASE_URL}ejemplos/${ejemplo.archivo}`} download>
                {ejemplo.etiqueta}
              </a>
              <small>Tipo: {ejemplo.tipoCarga}</small>
            </li>
          ))}
        </ul>
      </section>

      <section>
        <h3>Documentación</h3>
        <p className="panel-pruebas__nota">Se abre en otra pestaña.</p>
        <ul className="panel-pruebas__lista panel-pruebas__lista--archivos">
          {referenciasDocumento.map((referencia) => (
            <li key={referencia.etiqueta}>
              <a href={enlaceDocumento(referencia.ancla)} target="_blank" rel="noreferrer">
                {referencia.etiqueta}
              </a>
            </li>
          ))}
        </ul>
      </section>
    </aside>
  )
}

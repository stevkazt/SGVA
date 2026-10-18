// Puerta de entrada de la demo pública. No tiene dependencias.
//
// Cada usuario (X-Sgva-Usuario, que añade la Pages Function a partir del login)
// tiene su propio entorno: una base de datos PostgreSQL y un backend SGVA que la puerta levanta
// cuando hace falta. Hay dos entornos por usuario, "demo" (clon de una
// plantilla con datos de ejemplo) y "limpia" (vacía), pero solo uno corre a la
// vez. Solo entran los usuarios de USUARIOS_PERMITIDOS (y como mucho TOPE_SESIONES
// distintos) y los entornos no se borran solos: se liberan con demo/sesiones.sh.
//
// - Reenvía /api/* al backend del usuario según X-Sgva-Usuario y X-Sgva-Modo.
// - /panel/*: preparar el entorno, restaurar la demo o vaciar la base limpia.
// - /admin/*: solo desde este mismo equipo (no pasa por el túnel).
// - Si DEMO_KEY está definida, exige X-Demo-Key (la añade la Pages Function).
//
// Uso: SGVA_JAR=build/libs/sgva-0.0.1-SNAPSHOT.jar DEMO_KEY=... node demo/servidor.mjs
import { execFile, spawn } from 'node:child_process'
import { appendFileSync, mkdirSync, openSync } from 'node:fs'
import { createServer, request } from 'node:http'
import { join } from 'node:path'
import { promisify } from 'node:util'
import { sembrar } from './sembrar.mjs'

const ejecutar = promisify(execFile)
const config = {
  puerto: Number(process.env.PUERTO_PUERTA ?? 8081),
  clave: process.env.DEMO_KEY ?? '',
  contenedor: process.env.PG_CONTENEDOR ?? 'sgva-postgres',
  jar: process.env.SGVA_JAR ?? '',
  logs: process.env.LOGS_DIR ?? 'demo/logs',
  permitidos: (process.env.USUARIOS_PERMITIDOS ?? '').split(',').map((u) => u.trim().toLowerCase()).filter(Boolean),
  tope: 2,
  memoria: process.env.SGVA_JVM_MEM ?? '-Xmx512m',
  plantilla: 'sgva_plantilla',
  puertoPlantilla: 8092,
  puertoBase: 8100,
}
config.tope = Number(process.env.TOPE_SESIONES ?? (config.permitidos.length || 2))
if (!config.jar) throw new Error('Falta SGVA_JAR (ruta del JAR del backend).')
mkdirSync(config.logs, { recursive: true })

const PATRON_USUARIO = /^[a-z0-9]{3,20}$/
const entornos = new Map() // clave "<id>_<modo>" -> { proceso, puerto, listo }
const puertosEnUso = new Set()

class ErrorDeDemo extends Error {
  constructor(estado, mensaje) {
    super(mensaje)
    this.estado = estado
  }
}

function responderJson(res, estado, cuerpo) {
  res.writeHead(estado, { 'Content-Type': 'application/json; charset=utf-8' })
  res.end(JSON.stringify(cuerpo))
}

const dormir = (ms) => new Promise((resolver) => setTimeout(resolver, ms))

async function psql(base, sql) {
  const { stdout } = await ejecutar('docker', ['exec', config.contenedor, 'psql', '-U', 'sgva', '-d', base, '-v', 'ON_ERROR_STOP=1', '-tAc', sql])
  return stdout.trim()
}

// Las tareas que crean o borran bases y deciden el tope van de una en una.
let candado = Promise.resolve()
function exclusivo(tarea) {
  const resultado = candado.then(tarea)
  candado = resultado.catch(() => undefined)
  return resultado
}

const nombreBase = (id, modo) => `sgva_s_${id}_${modo}`
const existeBase = async (base) => (await psql('postgres', `select 1 from pg_database where datname='${base}'`)) === '1'

async function sesionesExistentes() {
  const nombres = (await psql('postgres', "select datname from pg_database where datname like 'sgva\\_s\\_%'")).split('\n').filter(Boolean)
  return new Set(nombres.map((n) => /^sgva_s_([a-z0-9]+)_(?:demo|limpia)$/.exec(n)?.[1]).filter(Boolean))
}

function puertoLibre(minimo) {
  for (let p = minimo; ; p++) {
    if (!puertosEnUso.has(p)) {
      puertosEnUso.add(p)
      return p
    }
  }
}

async function esperarBackend(puerto, proceso) {
  for (let i = 0; i < 90; i++) {
    if (proceso.exitCode !== null) throw new Error('El backend se cerró al arrancar; revisa los logs de demo/logs.')
    try {
      await fetch(`http://127.0.0.1:${puerto}/api/ponderaciones`, { signal: AbortSignal.timeout(2000) })
      return
    } catch {
      await dormir(1000)
    }
  }
  throw new Error('El backend no arrancó a tiempo.')
}

function lanzarBackend(base, puerto) {
  const bitacora = openSync(join(config.logs, `backend-${base}.log`), 'a')
  const proceso = spawn('java', [config.memoria, '-jar', config.jar], {
    env: { ...process.env, DB_NAME: base, SERVER_PORT: String(puerto) },
    stdio: ['ignore', bitacora, bitacora],
  })
  appendFileSync(join(config.logs, 'hijos.pid'), `${proceso.pid}\n`)
  return proceso
}

async function detenerProceso(proceso) {
  if (proceso.exitCode !== null) return
  const terminado = new Promise((resolver) => proceso.once('exit', resolver))
  proceso.kill('SIGTERM')
  const limite = setTimeout(() => proceso.kill('SIGKILL'), 15000)
  await terminado
  clearTimeout(limite)
}

async function detenerEntorno(clave) {
  const entorno = entornos.get(clave)
  if (!entorno) return
  entornos.delete(clave)
  await detenerProceso(entorno.proceso)
  puertosEnUso.delete(entorno.puerto)
}

// Plantilla con los datos de ejemplo: se siembra una vez con un backend temporal.
let plantillaLista
function asegurarPlantilla() {
  plantillaLista ??= (async () => {
    if (!(await existeBase(config.plantilla))) await psql('postgres', `create database ${config.plantilla}`)
    const hayDatos = (await psql(config.plantilla, "select coalesce(to_regclass('public.estudiantes')::text,'')")) !== ''
      && Number(await psql(config.plantilla, 'select count(*) from estudiantes')) > 0
    if (hayDatos) return
    console.log('Creando la plantilla de datos demo…')
    const proceso = lanzarBackend(config.plantilla, config.puertoPlantilla)
    try {
      await esperarBackend(config.puertoPlantilla, proceso)
      await sembrar(`http://127.0.0.1:${config.puertoPlantilla}`)
    } finally {
      await detenerProceso(proceso)
    }
  })().catch((error) => {
    plantillaLista = undefined
    throw error
  })
  return plantillaLista
}

async function crearBase(base, modo) {
  if (modo === 'demo') {
    await asegurarPlantilla()
    await psql('postgres', `create database "${base}" template ${config.plantilla}`)
  } else {
    await psql('postgres', `create database "${base}"`)
  }
}

/** Devuelve el puerto del backend del visitante, creándolo si hace falta. */
async function obtenerEntorno(id, modo) {
  const clave = `${id}_${modo}`
  const existente = entornos.get(clave)
  if (existente) return existente.listo

  const entrada = await exclusivo(async () => {
    const yaCreado = entornos.get(clave)
    if (yaCreado) return yaCreado

    const base = nombreBase(id, modo)
    if (!(await existeBase(base))) {
      const sesiones = await sesionesExistentes()
      if (!sesiones.has(id) && sesiones.size >= config.tope) {
        throw new ErrorDeDemo(403, `La demo ya la están usando ${config.tope} usuarios y no admite más.`)
      }
      await crearBase(base, modo)
    }
    await detenerEntorno(`${id}_${modo === 'demo' ? 'limpia' : 'demo'}`) // un solo backend por visitante
    const puerto = puertoLibre(config.puertoBase)
    const proceso = lanzarBackend(base, puerto)
    const listo = esperarBackend(puerto, proceso).then(() => puerto)
    const nueva = { proceso, puerto, listo }
    entornos.set(clave, nueva)
    listo.catch(() => detenerEntorno(clave))
    return nueva
  })
  return entrada.listo
}

async function reiniciarBase(id, modo) {
  await exclusivo(async () => {
    const sesiones = await sesionesExistentes()
    if (!sesiones.has(id) && sesiones.size >= config.tope) {
      throw new ErrorDeDemo(403, `La demo ya la están usando ${config.tope} usuarios y no admite más.`)
    }
    const base = nombreBase(id, modo)
    await detenerEntorno(`${id}_${modo}`)
    await psql('postgres', `drop database if exists "${base}" with (force)`)
    await crearBase(base, modo)
  })
}

async function liberar(id) {
  await exclusivo(async () => {
    for (const modo of ['demo', 'limpia']) {
      await detenerEntorno(`${id}_${modo}`)
      await psql('postgres', `drop database if exists "${nombreBase(id, modo)}" with (force)`)
    }
  })
}

function datosDeSesion(req) {
  let id = String(req.headers['x-sgva-usuario'] ?? '')
  if (!id && !config.clave) id = 'local' // pruebas locales sin la Pages Function
  if (!PATRON_USUARIO.test(id)) throw new ErrorDeDemo(400, 'Falta el usuario (X-Sgva-Usuario).')
  if (config.permitidos.length > 0 && !config.permitidos.includes(id)) {
    throw new ErrorDeDemo(403, `El usuario «${id}» no tiene acceso a la demo.`)
  }
  return { id, modo: req.headers['x-sgva-modo'] === 'limpia' ? 'limpia' : 'demo' }
}

async function atenderPanel(req, res, ruta) {
  const { id, modo } = datosDeSesion(req)
  if (ruta === '/panel/preparar') {
    await obtenerEntorno(id, modo)
    return responderJson(res, 200, { listo: true, usuario: id })
  }
  if (req.method === 'POST' && ruta === '/panel/restaurar-demo') {
    await reiniciarBase(id, 'demo')
    return responderJson(res, 200, { mensaje: 'Datos demo restaurados.' })
  }
  if (req.method === 'POST' && ruta === '/panel/vaciar-limpia') {
    await reiniciarBase(id, 'limpia')
    return responderJson(res, 200, { mensaje: 'Base limpia vaciada.' })
  }
  return responderJson(res, 404, { mensaje: 'Ruta del panel no encontrada.' })
}

async function atenderAdmin(req, res, ruta) {
  const local = ['127.0.0.1', '::1', '::ffff:127.0.0.1'].includes(req.socket.remoteAddress ?? '')
  if (!local || req.headers['cf-connecting-ip'] || req.headers['x-forwarded-for']) {
    return responderJson(res, 403, { mensaje: 'Solo desde este equipo.' })
  }
  if (req.method === 'GET' && ruta === '/admin/sesiones') {
    const ids = [...(await sesionesExistentes())]
    return responderJson(res, 200, {
      tope: config.tope,
      sesiones: ids.map((id) => ({ id, corriendo: ['demo', 'limpia'].filter((m) => entornos.has(`${id}_${m}`)) })),
    })
  }
  if (req.method === 'POST' && ruta === '/admin/preparar-plantilla') {
    await asegurarPlantilla()
    return responderJson(res, 200, { mensaje: 'Plantilla lista.' })
  }
  const liberarUno = /^\/admin\/liberar\/([a-z0-9]{3,20})$/.exec(ruta)
  if (req.method === 'POST' && liberarUno) {
    const ids = liberarUno[1] === 'todas' ? [...(await sesionesExistentes())] : [liberarUno[1]]
    for (const id of ids) await liberar(id)
    return responderJson(res, 200, { mensaje: `Liberadas ${ids.length} sesión(es).` })
  }
  return responderJson(res, 404, { mensaje: 'Ruta de administración no encontrada.' })
}

async function reenviar(req, res) {
  const { id, modo } = datosDeSesion(req)
  const puerto = await obtenerEntorno(id, modo)
  const destino = new URL(req.url, `http://127.0.0.1:${puerto}`)
  const cabeceras = { ...req.headers, host: destino.host }
  for (const nombre of ['x-demo-key', 'x-sgva-modo', 'x-sgva-usuario', 'authorization', 'cookie']) delete cabeceras[nombre]

  const saliente = request(destino, { method: req.method, headers: cabeceras }, (respuesta) => {
    res.writeHead(respuesta.statusCode ?? 502, respuesta.headers)
    respuesta.pipe(res)
  })
  saliente.on('error', () => {
    if (!res.headersSent) responderJson(res, 502, { mensaje: 'El backend de tu entorno no responde.' })
  })
  req.pipe(saliente)
}

const servidor = createServer(async (req, res) => {
  const ruta = (req.url ?? '/').split('?')[0]
  try {
    if (ruta.startsWith('/admin/')) return await atenderAdmin(req, res, ruta)
    if (config.clave && req.headers['x-demo-key'] !== config.clave) {
      return responderJson(res, 401, { mensaje: 'No autorizado.' })
    }
    if (ruta.startsWith('/panel/')) return await atenderPanel(req, res, ruta)
    if (ruta.startsWith('/api/')) return await reenviar(req, res)
    return responderJson(res, 404, { mensaje: 'No encontrado.' })
  } catch (error) {
    const estado = error instanceof ErrorDeDemo ? error.estado : 500
    return responderJson(res, estado, { mensaje: error instanceof Error ? error.message : String(error) })
  }
})

async function apagar() {
  await Promise.all([...entornos.keys()].map(detenerEntorno))
  process.exit(0)
}
process.on('SIGINT', apagar)
process.on('SIGTERM', apagar)

servidor.listen(config.puerto, '127.0.0.1', () => {
  console.log(`Puerta SGVA en http://127.0.0.1:${config.puerto} (clave ${config.clave ? 'activa' : 'NO definida'}, tope ${config.tope} visitantes)`)
})

// Puerta de entrada de la demo pública. No tiene dependencias.
//
// - Reenvía /api/* al backend que corresponda según la cabecera X-Sgva-Modo
//   ("demo" -> datos de ejemplo, "limpia" -> base vacía para que el visitante
//   cargue sus propios archivos). Cada modo es un backend con su propia base.
// - Expone /panel/* para restaurar la demo y vaciar la base limpia.
// - Si DEMO_KEY está definida, exige la cabecera X-Demo-Key. La envía la Pages
//   Function de Cloudflare, así el túnel no se puede usar saltándose la contraseña.
//
// Uso: DEMO_KEY=... node demo/servidor.mjs
import { execFile } from 'node:child_process'
import { createServer, request } from 'node:http'
import { promisify } from 'node:util'
import { sembrar } from './sembrar.mjs'

const ejecutar = promisify(execFile)
const config = {
  puerto: Number(process.env.PUERTO_PUERTA ?? 8081),
  demo: process.env.BACKEND_DEMO ?? 'http://localhost:8090',
  limpia: process.env.BACKEND_LIMPIA ?? 'http://localhost:8091',
  clave: process.env.DEMO_KEY ?? '',
  contenedor: process.env.PG_CONTENEDOR ?? 'sgva-postgres',
  baseDemo: process.env.DB_DEMO ?? 'sgva_publica_demo',
  baseLimpia: process.env.DB_LIMPIA ?? 'sgva_publica_limpia',
}

const ocupadas = new Set() // 'demo' | 'limpia' mientras se restaura o vacía
const TRUNCAR_TODO = `DO $$ DECLARE t record; BEGIN
  FOR t IN SELECT tablename FROM pg_tables WHERE schemaname = 'public' LOOP
    EXECUTE 'TRUNCATE TABLE ' || quote_ident(t.tablename) || ' RESTART IDENTITY CASCADE';
  END LOOP; END $$;`

function responderJson(res, estado, cuerpo) {
  res.writeHead(estado, { 'Content-Type': 'application/json; charset=utf-8' })
  res.end(JSON.stringify(cuerpo))
}

async function vaciar(base) {
  await ejecutar('docker', ['exec', config.contenedor, 'psql', '-U', 'sgva', '-d', base, '-v', 'ON_ERROR_STOP=1', '-c', TRUNCAR_TODO])
}

async function conBloqueo(modo, tarea) {
  if (ocupadas.has(modo)) throw new Error('Ya hay una operación en curso sobre esos datos; espera unos segundos.')
  ocupadas.add(modo)
  try {
    await tarea()
  } finally {
    ocupadas.delete(modo)
  }
}

async function responde(url) {
  try {
    await fetch(url, { signal: AbortSignal.timeout(3000) })
    return true
  } catch {
    return false
  }
}

async function atenderPanel(req, res, ruta) {
  if (req.method === 'GET' && ruta === '/panel/estado') {
    return responderJson(res, 200, {
      demo: await responde(`${config.demo}/api/ponderaciones`),
      limpia: await responde(`${config.limpia}/api/ponderaciones`),
      ocupadas: [...ocupadas],
    })
  }
  if (req.method === 'POST' && ruta === '/panel/restaurar-demo') {
    await conBloqueo('demo', async () => {
      await vaciar(config.baseDemo)
      await sembrar(config.demo)
    })
    return responderJson(res, 200, { mensaje: 'Datos demo restaurados.' })
  }
  if (req.method === 'POST' && ruta === '/panel/vaciar-limpia') {
    await conBloqueo('limpia', () => vaciar(config.baseLimpia))
    return responderJson(res, 200, { mensaje: 'Base limpia vaciada.' })
  }
  return responderJson(res, 404, { mensaje: 'Ruta del panel no encontrada.' })
}

function reenviar(req, res) {
  const modo = req.headers['x-sgva-modo'] === 'limpia' ? 'limpia' : 'demo'
  if (ocupadas.has(modo)) {
    return responderJson(res, 503, { mensaje: 'Se están restaurando los datos; intenta de nuevo en unos segundos.' })
  }
  const destino = new URL(req.url, config[modo])
  const cabeceras = { ...req.headers, host: destino.host }
  for (const nombre of ['x-demo-key', 'x-sgva-modo', 'authorization', 'cookie']) delete cabeceras[nombre]

  const saliente = request(destino, { method: req.method, headers: cabeceras }, (respuesta) => {
    res.writeHead(respuesta.statusCode ?? 502, respuesta.headers)
    respuesta.pipe(res)
  })
  saliente.on('error', () => {
    if (!res.headersSent) responderJson(res, 502, { mensaje: `El backend de ${modo} no responde.` })
  })
  req.pipe(saliente)
}

createServer(async (req, res) => {
  if (config.clave && req.headers['x-demo-key'] !== config.clave) {
    return responderJson(res, 401, { mensaje: 'No autorizado.' })
  }
  const ruta = (req.url ?? '/').split('?')[0]
  try {
    if (ruta.startsWith('/panel/')) return await atenderPanel(req, res, ruta)
    if (ruta.startsWith('/api/')) return reenviar(req, res)
    return responderJson(res, 404, { mensaje: 'No encontrado.' })
  } catch (error) {
    return responderJson(res, 500, { mensaje: error instanceof Error ? error.message : String(error) })
  }
}).listen(config.puerto, '127.0.0.1', () => {
  console.log(`Puerta SGVA en http://127.0.0.1:${config.puerto} (clave ${config.clave ? 'activa' : 'NO definida'})`)
})

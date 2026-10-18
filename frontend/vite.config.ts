import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// Con la puerta de la demo (demo/servidor.mjs) el proxy envía la clave interna y
// el usuario cuyo entorno se usa en desarrollo (DEMO_KEY y USUARIO_LOCAL).
const cabecerasDemo: Record<string, string> = {}
if (process.env.DEMO_KEY) cabecerasDemo['x-demo-key'] = process.env.DEMO_KEY
if (process.env.DEMO_KEY || process.env.USUARIO_LOCAL) cabecerasDemo['x-sgva-usuario'] = process.env.USUARIO_LOCAL ?? 'johann'

// El backend Spring Boot no expone CORS, así que en desarrollo se usa el
// proxy de Vite para servir /api desde el mismo origen del frontend.
export default defineConfig({
  plugins: [react()],
  server: {
    // Escucha en 0.0.0.0 para poder abrir la app desde otros equipos de la
    // misma red local (móvil, otro computador), no solo desde localhost.
    host: true,
    proxy: {
      '/api': {
        target: process.env.VITE_BACKEND_URL ?? 'http://localhost:8080',
        changeOrigin: true,
        headers: cabecerasDemo,
      },
      // Acciones del panel de pruebas (solo existen detrás de la puerta de la demo).
      '/panel': {
        target: process.env.VITE_BACKEND_URL ?? 'http://localhost:8080',
        changeOrigin: true,
        headers: cabecerasDemo,
      },
    },
  },
})

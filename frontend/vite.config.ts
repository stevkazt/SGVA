import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

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
      },
    },
  },
})

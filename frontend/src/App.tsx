import { NavLink, Route, HashRouter, Routes } from 'react-router-dom'
import santotoLogo from './assets/santoto-logo.png'
import { CicloDetallePage } from './pages/CicloDetallePage'
import { CiclosPage } from './pages/CiclosPage'
import { ComparacionPage } from './pages/ComparacionPage'
import { DashboardPage } from './pages/DashboardPage'
import { EncuestasPage } from './pages/EncuestasPage'
import { ProyectosPage } from './pages/ProyectosPage'
import { ResultadoAutoevaluacionPage } from './pages/ResultadoAutoevaluacionPage'
import { UploadPage } from './pages/UploadPage'

function App() {
  return (
    <HashRouter>
      <div className="app-shell">
        <nav className="top-nav">
          <div className="top-nav__inner">
            <span className="brand-lockup">
              <img src={santotoLogo} alt="Santo Tomás" />
              <span className="brand-lockup__modificador">SGVA</span>
            </span>
            <NavLink to="/" end className={({ isActive }) => (isActive ? 'active' : '')}>
              Dashboard
            </NavLink>
            <NavLink to="/carga" className={({ isActive }) => (isActive ? 'active' : '')}>
              Carga de datos
            </NavLink>
            <NavLink to="/encuestas" className={({ isActive }) => (isActive ? 'active' : '')}>
              Encuestas
            </NavLink>
            <NavLink to="/autoevaluacion" className={({ isActive }) => (isActive ? 'active' : '')}>
              Autoevaluación
            </NavLink>
            <NavLink to="/ciclos" className={({ isActive }) => (isActive ? 'active' : '')}>
              Ciclos
            </NavLink>
            <NavLink to="/proyectos" className={({ isActive }) => (isActive ? 'active' : '')}>
              Proyectos
            </NavLink>
            <NavLink to="/comparacion" className={({ isActive }) => (isActive ? 'active' : '')}>
              Comparación
            </NavLink>
          </div>
        </nav>
        <main>
          <Routes>
            <Route path="/" element={<DashboardPage />} />
            <Route path="/carga" element={<UploadPage />} />
            <Route path="/encuestas" element={<EncuestasPage />} />
            <Route path="/autoevaluacion" element={<ResultadoAutoevaluacionPage />} />
            <Route path="/ciclos" element={<CiclosPage />} />
            <Route path="/ciclos/:id" element={<CicloDetallePage />} />
            <Route path="/proyectos" element={<ProyectosPage />} />
            <Route path="/comparacion" element={<ComparacionPage />} />
          </Routes>
        </main>
      </div>
    </HashRouter>
  )
}

export default App

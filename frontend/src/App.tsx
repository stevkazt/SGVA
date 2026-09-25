import { NavLink, Route, HashRouter, Routes } from 'react-router-dom'
import santotoLogo from './assets/santoto-logo.png'
import { DashboardPage } from './pages/DashboardPage'
import { EncuestasPage } from './pages/EncuestasPage'
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
          </div>
        </nav>
        <main>
          <Routes>
            <Route path="/" element={<DashboardPage />} />
            <Route path="/carga" element={<UploadPage />} />
            <Route path="/encuestas" element={<EncuestasPage />} />
          </Routes>
        </main>
      </div>
    </HashRouter>
  )
}

export default App

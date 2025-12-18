import {useMemo, useState} from 'react'
import './index.css'
import { HashRouter as Router, Route, Routes } from 'react-router-dom'
import NavBar from './components/NavBar'
import Dashboard from './pages/Dashboard'
import Statistics from './pages/Statistics'
import Settings from './pages/Settings'
import { IpDetectionProvider } from './context/IpDetectionContext'


function App() {

  return (
    <IpDetectionProvider>
      <Router>
        <div className='app-shell'>
          <video
            className='background-video'
            src="/background.mp4"
            autoPlay
            loop
            muted
            playsInline
          />
          <div className='app-content'>
            <div className="w-full flex items-center justify-center">
              <NavBar />
            </div>
            <div>
              <Routes>
                <Route path="/" element={<Dashboard />} />
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/statistics" element={<Statistics />} />
                <Route path="/settings" element={<Settings />} />
              </Routes>
            </div>
          </div>
        </div>
      </Router>
    </IpDetectionProvider>
  )
}

export default App
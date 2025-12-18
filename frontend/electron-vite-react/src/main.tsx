import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import { AttackFeedProvider } from './context/AttackFeedContext'

import './index.css'

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <AttackFeedProvider>
      <App />
    </AttackFeedProvider>
  </React.StrictMode>,
)

postMessage({ payload: 'removeLoading' }, '*')

import React from 'react'
import { Link } from 'react-router-dom'

const NavBar = () => {
  return (
    <nav className="navbar">
        <div className='flex items-center gap-2'>
            <img src='logo_vector.png' className='w-8 h-8'/>
            <h1 className=' text-emerald-500 drop-shadow-[0_0_10px_rgba(34,197,94,0.9)]'>Cyber Threat Map</h1>
        </div>
        <div className='space-x-12 mr-2'>
            <Link className="text-green-500 hover:[text-shadow:_0_0_10px_#26A2A2] no-underline font-semibold" to="/dashboard">[ Dashboard ]</Link>
            <Link className="text-green-500 hover:[text-shadow:_0_0_10px_#26A2A2] no-underline font-semibold" to="/statistics">[ Statistics ]</Link>
            <Link className="text-green-500 hover:[text-shadow:_0_0_10px_#26A2A2] no-underline font-semibold" to="/settings">[ Settings ]</Link>
        </div>
    </nav>
  )
}

export default NavBar
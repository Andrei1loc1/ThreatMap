import React from 'react'

interface Props {
    name: string,
    action: () => void,
}

const ButtonSetting = ({name, action} : Props) => {
    return (
        <button
            onClick={action}
            className="relative w-36 py-1 bg-cyan-500/20 border-2 border-cyan-500 text-cyan-300 font-mono text-sm uppercase tracking-wider rounded-lg hover:border-cyan-300
                                    active:scale-95 transform transition-all duration-200 shadow-lg shadow-cyan-500/30 overflow-hidden group">
            <span className="relative z-10">{name}</span>
            <span className="absolute inset-0 opacity-0 group-active:opacity-40 group-active:animate-ping"></span>
        </button>
    )
}
export default ButtonSetting

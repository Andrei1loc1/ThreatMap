import React from 'react'

interface Props {
    settingText : string,
    onChange?: (enabled: boolean) => void,
}

const ToggleSetting = ({settingText, onChange} : Props) => {
    return (
        <div className="flex items-center space-x-4 justify-between w-full">
            <span className="text-lg font-mono font-medium">{settingText}</span>
            <label className="relative inline-flex items-center cursor-pointer">
                <input type="checkbox" className="sr-only peer" onChange={(e) => onChange?.(e.target.checked)} />
                <div className="w-11 h-6 bg-gray-700 rounded-full peer peer-checked:bg-green-300/30 transition-colors duration-300 after:content-[''] after:absolute after:top-0.5 after:left-0.5 after:bg-cyan-300/80 after:rounded-full after:h-5 after:w-5 after:transition-all after:duration-300 peer-checked:after:translate-x-5"></div>
            </label>
        </div>
    )
}
export default ToggleSetting

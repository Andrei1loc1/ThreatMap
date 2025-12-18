import React from 'react'

interface Props{
    settingText: string,
    options: string[],
}

const OptionSetting = ({settingText, options} : Props) => {
    return (
        <div className="flex items-center space-x-4 justify-between w-full">
            <span className="text-lg font-mono font-medium">{settingText}</span>
            <div className="relative group">
                <select
                    defaultValue="matrix"
                    className="dropdown-button">
                    {options.map((option: string, index: number) => (
                        <option key={index} value={option}>{option}</option>
                    ))}
                </select>
                <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                    <svg className="w-5 h-5 text-cyan-400 group-hover:text-cyan-200 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7" />
                    </svg>
                </div>
                <div className="absolute -inset-1 rounded-lg bg-cyan-500 opacity-0 group-hover:opacity-20 group-focus-within:opacity-30 blur-xl transition-opacity duration-500 -z-10"></div>
            </div>
        </div>
    )
}
export default OptionSetting

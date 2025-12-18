import React from 'react'
import ButtonSetting from "@/components/Buttons/ButtonSetting";

const SettingCard2 = () => {
    return (
        <div className="light-card h-36 flex flex-col items-start justify-start mt-5 pt-3 px-5 gap-4 pb-8">
            <h3 className="text-xl self-start text-cyan-300/80 drop-shadow-[0_0_6px_rgba(34,211,238,0.9)]">Data Management</h3>
            <div className="flex items-start space-x-4 justify-start w-full">
                <div className="flex items-center space-x-4 justify-between w-full">
                    <span className="text-lg font-mono font-medium">Activate real time server monitoring</span>
                    <ButtonSetting name={"ACTIVATE"} action={() => {}} />
                </div>
            </div>
            <div className="flex items-start space-x-4 justify-start w-full">
                <div className="flex items-center space-x-4 justify-between w-full">
                    <span className="text-lg font-mono font-medium">Connect your email account for real time alerts</span>
                    <ButtonSetting name={"CONNECT"} action={() => {}} />
                </div>
            </div>
        </div>
    )
}
export default SettingCard2

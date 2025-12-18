import React from 'react'
import ToggleSetting from "@/components/Buttons/ToggleSetting";
import OptionSetting from "@/components/Buttons/OptionSetting";

const SettingCard1 = () => {
    return (
        <div className="light-card grid grid-cols-3 gap-8 px-4">
            <div className="flex flex-col w-full h-full items-start justify-start gap-4 mb-5">
                <h3 className="text-xl text-cyan-300/80 drop-shadow-[0_0_6px_rgba(34,211,238,0.9)]">Notification</h3>
                <ToggleSetting settingText={"Activate cyber sound nootification"}/>
                <ToggleSetting settingText={"Show pop-up notification on CRITICAL threat"}/>
            </div>
            <div className="h-40 bg-cyan-300/40 rounded-lg w-2 drop-shadow-[0_0_6px_rgba(34,211,238,0.9)]" />
            <div className="flex flex-col w-full h-full items-start justify-start gap-4 mb-5">
                <h3 className="text-xl text-cyan-300/80 drop-shadow-[0_0_6px_rgba(34,211,238,0.9)]">Display Settings</h3>
                <OptionSetting settingText={"Change the cyber theme app"} options={["Matrix Rain", "Purple Univers"]} />
                <OptionSetting settingText={"Choose the MAP style for attacks"} options={["Street Mode", "Hacker Mode"]} />
            </div>
        </div>
    )
}
export default SettingCard1

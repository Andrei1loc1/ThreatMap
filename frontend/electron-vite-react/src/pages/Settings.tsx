import React from 'react'
import SettingCard1 from "@/components/Settings/SettingCard1";
import SettingCard2 from "@/components/Settings/SettingCard2";



const Settings = () => {
  return (
    <div className='mx-20 mt-10 pb-10 flex flex-col gap-6 max-w-6xl'>
      <h2 className='light-title'>General Settings</h2>
        <SettingCard1 />
        <SettingCard2 />
    </div>
  )
}

export default Settings

import React, { useRef, useState } from 'react'
import { Mapbox3D } from '@/components/MapBox3D'
import { useAttackFeed } from '@/context/AttackFeedContext'
import { FAILURE_THRESHOLD } from '@/constants/detection'
import { renderTotalAttacks, renderActiveThreats, renderTopAttackVectors, renderAttackFeed, handleLoadClick, handleFileChange } from '@/utils/dashboardUtils'
import { useDashboardData } from '@/hooks/useDashboardData'
import RenderAttackInfo from "@/components/Cards/RenderAttackInfo";
import AttackFeed from "@/components/Cards/AttackFeed";
import { useNavigate } from 'react-router-dom';


const Dashboard = () => {
    const navigate = useNavigate();
  const { attacks, isLoading, error, uploadLog } = useAttackFeed()
  const fileInputRef = useRef<HTMLInputElement | null>(null)
  const [uploadMessage, setUploadMessage] = useState('Incarca un log pentru a actualiza feed-ul')

  const { flaggedIps, totalFailedAttempts } = useDashboardData(attacks, FAILURE_THRESHOLD)

  const handleLoadClickFn = handleLoadClick(fileInputRef);

  const handleFileChangeFn = handleFileChange(setUploadMessage, uploadLog, isLoading);

  const handleClickDetails = () => {
      navigate("/statistics");
  }

  return (
    <div className='grid grid-cols-[4fr_2fr] gap-16 mx-20 mt-10 pb-10'>
      <div className=''>
        <h2 className='light-title'>Main Dashboard</h2>
        <div className='light-card h-80 overflow-hidden p-0 items-stretch'>
          <div className='h-full w-full'>
            <Mapbox3D flaggedIps={flaggedIps} />
          </div>
        </div>
        <h2 className='light-title'>Incoming attack feed</h2>
        <AttackFeed isLoading={isLoading} error={error} flaggedIps={flaggedIps} fileInputRef={fileInputRef} setUploadMessage={setUploadMessage} uploadLog={uploadLog} totalFailedAttempts={totalFailedAttempts} />
      </div>
      <div className=''>
        <h2 className="light-title">System Stats</h2>
        <RenderAttackInfo fc={() => renderTotalAttacks(isLoading, error, flaggedIps, totalFailedAttempts)} />
        <RenderAttackInfo fc={() => renderActiveThreats(isLoading, error, flaggedIps)} />
        <h2 className="light-title mt-6">Regional Stats</h2>
          <div className='light-card flex flex-col justify-center p-4 my-5'>
              {renderTopAttackVectors(isLoading, error, flaggedIps)}
              <button onClick={handleClickDetails} className="inline-flex items-center justify-center gap-3 bg-cyan-400/5 text-cyan-300 tracking-wider font-mono text-sm border border-cyan-400 rounded-lg px-5 py-2.5 hover:bg-cyan-400/10 hover:border-cyan-300 transition-all">
                  <span>view all details</span>
                  <span className="inline-block translate-y-[4px]">˅</span>
              </button>
          </div>
      </div>
    </div>
  )
}

export default Dashboard

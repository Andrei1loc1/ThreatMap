import React from 'react'
import {handleFileChange, handleLoadClick, renderAttackFeed} from "@/utils/dashboardUtils";
import {FAILURE_THRESHOLD} from "@/constants/detection";
import {AttackEvent} from "@/utils/AttackEvent";

interface Props {
    isLoading: boolean,
    error: string | null,
    flaggedIps: AttackEvent[],
    fileInputRef: React.RefObject<HTMLInputElement>,
    setUploadMessage: React.Dispatch<React.SetStateAction<string>>,
    uploadLog: (file: File) => Promise<void>,
    totalFailedAttempts: number,
}

const AttackFeed = ({isLoading, error, flaggedIps, fileInputRef, setUploadMessage, uploadLog, totalFailedAttempts} : Props) => {
    const handleLoadClickFn = handleLoadClick(fileInputRef);
    const handleFileChangeFn = handleFileChange(setUploadMessage, uploadLog, isLoading);

    return (
        <div className='light-card h-40 flex flex-col gap-3'>
            <div className='flex items-center justify-between gap-3'>
                <div>
                    <p className='text-xs text-slate-300'>
                        Total fail-uri detectate: <span className='font-semibold text-white'>{totalFailedAttempts}</span>
                    </p>
                </div>
                <div className='flex items-center gap-2'>
                    <input
                        ref={fileInputRef}
                        type='file'
                        accept='.log,.txt,.csv'
                        className='hidden'
                        onChange={handleFileChangeFn}
                    />
                    <button
                        className='bg-white/5 rounded-md border border-cyan-400/50 px-3 py-1.5 text-xs uppercase tracking-wide text-cyan-100 hover:bg-cyan-400/10 transition-colors disabled:opacity-50'
                        onClick={handleLoadClickFn}
                        disabled={isLoading}
                    >
                        Load file
                    </button>
                </div>
            </div>
            {renderAttackFeed(isLoading, error, flaggedIps, FAILURE_THRESHOLD)}
        </div>
    )
}
export default AttackFeed

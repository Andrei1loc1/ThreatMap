import React from 'react'
import {useAiRisk} from "@/hooks/useAiRisk";

const AiScanRisk = () => {
    const { percent, isLoading: riskLoading, error: riskError, fetchRisk } = useAiRisk();

    return (<>
        <div className="relative group perspective-1000">
            <button
                onClick={fetchRisk}
                disabled={riskLoading}
                className={`relative px-10 py-3 rounded-2xl font-mono text-lg uppercase tracking-widest
                  bg-black/40 backdrop-blur-xl border-2 ${riskLoading ? 'border-yellow-500/70' : 'border-cyan-400/60'} text-cyan-300 shadow-cyan-500/50
                  transition-all duration-500 transform-gpu hover:scale-105 hover:bg-cyan-400/10 hover:shadow-3xl active:scale-95 active:duration-150
                  disabled:opacity-70 disabled:cursor-not-allowed overflow-hidden group`}
            >
                    <span className="relative z-10 flex items-center gap-4">
                    {riskLoading ? (
                        <><div className="w-6 h-6 border-4 border-t-transparent border-yellow-400 rounded-full animate-spin"></div>
                            ANALYZING THREAT...
                        </>
                    ) : ( <>INITIATE RISK SCAN</> )}
                  </span>
            </button>
        </div>
        {percent !== null && (
            <div className="relative p-0.5 bg-gradient-to-br from-cyan-400/80 rounded-3xl animate-[pulse_4s_ease-in-out_infinite]">
                <div className="relative bg-black/95 backdrop-blur-2xl rounded-3xl p-5 border-2 border-cyan-400
                  shadow-[0_0_20px_#22d3ee] overflow-hidden">

                    <div className="absolute inset-0 opacity-30 pointer-events-none bg-gradient-to-b from-transparent via-transparent to-transparent bg-[length:100%_4px] bg-repeat-y
                    animate-[scanlines_8s_linear_infinite] bg-stripes bg-stripes-cyan"></div>

                    <div className="relative z-10 text-center">
                        <p className="text-cyan-500 text-sm font-mono tracking-widest mb-2 animate-flicker">
                            ███ THREAT LEVEL DETECTED ███
                        </p>
                        <div className="text-6xl font-bold bg-white
                      bg-clip-text text-transparent animate-[gradient_6s_ease-in-out_infinite]">
                            {percent}%
                        </div>
                        <p className="text-2xl mt-4 font-mono text-cyan-300 tracking-wider">
                            {percent < 30 ? 'LOW RISK' :
                                percent < 70 ? 'ELEVATED THREAT' :
                                    'CRITICAL DANGER'}
                        </p>
                    </div>
                </div>
            </div>
        )}
    </>
    )
}
export default AiScanRisk

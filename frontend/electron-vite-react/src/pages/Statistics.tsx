import React, { useEffect } from 'react';
import { useIpDetection } from '../context/IpDetectionContext';
import Table from '../components/Statistics/Table';
import { useAiRisk } from '@/hooks/useAiRisk';
import AIScanRisk from "@/components/Cards/AIScanRisk";

const Statistics = () => {
  const { fetchLogIps, ipDataList } = useIpDetection();

  useEffect(() => {
    fetchLogIps();
    const intervalId = window.setInterval(fetchLogIps, 10000); // Poll every 10s for real-time
    return () => window.clearInterval(intervalId);
  }, [fetchLogIps]);

  return (
    <div className=' mx-20 mt-10 pb-10'>
        <h2 className="light-title text-2xl font-bold text-gray-900">IP Data Statistics</h2>
        <Table />
        <div className="flex flex-col mt-8 gap-4">
            <div className="relative">
                <h2 className="light-title relative inline-block">AI DANGER SCORE</h2>
            </div>
            <AIScanRisk />
        </div>

    </div>
  )
}

export default Statistics

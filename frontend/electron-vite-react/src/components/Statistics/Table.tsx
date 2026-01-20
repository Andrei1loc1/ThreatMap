import React from 'react';
import { useIpDetection } from '../../context/IpDetectionContext';

const Table: React.FC = () => {
    const { ipDataList, loading, error, fetchLogIps } = useIpDetection();

    if (loading) {
        return <div className="text-center text-gray-400">Loading IP data...</div>;
    }

    if (error) {
        return <div className="text-center text-red-400">Error: {error}</div>;
    }

    if (!ipDataList || ipDataList.length === 0) {
        return <div className="text-center text-gray-400">No IP data available. Load log IPs.</div>;
    }

    return (
        <div className="overflow-x-auto rounded-2xl border-2 table-border-primary" style={{boxShadow: '0 0 12px var(--primary-color)'}}>
            <table className="min-w-full text-white rounded-lg ">
                <thead>
                    <tr className="table-themed backdrop-blur-md">
                        <th className="px-4 py-2 text-left">IP Address</th>
                        <th className="px-4 py-2 text-left">Country</th>
                        <th className="px-4 py-2 text-left">City</th>
                        <th className="px-4 py-2 text-left">ISP</th>
                        <th className="px-4 py-2 text-left">Organization</th>
                        <th className="px-4 py-2 text-left">VPN/Proxy</th>
                    </tr>
                </thead>
                <tbody>
                    {ipDataList.map((ip, index) => (
                        <tr key={index} className="border-b border-gray-600">
                            <td className="px-4 py-2 font-semibold">{ip.ip}</td>
                            <td className="px-4 py-2">{ip.country}</td>
                            <td className="px-4 py-2">{ip.city}</td>
                            <td className="px-4 py-2">{ip.isp}</td>
                            <td className="px-4 py-2">{ip.org}</td>
                            <td className="px-4 py-2">{ip.isVpn ? 'Yes' : 'No'}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default Table;

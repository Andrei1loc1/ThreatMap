import React, { useState, useEffect } from 'react'
import ButtonSetting from "@/components/Buttons/ButtonSetting";
import EmailConnectModal from "./EmailConnectModal";

const SettingCard2 = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isSimulating, setIsSimulating] = useState(false);

    useEffect(() => {
        const fetchStatus = async () => {
            try {
                const response = await fetch('http://localhost:8080/api/simulator/status', {
                    headers: {
                        'X-API-KEY': import.meta.env.VITE_API_KEY,
                    },
                });
                if (response.ok) {
                    const data = await response.json();
                    setIsSimulating(data.running);
                }
            } catch (error) {
                console.error('Error fetching simulation status:', error);
            }
        };
        fetchStatus();
    }, []);

    const handleToggleSimulation = async () => {
        if (isSimulating) {
            // Stop simulation
            try {
                const response = await fetch("http://localhost:8080/api/simulator/stop", {
                    method: 'POST',
                    headers: {
                        'X-API-KEY': import.meta.env.VITE_API_KEY,
                    },
                });
                if (response.ok) {
                    setIsSimulating(false);
                    alert("Simulation stopped");
                } else {
                    alert("Error stopping simulation");
                }
            } catch (error) {
                alert("Error stopping simulation");
            }
        } else {
            // Start simulation
            try {
                const response = await fetch("http://localhost:8080/api/simulator/start", {
                    method: 'POST',
                    headers: {
                        'X-API-KEY': import.meta.env.VITE_API_KEY,
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        duration: 60, rate: 2, attackPercent: 30
                    })
                });
                if (response.ok) {
                    setIsSimulating(true);
                    alert("Simulation started");
                } else {
                    alert("Error starting simulation");
                }
            } catch (error) {
                alert("Error starting simulation");
            }
        }
    }

    return (
        <div className="light-card h-36 flex flex-col items-start justify-start mt-5 pt-3 px-5 gap-4 pb-8">
            <h3 className="text-xl self-start secondary-text drop-shadow-[0_0_6px_rgba(34,211,238,0.9)]">Data Management</h3>
            <div className="flex items-start space-x-4 justify-start w-full">
                <div className="flex items-center space-x-4 justify-between w-full">
                    <span className="text-lg font-mono font-medium">Activate real time simulation server monitoring</span>
                    <ButtonSetting name={isSimulating ? "STOP" : "ACTIVATE"} action={handleToggleSimulation} />
                </div>
            </div>
            <div className="flex items-start space-x-4 justify-start w-full">
                <div className="flex items-center space-x-4 justify-between w-full">
                    <span className="text-lg font-mono font-medium">Connect your email account for real time alerts</span>
                    <ButtonSetting name={"CONNECT"} action={() => setIsModalOpen(true)} />
                </div>
            </div>
            <EmailConnectModal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} />
        </div>
    )
}
export default SettingCard2

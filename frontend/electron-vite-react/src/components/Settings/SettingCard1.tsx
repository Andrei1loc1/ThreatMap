import React from 'react'
import ToggleSetting from "@/components/Buttons/ToggleSetting";
import OptionSetting from "@/components/Buttons/OptionSetting";
import ButtonSetting from "@/components/Buttons/ButtonSetting";
import { useTheme } from '@/hooks/useTheme';

const SettingCard1 = () => {
    const { theme, changeTheme } = useTheme();

    const playSound = () => {
        try {
            const audioContext = new (window.AudioContext || (window as any).webkitAudioContext)();
            const oscillator = audioContext.createOscillator();
            const gainNode = audioContext.createGain();
            oscillator.connect(gainNode);
            gainNode.connect(audioContext.destination);
            oscillator.frequency.setValueAtTime(800, audioContext.currentTime);
            gainNode.gain.setValueAtTime(0.1, audioContext.currentTime);
            oscillator.start(audioContext.currentTime);
            oscillator.stop(audioContext.currentTime + 0.2);
        } catch (e) {
            console.warn('Sound not supported');
        }
    };

    const handleSoundToggle = (enabled: boolean) => {
        localStorage.setItem('soundEnabled', enabled.toString());
        if (enabled) {
            playSound(); // Play instant notification when activated
        }
    };

    const handlePopupToggle = (enabled: boolean) => {
        localStorage.setItem('popupEnabled', enabled.toString());
        if (enabled) {
            // Request permission and show instant notification
            if (Notification.permission === 'granted') {
                new Notification('Cyber Threat Alert', {
                    body: 'Pop-up notifications activated for critical threats.'
                });
            } else if (Notification.permission !== 'denied') {
                Notification.requestPermission().then(permission => {
                    if (permission === 'granted') {
                        new Notification('Cyber Threat Alert', {
                            body: 'Pop-up notifications activated for critical threats.'
                        });
                    }
                });
            }
        }
    };

    const handleResetSimulation = async () => {
        if (confirm('Are you sure you want to reset the simulation? This will delete all data.')) {
            try {
                const response = await fetch('http://localhost:8080/api/reset', {
                    method: 'DELETE',
                    headers: {
                        'X-API-KEY': import.meta.env.VITE_API_KEY,
                        'Content-Type': 'application/json',
                    },
                });
                if (response.ok) {
                    alert('Simulation reset successfully');
                    window.location.reload(); // Reload to reset frontend state
                } else {
                    alert('Failed to reset simulation');
                }
            } catch (error) {
                alert('Error resetting simulation');
            }
        }
    };

    return (
        <div className="light-card grid grid-cols-3 gap-8 px-4">
            <div className="flex flex-col w-full h-full items-start justify-start gap-4 mb-5">
                <h3 className="text-xl secondary-text drop-shadow-[0_0_6px_rgba(34,211,238,0.9)]">Notification</h3>
                <ToggleSetting settingText={"Activate cyber sound nootification"} onChange={handleSoundToggle}/>
                <ToggleSetting settingText={"Show pop-up notification on CRITICAL threat"} onChange={handlePopupToggle}/>
            </div>
            <div className="h-40 bg-cyan-300/40 rounded-lg w-2 drop-shadow-[0_0_6px_rgba(34,211,238,0.9)]" />
            <div className="flex flex-col w-full h-full items-start justify-start gap-4 mb-5">
                <h3 className="text-xl secondary-text drop-shadow-[0_0_6px_rgba(34,211,238,0.9)]">Display Settings</h3>
                <OptionSetting settingText={"Change the cyber theme app"} options={["Matrix Rain", "Purple Univers"]} onChange={(value) => changeTheme(value === 'Purple Univers' ? 'purple' : 'blue')} value={theme === 'purple' ? 'Purple Univers' : 'Matrix Rain'} />
                <div className="flex items-center space-x-4 justify-between w-full">
                    <span className="text-lg font-mono font-medium">Reset Simulation</span>
                    <ButtonSetting name="RESET" action={handleResetSimulation} />
                </div>
            </div>
        </div>
    )
}
export default SettingCard1

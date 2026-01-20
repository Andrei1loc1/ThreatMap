import React, { useState } from 'react';

interface EmailConnectModalProps {
    isOpen: boolean;
    onClose: () => void;
}

const EmailConnectModal: React.FC<EmailConnectModalProps> = ({ isOpen, onClose }) => {
    const [email, setEmail] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!email.trim()) {
            setError('Email is required');
            return;
        }
        setLoading(true);
        setError('');
        try {
            const response = await fetch('http://localhost:8080/api/connect-email', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-API-KEY': import.meta.env.VITE_API_KEY,
                },
                body: JSON.stringify({ email }),
            });
            if (response.ok) {
                onClose();
                setEmail('');
            } else {
                setError('Failed to connect email');
            }
        } catch (err) {
            setError('Network error');
        }
        setLoading(false);
    };

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-gray-900 border-2 border-cyan-500 rounded-lg p-6 w-96 shadow-lg">
                <h2 className="text-xl text-cyan-300 mb-4">Connect Email for Alerts</h2>
                <form onSubmit={handleSubmit}>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="Enter your email"
                        className="w-full p-2 bg-gray-800 text-cyan-300 border border-cyan-500 rounded mb-4"
                        required
                    />
                    {error && <p className="text-red-500 mb-4">{error}</p>}
                    <div className="flex justify-end space-x-2">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2 bg-gray-700 text-cyan-300 rounded hover:bg-gray-600"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={loading}
                            className="px-4 py-2 bg-cyan-500 text-black rounded hover:bg-cyan-400 disabled:opacity-50"
                        >
                            {loading ? 'Connecting...' : 'Connect'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default EmailConnectModal;
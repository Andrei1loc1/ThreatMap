import React, {
  createContext,
  useContext,
  useState,
  ReactNode,
  useCallback,
} from 'react';

interface IpData{
    ip: string,
    country: string;
    city: string;
    latitudine: number;
    longitudine: number;
    isp: string;
    org: string;
    isVpn: boolean;
}
interface IpDetectionContextType{
    ipData: IpData | null;
    ipDataList: IpData[];
    loading: boolean;
    error: string | null;
    fetchIpData: (ip: string) => Promise<void>;
    fetchLogIps: () => Promise<void>;
}

const IpDetectionContext = createContext<IpDetectionContextType | undefined>(undefined);

export const useIpDetection = () => {
    const context = useContext(IpDetectionContext);
    if (!context) {
        throw new Error('useIpDetection must be used within IpDetectionProvider');
    }
    return context;
}
interface IpDetectionProviderProps {
  children: ReactNode;
}
export const IpDetectionProvider: React.FC<IpDetectionProviderProps> = ({ children }) => {
  const [ipData, setIpData] = useState<IpData | null>(null);
  const [ipDataList, setIpDataList] = useState<IpData[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
    const fetchIpData = useCallback(async (ip: string) => {
        setLoading(true);
        setError(null);
        try {
            const response = await fetch(`http://localhost:8080/api/ip-detection/analyze?ip=${ip}`);
            if (response.status === 404) {
                setError('IP invalid');
                setIpData(null);
                return;
            }
            if (!response.ok) {
                throw new Error('Failed to fetch IP data');
            }
            const data: IpData = await response.json();
            setIpData(data);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Unknown error');
        } finally {
            setLoading(false);
        }
    }, []);
  const fetchLogIps = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch('http://localhost:8080/api/ip-detection/log-ips');
      if (!response.ok) {
        throw new Error('Failed to fetch log IPs');
      }
      const data: IpData[] = await response.json();
      setIpDataList(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    } finally {
      setLoading(false);
    }
  }, []);
  const value: IpDetectionContextType = {
    ipData,
    ipDataList,
    loading,
    error,
    fetchIpData,
    fetchLogIps,
  };
  return (
    <IpDetectionContext.Provider value={value}>
      {children}
    </IpDetectionContext.Provider>
  );
};

import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react'
import { AttackEvent } from '@/utils/AttackEvent'

type AttackFeedContextValue = {
  attacks: AttackEvent[]
  isLoading: boolean
  error: string | null
  refresh: () => Promise<void>
  uploadLog: (file: File) => Promise<void>
}

const AttackFeedContext = createContext<AttackFeedContextValue | undefined>(undefined)

const playSound = () => {
  if (localStorage.getItem('soundEnabled') === 'true') {
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
  }
};

export const AttackFeedProvider = ({ children }: { children: React.ReactNode }) => {
  const [attacks, setAttacks] = useState<AttackEvent[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const previousAttacksCount = useRef(0)
  const attacksRef = useRef(attacks)

  useEffect(() => {
    attacksRef.current = attacks;
  }, [attacks]);

  useEffect(() => {
    const showPeriodicPopup = () => {
      const enabled = localStorage.getItem('popupEnabled') === 'true';
      const currentAttacks = attacksRef.current;
      if (enabled && currentAttacks.length > 0) {
        if (Notification.permission === 'granted') {
          new Notification('Cyber Threat Alert', {
            body: `Total threats detected: ${currentAttacks.length}`,
            icon: '/favicon.ico' // optional
          });
        }
      }
    };

    const interval = setInterval(showPeriodicPopup, 600000); // 10 minutes
    return () => clearInterval(interval);
  }, [])

  const refresh = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const resp = await fetch('http://localhost:8080/api/attacks', {
        headers: {
          'X-API-KEY': import.meta.env.VITE_API_KEY,
        },
      })
      if (!resp.ok) {
        throw new Error(`Request failed with status ${resp.status}`)
      }

      const data: AttackEvent[] = await resp.json()
      setAttacks(data)
      if (data.length > previousAttacksCount.current) {
        playSound()
      }
      previousAttacksCount.current = data.length
    } catch (err) {
      setError((err as Error).message)
    } finally {
      setIsLoading(false)
    }
  }, [])

  const uploadLog = useCallback(
    async (file: File) => {
      const formData = new FormData()
      formData.append('file', file)

      setIsLoading(true)
      setError(null)

       try {
         const resp = await fetch('http://localhost:8080/api/process', {
           method: 'POST',
           body: formData,
           headers: {
             'X-API-KEY': import.meta.env.VITE_API_KEY,
           },
         })

        if (!resp.ok) {
          throw new Error(`Upload failed with status ${resp.status}`)
        }

        const data: AttackEvent[] = await resp.json()
        setAttacks(data)
      } catch (err) {
        setError((err as Error).message)
        throw err
      } finally {
        setIsLoading(false)
      }
    },
    [],
  )

  useEffect(() => {
    refresh()
    const intervalId = window.setInterval(refresh, 10000)
    return () => window.clearInterval(intervalId)
  }, [refresh])

  const value = useMemo(
    () => ({
      attacks,
      isLoading,
      error,
      refresh,
      uploadLog,
    }),
    [attacks, error, isLoading, refresh, uploadLog],
  )

  return <AttackFeedContext.Provider value={value}>{children}</AttackFeedContext.Provider>
}

export const useAttackFeed = () => {
  const ctx = useContext(AttackFeedContext)
  if (!ctx) {
    throw new Error('useAttackFeed must be used inside AttackFeedProvider')
  }
  return ctx
}

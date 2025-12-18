import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
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

export const AttackFeedProvider = ({ children }: { children: React.ReactNode }) => {
  const [attacks, setAttacks] = useState<AttackEvent[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const refresh = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const resp = await fetch('http://localhost:8080/api/attacks')
      if (!resp.ok) {
        throw new Error(`Request failed with status ${resp.status}`)
      }

      const data: AttackEvent[] = await resp.json()
      setAttacks(data)
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

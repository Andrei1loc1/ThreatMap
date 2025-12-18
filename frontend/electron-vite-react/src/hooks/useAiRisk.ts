import { useCallback, useState } from 'react'

type AiRiskResponse = {
  dangerPercent: number
}

export const useAiRisk = () => {
  const [percent, setPercent] = useState<number | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchRisk = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      const resp = await fetch('http://localhost:8080/api/stats/ai-risk')
      if (!resp.ok) {
        throw new Error(`AI risk request failed with status ${resp.status}`)
      }
      const data: AiRiskResponse = await resp.json()
      setPercent(typeof data.dangerPercent === 'number' ? data.dangerPercent : null)
      return data.dangerPercent ?? null
    } catch (err) {
      setError((err as Error).message)
      setPercent(null)
      return null
    } finally {
      setIsLoading(false)
    }
  }, [])

  return { percent, isLoading, error, fetchRisk }
}

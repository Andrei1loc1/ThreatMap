import { useCallback, useState } from 'react'

type AiRiskResponse = {
  dangerPercent: number
}

export const useAiRisk = () => {
  const [percent, setPercent] = useState<number | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchRisk = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      // Send sample log lines for AI analysis
      const logLines = [
        "2023-12-18T22:42:31 host sshd[123]: Failed password for root from 203.0.113.1 port 22 ssh2",
        "2023-12-18T22:42:33 host sshd[123]: Failed password for admin from 89.248.172.10 port 22 ssh2",
        "2023-12-18T22:42:36 host sshd[123]: Accepted password for user from 185.123.45.67 port 22 ssh2"
      ];
      const resp = await fetch('http://localhost:8080/api/stats/ai-risk', {
        method: 'POST',
        headers: {
          'X-API-KEY': import.meta.env.VITE_API_KEY,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(logLines),
      });
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

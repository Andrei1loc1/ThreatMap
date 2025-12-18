import { useMemo } from 'react'
import { AttackEvent } from '@/utils/AttackEvent'

export const useDashboardData = (attacks: AttackEvent[], FAILURE_THRESHOLD: number) => {
  const flaggedIps = useMemo(
    () => attacks.filter((attack) => attack.failedAttempts >= FAILURE_THRESHOLD),
    [attacks, FAILURE_THRESHOLD],
  )

  const totalFailedAttempts = useMemo(
    () => attacks.reduce((sum, attack) => sum + attack.failedAttempts, 0),
    [attacks],
  )

  return {
    flaggedIps,
    totalFailedAttempts,
  }
}
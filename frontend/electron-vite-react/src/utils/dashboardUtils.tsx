import React, {useRef, useState} from 'react'
import { AttackEvent } from './AttackEvent'
import LoadingSpinner from '@/components/loadings/LoadingSpinner'

export const handleLoadClick = (fileInputRef : React.RefObject<HTMLInputElement>) => {
    return (_event: React.MouseEvent<HTMLButtonElement>) => fileInputRef.current?.click()
}

export const handleFileChange = (
    setUploadMessage: React.Dispatch<React.SetStateAction<string>>,
    uploadLog: (file: File) => Promise<void>,
    isLoading: boolean
) => {
    return async (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0]
        if (!file) return

        setUploadMessage(`Se proceseaza ${file.name}...`)
        try {
            await uploadLog(file)
            setUploadMessage(`Am incarcat ${file.name}`)
        } catch (err) {
            const message = err instanceof Error ? err.message : 'Nu am putut incarca logul'
            setUploadMessage(message)
        } finally {
            event.target.value = ''
        }
    }
}

export const renderTotalAttacks = (
  isLoading: boolean,
  error: string | null,
  flaggedIps: AttackEvent[],
  totalFailedAttempts: number
): JSX.Element => {
  if (isLoading) {
    return <LoadingSpinner text="Loading latest events..." />
  }
  if (error) {
    return <p className='text-sm text-red-400'>Failed to load: {error}</p>
  }
  if (!flaggedIps.length) {
    return (
      <div className='text-sm text-gray-400 space-y-1'>
        <p>Incarca un fisier de log pentru a reimprospata datele.</p>
      </div>
    )
  }
  return (
    <div className="block text-center">
      <p className='text-lg font-bold text-white'>Total Attacks</p>
      <p className='text-2xl font-bold text-white'>{totalFailedAttempts}</p>
    </div>
  )
}

export const renderActiveThreats = (
  isLoading: boolean,
  error: string | null,
  flaggedIps: AttackEvent[]
): JSX.Element => {
  if (isLoading) {
    return <LoadingSpinner text="Loading..." />
  }
  if (error) {
    return <p className='text-sm text-red-400'>Failed to load: {error}</p>
  }
  if (!flaggedIps.length) {
    return (
      <div className='text-sm text-gray-400 space-y-1'>
        <p>No active threats detected</p>
      </div>
    )
  }

  return (
    <div className="block text-center">
      <p className='text-lg font-bold text-white'>Active Threats</p>
      <p className='text-2xl font-bold text-white'>{flaggedIps.length}</p>
    </div>
  )
}

export const renderTopAttackVectors = (
  isLoading: boolean,
  error: string | null,
  flaggedIps: AttackEvent[]
): JSX.Element => {
  if (isLoading) {
    return <LoadingSpinner text="Loading..." />
  }
  if (error) {
    return <p className='text-sm text-red-400'>Failed to load: {error}</p>
  }
  if (!flaggedIps.length) {
    return (
      <div className='text-sm text-gray-400 space-y-1'>
        <p>No attack data available</p>
      </div>
    )
  }

  const uniqueCountries = new Set(flaggedIps.map(attack => attack.location?.country).filter(Boolean))

  return (
    <div className="block text-center">
      <p className='text-lg font-bold text-white'>Attack Sources</p>
      <p className='text-2xl font-bold text-white'>{uniqueCountries.size}</p>
    </div>
  )
}

export const renderAttackFeed = (
  isLoading: boolean,
  error: string | null,
  flaggedIps: AttackEvent[],
  FAILURE_THRESHOLD: number
): JSX.Element => {
  if (isLoading) {
    return <LoadingSpinner text="Loading latest events..." />
  }

  if (error) {
    return <p className='text-sm text-red-400'>Failed to load: {error}</p>
  }

  if (!flaggedIps.length) {
    return (
      <>
        <p>Niciun IP nu a depasit pragul de {FAILURE_THRESHOLD} incercari esuate.</p>
        <p>Incarca un fisier de log pentru a reimprospata datele.</p>
      </>
    )
  }

  return (
    <div className='overflow-x-auto'>
      <table className='min-w-full text-left text-sm text-slate-200'>
        <thead className='text-xs uppercase tracking-widest text-slate-400 border-b border-white/5'>
          <tr>
            <th className='py-2 pr-4'>IP</th>
            <th className='py-2 pr-4'>Fail-uri</th>
            <th className='py-2 pr-4'>Locatie</th>
            <th className='py-2'>Detectat</th>
          </tr>
        </thead>
        <tbody>
          {flaggedIps.map((attack, idx) => (
            <tr key={`${attack.adresaIP}-${idx}`} className='border-b border-white/5'>
              <td className='py-2 pr-4 font-mono text-slate-100'>{attack.adresaIP}</td>
              <td className='py-2 pr-4'>{attack.failedAttempts}</td>
              <td className='py-2 pr-4 text-slate-300'>
                {attack.location
                  ? `${attack.location.city}, ${attack.location.country}`
                  : 'Locatie necunoscuta'}
              </td>
              <td className='py-2 text-xs text-slate-400'>
                {attack.detectionTime
                  ? new Date(attack.detectionTime).toLocaleString()
                  : 'necunoscut'}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
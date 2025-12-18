export interface GeoLocation {
  country: string
  city: string
  latitudine: number
  longitudine: number
}

export interface AttackEvent {
  adresaIP: string
  failedAttempts: number
  detectionTime: string        
  location: GeoLocation | null
}
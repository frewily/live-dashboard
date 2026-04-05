export interface DeviceState {
  deviceId: string
  deviceName: string
  platform: string
  appName: string
  displayTitle: string
  online: boolean
  lastSeenAt: number
}

export interface CurrentResponse {
  devices: DeviceState[]
  serverTime: string
  viewerCount: number
}

export async function fetchCurrent(): Promise<CurrentResponse> {
  const res = await fetch('/api/current')
  if (!res.ok) {
    throw new Error(`HTTP ${res.status}`)
  }
  return res.json()
}

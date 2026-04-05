<script setup lang="ts">
import { computed } from 'vue'
import { useDashboard } from './composables/useDashboard'
import Header from './components/Header.vue'
import CurrentStatus from './components/CurrentStatus.vue'
import DeviceCard from './components/DeviceCard.vue'

const { current, loading, error, viewerCount } = useDashboard()

const devices = computed(() => {
  const arr = current.value?.devices ?? []
  return [...arr].sort((a, b) => a.deviceId.localeCompare(b.deviceId))
})

const selectedDevice = computed(() => {
  if (devices.value.length === 0) return undefined
  return devices.value.find(d => d.online) || devices.value[0]
})
</script>

<template>
  <div class="container">
    <Header 
      :server-time="current?.serverTime"
      :viewer-count="viewerCount" 
    />

    <div v-if="error" class="error">
      <p>(>_&lt;) 连接失败了喵...</p>
      <p style="font-size: 12px; margin-top: 4px;">别担心，会自动重试的~</p>
    </div>

    <div v-if="loading && !current" class="loading">
      <p style="font-size: 24px;">(=^-ω-^=)</p>
      <div class="loading-dots">
        <span>●</span>
        <span>●</span>
        <span>●</span>
      </div>
      <p style="font-size: 12px; margin-top: 8px;">正在加载喵~</p>
    </div>

    <template v-if="current">
      <CurrentStatus :device="selectedDevice" />

      <h2 style="font-size: 12px; color: var(--color-text-muted); text-transform: uppercase; margin-bottom: 12px;">
        Devices
      </h2>

      <div v-if="devices.length === 0" style="text-align: center; padding: 24px;">
        <p style="font-size: 18px; margin-bottom: 8px;">( -ω-) zzZ</p>
        <p style="font-size: 12px; color: var(--color-text-muted);">还没有设备连接呢~</p>
      </div>

      <DeviceCard 
        v-for="device in devices" 
        :key="device.deviceId" 
        :device="device" 
      />
    </template>

    <footer class="footer">
      Monika Now · 每 10 秒自动刷新 · (◕ᴗ◕)
    </footer>
  </div>
</template>

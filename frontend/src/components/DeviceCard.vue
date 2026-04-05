<script setup lang="ts">
import { computed } from 'vue'
import type { DeviceState } from '../api'

const props = defineProps<{
  device: DeviceState
}>()

const platformIcon = computed(() => {
  const icons: Record<string, string> = {
    windows: '🖥️',
    android: '📱',
    macos: '🍎'
  }
  return icons[props.device.platform] || '💻'
})
</script>

<template>
  <div class="device-card" :class="{ active: device.online }">
    <div class="device-header">
      <span class="device-name">
        {{ platformIcon }} {{ device.deviceName }}
      </span>
      <span class="device-status" :class="device.online ? 'online' : 'offline'">
        {{ device.online ? '在线' : '离线' }}
      </span>
    </div>
    <div class="device-info">
      {{ device.online ? device.appName : '(-.-) zzZ' }}
    </div>
    <div v-if="device.online && device.displayTitle" class="device-info" style="margin-top: 4px;">
      📝 {{ device.displayTitle }}
    </div>
  </div>
</template>

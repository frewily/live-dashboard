<script setup lang="ts">
import { computed } from 'vue'
import type { DeviceState } from '../api'

const props = defineProps<{
  device?: DeviceState
}>()

const isOnline = computed(() => props.device?.online ?? false)

const description = computed(() => {
  if (!props.device || !isOnline.value) return null
  return props.device.appName
})
</script>

<template>
  <div class="status-bubble">
    <div v-if="isOnline">
      <p class="status-label">Monika 现在...</p>
      <p class="status-text">{{ description }}</p>
      <p v-if="device?.displayTitle" style="font-size: 14px; color: var(--color-text-muted); margin-top: 8px;">
        📝 {{ device.displayTitle }}
      </p>
    </div>
    <div v-else style="text-align: center; padding: 8px 0;">
      <p style="font-size: 24px; margin-bottom: 8px;">(-.-)zzZ</p>
      <p style="font-size: 14px; color: var(--color-text-muted);">Monika 不在电脑前喵~</p>
    </div>
  </div>
</template>

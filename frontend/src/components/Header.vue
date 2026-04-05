<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  serverTime?: string
  viewerCount: number
}>()

const timeStr = computed(() => {
  if (!props.serverTime) return '--:--'
  const d = new Date(props.serverTime)
  if (isNaN(d.getTime())) return '--:--'
  return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
})

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour >= 5 && hour < 9) return { kaomoji: '(* ^ ω ^)', text: '早上好呀~' }
  if (hour >= 9 && hour < 12) return { kaomoji: "(o´▽`o)", text: '上午好呀~' }
  if (hour >= 12 && hour < 14) return { kaomoji: "(´～`)", text: '午饭时间~' }
  if (hour >= 14 && hour < 18) return { kaomoji: '(◕‿◕)', text: '下午好呀~' }
  if (hour >= 18 && hour < 22) return { kaomoji: '(✿╹◡╹)', text: '晚上好呀~' }
  return { kaomoji: '(￣o￣) . z Z', text: '夜深了喵~' }
})
</script>

<template>
  <header class="header">
    <div>
      <h1 class="title">Monika Now</h1>
      <p class="greeting">
        <span>{{ greeting.kaomoji }}</span>
        {{ greeting.text }}
      </p>
    </div>
    <div style="text-align: right;">
      <p v-if="viewerCount > 0" class="viewer-count">
        {{ viewerCount }} 人在看喵~
      </p>
      <p class="time">{{ timeStr }}</p>
    </div>
  </header>
</template>

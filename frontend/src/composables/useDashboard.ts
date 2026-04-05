import { ref, onMounted, onUnmounted } from 'vue'
import { fetchCurrent, type CurrentResponse } from '../api'

const POLL_INTERVAL = 10 * 1000

export function useDashboard() {
  const current = ref<CurrentResponse | null>(null)
  const loading = ref(true)
  const error = ref<string | null>(null)
  const viewerCount = ref(0)

  let pollId: ReturnType<typeof setInterval> | null = null

  const doFetch = async () => {
    try {
      error.value = null
      const data = await fetchCurrent()
      current.value = data
      viewerCount.value = data.viewerCount
      loading.value = false
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to fetch data'
    }
  }

  onMounted(() => {
    doFetch()
    pollId = setInterval(doFetch, POLL_INTERVAL)
  })

  onUnmounted(() => {
    if (pollId !== null) {
      clearInterval(pollId)
    }
  })

  return {
    current,
    loading,
    error,
    viewerCount
  }
}

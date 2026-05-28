import type { AuthResponse } from '~/types/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(null)
  const userId = ref<number | null>(null)
  const nickname = ref<string | null>(null)

  const isLoggedIn = computed(() => !!token.value)

  function init() {
    if (process.client) {
      const saved = localStorage.getItem('auth')
      if (saved) {
        try {
          const data = JSON.parse(saved) as AuthResponse
          token.value = data.token
          userId.value = data.userId
          nickname.value = data.nickname
        } catch { /* ignore */ }
      }
    }
  }

  function setAuth(data: AuthResponse) {
    token.value = data.token
    userId.value = data.userId
    nickname.value = data.nickname
    localStorage.setItem('auth', JSON.stringify(data))
  }

  function logout() {
    token.value = null
    userId.value = null
    nickname.value = null
    localStorage.removeItem('auth')
    navigateTo('/login')
  }

  // Hydrate on store creation
  init()

  return { token, userId, nickname, isLoggedIn, setAuth, logout }
})

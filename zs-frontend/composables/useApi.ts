import type { ApiError } from '~/types/api'

export function useApi() {
  const auth = useAuthStore()

  async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
    const headers: Record<string, string> = {}
    if (auth.token) {
      headers['Authorization'] = `Bearer ${auth.token}`
    }
    if (body) {
      headers['Content-Type'] = 'application/json'
    }

    const baseURL = 'http://localhost:8080'
    return $fetch<T>(path, {
      baseURL,
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined,
      onResponseError({ response }) {
        if (response.status === 401 || response.status === 403) {
          auth.logout()
        }
      }
    })
  }

  return {
    get: <T>(path: string) => request<T>('GET', path),
    post: <T>(path: string, body?: unknown) => request<T>('POST', path, body),
    put: <T>(path: string, body?: unknown) => request<T>('PUT', path, body),
    del: <T>(path: string) => request<T>('DELETE', path),
  }
}

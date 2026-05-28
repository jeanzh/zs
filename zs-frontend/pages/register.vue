<script setup lang="ts">
definePageMeta({ middleware: undefined })

const api = useApi()
const auth = useAuthStore()
const router = useRouter()

const nickname = ref('')
const phone = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function handleRegister() {
  error.value = ''
  if (nickname.value.length < 2) {
    error.value = '昵称至少2个字'
    return
  }
  if (phone.value.length !== 11) {
    error.value = '请输入正确的手机号'
    return
  }
  if (password.value.length < 6) {
    error.value = '密码至少6位'
    return
  }
  loading.value = true
  try {
    const data = await api.post<any>('/api/auth/register', {
      nickname: nickname.value,
      phone: phone.value,
      password: password.value,
    })
    if (data.error) {
      error.value = data.error
      return
    }
    auth.setAuth(data)
    router.push('/')
  } catch {
    error.value = '注册失败，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="max-w-sm mx-auto mt-8">
    <h1 class="text-2xl font-bold text-center mb-8">注册</h1>
    <form class="space-y-4" @submit.prevent="handleRegister">
      <div>
        <label class="block text-sm text-gray-600 mb-1">昵称</label>
        <input
          v-model="nickname"
          type="text"
          maxlength="32"
          class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg focus:outline-none focus:border-[#c62828] text-sm"
          placeholder="请输入昵称（2-32个字）"
        />
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">手机号</label>
        <input
          v-model="phone"
          type="text"
          maxlength="11"
          class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg focus:outline-none focus:border-[#c62828] text-sm"
          placeholder="请输入手机号"
        />
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">密码</label>
        <input
          v-model="password"
          type="password"
          class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg focus:outline-none focus:border-[#c62828] text-sm"
          placeholder="请输入密码（6-64位）"
        />
      </div>
      <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>
      <button
        type="submit"
        :disabled="loading"
        class="w-full py-2.5 bg-[#c62828] text-white rounded-lg text-sm font-medium hover:bg-red-800 disabled:opacity-50 transition-colors"
      >
        {{ loading ? '注册中...' : '注册' }}
      </button>
    </form>
    <p class="text-center text-sm text-gray-500 mt-6">
      已有账号？<NuxtLink to="/login" class="text-[#c62828]">登录</NuxtLink>
    </p>
  </div>
</template>

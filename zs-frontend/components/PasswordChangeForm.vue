<script setup lang="ts">
const api = useApi()

const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const error = ref('')
const success = ref('')
const loading = ref(false)

async function handleSubmit() {
  error.value = ''
  success.value = ''
  if (!oldPassword.value || !newPassword.value) {
    error.value = '请填写所有字段'
    return
  }
  if (newPassword.value.length < 6) {
    error.value = '新密码至少6位'
    return
  }
  if (newPassword.value !== confirmPassword.value) {
    error.value = '两次密码不一致'
    return
  }
  loading.value = true
  try {
    const data = await api.put<any>('/api/user/password', {
      oldPassword: oldPassword.value,
      newPassword: newPassword.value,
    })
    if (data.error) {
      error.value = data.error
    } else {
      success.value = '密码修改成功'
      oldPassword.value = ''
      newPassword.value = ''
      confirmPassword.value = ''
    }
  } catch {
    error.value = '修改失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <form class="space-y-4 max-w-sm" @submit.prevent="handleSubmit">
    <div>
      <label class="block text-sm text-gray-600 mb-1">原密码</label>
      <input v-model="oldPassword" type="password" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828]" />
    </div>
    <div>
      <label class="block text-sm text-gray-600 mb-1">新密码</label>
      <input v-model="newPassword" type="password" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828]" />
    </div>
    <div>
      <label class="block text-sm text-gray-600 mb-1">确认新密码</label>
      <input v-model="confirmPassword" type="password" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828]" />
    </div>
    <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>
    <p v-if="success" class="text-green-600 text-sm">{{ success }}</p>
    <button
      type="submit"
      :disabled="loading"
      class="px-6 py-2.5 bg-[#c62828] text-white rounded-lg text-sm font-medium hover:bg-red-800 disabled:opacity-50 transition-colors"
    >
      {{ loading ? '保存中...' : '修改密码' }}
    </button>
  </form>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })

const api = useApi()

const bookId = ref<number | null>(null)
const seq = ref<number | null>(null)
const title = ref('')
const sourceUrl = ref('')
const wordCount = ref<number | null>(null)
const error = ref('')
const success = ref('')
const loading = ref(false)

async function handleSubmit() {
  error.value = ''
  success.value = ''
  if (!bookId.value || !seq.value || !title.value) {
    error.value = '请填写书籍ID、章节序号和标题'
    return
  }
  loading.value = true
  try {
    const data = await api.post<any>(`/api/admin/books/${bookId.value}/chapters`, {
      seq: seq.value,
      title: title.value,
      sourceUrl: sourceUrl.value || undefined,
      wordCount: wordCount.value || undefined,
    })
    if (data.error) {
      error.value = data.error
    } else {
      success.value = `章节「${data.title}」添加成功 (ID: ${data.id})`
      seq.value = (seq.value || 1) + 1
      title.value = ''
      sourceUrl.value = ''
      wordCount.value = null
    }
  } catch {
    error.value = '添加失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="max-w-lg mx-auto">
    <h1 class="text-xl font-bold mb-6">管理 - 添加章节</h1>

    <form class="space-y-4" @submit.prevent="handleSubmit">
      <div>
        <label class="block text-sm text-gray-600 mb-1">书籍 ID *</label>
        <input v-model.number="bookId" type="number" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828]" />
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">章节序号 *</label>
        <input v-model.number="seq" type="number" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828]" />
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">章节标题 *</label>
        <input v-model="title" maxlength="512" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828]" />
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">来源 URL</label>
        <input v-model="sourceUrl" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828]" />
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">字数</label>
        <input v-model.number="wordCount" type="number" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828]" />
      </div>
      <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>
      <p v-if="success" class="text-green-600 text-sm">{{ success }}</p>
      <button
        type="submit"
        :disabled="loading"
        class="w-full py-3 bg-[#c62828] text-white rounded-lg font-medium hover:bg-red-800 disabled:opacity-50 transition-colors"
      >
        {{ loading ? '添加中...' : '添加章节' }}
      </button>
    </form>
  </div>
</template>

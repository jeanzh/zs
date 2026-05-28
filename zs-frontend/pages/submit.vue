<script setup lang="ts">
import type { BookSubmitResponse } from '~/types/api'

definePageMeta({ middleware: 'auth' })

const api = useApi()
const router = useRouter()

const form = reactive({
  title: '',
  author: '',
  tags: '',
  summary: '',
  worldBackground: '',
  highlights: '',
  rating: 0,
  sourceUrl: '',
  bookUrl: '',
  latestUpdateUrl: '',
  reviewContent: '',
})

const error = ref('')
const loading = ref(false)
const result = ref<BookSubmitResponse | null>(null)

async function handleSubmit() {
  error.value = ''
  if (!form.title || !form.author) {
    error.value = '书名和作者为必填项'
    return
  }
  if (form.rating < 1 || form.rating > 5) {
    error.value = '请选择评分'
    return
  }
  if (form.reviewContent.length < 10) {
    error.value = '评价内容至少10个字'
    return
  }
  loading.value = true
  try {
    const data = await api.post<any>('/api/books/submit', {
      title: form.title,
      author: form.author,
      tags: form.tags ? form.tags.split(',').map(t => t.trim()).filter(Boolean) : [],
      summary: form.summary || undefined,
      worldBackground: form.worldBackground || undefined,
      highlights: form.highlights || undefined,
      rating: form.rating,
      sourceUrl: form.sourceUrl || undefined,
      bookUrl: form.bookUrl || undefined,
      latestUpdateUrl: form.latestUpdateUrl || undefined,
      reviewContent: form.reviewContent,
    })
    if (data.error) {
      error.value = data.error
      return
    }
    result.value = data
  } catch {
    error.value = '提交失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="max-w-lg mx-auto">
    <h1 class="text-xl font-bold mb-6">提交投稿</h1>

    <!-- Success result -->
    <div v-if="result" class="bg-green-50 border border-green-200 rounded-xl p-6 text-center">
      <p class="text-green-700 font-medium text-lg mb-2">投稿成功！</p>
      <p class="text-sm text-green-600 mb-4">
        {{ result.newlyCreated ? '新书已入库' : '该书已存在，评价已追加' }}
        · 当前评分 {{ result.avgRating }}（{{ result.reviewCount }}人评价）
      </p>
      <div class="flex gap-3 justify-center">
        <NuxtLink
          :to="`/books/${result.bookId}`"
          class="px-4 py-2 bg-[#c62828] text-white rounded-lg text-sm hover:bg-red-800 transition-colors"
        >
          查看书籍
        </NuxtLink>
        <button
          class="px-4 py-2 border border-[#e8e4df] rounded-lg text-sm hover:bg-gray-50 transition-colors"
          @click="result = null"
        >
          继续投稿
        </button>
      </div>
    </div>

    <!-- Form -->
    <form v-else class="space-y-4" @submit.prevent="handleSubmit">
      <div class="grid grid-cols-2 gap-4">
        <div>
          <label class="block text-sm text-gray-600 mb-1">书名 *</label>
          <input v-model="form.title" maxlength="256" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828]" />
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1">作者 *</label>
          <input v-model="form.author" maxlength="128" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828]" />
        </div>
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">标签（逗号分隔）</label>
        <input v-model="form.tags" placeholder="仙侠, 玄幻, 武侠" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828]" />
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">简介</label>
        <textarea v-model="form.summary" maxlength="2000" rows="2" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828] resize-none" />
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">世界观设定</label>
        <textarea v-model="form.worldBackground" maxlength="2000" rows="2" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828] resize-none" />
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">精彩看点</label>
        <textarea v-model="form.highlights" maxlength="2000" rows="2" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828] resize-none" />
      </div>
      <div class="grid grid-cols-2 gap-4">
        <div>
          <label class="block text-sm text-gray-600 mb-1">书籍 URL</label>
          <input v-model="form.bookUrl" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828]" />
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1">来源 URL</label>
          <input v-model="form.sourceUrl" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828]" />
        </div>
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">最新更新 URL</label>
        <input v-model="form.latestUpdateUrl" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828]" />
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">评分 *</label>
        <RatingStars v-model="form.rating" :readonly="false" />
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">评价内容 *（至少10字）</label>
        <textarea v-model="form.reviewContent" maxlength="2000" rows="3" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828] resize-none" placeholder="写下你对这本书的评价..." />
        <p class="text-xs text-gray-400 mt-1">{{ form.reviewContent.length }}/2000</p>
      </div>
      <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>
      <button
        type="submit"
        :disabled="loading"
        class="w-full py-3 bg-[#c62828] text-white rounded-lg font-medium hover:bg-red-800 disabled:opacity-50 transition-colors"
      >
        {{ loading ? '提交中...' : '提交投稿' }}
      </button>
    </form>
  </div>
</template>

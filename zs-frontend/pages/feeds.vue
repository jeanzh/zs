<script setup lang="ts">
import type { FeedItemResponse, PagedResponse } from '~/types/api'

definePageMeta({ middleware: 'auth' })

const api = useApi()

const page = ref(0)
const data = ref<PagedResponse<FeedItemResponse> | null>(null)
const loading = ref(true)
const error = ref('')

async function fetchData() {
  loading.value = true
  error.value = ''
  try {
    data.value = await api.get<PagedResponse<FeedItemResponse>>(
      `/api/user/feeds?page=${page.value}&size=20`
    )
  } catch {
    error.value = '加载失败'
  } finally {
    loading.value = false
  }
}

async function dismissFeed(feedId: number) {
  try {
    await api.del(`/api/user/feeds/${feedId}`)
    fetchData()
  } catch { /* ignore */ }
}

function goPage(p: number) {
  page.value = p
  fetchData()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(fetchData)
</script>

<template>
  <div>
    <h1 class="text-xl font-bold mb-6">动态</h1>

    <div v-if="loading" class="text-center py-20 text-gray-400">加载中...</div>
    <p v-else-if="error" class="text-center py-20 text-red-500">{{ error }}</p>
    <div v-else-if="data && data.content.length > 0" class="space-y-3">
      <div
        v-for="feed in data.content"
        :key="feed.feedId"
        class="bg-white rounded-xl p-4 border border-[#e8e4df]"
      >
        <div class="flex items-start justify-between">
          <div class="flex-1 min-w-0">
            <NuxtLink
              :to="`/books/${feed.bookId}`"
              class="text-sm font-medium text-gray-900 hover:text-[#c62828] transition-colors"
            >
              {{ feed.bookTitle }}
            </NuxtLink>
            <p class="text-xs text-gray-500 mt-0.5">{{ feed.bookAuthor }}</p>
            <p class="text-sm text-[#c62828] mt-2">
              更新：{{ feed.chapterTitle || '新章节' }}
            </p>
            <p class="text-xs text-gray-400 mt-1">{{ new Date(feed.createdAt).toLocaleString('zh-CN') }}</p>
          </div>
          <button
            class="shrink-0 ml-3 px-3 py-1 text-xs text-gray-400 border border-[#e8e4df] rounded hover:text-red-500 hover:border-red-200 transition-colors"
            @click="dismissFeed(feed.feedId)"
          >
            清除
          </button>
        </div>
      </div>
      <Pagination
        :page="data.page"
        :total-pages="data.totalPages"
        :last="data.last"
        @change="goPage"
      />
    </div>
    <p v-else class="text-center py-20 text-gray-400">暂无更新</p>
  </div>
</template>

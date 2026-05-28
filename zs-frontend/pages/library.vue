<script setup lang="ts">
import type { BookListItemResponse, PagedResponse } from '~/types/api'
import { formatDate } from '~/utils'

const api = useApi()

const page = ref(0)
const selectedTag = ref('')
const data = ref<PagedResponse<BookListItemResponse> | null>(null)
const allTags = ref<string[]>([])
const loading = ref(true)

async function fetchData() {
  loading.value = true
  try {
    const params = new URLSearchParams({ page: String(page.value), size: String(20) })
    if (selectedTag.value) params.set('tag', selectedTag.value)
    data.value = await api.get<PagedResponse<BookListItemResponse>>(
      `/api/books/library?${params.toString()}`
    )
    // Collect all unique tags for filter buttons
    if (!selectedTag.value && data.value) {
      const tagSet = new Set<string>()
      data.value.content.forEach(b => b.tags?.forEach(t => tagSet.add(t)))
      allTags.value = [...tagSet].sort()
    }
  } catch { /* ignore */ } finally {
    loading.value = false
  }
}

function selectTag(tag: string) {
  selectedTag.value = selectedTag.value === tag ? '' : tag
  page.value = 0
  fetchData()
}

function goPage(p: number) {
  page.value = p
  fetchData()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const statusLabel: Record<string, string> = {
  ONGOING: '连载中',
  COMPLETED: '已完结',
  PAUSED: '暂停',
}

onMounted(fetchData)
</script>

<template>
  <div>
    <h1 class="text-xl font-bold mb-4">书库</h1>

    <!-- Tag filter -->
    <div class="flex flex-wrap gap-2 mb-5">
      <button
        class="px-3 py-1 rounded-full text-xs transition-colors"
        :class="!selectedTag
          ? 'bg-[#c62828] text-white'
          : 'bg-white border border-[#e8e4df] text-gray-600 hover:border-[#c62828]'"
        @click="selectedTag = ''; page = 0; fetchData()"
      >
        全部
      </button>
      <button
        v-for="tag in allTags.slice(0, 20)" :key="tag"
        class="px-3 py-1 rounded-full text-xs transition-colors"
        :class="selectedTag === tag
          ? 'bg-[#c62828] text-white'
          : 'bg-white border border-[#e8e4df] text-gray-600 hover:border-[#c62828]'"
        @click="selectTag(tag)"
      >
        {{ tag }}
      </button>
    </div>

    <!-- Book list -->
    <div v-if="loading" class="text-center py-20 text-gray-400">加载中...</div>
    <div v-else-if="data && data.content.length > 0" class="space-y-3">
      <NuxtLink
        v-for="book in data.content" :key="book.id"
        :to="`/books/${book.id}`"
        class="block bg-white rounded-xl p-4 border border-[#e8e4df] hover:border-[#c62828] transition-colors"
      >
        <div class="flex items-start justify-between gap-4">
          <div class="flex-1 min-w-0">
            <h3 class="text-sm font-medium text-gray-900 truncate">
              {{ book.title }}
            </h3>
            <p class="text-xs text-gray-500 mt-1">{{ book.author }}</p>
            <div class="flex flex-wrap gap-1 mt-2">
              <span
                v-for="tag in book.tags?.slice(0, 3)" :key="tag"
                class="px-1.5 py-0.5 bg-red-50 text-[#c62828] rounded text-xs"
              >
                {{ tag }}
              </span>
              <span
                class="px-1.5 py-0.5 text-xs rounded"
                :class="book.serialStatus === 'COMPLETED'
                  ? 'bg-green-50 text-green-700'
                  : 'bg-blue-50 text-blue-700'"
              >
                {{ statusLabel[book.serialStatus] || book.serialStatus }}
              </span>
            </div>
            <p v-if="book.summary" class="text-xs text-gray-400 mt-2 line-clamp-2">
              {{ book.summary }}
            </p>
          </div>
          <div class="text-right shrink-0">
            <div class="text-sm font-medium text-[#f9a825]">
              {{ book.avgRating }} 分
            </div>
            <div class="text-xs text-gray-400 mt-1">{{ book.reviewCount }}人评价</div>
            <div v-if="book.latestChapterTitle" class="text-xs text-gray-400 mt-2 max-w-24 truncate">
              {{ book.latestChapterTitle }}
            </div>
          </div>
        </div>
      </NuxtLink>
      <Pagination
        :page="data.page"
        :total-pages="data.totalPages"
        :last="data.last"
        @change="goPage"
      />
    </div>
    <p v-else class="text-center py-20 text-gray-400">暂无书籍</p>
  </div>
</template>

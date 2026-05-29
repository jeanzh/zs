<script setup lang="ts">
import type { BookListItemResponse, PagedResponse } from '~/types/api'

const api = useApi()
const auth = useAuthStore()

const page = ref(0)
const data = ref<PagedResponse<BookListItemResponse> | null>(null)
const loading = ref(true)

async function fetchData() {
  loading.value = true
  try {
    data.value = await api.get<PagedResponse<BookListItemResponse>>(
      `/api/user/shelf?page=${page.value}&size=20`
    )
  } catch { /* ignore */ } finally {
    loading.value = false
  }
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

onMounted(() => {
  if (!auth.isLoggedIn) {
    navigateTo('/login')
    return
  }
  fetchData()
})
</script>

<template>
  <div>
    <h1 class="text-xl font-bold mb-4">书架</h1>

    <div v-if="loading" class="text-center py-20 text-gray-400">加载中...</div>
    <div v-else-if="data && data.content.length > 0" class="space-y-3">
      <NuxtLink
        v-for="book in data.content" :key="book.id"
        :to="`/books/${book.id}`"
        class="block bg-white rounded-xl p-4 border border-[#e8e4df] hover:border-[#c62828] transition-colors"
      >
        <div class="flex items-start justify-between gap-4">
          <div class="flex-1 min-w-0">
            <h3 class="text-sm font-medium text-gray-900 truncate">{{ book.title }}</h3>
            <p class="text-xs text-gray-500 mt-1">{{ book.author }}</p>
            <div class="flex flex-wrap gap-1 mt-2">
              <span
                v-for="tag in book.tags?.slice(0, 3)" :key="tag"
                class="px-1.5 py-0.5 bg-red-50 text-[#c62828] rounded text-xs"
              >{{ tag }}</span>
              <span
                class="px-1.5 py-0.5 text-xs rounded"
                :class="book.serialStatus === 'COMPLETED'
                  ? 'bg-green-50 text-green-700'
                  : 'bg-blue-50 text-blue-700'"
              >{{ statusLabel[book.serialStatus] || book.serialStatus }}</span>
            </div>
            <p v-if="book.latestChapterTitle" class="text-xs text-gray-500 mt-2">
              最近更新：
              <a
                v-if="book.latestUpdateUrl"
                :href="book.latestUpdateUrl"
                target="_blank"
                class="text-[#c62828] hover:underline"
                @click.stop
              >{{ book.latestChapterTitle }}</a>
              <span v-else>{{ book.latestChapterTitle }}</span>
            </p>
          </div>
          <div class="text-right shrink-0">
            <div class="text-sm font-medium text-[#f9a825]">{{ book.avgRating }} 分</div>
            <div class="text-xs text-gray-400 mt-1">{{ book.reviewCount }}人评价</div>
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
    <p v-else class="text-center py-20 text-gray-400">暂无关注书籍，去 <NuxtLink to="/library" class="text-[#c62828] hover:underline">书库</NuxtLink> 逛逛吧</p>
  </div>
</template>

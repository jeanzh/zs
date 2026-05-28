<script setup lang="ts">
import type { BookListItemResponse, PagedResponse, ReviewItemResponse } from '~/types/api'

const route = useRoute()
const api = useApi()
const auth = useAuthStore()

const bookId = computed(() => Number(route.params.id))
const page = ref(0)
const sort = ref<'newest' | 'hot'>('newest')
const book = ref<BookListItemResponse | null>(null)
const reviews = ref<PagedResponse<ReviewItemResponse> | null>(null)
const loading = ref(true)
const loadingBook = ref(true)
const following = ref(false)
const followLoading = ref(false)

async function fetchBook() {
  try {
    book.value = await api.get<BookListItemResponse>(`/api/books/${bookId.value}`)
  } catch {
    /* ignore */
  } finally {
    loadingBook.value = false
  }
}

async function fetchReviews() {
  loading.value = true
  try {
    reviews.value = await api.get<PagedResponse<ReviewItemResponse>>(
      `/api/books/${bookId.value}/reviews?page=${page.value}&size=20&sort=${sort.value}`
    )
  } catch {
    /* ignore */
  } finally {
    loading.value = false
  }
}

async function toggleFollow() {
  if (!auth.isLoggedIn) {
    navigateTo('/login')
    return
  }
  followLoading.value = true
  try {
    if (following.value) {
      await api.del(`/api/books/${bookId.value}/follow`)
      following.value = false
    } else {
      await api.post(`/api/books/${bookId.value}/follow`)
      following.value = true
    }
  } catch {
    /* ignore */
  } finally {
    followLoading.value = false
  }
}

function goPage(p: number) {
  page.value = p
  fetchReviews()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function changeSort(s: 'newest' | 'hot') {
  sort.value = s
  page.value = 0
  fetchReviews()
}

const statusLabel: Record<string, string> = {
  ONGOING: '连载中',
  COMPLETED: '已完结',
  PAUSED: '暂停',
}

onMounted(() => { fetchBook(); fetchReviews() })
</script>

<template>
  <div>
    <!-- Book info -->
    <div v-if="!loadingBook && book" class="mb-6">
      <div class="flex items-start justify-between">
        <div>
          <h1 class="text-xl font-bold">{{ book.title }}</h1>
          <p class="text-sm text-gray-500 mt-1">{{ book.author }}</p>
          <div class="flex flex-wrap gap-1.5 mt-2">
            <span
              v-for="tag in book.tags?.slice(0, 4)" :key="tag"
              class="px-2 py-0.5 bg-red-50 text-[#c62828] rounded text-xs"
            >{{ tag }}</span>
            <span
              class="px-2 py-0.5 text-xs rounded"
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
            >{{ book.latestChapterTitle }}</a>
            <span v-else>{{ book.latestChapterTitle }}</span>
          </p>
          <p v-if="book.summary" class="text-sm text-gray-500 mt-2 line-clamp-3">
            {{ book.summary }}
          </p>
        </div>
        <button
          :disabled="followLoading"
          class="px-4 py-2 rounded-lg text-sm font-medium transition-colors shrink-0"
          :class="following
            ? 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            : 'bg-[#c62828] text-white hover:bg-red-800'"
          @click="toggleFollow"
        >
          {{ followLoading ? '...' : following ? '已关注' : '关注' }}
        </button>
      </div>
    </div>

    <!-- Sort tabs -->
    <div class="flex gap-4 mb-4 border-b border-[#e8e4df]">
      <button
        class="pb-2 text-sm transition-colors"
        :class="sort === 'newest'
          ? 'text-[#c62828] border-b-2 border-[#c62828] font-medium'
          : 'text-gray-500'"
        @click="changeSort('newest')"
      >
        最新
      </button>
      <button
        class="pb-2 text-sm transition-colors"
        :class="sort === 'hot'
          ? 'text-[#c62828] border-b-2 border-[#c62828] font-medium'
          : 'text-gray-500'"
        @click="changeSort('hot')"
      >
        最热
      </button>
    </div>

    <div v-if="loading" class="text-center py-20 text-gray-400">加载中...</div>
    <div v-else-if="reviews && reviews.content.length > 0" class="space-y-4">
      <ReviewCard
        v-for="review in reviews.content"
        :key="review.id"
        :review="review"
      />
      <Pagination
        :page="reviews.page"
        :total-pages="reviews.totalPages"
        :last="reviews.last"
        @change="goPage"
      />
    </div>
    <p v-else class="text-center py-20 text-gray-400">暂无书评</p>
  </div>
</template>

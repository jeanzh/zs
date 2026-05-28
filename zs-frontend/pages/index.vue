<script setup lang="ts">
import type { PagedResponse, ReviewItemResponse } from '~/types/api'

const api = useApi()

const page = ref(0)
const data = ref<PagedResponse<ReviewItemResponse> | null>(null)
const loading = ref(true)
const error = ref('')

async function fetchData() {
  loading.value = true
  error.value = ''
  try {
    data.value = await api.get<PagedResponse<ReviewItemResponse>>(
      `/api/books?page=${page.value}&size=20`
    )
  } catch {
    error.value = '加载失败'
  } finally {
    loading.value = false
  }
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
    <h1 class="text-xl font-bold mb-6">发现</h1>

    <div v-if="loading" class="text-center py-20 text-gray-400">加载中...</div>
    <p v-else-if="error" class="text-center py-20 text-red-500">{{ error }}</p>
    <div v-else-if="data && data.content.length > 0" class="space-y-4">
      <ReviewCard
        v-for="review in data.content"
        :key="review.id"
        :review="review"
      />
      <Pagination
        :page="data.page"
        :total-pages="data.totalPages"
        :last="data.last"
        @change="goPage"
      />
    </div>
    <p v-else class="text-center py-20 text-gray-400">暂无内容</p>
  </div>
</template>

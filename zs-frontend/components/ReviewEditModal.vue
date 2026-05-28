<script setup lang="ts">
import type { ReviewItemResponse } from '~/types/api'

const props = defineProps<{
  review: ReviewItemResponse
}>()

const emit = defineEmits<{
  close: []
  updated: []
}>()

const api = useApi()

const rating = ref(props.review.rating)
const content = ref(props.review.content)
const error = ref('')
const loading = ref(false)

async function handleSave() {
  error.value = ''
  if (!content.value) {
    error.value = '评价内容不能为空'
    return
  }
  loading.value = true
  try {
    const data = await api.put<any>(`/api/user/reviews/${props.review.id}`, {
      rating: rating.value,
      content: content.value,
    })
    if (data.error) {
      error.value = data.error
    } else {
      emit('updated')
      emit('close')
    }
  } catch {
    error.value = '保存失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4" @click.self="emit('close')">
    <div class="bg-white rounded-xl w-full max-w-md p-6">
      <h3 class="text-lg font-medium mb-4">编辑书评</h3>
      <div class="space-y-4">
        <div>
          <label class="block text-sm text-gray-600 mb-1">评分</label>
          <RatingStars v-model="rating" :readonly="false" />
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1">评价内容</label>
          <textarea v-model="content" rows="4" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828] resize-none" />
        </div>
        <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>
        <div class="flex gap-3 justify-end">
          <button
            class="px-4 py-2 border border-[#e8e4df] rounded-lg text-sm hover:bg-gray-50 transition-colors"
            @click="emit('close')"
          >
            取消
          </button>
          <button
            :disabled="loading"
            class="px-4 py-2 bg-[#c62828] text-white rounded-lg text-sm hover:bg-red-800 disabled:opacity-50 transition-colors"
            @click="handleSave"
          >
            {{ loading ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

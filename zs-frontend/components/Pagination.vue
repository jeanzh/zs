<script setup lang="ts">
const props = defineProps<{
  page: number
  totalPages: number
  last: boolean
}>()

const emit = defineEmits<{
  change: [page: number]
}>()

const pages = computed(() => {
  const result: number[] = []
  const start = Math.max(0, props.page - 2)
  const end = Math.min(props.totalPages, props.page + 3)
  for (let i = start; i < end; i++) {
    result.push(i)
  }
  return result
})
</script>

<template>
  <div v-if="totalPages > 1" class="flex items-center justify-center gap-1 mt-6">
    <button
      :disabled="page === 0"
      class="px-3 py-1.5 text-sm rounded border border-[#e8e4df] disabled:opacity-30 hover:bg-gray-50 transition-colors"
      @click="emit('change', page - 1)"
    >
      上一页
    </button>
    <button
      v-for="p in pages" :key="p"
      class="w-9 h-9 text-sm rounded transition-colors"
      :class="p === page
        ? 'bg-[#c62828] text-white'
        : 'hover:bg-gray-100 text-gray-600'"
      @click="emit('change', p)"
    >
      {{ p + 1 }}
    </button>
    <button
      :disabled="last"
      class="px-3 py-1.5 text-sm rounded border border-[#e8e4df] disabled:opacity-30 hover:bg-gray-50 transition-colors"
      @click="emit('change', page + 1)"
    >
      下一页
    </button>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{ error: any }>()

const message = computed(() => {
  if (props.error?.statusCode === 404) return '页面不存在'
  return '出了点问题'
})
</script>

<template>
  <div class="text-center py-20">
    <p class="text-4xl font-bold text-gray-300 mb-4">{{ error?.statusCode || '?' }}</p>
    <p class="text-gray-500 mb-6">{{ message }}</p>
    <NuxtLink to="/" class="text-[#c62828] hover:underline text-sm">返回首页</NuxtLink>
  </div>
</template>

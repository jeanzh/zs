<script setup lang="ts">
import type { ReviewItemResponse } from '~/types/api'
import { formatDate } from '~/utils'

defineProps<{
  review: ReviewItemResponse
}>()
</script>

<template>
  <div class="bg-white rounded-xl p-4 border border-[#e8e4df]">
    <!-- Book info header -->
    <div class="flex items-start justify-between mb-2">
      <div>
        <NuxtLink
          :to="`/books/${review.bookId}`"
          class="text-base font-medium text-gray-900 hover:text-[#c62828] transition-colors"
        >
          {{ review.bookTitle }}
        </NuxtLink>
        <p class="text-xs text-gray-500 mt-0.5">{{ review.bookAuthor }}</p>
      </div>
      <RatingStars :model-value="review.rating" />
    </div>

    <!-- Review content -->
    <p class="text-sm text-gray-700 leading-relaxed mb-3">
      {{ review.content }}
    </p>

    <!-- Footer -->
    <div class="flex items-center justify-between text-xs text-gray-400">
      <div class="flex items-center gap-2">
        <span>{{ review.userNickname }}</span>
        <span>·</span>
        <span>{{ review.likes }} 赞</span>
      </div>
      <div class="flex items-center gap-3">
        <a
          v-if="review.sourceUrl"
          :href="review.sourceUrl"
          target="_blank"
          class="text-[#c62828] hover:underline"
        >
          来源
        </a>
        <span>{{ formatDate(review.createdAt) }}</span>
      </div>
    </div>
  </div>
</template>

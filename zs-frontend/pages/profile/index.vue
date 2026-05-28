<script setup lang="ts">
import type { PagedResponse, ReviewItemResponse, UserInfoResponse } from '~/types/api'

definePageMeta({ middleware: 'auth' })

const api = useApi()
const auth = useAuthStore()

const tab = ref<'info' | 'password' | 'reviews'>('info')
const user = ref<UserInfoResponse | null>(null)

// Profile edit
const editNickname = ref('')
const editAvatarUrl = ref('')
const profileError = ref('')
const profileSuccess = ref('')

// Reviews
const reviewPage = ref(0)
const reviews = ref<PagedResponse<ReviewItemResponse> | null>(null)
const reviewLoading = ref(false)
const editingReview = ref<ReviewItemResponse | null>(null)

async function loadUser() {
  try {
    user.value = await api.get<UserInfoResponse>('/api/user/me')
    editNickname.value = user.value.nickname
    editAvatarUrl.value = user.value.avatarUrl || ''
  } catch { /* ignore */ }
}

async function loadReviews() {
  reviewLoading.value = true
  try {
    reviews.value = await api.get<PagedResponse<ReviewItemResponse>>(
      `/api/user/reviews?page=${reviewPage.value}&size=10`
    )
  } catch { /* ignore */ } finally {
    reviewLoading.value = false
  }
}

async function saveProfile() {
  profileError.value = ''
  profileSuccess.value = ''
  try {
    const data = await api.put<any>('/api/user/profile', {
      nickname: editNickname.value || undefined,
      avatarUrl: editAvatarUrl.value || undefined,
    })
    if (data.error) {
      profileError.value = data.error
    } else {
      profileSuccess.value = '保存成功'
      auth.nickname = data.nickname
      user.value!.nickname = data.nickname
      user.value!.avatarUrl = data.avatarUrl
    }
  } catch {
    profileError.value = '保存失败'
  }
}

function reviewPageGo(p: number) {
  reviewPage.value = p
  loadReviews()
}

onMounted(() => {
  loadUser()
  loadReviews()
})
</script>

<template>
  <div>
    <h1 class="text-xl font-bold mb-6">我的</h1>

    <!-- Tabs -->
    <div class="flex gap-4 mb-6 border-b border-[#e8e4df]">
      <button
        v-for="t in [
          { key: 'info' as const, label: '个人信息' },
          { key: 'password' as const, label: '修改密码' },
          { key: 'reviews' as const, label: '我的书评' },
        ]" :key="t.key"
        class="pb-2 text-sm transition-colors"
        :class="tab === t.key
          ? 'text-[#c62828] border-b-2 border-[#c62828] font-medium'
          : 'text-gray-500'"
        @click="tab = t.key"
      >
        {{ t.label }}
      </button>
    </div>

    <!-- Info tab -->
    <div v-if="tab === 'info' && user" class="space-y-4 max-w-sm">
      <div class="bg-white rounded-xl p-4 border border-[#e8e4df] space-y-2 text-sm">
        <p><span class="text-gray-500">手机号：</span>{{ user.phone }}</p>
        <p><span class="text-gray-500">状态：</span>{{ user.status === 'ACTIVE' ? '正常' : '禁用' }}</p>
        <p><span class="text-gray-500">注册时间：</span>{{ new Date(user.createdAt).toLocaleString('zh-CN') }}</p>
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">昵称</label>
        <input v-model="editNickname" maxlength="32" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828]" />
      </div>
      <div>
        <label class="block text-sm text-gray-600 mb-1">头像 URL</label>
        <input v-model="editAvatarUrl" maxlength="512" class="w-full px-4 py-2.5 border border-[#e8e4df] rounded-lg text-sm focus:outline-none focus:border-[#c62828]" />
      </div>
      <p v-if="profileError" class="text-red-500 text-sm">{{ profileError }}</p>
      <p v-if="profileSuccess" class="text-green-600 text-sm">{{ profileSuccess }}</p>
      <button
        class="px-6 py-2.5 bg-[#c62828] text-white rounded-lg text-sm font-medium hover:bg-red-800 transition-colors"
        @click="saveProfile"
      >
        保存
      </button>
    </div>

    <!-- Password tab -->
    <div v-if="tab === 'password'">
      <PasswordChangeForm />
    </div>

    <!-- Reviews tab -->
    <div v-if="tab === 'reviews'">
      <div v-if="reviewLoading" class="text-center py-12 text-gray-400">加载中...</div>
      <div v-else-if="reviews && reviews.content.length > 0" class="space-y-3">
        <div
          v-for="review in reviews.content" :key="review.id"
          class="bg-white rounded-xl p-4 border border-[#e8e4df]"
        >
          <div class="flex items-start justify-between mb-2">
            <div>
              <span class="text-sm font-medium text-gray-900">{{ review.bookTitle }}</span>
              <span class="text-xs text-gray-400 ml-2">{{ review.bookAuthor }}</span>
            </div>
            <RatingStars :model-value="review.rating" />
          </div>
          <p class="text-sm text-gray-700 mb-3">{{ review.content }}</p>
          <div class="flex items-center justify-between text-xs text-gray-400">
            <span>{{ new Date(review.createdAt).toLocaleString('zh-CN') }} · {{ review.likes }} 赞</span>
            <button
              class="text-[#c62828] hover:underline"
              @click="editingReview = review"
            >
              编辑
            </button>
          </div>
        </div>
        <Pagination
          :page="reviews.page"
          :total-pages="reviews.totalPages"
          :last="reviews.last"
          @change="reviewPageGo"
        />
      </div>
      <p v-else class="text-center py-12 text-gray-400">暂无书评</p>
    </div>

    <!-- Edit modal -->
    <ReviewEditModal
      v-if="editingReview"
      :review="editingReview"
      @close="editingReview = null"
      @updated="loadReviews()"
    />
  </div>
</template>

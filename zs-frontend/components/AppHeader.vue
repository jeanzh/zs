<script setup lang="ts">
const auth = useAuthStore()
const mobileMenuOpen = ref(false)

const links = [
  { to: '/', label: '发现' },
  { to: '/library', label: '书库' },
  { to: '/submit', label: '投稿' },
  { to: '/feeds', label: '动态' },
  { to: '/admin', label: '管理' },
]

function handleLogout() {
  auth.logout()
  mobileMenuOpen.value = false
}
</script>

<template>
  <header class="bg-white border-b border-[#e8e4df] sticky top-0 z-50">
    <div class="max-w-2xl mx-auto px-4 h-14 flex items-center justify-between">
      <NuxtLink to="/" class="text-lg font-bold text-[#c62828] tracking-wide">
        小说追踪
      </NuxtLink>

      <!-- Desktop nav -->
      <nav class="hidden sm:flex items-center gap-1">
        <template v-for="link in links" :key="link.to">
          <NuxtLink
            :to="link.to"
            class="px-3 py-1.5 rounded text-sm text-gray-600 hover:bg-gray-100 transition-colors"
            active-class="text-[#c62828] font-medium bg-red-50"
          >
            {{ link.label }}
          </NuxtLink>
        </template>
        <template v-if="auth.isLoggedIn">
          <NuxtLink
            to="/profile"
            class="px-3 py-1.5 rounded text-sm text-gray-600 hover:bg-gray-100 transition-colors"
            active-class="text-[#c62828] font-medium bg-red-50"
          >
            {{ auth.nickname }}
          </NuxtLink>
          <button
            class="px-3 py-1.5 rounded text-sm text-gray-500 hover:text-red-600 transition-colors"
            @click="handleLogout"
          >
            退出
          </button>
        </template>
        <template v-else>
          <NuxtLink
            to="/login"
            class="px-3 py-1.5 rounded text-sm text-gray-600 hover:bg-gray-100 transition-colors"
          >
            登录
          </NuxtLink>
        </template>
      </nav>

      <!-- Mobile hamburger -->
      <button
        class="sm:hidden p-2 text-gray-600"
        @click="mobileMenuOpen = !mobileMenuOpen"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path v-if="!mobileMenuOpen" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
          <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
        </svg>
      </button>
    </div>

    <!-- Mobile menu -->
    <div v-if="mobileMenuOpen" class="sm:hidden border-t border-[#e8e4df] bg-white px-4 pb-4">
      <NuxtLink
        v-for="link in links" :key="link.to"
        :to="link.to"
        class="block py-3 text-sm text-gray-700 border-b border-gray-50 last:border-0"
        active-class="text-[#c62828] font-medium"
        @click="mobileMenuOpen = false"
      >
        {{ link.label }}
      </NuxtLink>
      <template v-if="auth.isLoggedIn">
        <NuxtLink
          to="/profile"
          class="block py-3 text-sm text-gray-700 border-b border-gray-50"
          active-class="text-[#c62828] font-medium"
          @click="mobileMenuOpen = false"
        >
          我的 ({{ auth.nickname }})
        </NuxtLink>
        <button
          class="block py-3 text-sm text-gray-500 w-full text-left"
          @click="handleLogout"
        >
          退出登录
        </button>
      </template>
      <NuxtLink
        v-else
        to="/login"
        class="block py-3 text-sm text-gray-700 border-b border-gray-50"
        @click="mobileMenuOpen = false"
      >
        登录
      </NuxtLink>
    </div>
  </header>
</template>

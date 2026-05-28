export default defineNuxtConfig({
  ssr: false,
  devServer: { port: 3000 },
  nitro: {
    devProxy: {
      '/api': { target: 'http://localhost:8080', changeOrigin: true }
    }
  },
  modules: ['@pinia/nuxt', '@nuxtjs/tailwindcss'],
  tailwindcss: {
    cssPath: '~/assets/css/main.css'
  }
})

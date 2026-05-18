import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { quasar, transformAssetUrls } from '@quasar/vite-plugin'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue({
      template: { transformAssetUrls }
    }),
    quasar()
  ],
  server: {
    port: 5173, // puerto del frontend
    proxy: {
      // redirigir /auth y /api al backend Spring Boot
      '/auth': {
        target: 'import.meta.env.VITE_API_URL', // tu backend
        changeOrigin: true,
        secure: false,
      },
      '/api': {
        target: 'import.meta.env.VITE_API_URL',
        changeOrigin: true,
        secure: false,
      }
    }
  }
})

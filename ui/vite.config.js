import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
      vue(),
  ],
  optimizeDeps: {
    include: [
      `monaco-editor/esm/vs/language/json/json.worker`,
      `monaco-editor/esm/vs/language/css/css.worker`,
      `monaco-editor/esm/vs/language/html/html.worker`,
      `monaco-editor/esm/vs/language/typescript/ts.worker`,
      `monaco-editor/esm/vs/editor/editor.worker`
    ],
  },
  server: {
    proxy: {
      '/api/v1': {
        target: 'http://localhost:8081', // API服务器的地址
        ws: true,
        changeOrigin: true,
      },
      '/fvite': {
        target: 'http://localhost:8081', // API服务器的地址
        ws: true,
        changeOrigin: true,
      }
    },
  }
})

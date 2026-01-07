// Plugins
import Components from 'unplugin-vue-components/vite'
import Vue from '@vitejs/plugin-vue'
import Vuetify, { transformAssetUrls } from 'vite-plugin-vuetify'
import ViteFonts from 'unplugin-fonts/vite'

// Utilities
import { defineConfig } from 'vite'
import { fileURLToPath, URL } from 'node:url'
import { templateCompilerOptions } from '@tresjs/core'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    Vue({
      ...templateCompilerOptions,
      template: {
        transformAssetUrls,
        compilerOptions: {
          isCustomElement: (tag) =>
            tag.startsWith('swiper-') ||
            (templateCompilerOptions?.template?.compilerOptions?.isCustomElement
              ? templateCompilerOptions.template.compilerOptions.isCustomElement(tag)
              : false),
        },
      },
    }),
    // https://github.com/vuetifyjs/vuetify-loader/tree/master/packages/vite-plugin#readme
    Vuetify(),
    Components(),
    ViteFonts({
      google: {
        families: [{
          name: 'Roboto',
          styles: 'wght@100;300;400;500;700;900',
        }],
      },
    }),
  ],
  define: { 'process.env': {} },
  optimizeDeps: {
    exclude: ["vue"], //Vue 중복최적화 방지
    include: ["chart.js", "vuetify"], //필요한 것만 최적화
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
    extensions: [
      '.js',
      '.json',
      '.jsx',
      '.mjs',
      '.ts',
      '.tsx',
      '.vue',
    ],
  },
  server: {
    port: 3000,
  },
  css: {
    preprocessorOptions: {
      sass: {
        api: 'modern-compiler',
      },
    },
  },
})

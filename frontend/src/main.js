/**
 * main.js
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

// Plugins
import { registerPlugins } from '@/plugins'

// Components
import App from './App.vue'

// Composables
import { createApp } from 'vue'

// router
import { createWebHistory, createRouter } from 'vue-router'

import MainView from './components/MainView.vue'
import SearchView from './components/search/SearchView.vue'
import DetailView from './components/detail/DetailView.vue'
import Test from './components/Test.vue'

const routes = [
    { path: '/', component: MainView },
    { path: '/test', component: Test },
    { path: '/search', component: SearchView},
    { path: '/detail', component: DetailView}
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

// 상태 관리
import { createPinia } from 'pinia'

const app = createApp(App)
registerPlugins(app)
app.use(router)
const pinia = createPinia()
app.use(pinia)
app.mount('#app')


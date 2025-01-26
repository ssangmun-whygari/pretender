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

// 상태 관리
import { createPinia } from 'pinia'
import { useNavigationStore } from './composables/stores/navigation'

import AppHeader from './components/AppHeader.vue'
import MainView from './components/MainView.vue'
import SearchView from './components/search/SearchView.vue'
import DetailView from './components/detail/DetailView.vue'
import Test from './components/Test.vue'
import Test2 from './components/Test2.vue'
import Test3 from './components/Test3.vue'
import SignUpView from './components/members/SignUpView.vue'
import MyPageView from './components/members/myPage/MyPageView.vue'
import Background3Dmodel from './components/members/myPage/Background3Dmodel.vue'
import LoginView from './components/members/LoginView.vue'
import LogoutView from './components/members/LogoutView.vue'

const pinia = createPinia()

const routes = [
    { path: '/', component: MainView },
    { path: '/test', component: Test },
    { path: '/test2', component: Test2 },
    { path: '/test3', component: Test3 },
    { path: '/search', component: SearchView},
    { path: '/detail', component: DetailView},
    { path: '/signUp', component: SignUpView},
    { path: '/login', component: LoginView},
    { path: '/logout', component: LogoutView},
    { path: '/myPage', component: MyPageView}
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

router.beforeEach((to, from, next) => {
    if (to.path === '/login') {
        const navigationStore = useNavigationStore()
        if (!navigationStore.previousPage) {
            navigationStore.setPreviousPage(from.fullPath)
        }
    }
    next()
})

const app = createApp(App)

app.use(pinia)
app.use(router)
registerPlugins(app)

app.mount('#app')

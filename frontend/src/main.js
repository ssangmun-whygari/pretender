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

// css
import './assets/styles.css'

// 상태 관리
import { createPinia } from 'pinia'
import { useNavigationStore } from './composables/stores/navigation'

import AppHeader from './components/AppHeader.vue'
import MainView from './components/MainView.vue'
import PopularMovieCarousel from './components/PopularMovieCarousel.vue'
import SearchView from './components/search/SearchView.vue'
import DetailView from './components/detail/DetailView.vue'
import Test from './components/test/Test.vue'
import Test2 from './components/test/Test2.vue'
import Test3 from './components/test/Test3.vue'
import Test4_socialLogin from './components/test/Test4_socialLogin.vue'
import SignUpView from './components/members/SignUpView.vue'
import MyPageView from './components/members/myPage/MyPageView.vue'
import Background3Dmodel from './components/members/myPage/Background3Dmodel.vue'
import LoginView from './components/members/LoginView.vue'
import LogoutView from './components/members/LogoutView.vue'
import SocialLoginSuccess from './components/members/SocialLoginSuccess.vue'

const pinia = createPinia()

const routes = [
    { path: '/', component: MainView },
    { path: '/test', component: Test },
    { path: '/test2', component: Test2 },
    { path: '/test3', component: Test3 },
    { path: '/test4', component: Test4_socialLogin },
    { path: '/search', component: SearchView},
    { path: '/detail', component: DetailView},
    { path: '/signUp', component: SignUpView},
    { path: '/socialLogin/success', component: SocialLoginSuccess},
    { path: '/login', component: LoginView},
    { path: '/logout', component: LogoutView},
    { path: '/myPage', component: MyPageView}
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

const app = createApp(App)

app.use(pinia)
app.use(router)
registerPlugins(app)

app.mount('#app')

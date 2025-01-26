import { createRouter, createWebHistory } from 'vue-router'
import { useNavigationStore } from '../stores/navigation' 

const routes = [
    {
      path: '/',
      name: 'MainView',
      component: () => import('@/components/MainView.vue'),
    },
    {
      path: '/test',
      name: 'Test',
      component: () => import('@/components/Test.vue'),
    },
    {
      path: '/test2',
      name: 'Test2',
      component: () => import('@/components/Test2.vue'),
    },
    {
      path: '/test3',
      name: 'Test3',
      component: () => import('@/components/Test3.vue'),
    },
    {
      path: '/search',
      name: 'SearchView',
      component: () => import('@/components/search/SearchView.vue'),
    },
    {
      path: '/detail',
      name: 'DetailView',
      component: () => import('@/components/detail/DetailView.vue'),
    },
    {
      path: '/signUp',
      name: 'SignUpView',
      component: () => import('@/components/members/SignUpView.vue'),
    },
    {
      path: '/login',
      name: 'LoginView',
      component: () => import('@/components/members/LoginView.vue'),
    },
    {
      path: '/logout',
      name: 'LogoutView',
      component: () => import('@/components/members/LogoutView.vue'),
    },
    {
      path: '/myPage',
      name: 'MyPageView',
      component: () => import('@/components/members/myPage/MyPageView.vue'),
      meta: { requiresAuth: true }, 
    },
  ]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 네비게이션 
router.beforeEach((to, from, next) => {
  if (to.path === '/login') {
    const navigationStore = useNavigationStore()
    if (!navigationStore.previousPage) {
      navigationStore.setPreviousPage(from.fullPath)
    }
  }
  next()
})

export default router
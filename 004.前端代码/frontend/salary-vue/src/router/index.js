import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/LoginView.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/RegisterView.vue') },
  { path: '/', name: 'Dashboard', component: () => import('../views/DashboardView.vue'), meta: { requiresAuth: true } },
  { path: '/records', name: 'Records', component: () => import('../views/RecordsView.vue'), meta: { requiresAuth: true } },
  { path: '/statistics', name: 'Statistics', component: () => import('../views/StatisticsView.vue'), meta: { requiresAuth: true } },
  { path: '/settings', name: 'Settings', component: () => import('../views/SettingsView.vue'), meta: { requiresAuth: true } },
  { path: '/budget', name: 'Budget', component: () => import('../views/BudgetView.vue'), meta: { requiresAuth: true } },
  { path: '/categories', name: 'Categories', component: () => import('../views/CategoriesView.vue'), meta: { requiresAuth: true } },
  { path: '/security', name: 'Security', component: () => import('../views/SecurityView.vue'), meta: { requiresAuth: true } },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('fm_token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && token) {
    next('/')
  } else {
    next()
  }
})

export default router

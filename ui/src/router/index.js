import { createRouter, createWebHashHistory } from 'vue-router'

// 公共路由
const routes = [
  {
    path: '/',
    redirect: '/development',
  },
  {
    path: '/login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/projects',
    component: () => import('../views/HomeView.vue')
  },
  {
    path: '/development',
    component: () => import('../views/development/Index.vue')
  },
  {
    path: '/docs',
    component: () => import('../views/doc/Index.vue'),
    children: [
      {
        path: '/docs/debug',
        component: () => import('../views/doc/Debug.vue')
      },
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router

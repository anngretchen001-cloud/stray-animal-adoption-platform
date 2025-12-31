import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'


const routes = [
  { path: '/', redirect: '/home' },
  { path: '/login', component: () => import('@/views/Login.vue') },
  {path: '/register', component: () => import('@/views/Register.vue') },
  {path:'/test', component: () => import('@/views/test.vue') },
  {path: '/animals',component: () => import('@/views/AnimalList.vue')},
  {
  path: '/animal/:id',
  component: () => import('@/views/AnimalDetail.vue').then(m => m.default)
}
,
{
  path: '/applications',
  component: () => import('@/views/Applications.vue'), // 确保路径和文件名正确
  meta: { requiresAuth: true } // 需要登录
}
,
  { path: '/home', component: () => import('@/views/Home.vue'), meta: { requiresAuth: true } },
  { path: '/profile', component: () => import('@/views/Profile.vue'), meta: { requiresAuth: true } },
  { path: '/organization', component: () => import('@/views/Organization_back.vue'), meta: { requiresRole: ['ORG'] } },
  { path: '/admin', component: () => import('@/views/Admin_back.vue'), meta: { requiresRole: ['ADMIN'] } },
  {path:'/sharing',component: () => import('@/views/Sharing.vue')},
  {
  path: '/community/:id',
  name: 'PostDetail',
  component: () => import('@/views/PostDetail.vue')
  },
  // router/index.js
{
  path: '/community/publish',
  name: 'Publish',
  component: () => import('@/views/Publish.vue'),
  meta: { requiresAuth: true } // 需要登录
},
// router/index.js
{
  path: '/community/edit/:id',
  name: 'EditPost',
  component: () => import('@/views/Publish.vue'),  // 复用同一个组件
  meta: { requiresAuth: true }
},
{
  path:'/community/mypost',
  component: () => import('@/views/MyPost.vue'),
  meta: { requiresAuth: true }
}

]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const isLogin = !!userStore.token

  // 白名单：不需要 token
  const whiteList = ['/login', '/register']

  // 1) 未登录 → 只能访问白名单
  if (!isLogin) {
    if (whiteList.includes(to.path)) {
      return next()
    }
    return next('/login')  // 未登录访问其他页面 → 去登录
  }

  // 2) 登录后不能访问登录/注册
  if (isLogin && whiteList.includes(to.path)) {
    return next('/home')
  }

  // 3) 角色鉴权
  if (to.meta.requiresRole && !to.meta.requiresRole.includes(userStore.role)) {
    return next('/home')
  }

  next()
})


export default router

import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import { useUserStore } from '@/stores/user'
import { redirectToLogin } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import 'element-plus/es/components/message/style/css'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/tools',
      name: 'tools',
      component: () => import('@/views/Tools.vue')
    },
    {
      path: '/tools/:id',
      name: 'tool-detail',
      component: () => import('@/views/ToolDetail.vue')
    },
    {
      path: '/tutorials',
      name: 'tutorials',
      component: () => import('@/views/Tutorials.vue')
    },
    {
      path: '/tutorials/:id',
      name: 'tutorial-detail',
      component: () => import('@/views/TutorialDetail.vue')
    },
    {
      path: '/prompts',
      name: 'prompts',
      component: () => import('@/views/Prompts.vue')
    },
    {
      path: '/prompts/:id',
      name: 'prompt-detail',
      component: () => import('@/views/PromptDetail.vue')
    },
    {
      path: '/apis',
      name: 'apis',
      component: () => import('@/views/APIs.vue')
    },
    {
      path: '/apis/:id',
      name: 'api-detail',
      component: () => import('@/views/ApiDetail.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/Register.vue')
    },
    {
      path: '/admin',
      component: () => import('@/views/admin/AdminLayout.vue'),
      redirect: '/admin/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'admin-dashboard',
          component: () => import('@/views/admin/Dashboard.vue')
        },
        {
          path: 'tools',
          name: 'admin-tools',
          component: () => import('@/views/admin/Tools.vue')
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('@/views/admin/Users.vue')
        },
        {
          path: 'tutorials',
          name: 'admin-tutorials',
          component: () => import('@/views/admin/Tutorials.vue')
        },
        {
          path: 'prompts',
          name: 'admin-prompts',
          component: () => import('@/views/admin/Prompts.vue')
        },
        {
          path: 'apis',
          name: 'admin-apis',
          component: () => import('@/views/admin/Apis.vue')
        }
      ]
    }
  ]
})

// 后台访问控制：未登录或非 ADMIN 角色不能进入 /admin
router.beforeEach(async (to, _from, next) => {
  if (!to.path.startsWith('/admin')) {
    next()
    return
  }
  const userStore = useUserStore()
  const hasToken = !!localStorage.getItem('token') || !!userStore.token
  if (!hasToken) {
    redirectToLogin(to.fullPath)
    next(false)
    return
  }
  // 有 token 但用户信息可能未拉取（如刷新页面），先初始化
  if (!userStore.user) {
    await userStore.initUser()
  }
  if (!userStore.isLoggedIn) {
    redirectToLogin(to.fullPath)
    next(false)
    return
  }
  if (!userStore.isAdmin) {
    ElMessage.error('无权限访问后台，仅管理员可进入')
    next({ path: '/' })
    return
  }
  next()
})

export default router

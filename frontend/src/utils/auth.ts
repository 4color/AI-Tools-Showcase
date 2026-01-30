import router from '@/router'

/**
 * 跳转到登录页，并保存当前路径以便登录后返回
 * @param redirectPath 可选，指定要返回的路径。如果不提供，则使用当前路径
 */
export function redirectToLogin(redirectPath?: string) {
  const path = redirectPath || router.currentRoute.value.fullPath
  
  // 如果已经在登录页，不重复跳转
  if (router.currentRoute.value.path === '/login') {
    return
  }
  
  router.push({
    path: '/login',
    query: {
      redirect: path
    }
  })
}

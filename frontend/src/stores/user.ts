import { defineStore } from 'pinia'
import type { User } from '@/types'
import {userApi} from "@/api/user.api";
import { encryptPassword } from '@/utils/crypto'

export const useUserStore = defineStore('user', {
  state: () => {
    const token = localStorage.getItem('token') || ''
    return {
      user: null as User | null,
      token: token,
      isLoggedIn: false // 初始状态，会在初始化时根据 token 设置
    }
  },

  getters: {
    isAdmin: (state) => state.user?.role === 'ADMIN'
  },

  actions: {
    async login(credentials: { username: string; password: string }) {
      try {
        // 加密密码后传输
        let encryptedPassword: string
        try {
          encryptedPassword = encryptPassword(credentials.password)
        } catch (encryptError) {
          console.error('密码加密失败:', encryptError)
          return { success: false, error: new Error('密码加密失败，请检查 crypto-js 是否已安装') }
        }
        
        const encryptedCredentials = {
          username: credentials.username,
          password: encryptedPassword
        }
        const response = await userApi.login(encryptedCredentials) as unknown as { token: string; user: User }
        const { token, user } = response
        this.token = token
        this.user = user
        this.isLoggedIn = true
        localStorage.setItem('token', token)
        return { success: true }
      } catch (error) {
        console.error('登录失败:', error)
        return { success: false, error }
      }
    },

    async register(userData: { username: string; email: string; password: string }) {
      try {
        // 加密密码后传输
        const encryptedUserData = {
          username: userData.username,
          email: userData.email,
          password: encryptPassword(userData.password)
        }
        const response = await userApi.register(encryptedUserData) as unknown as { token: string; user: User }
        const { token, user } = response
        this.token = token
        this.user = user
        this.isLoggedIn = true
        localStorage.setItem('token', token)
        return { success: true }
      } catch (error) {
        return { success: false, error }
      }
    },

    async fetchProfile() {
      try {
        const user = await userApi.getProfile() as unknown as User
        this.user = user
        this.isLoggedIn = true
      } catch (error) {
        this.logout()
      }
    },

    logout() {
      this.user = null
      this.token = ''
      this.isLoggedIn = false
      localStorage.removeItem('token')
    },

    // 初始化用户状态（应用启动时调用）
    async initUser() {
      const token = localStorage.getItem('token')
      if (token) {
        this.token = token
        // 尝试获取用户信息
        try {
          await this.fetchProfile()
        } catch (error) {
          // 如果获取用户信息失败（token 可能已过期），清除 token
          console.error('获取用户信息失败:', error)
          this.logout()
        }
      }
    }
  }
})
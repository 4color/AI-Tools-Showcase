import axios from 'axios'
import type {User} from '@/types'
import {ElMessage} from "element-plus";

const request = axios.create({
    baseURL: '/api'
})

// 请求拦截器
request.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    (response) => {
        // blob 请求类型返回整个内容
        if (response.config.responseType === 'blob') {
            return response.data
        }

        // 正常请求：code == 200 时返回 data 内容
        if (response.data.code === 200) {
            return response.data.data
        }

        // 正常请求：code == 200 时返回 data 内容
        if (response.data.code && response.data.code != 200) {
            ElMessage.error(response.data.message);
            return;
        }

        return response.data
    },
    (error) => {
        if (error.response?.status === 401) {
            // 排除登录和注册接口，避免在登录/注册失败时跳转页面
            const url = error.config?.url || ''
            if (!url.includes('/auth/login') && !url.includes('/auth/register')) {
                localStorage.removeItem('token')
                // 检查当前路径，避免重复跳转
                if (window.location.pathname !== '/login' && window.location.pathname !== '/register') {
                    // 使用路由跳转，保存当前路径
                    const currentPath = window.location.pathname + window.location.search
                    window.location.href = `/login?redirect=${encodeURIComponent(currentPath)}`
                }
            }
        }
        return Promise.reject(error)
    }
)


export default request
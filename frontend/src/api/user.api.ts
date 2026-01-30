import type {User} from '@/types'
import request from "@/utils/request.api";

export const userApi = {
    login: (data: { username: string; password: string }) =>
        request.post<{ token: string; user: User }>('/auth/login', data),

    register: (data: { username: string; email: string; password: string }) =>
        request.post<{ token: string; user: User }>('/auth/register', data),

    getProfile: () =>
        request.get<User>('/user/profile')
}

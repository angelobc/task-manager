import { apiClient } from './client'
import { ApiResponse, AuthResponse } from '@/types'

export const authApi = {
  register: (data: { email: string; password: string; fullName: string }) =>
    apiClient.post<ApiResponse<AuthResponse>>('/auth/register', data),

  login: (data: { email: string; password: string }) =>
    apiClient.post<ApiResponse<AuthResponse>>('/auth/login', data),

  logout: (refreshToken: string) =>
    apiClient.post('/auth/logout', { refreshToken }),
}

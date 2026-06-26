import { apiClient, apiData } from './apiClient'
import type { AuthResponse, RegisterResponse } from '../types/domain'

export interface RegisterPayload {
  email: string
  username: string
  password: string
  displayName: string
  language: 'en' | 'vi'
}

export interface LoginPayload {
  emailOrUsername: string
  password: string
}

export const authService = {
  register: (payload: RegisterPayload) =>
    apiData<RegisterResponse>(apiClient.post('/auth/register', payload)),
  login: (payload: LoginPayload) => apiData<AuthResponse>(apiClient.post('/auth/login', payload)),
  verifyEmail: (token: string) => apiData<void>(apiClient.post('/auth/verify-email', { token })),
  refresh: (refreshToken: string) =>
    apiData<AuthResponse>(apiClient.post('/auth/refresh', { refreshToken })),
  logout: (refreshToken: string) => apiData<void>(apiClient.post('/auth/logout', { refreshToken })),
}

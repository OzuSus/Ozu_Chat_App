import axios, { AxiosError } from 'axios'

import { useAuthStore } from '../stores/authStore'
import type { ApiResponse } from '../types/domain'

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api',
  timeout: 20_000,
})

apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const apiData = async <T>(promise: Promise<{ data: ApiResponse<T> }>): Promise<T> => {
  try {
    const response = await promise
    return response.data.data
  } catch (error) {
    if (error instanceof AxiosError) {
      const data = error.response?.data as Partial<ApiResponse<unknown>> | undefined
      throw new Error(data?.message ?? error.message)
    }
    throw error
  }
}

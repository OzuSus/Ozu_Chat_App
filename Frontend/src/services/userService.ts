import { apiClient, apiData } from './apiClient'
import type { Attachment, ThemePreference, UserProfile, UserSummary } from '../types/domain'

export interface UpdateProfilePayload {
  displayName?: string
  bio?: string
  language?: 'en' | 'vi'
  theme?: Partial<ThemePreference>
}

export const userService = {
  me: () => apiData<UserProfile>(apiClient.get('/users/me')),
  updateProfile: (payload: UpdateProfilePayload) =>
    apiData<UserProfile>(apiClient.patch('/users/me', payload)),
  search: (query: string) =>
    apiData<UserSummary[]>(apiClient.get('/users/search', { params: { q: query } })),
  uploadAvatar: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return apiData<Attachment>(apiClient.post('/users/me/avatar', formData))
  },
  removeAvatar: () => apiData<UserProfile>(apiClient.delete('/users/me/avatar')),
}

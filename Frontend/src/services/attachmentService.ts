import { apiClient, apiData } from './apiClient'
import type { Attachment } from '../types/domain'

export const attachmentService = {
  upload: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return apiData<Attachment>(apiClient.post('/attachments', formData))
  },
}

import { apiClient, apiData } from './apiClient'
import type { Conversation, ConversationType } from '../types/domain'

export interface CreateConversationPayload {
  type: ConversationType
  name?: string
  memberIds: string[]
}

export const conversationService = {
  list: () => apiData<Conversation[]>(apiClient.get('/conversations')),
  create: (payload: CreateConversationPayload) =>
    apiData<Conversation>(apiClient.post('/conversations', payload)),
}

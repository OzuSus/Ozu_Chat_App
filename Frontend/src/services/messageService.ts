import { apiClient, apiData } from './apiClient'
import type { Message, MessageType, PageResponse } from '../types/domain'

export interface SendMessagePayload {
  type: MessageType
  content?: string
  attachmentIds?: string[]
  replyToMessageId?: string
}

export const messageService = {
  list: (conversationId: string) =>
    apiData<PageResponse<Message>>(
      apiClient.get(`/conversations/${conversationId}/messages`, {
        params: { size: 80 },
      }),
    ),
  send: (conversationId: string, payload: SendMessagePayload) =>
    apiData<Message>(apiClient.post(`/conversations/${conversationId}/messages`, payload)),
  typing: (conversationId: string) =>
    apiData<void>(apiClient.post(`/conversations/${conversationId}/typing`)),
  stopTyping: (conversationId: string) =>
    apiData<void>(apiClient.post(`/conversations/${conversationId}/stop-typing`)),
}

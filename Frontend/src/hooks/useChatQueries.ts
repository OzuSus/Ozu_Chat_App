import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'

import { attachmentService } from '../services/attachmentService'
import { conversationService, type CreateConversationPayload } from '../services/conversationService'
import { messageService, type SendMessagePayload } from '../services/messageService'

export const queryKeys = {
  conversations: ['conversations'] as const,
  messages: (conversationId: string) => ['messages', conversationId] as const,
  users: (query: string) => ['users', query] as const,
}

export const useConversations = () =>
  useQuery({
    queryKey: queryKeys.conversations,
    queryFn: conversationService.list,
  })

export const useMessages = (conversationId: string | null) =>
  useQuery({
    queryKey: conversationId ? queryKeys.messages(conversationId) : ['messages', 'idle'],
    queryFn: () => messageService.list(conversationId ?? ''),
    enabled: Boolean(conversationId),
    select: (page) => page.items.slice().reverse(),
  })

export const useCreateConversation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: CreateConversationPayload) => conversationService.create(payload),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: queryKeys.conversations })
    },
  })
}

export const useSendMessage = (conversationId: string | null) => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: SendMessagePayload) => {
      if (!conversationId) {
        throw new Error('Conversation is required')
      }
      return messageService.send(conversationId, payload)
    },
    onSuccess: () => {
      if (conversationId) {
        void queryClient.invalidateQueries({ queryKey: queryKeys.messages(conversationId) })
        void queryClient.invalidateQueries({ queryKey: queryKeys.conversations })
      }
    },
  })
}

export const useUploadAttachment = () =>
  useMutation({
    mutationFn: (file: File) => attachmentService.upload(file),
  })

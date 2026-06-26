import { Client } from '@stomp/stompjs'
import { useEffect } from 'react'
import { useQueryClient } from '@tanstack/react-query'

import { queryKeys } from './useChatQueries'
import { useAuthStore } from '../stores/authStore'

interface RealtimeEvent {
  event: string
  payload: unknown
  timestamp: string
}

export const useRealtime = (conversationId: string | null) => {
  const queryClient = useQueryClient()
  const accessToken = useAuthStore((state) => state.accessToken)

  useEffect(() => {
    if (!conversationId || !accessToken) {
      return undefined
    }

    const client = new Client({
      brokerURL: import.meta.env.VITE_WS_URL ?? 'ws://localhost:8080/ws',
      connectHeaders: {
        Authorization: `Bearer ${accessToken}`,
      },
      reconnectDelay: 3000,
    })

    client.onConnect = () => {
      client.subscribe(`/topic/conversations/${conversationId}`, (message) => {
        const event = JSON.parse(message.body) as RealtimeEvent
        if (
          ['receive-message', 'edit-message', 'delete-message', 'recall-message', 'reaction'].includes(
            event.event,
          )
        ) {
          void queryClient.invalidateQueries({ queryKey: queryKeys.messages(conversationId) })
          void queryClient.invalidateQueries({ queryKey: queryKeys.conversations })
        }
      })
    }

    client.activate()
    return () => {
      void client.deactivate()
    }
  }, [accessToken, conversationId, queryClient])
}

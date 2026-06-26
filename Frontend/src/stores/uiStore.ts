import { create } from 'zustand'

interface UiState {
  activeConversationId: string | null
  settingsOpen: boolean
  newChatOpen: boolean
  setActiveConversationId: (conversationId: string | null) => void
  setSettingsOpen: (open: boolean) => void
  setNewChatOpen: (open: boolean) => void
}

export const useUiStore = create<UiState>((set) => ({
  activeConversationId: null,
  settingsOpen: false,
  newChatOpen: false,
  setActiveConversationId: (activeConversationId) => set({ activeConversationId }),
  setSettingsOpen: (settingsOpen) => set({ settingsOpen }),
  setNewChatOpen: (newChatOpen) => set({ newChatOpen }),
}))

import { create } from 'zustand'
import { persist } from 'zustand/middleware'

import type { AuthResponse, UserProfile } from '../types/domain'

interface AuthState {
  accessToken: string | null
  refreshToken: string | null
  user: UserProfile | null
  setAuth: (auth: AuthResponse) => void
  setUser: (user: UserProfile) => void
  clearAuth: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      refreshToken: null,
      user: null,
      setAuth: (auth) =>
        set({
          accessToken: auth.accessToken,
          refreshToken: auth.refreshToken,
          user: auth.user,
        }),
      setUser: (user) => set({ user }),
      clearAuth: () =>
        set({
          accessToken: null,
          refreshToken: null,
          user: null,
        }),
    }),
    {
      name: 'ozu-auth',
    },
  ),
)

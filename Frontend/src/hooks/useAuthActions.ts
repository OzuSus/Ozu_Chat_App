import { useMutation, useQueryClient } from '@tanstack/react-query'
import i18n from 'i18next'

import { authService, type LoginPayload, type RegisterPayload } from '../services/authService'
import { useAuthStore } from '../stores/authStore'

export const useAuthActions = () => {
  const queryClient = useQueryClient()
  const setAuth = useAuthStore((state) => state.setAuth)
  const clearAuth = useAuthStore((state) => state.clearAuth)
  const refreshToken = useAuthStore((state) => state.refreshToken)

  const login = useMutation({
    mutationFn: (payload: LoginPayload) => authService.login(payload),
    onSuccess: (auth) => {
      setAuth(auth)
      void i18n.changeLanguage(auth.user.language)
      localStorage.setItem('ozu-language', auth.user.language)
    },
  })

  const register = useMutation({
    mutationFn: (payload: RegisterPayload) => authService.register(payload),
  })

  const verifyEmail = useMutation({
    mutationFn: (token: string) => authService.verifyEmail(token),
  })

  const logout = useMutation({
    mutationFn: () => (refreshToken ? authService.logout(refreshToken) : Promise.resolve()),
    onSettled: () => {
      clearAuth()
      queryClient.clear()
    },
  })

  return { login, register, verifyEmail, logout }
}

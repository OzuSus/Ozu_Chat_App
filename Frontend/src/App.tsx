import CssBaseline from '@mui/material/CssBaseline'
import { ThemeProvider, createTheme } from '@mui/material/styles'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { createRootRoute, createRoute, createRouter, Outlet, RouterProvider } from '@tanstack/react-router'
import { useMemo } from 'react'

import { ConversationSidebar } from './components/ConversationSidebar'
import { MessagePanel } from './components/MessagePanel'
import { NewConversationDialog } from './components/NewConversationDialog'
import { SettingsDrawer } from './components/SettingsDrawer'
import { useConversations } from './hooks/useChatQueries'
import { useRealtime } from './hooks/useRealtime'
import { SignInPage } from './pages/signIn'
import { SignUpPage } from './pages/signUp'
import { VerifyEmailPage } from './pages/verifyEmail'
import { useAuthStore } from './stores/authStore'
import { useUiStore } from './stores/uiStore'
import type { ThemePreference } from './types/domain'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 15_000,
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
})

const AppProviders = () => {
  const user = useAuthStore((state) => state.user)
  const themePreference = user?.theme ?? defaultTheme
  const muiTheme = useMemo(() => buildMuiTheme(themePreference), [themePreference])

  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={muiTheme}>
        <CssBaseline />
        <ThemeCssVars preference={themePreference} />
        <Outlet />
      </ThemeProvider>
    </QueryClientProvider>
  )
}

const HomeRoute = () => {
  const accessToken = useAuthStore((state) => state.accessToken)
  return accessToken ? <ChatWorkspace /> : <SignInPage />
}

const ChatWorkspace = () => {
  const conversations = useConversations()
  const activeConversationId = useUiStore((state) => state.activeConversationId)
  const activeConversation =
    conversations.data?.find((conversation) => conversation.id === activeConversationId) ??
    conversations.data?.[0] ??
    null

  useRealtime(activeConversation?.id ?? null)

  return (
    <main className="chat-app-shell">
      <ConversationSidebar conversations={conversations.data ?? []} />
      <MessagePanel conversation={activeConversation} />
      <SettingsDrawer />
      <NewConversationDialog />
    </main>
  )
}

const rootRoute = createRootRoute({
  component: AppProviders,
})

const indexRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/',
  component: HomeRoute,
})

const verifyRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/verify-email',
  validateSearch: (search: Record<string, unknown>) => ({
    token: typeof search.token === 'string' ? search.token : undefined,
  }),
  component: VerifyEmailPage,
})

const signInRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/sign-in',
  component: SignInPage,
})

const signUpRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/sign-up',
  component: SignUpPage,
})

const routeTree = rootRoute.addChildren([indexRoute, signInRoute, signUpRoute, verifyRoute])
const router = createRouter({ routeTree })

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}

export const App = () => <RouterProvider router={router} />

const defaultTheme: ThemePreference = {
  mode: 'SYSTEM',
  primaryColor: '#5b7cfa',
  secondaryColor: '#00b8a9',
  accentColor: '#ffb703',
  fontSize: 15,
  borderRadius: 16,
}

const buildMuiTheme = (preference: ThemePreference) => {
  const systemDark = window.matchMedia?.('(prefers-color-scheme: dark)').matches
  const mode =
    preference.mode === 'SYSTEM' ? (systemDark ? 'dark' : 'light') : preference.mode === 'DARK' ? 'dark' : 'light'

  return createTheme({
    palette: {
      mode,
      primary: { main: preference.primaryColor },
      secondary: { main: preference.secondaryColor },
      warning: { main: preference.accentColor },
      background: {
        default: mode === 'dark' ? '#10131a' : '#f6f8fb',
        paper: mode === 'dark' ? '#171b25' : '#ffffff',
      },
    },
    shape: {
      borderRadius: preference.borderRadius,
    },
    typography: {
      fontFamily:
        'Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif',
      fontSize: preference.fontSize,
    },
    components: {
      MuiButton: {
        styleOverrides: {
          root: {
            textTransform: 'none',
            fontWeight: 700,
          },
        },
      },
      MuiPaper: {
        styleOverrides: {
          root: {
            backgroundImage: 'none',
          },
        },
      },
    },
  })
}

const ThemeCssVars = ({ preference }: { preference: ThemePreference }) => (
  <style>
    {`
      :root {
        --primary: ${preference.primaryColor};
        --secondary: ${preference.secondaryColor};
        --accent: ${preference.accentColor};
        --radius: ${preference.borderRadius}px;
        --font-size: ${preference.fontSize}px;
      }
    `}
  </style>
)

export default App

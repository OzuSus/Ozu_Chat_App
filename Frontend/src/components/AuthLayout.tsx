import Alert from '@mui/material/Alert'
import Box from '@mui/material/Box'
import Chip from '@mui/material/Chip'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import Typography from '@mui/material/Typography'
import { motion } from 'framer-motion'
import type { ReactNode } from 'react'
import { useTranslation } from 'react-i18next'

import { useAuthStore } from '../stores/authStore'

interface AuthLayoutProps {
  title: string
  subtitle: string
  error?: Error | null
  notice?: ReactNode
  children: ReactNode
}

export const AuthLayout = ({ title, subtitle, error, notice, children }: AuthLayoutProps) => {
  const { t } = useTranslation()
  const clearAuth = useAuthStore((state) => state.clearAuth)

  return (
    <main className="auth-screen">
      <motion.section
        className="auth-brand"
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.35 }}
      >
        <button className="brand-button" type="button" onClick={clearAuth} aria-label="Ozu Chat">
          <span className="brand-mark">O</span>
        </button>
        <Box>
          <Typography variant="h3" component="h1">
            Ozu Chat
          </Typography>
          <Typography color="text.secondary">{t('authTagline')}</Typography>
        </Box>
        <Stack direction="row" spacing={1} useFlexGap sx={{ flexWrap: 'wrap' }}>
          <Chip label="JWT Auth" />
          <Chip label="MongoDB" />
          <Chip label="Realtime" />
          <Chip label="Responsive" />
        </Stack>
      </motion.section>

      <Paper className="auth-card" elevation={0}>
        <Stack spacing={0.5}>
          <Typography variant="h5" component="h2">
            {title}
          </Typography>
          <Typography color="text.secondary">{subtitle}</Typography>
        </Stack>
        {error ? <Alert severity="error">{error.message}</Alert> : null}
        {notice ? <Alert severity="success">{notice}</Alert> : null}
        {children}
      </Paper>
    </main>
  )
}

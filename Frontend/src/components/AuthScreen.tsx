import { zodResolver } from '@hookform/resolvers/zod'
import LoadingButton from '@mui/material/Button'
import Alert from '@mui/material/Alert'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Chip from '@mui/material/Chip'
import Divider from '@mui/material/Divider'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import Tab from '@mui/material/Tab'
import Tabs from '@mui/material/Tabs'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { motion } from 'framer-motion'
import { useMemo, useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { z } from 'zod'

import { useAuthActions } from '../hooks/useAuthActions'
import type { RegisterResponse } from '../types/domain'

const loginSchema = z.object({
  emailOrUsername: z.string().min(3),
  password: z.string().min(8),
})

const registerSchema = z.object({
  email: z.string().email(),
  username: z.string().regex(/^[a-zA-Z0-9_]{3,30}$/),
  displayName: z.string().min(2).max(60),
  password: z.string().min(8).max(72),
})

const verifySchema = z.object({
  token: z.string().min(16),
})

type LoginForm = z.infer<typeof loginSchema>
type RegisterForm = z.infer<typeof registerSchema>
type VerifyForm = z.infer<typeof verifySchema>

export const AuthScreen = () => {
  const { t, i18n } = useTranslation()
  const [tab, setTab] = useState(0)
  const [registration, setRegistration] = useState<RegisterResponse | null>(null)
  const { login, register, verifyEmail } = useAuthActions()
  const activeError = login.error ?? register.error ?? verifyEmail.error

  const loginForm = useForm<LoginForm>({
    resolver: zodResolver(loginSchema),
    defaultValues: { emailOrUsername: '', password: '' },
  })
  const registerForm = useForm<RegisterForm>({
    resolver: zodResolver(registerSchema),
    defaultValues: { email: '', username: '', displayName: '', password: '' },
  })
  const verifyForm = useForm<VerifyForm>({
    resolver: zodResolver(verifySchema),
    defaultValues: { token: '' },
  })

  const language = useMemo(() => (i18n.language.startsWith('en') ? 'en' : 'vi'), [i18n.language])

  const changeLanguage = (nextLanguage: 'en' | 'vi') => {
    localStorage.setItem('ozu-language', nextLanguage)
    void i18n.changeLanguage(nextLanguage)
  }

  return (
    <main className="auth-screen">
      <motion.section
        className="auth-brand"
        initial={{ opacity: 0, y: 18 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.45 }}
      >
        <div className="brand-mark">O</div>
        <Typography variant="h3" component="h1">
          {t('appName')}
        </Typography>
        <Typography color="text.secondary">
          Realtime chat for web and mobile with secure auth, expressive media, and personal themes.
        </Typography>
        <Stack direction="row" spacing={1} useFlexGap sx={{ flexWrap: 'wrap' }}>
          <Chip label="JWT" />
          <Chip label="MongoDB" />
          <Chip label="Realtime" />
          <Chip label="i18n" />
        </Stack>
      </motion.section>

      <Paper className="auth-card" elevation={0}>
        <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'center' }}>
          <Tabs value={tab} onChange={(_, value: number) => setTab(value)} variant="scrollable">
            <Tab label={t('signIn')} />
            <Tab label={t('signUp')} />
            <Tab label={t('verify')} />
          </Tabs>
          <Stack direction="row" spacing={0.5}>
            <Button size="small" variant={language === 'vi' ? 'contained' : 'text'} onClick={() => changeLanguage('vi')}>
              VI
            </Button>
            <Button size="small" variant={language === 'en' ? 'contained' : 'text'} onClick={() => changeLanguage('en')}>
              EN
            </Button>
          </Stack>
        </Stack>

        <Divider />

        {activeError ? <Alert severity="error">{activeError.message}</Alert> : null}
        {registration ? (
          <Alert severity="success">
            {t('registered')} <strong>{t('devHint')}:</strong> {registration.devVerificationToken}
          </Alert>
        ) : null}
        {verifyEmail.isSuccess ? <Alert severity="success">Email verified. You can sign in now.</Alert> : null}

        {tab === 0 ? (
          <Box
            component="form"
            className="auth-form"
            onSubmit={loginForm.handleSubmit((values) => login.mutate(values))}
          >
            <Controller
              control={loginForm.control}
              name="emailOrUsername"
              render={({ field, fieldState }) => (
                <TextField
                  {...field}
                  label={t('emailOrUsername')}
                  error={Boolean(fieldState.error)}
                  helperText={fieldState.error?.message}
                  autoComplete="username"
                />
              )}
            />
            <Controller
              control={loginForm.control}
              name="password"
              render={({ field, fieldState }) => (
                <TextField
                  {...field}
                  type="password"
                  label={t('password')}
                  error={Boolean(fieldState.error)}
                  helperText={fieldState.error?.message}
                  autoComplete="current-password"
                />
              )}
            />
            <LoadingButton type="submit" variant="contained" disabled={login.isPending}>
              {t('continue')}
            </LoadingButton>
          </Box>
        ) : null}

        {tab === 1 ? (
          <Box
            component="form"
            className="auth-form"
            onSubmit={registerForm.handleSubmit((values) =>
              register.mutate(
                { ...values, language },
                {
                  onSuccess: (response) => {
                    setRegistration(response)
                    verifyForm.setValue('token', response.devVerificationToken)
                    setTab(2)
                  },
                },
              ),
            )}
          >
            <Controller
              control={registerForm.control}
              name="email"
              render={({ field, fieldState }) => (
                <TextField {...field} label={t('email')} error={Boolean(fieldState.error)} helperText={fieldState.error?.message} />
              )}
            />
            <Controller
              control={registerForm.control}
              name="username"
              render={({ field, fieldState }) => (
                <TextField {...field} label={t('username')} error={Boolean(fieldState.error)} helperText={fieldState.error?.message} />
              )}
            />
            <Controller
              control={registerForm.control}
              name="displayName"
              render={({ field, fieldState }) => (
                <TextField {...field} label={t('displayName')} error={Boolean(fieldState.error)} helperText={fieldState.error?.message} />
              )}
            />
            <Controller
              control={registerForm.control}
              name="password"
              render={({ field, fieldState }) => (
                <TextField
                  {...field}
                  type="password"
                  label={t('password')}
                  error={Boolean(fieldState.error)}
                  helperText={fieldState.error?.message}
                />
              )}
            />
            <LoadingButton type="submit" variant="contained" disabled={register.isPending}>
              {t('signUp')}
            </LoadingButton>
          </Box>
        ) : null}

        {tab === 2 ? (
          <Box
            component="form"
            className="auth-form"
            onSubmit={verifyForm.handleSubmit((values) => verifyEmail.mutate(values.token))}
          >
            <Controller
              control={verifyForm.control}
              name="token"
              render={({ field, fieldState }) => (
                <TextField
                  {...field}
                  label={t('verifyToken')}
                  error={Boolean(fieldState.error)}
                  helperText={fieldState.error?.message}
                  multiline
                  minRows={3}
                />
              )}
            />
            <LoadingButton type="submit" variant="contained" disabled={verifyEmail.isPending}>
              {t('verify')}
            </LoadingButton>
          </Box>
        ) : null}
      </Paper>
    </main>
  )
}

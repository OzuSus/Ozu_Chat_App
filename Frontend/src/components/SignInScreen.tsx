import { zodResolver } from '@hookform/resolvers/zod'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { Link, useNavigate } from '@tanstack/react-router'
import { Controller, useForm } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { z } from 'zod'

import { useAuthActions } from '../hooks/useAuthActions'
import { AuthLayout } from './AuthLayout'

const loginSchema = z.object({
  emailOrUsername: z.string().min(3, 'Enter your email or username'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
})

type LoginForm = z.infer<typeof loginSchema>

export const SignInScreen = () => {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const { login } = useAuthActions()
  const form = useForm<LoginForm>({
    resolver: zodResolver(loginSchema),
    defaultValues: { emailOrUsername: '', password: '' },
  })

  return (
    <AuthLayout
      title={t('signIn')}
      subtitle={t('signInSubtitle')}
      error={login.error}
    >
      <Box
        component="form"
        className="auth-form"
        onSubmit={form.handleSubmit((values) =>
          login.mutate(values, {
            onSuccess: () => {
              void navigate({ to: '/' })
            },
          }),
        )}
      >
        <Controller
          control={form.control}
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
          control={form.control}
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
        <Button type="submit" variant="contained" disabled={login.isPending}>
          {t('continue')}
        </Button>
      </Box>
      <Stack direction="row" spacing={1} className="auth-links">
        <Typography color="text.secondary">{t('noAccount')}</Typography>
        <Link to="/sign-up">{t('signUp')}</Link>
        <span aria-hidden="true">/</span>
        <Link to="/verify-email" search={{ token: undefined }}>{t('verify')}</Link>
      </Stack>
    </AuthLayout>
  )
}

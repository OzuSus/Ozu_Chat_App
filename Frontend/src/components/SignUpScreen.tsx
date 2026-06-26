import { zodResolver } from '@hookform/resolvers/zod'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { Link, useNavigate } from '@tanstack/react-router'
import { Controller, useForm } from 'react-hook-form'
import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { z } from 'zod'

import { useAuthActions } from '../hooks/useAuthActions'
import type { RegisterResponse } from '../types/domain'
import { AuthLayout } from './AuthLayout'

const registerSchema = z.object({
  email: z.string().email('Enter a valid email'),
  username: z.string().regex(/^[a-zA-Z0-9_]{3,30}$/, 'Use 3-30 letters, numbers, or underscores'),
  displayName: z.string().min(2, 'Display name is required').max(60),
  password: z.string().min(8, 'Password must be at least 8 characters').max(72),
})

type RegisterForm = z.infer<typeof registerSchema>

export const SignUpScreen = () => {
  const { t, i18n } = useTranslation()
  const navigate = useNavigate()
  const { register } = useAuthActions()
  const [registration, setRegistration] = useState<RegisterResponse | null>(null)
  const form = useForm<RegisterForm>({
    resolver: zodResolver(registerSchema),
    defaultValues: { email: '', username: '', displayName: '', password: '' },
  })

  return (
    <AuthLayout
      title={t('signUp')}
      subtitle={t('signUpSubtitle')}
      error={register.error}
      notice={
        registration ? (
          <span>
            {t('registered')} {t('devHint')}: <strong>{registration.devVerificationToken}</strong>
          </span>
        ) : null
      }
    >
      <Box
        component="form"
        className="auth-form"
        onSubmit={form.handleSubmit((values) =>
          register.mutate(
            {
              ...values,
              language: i18n.language.startsWith('en') ? 'en' : 'vi',
            },
            {
              onSuccess: (response) => {
                setRegistration(response)
                void navigate({
                  to: '/verify-email',
                  search: { token: response.devVerificationToken },
                })
              },
            },
          ),
        )}
      >
        <Controller
          control={form.control}
          name="email"
          render={({ field, fieldState }) => (
            <TextField {...field} label={t('email')} error={Boolean(fieldState.error)} helperText={fieldState.error?.message} />
          )}
        />
        <Controller
          control={form.control}
          name="username"
          render={({ field, fieldState }) => (
            <TextField {...field} label={t('username')} error={Boolean(fieldState.error)} helperText={fieldState.error?.message} />
          )}
        />
        <Controller
          control={form.control}
          name="displayName"
          render={({ field, fieldState }) => (
            <TextField {...field} label={t('displayName')} error={Boolean(fieldState.error)} helperText={fieldState.error?.message} />
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
            />
          )}
        />
        <Button type="submit" variant="contained" disabled={register.isPending}>
          {t('signUp')}
        </Button>
      </Box>
      <Stack direction="row" spacing={1} className="auth-links">
        <Typography color="text.secondary">{t('alreadyVerified')}</Typography>
        <Link to="/sign-in">{t('signIn')}</Link>
      </Stack>
    </AuthLayout>
  )
}

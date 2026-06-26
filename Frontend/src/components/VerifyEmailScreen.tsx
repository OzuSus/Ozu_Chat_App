import { zodResolver } from '@hookform/resolvers/zod'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { Link, useNavigate, useSearch } from '@tanstack/react-router'
import { Controller, useForm } from 'react-hook-form'
import { useEffect } from 'react'
import { useTranslation } from 'react-i18next'
import { z } from 'zod'

import { useAuthActions } from '../hooks/useAuthActions'
import { AuthLayout } from './AuthLayout'

const verifySchema = z.object({
  token: z.string().min(16, 'Verification token is required'),
})

type VerifyForm = z.infer<typeof verifySchema>

export const VerifyEmailScreen = () => {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const search = useSearch({ from: '/verify-email' })
  const { verifyEmail } = useAuthActions()
  const form = useForm<VerifyForm>({
    resolver: zodResolver(verifySchema),
    defaultValues: { token: typeof search.token === 'string' ? search.token : '' },
  })

  useEffect(() => {
    if (typeof search.token === 'string') {
      form.setValue('token', search.token)
    }
  }, [form, search.token])

  return (
    <AuthLayout
      title={t('verify')}
      subtitle={t('verifySubtitle')}
      error={verifyEmail.error}
      notice={verifyEmail.isSuccess ? t('emailVerified') : null}
    >
      <Box
        component="form"
        className="auth-form"
        onSubmit={form.handleSubmit((values) =>
          verifyEmail.mutate(values.token, {
            onSuccess: () => {
              window.setTimeout(() => {
                void navigate({ to: '/sign-in' })
              }, 700)
            },
          }),
        )}
      >
        <Controller
          control={form.control}
          name="token"
          render={({ field, fieldState }) => (
            <TextField
              {...field}
              label={t('verifyToken')}
              error={Boolean(fieldState.error)}
              helperText={fieldState.error?.message}
              multiline
              minRows={4}
            />
          )}
        />
        <Button type="submit" variant="contained" disabled={verifyEmail.isPending}>
          {t('verify')}
        </Button>
      </Box>
      <Stack direction="row" spacing={1} className="auth-links">
        <Typography color="text.secondary">{t('haveAccount')}</Typography>
        <Link to="/sign-in">{t('signIn')}</Link>
      </Stack>
    </AuthLayout>
  )
}

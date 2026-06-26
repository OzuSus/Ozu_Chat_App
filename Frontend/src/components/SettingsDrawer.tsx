import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Divider from '@mui/material/Divider'
import Drawer from '@mui/material/Drawer'
import IconButton from '@mui/material/IconButton'
import Slider from '@mui/material/Slider'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import ToggleButton from '@mui/material/ToggleButton'
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup'
import Typography from '@mui/material/Typography'
import { Camera, X } from 'lucide-react'
import { useEffect, useState } from 'react'
import type { ChangeEvent } from 'react'
import { useTranslation } from 'react-i18next'
import { useMutation } from '@tanstack/react-query'

import { userService } from '../services/userService'
import { useAuthStore } from '../stores/authStore'
import { useUiStore } from '../stores/uiStore'
import type { ThemeMode, ThemePreference } from '../types/domain'
import { AvatarView } from './AvatarView'

export const SettingsDrawer = () => {
  const { t, i18n } = useTranslation()
  const open = useUiStore((state) => state.settingsOpen)
  const setOpen = useUiStore((state) => state.setSettingsOpen)
  const user = useAuthStore((state) => state.user)
  const setUser = useAuthStore((state) => state.setUser)
  const [displayName, setDisplayName] = useState(user?.displayName ?? '')
  const [bio, setBio] = useState(user?.bio ?? '')
  const [language, setLanguage] = useState<'en' | 'vi'>(user?.language ?? 'vi')
  const [theme, setTheme] = useState<ThemePreference>(
    user?.theme ?? {
      mode: 'SYSTEM',
      primaryColor: '#5b7cfa',
      secondaryColor: '#00b8a9',
      accentColor: '#ffb703',
      fontSize: 15,
      borderRadius: 16,
    },
  )

  useEffect(() => {
    if (user) {
      setDisplayName(user.displayName)
      setBio(user.bio)
      setLanguage(user.language)
      setTheme(user.theme)
    }
  }, [user])

  const updateProfile = useMutation({
    mutationFn: () => userService.updateProfile({ displayName, bio, language, theme }),
    onSuccess: (profile) => {
      setUser(profile)
      localStorage.setItem('ozu-language', profile.language)
      void i18n.changeLanguage(profile.language)
    },
  })

  const uploadAvatar = useMutation({
    mutationFn: (file: File) => userService.uploadAvatar(file),
    onSuccess: (attachment) => {
      if (user) {
        setUser({ ...user, avatarUrl: attachment.url })
      }
    },
  })

  const removeAvatar = useMutation({
    mutationFn: userService.removeAvatar,
    onSuccess: setUser,
  })

  const chooseAvatar = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (file) {
      uploadAvatar.mutate(file)
    }
  }

  return (
    <Drawer anchor="right" open={open} onClose={() => setOpen(false)}>
      <Box className="settings-drawer">
        <Stack direction="row" sx={{ justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h6">{t('settings')}</Typography>
          <IconButton onClick={() => setOpen(false)}>
            <X size={18} />
          </IconButton>
        </Stack>

        <Stack spacing={1.25} sx={{ alignItems: 'center' }}>
          <AvatarView name={user?.displayName ?? 'Ozu'} src={user?.avatarUrl} size={84} />
          <Stack direction="row" spacing={1}>
            <Button component="label" variant="outlined" startIcon={<Camera size={16} />}>
              {t('uploadAvatar')}
              <input hidden type="file" accept="image/*" onChange={chooseAvatar} />
            </Button>
            <Button color="error" onClick={() => removeAvatar.mutate()} disabled={!user?.avatarUrl}>
              {t('removeAvatar')}
            </Button>
          </Stack>
        </Stack>

        <Divider />

        <TextField value={displayName} onChange={(event) => setDisplayName(event.target.value)} label={t('displayName')} />
        <TextField value={bio} onChange={(event) => setBio(event.target.value)} label={t('bio')} multiline minRows={3} />

        <Stack spacing={1}>
          <Typography variant="subtitle2">{t('language')}</Typography>
          <ToggleButtonGroup
            value={language}
            exclusive
            onChange={(_, value: 'en' | 'vi' | null) => value && setLanguage(value)}
            fullWidth
          >
            <ToggleButton value="vi">VI</ToggleButton>
            <ToggleButton value="en">EN</ToggleButton>
          </ToggleButtonGroup>
        </Stack>

        <Stack spacing={1}>
          <Typography variant="subtitle2">{t('theme')}</Typography>
          <ToggleButtonGroup
            value={theme.mode}
            exclusive
            onChange={(_, value: ThemeMode | null) => value && setTheme((current) => ({ ...current, mode: value }))}
            fullWidth
          >
            <ToggleButton value="LIGHT">{t('light')}</ToggleButton>
            <ToggleButton value="DARK">{t('dark')}</ToggleButton>
            <ToggleButton value="SYSTEM">{t('system')}</ToggleButton>
          </ToggleButtonGroup>
        </Stack>

        <ColorField label={t('primary')} value={theme.primaryColor} onChange={(primaryColor) => setTheme((current) => ({ ...current, primaryColor }))} />
        <ColorField label={t('secondary')} value={theme.secondaryColor} onChange={(secondaryColor) => setTheme((current) => ({ ...current, secondaryColor }))} />
        <ColorField label={t('accent')} value={theme.accentColor} onChange={(accentColor) => setTheme((current) => ({ ...current, accentColor }))} />

        <Stack spacing={1}>
          <Typography variant="subtitle2">{t('fontSize')}</Typography>
          <Slider value={theme.fontSize} min={13} max={20} onChange={(_, value) => setTheme((current) => ({ ...current, fontSize: Number(value) }))} />
        </Stack>
        <Stack spacing={1}>
          <Typography variant="subtitle2">{t('radius')}</Typography>
          <Slider value={theme.borderRadius} min={8} max={28} onChange={(_, value) => setTheme((current) => ({ ...current, borderRadius: Number(value) }))} />
        </Stack>

        <Button variant="contained" onClick={() => updateProfile.mutate()} disabled={updateProfile.isPending}>
          {t('save')}
        </Button>
      </Box>
    </Drawer>
  )
}

interface ColorFieldProps {
  label: string
  value: string
  onChange: (value: string) => void
}

const ColorField = ({ label, value, onChange }: ColorFieldProps) => (
  <Stack direction="row" spacing={1.5} sx={{ alignItems: 'center' }}>
    <input className="color-input" type="color" value={value} onChange={(event) => onChange(event.target.value)} aria-label={`${label} picker`} />
    <TextField value={value} onChange={(event) => onChange(event.target.value)} label={label} fullWidth />
  </Stack>
)

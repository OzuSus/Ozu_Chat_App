import AvatarGroup from '@mui/material/AvatarGroup'
import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import Checkbox from '@mui/material/Checkbox'
import Dialog from '@mui/material/Dialog'
import DialogActions from '@mui/material/DialogActions'
import DialogContent from '@mui/material/DialogContent'
import DialogTitle from '@mui/material/DialogTitle'
import List from '@mui/material/List'
import ListItemButton from '@mui/material/ListItemButton'
import ListItemIcon from '@mui/material/ListItemIcon'
import ListItemText from '@mui/material/ListItemText'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import ToggleButton from '@mui/material/ToggleButton'
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup'
import Typography from '@mui/material/Typography'
import { useQuery } from '@tanstack/react-query'
import { useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'

import { useCreateConversation } from '../hooks/useChatQueries'
import { userService } from '../services/userService'
import { useAuthStore } from '../stores/authStore'
import { useUiStore } from '../stores/uiStore'
import type { ConversationType, UserSummary } from '../types/domain'
import { AvatarView } from './AvatarView'

export const NewConversationDialog = () => {
  const { t } = useTranslation()
  const open = useUiStore((state) => state.newChatOpen)
  const setOpen = useUiStore((state) => state.setNewChatOpen)
  const setActiveConversationId = useUiStore((state) => state.setActiveConversationId)
  const currentUser = useAuthStore((state) => state.user)
  const [query, setQuery] = useState('')
  const [type, setType] = useState<ConversationType>('PRIVATE')
  const [name, setName] = useState('')
  const [selected, setSelected] = useState<UserSummary[]>([])
  const createConversation = useCreateConversation()

  const usersQuery = useQuery({
    queryKey: ['users', query],
    queryFn: () => userService.search(query),
    enabled: open,
  })

  const users = useMemo(
    () => (usersQuery.data ?? []).filter((user) => user.id !== currentUser?.id),
    [currentUser?.id, usersQuery.data],
  )

  const toggleUser = (user: UserSummary) => {
    setSelected((items) => {
      const exists = items.some((item) => item.id === user.id)
      if (exists) {
        return items.filter((item) => item.id !== user.id)
      }
      if (type === 'PRIVATE') {
        return [user]
      }
      return [...items, user]
    })
  }

  const close = () => {
    setOpen(false)
    setSelected([])
    setQuery('')
    setName('')
  }

  const submit = () => {
    createConversation.mutate(
      {
        type,
        name: type === 'GROUP' ? name : undefined,
        memberIds: selected.map((user) => user.id),
      },
      {
        onSuccess: (conversation) => {
          setActiveConversationId(conversation.id)
          close()
        },
      },
    )
  }

  return (
    <Dialog open={open} onClose={close} fullWidth maxWidth="sm">
      <DialogTitle>{t('newChat')}</DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ pt: 1 }}>
          <ToggleButtonGroup
            value={type}
            exclusive
            onChange={(_, value: ConversationType | null) => {
              if (value) {
                setType(value)
                setSelected([])
              }
            }}
            fullWidth
          >
            <ToggleButton value="PRIVATE">{t('privateChat')}</ToggleButton>
            <ToggleButton value="GROUP">{t('groupChat')}</ToggleButton>
          </ToggleButtonGroup>

          {type === 'GROUP' ? (
            <TextField value={name} onChange={(event) => setName(event.target.value)} label={t('groupName')} />
          ) : null}

          <TextField value={query} onChange={(event) => setQuery(event.target.value)} label={t('searchUsers')} />

          {selected.length > 0 ? (
            <Stack direction="row" spacing={1} sx={{ alignItems: 'center' }}>
              <AvatarGroup max={5}>
                {selected.map((user) => (
                  <AvatarView key={user.id} name={user.displayName} src={user.avatarUrl} size={32} />
                ))}
              </AvatarGroup>
              <Typography variant="body2" color="text.secondary">
                {selected.map((user) => user.displayName).join(', ')}
              </Typography>
            </Stack>
          ) : null}

          <Box className="user-picker">
            <List dense disablePadding>
              {users.map((user) => {
                const checked = selected.some((item) => item.id === user.id)
                return (
                  <ListItemButton key={user.id} onClick={() => toggleUser(user)} selected={checked}>
                    <ListItemIcon>
                      <Checkbox edge="start" checked={checked} tabIndex={-1} disableRipple />
                    </ListItemIcon>
                    <AvatarView name={user.displayName} src={user.avatarUrl} size={36} />
                    <ListItemText
                      primary={user.displayName}
                      secondary={`@${user.username}`}
                      sx={{ ml: 1.5 }}
                    />
                  </ListItemButton>
                )
              })}
            </List>
          </Box>
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={close}>Cancel</Button>
        <Button
          variant="contained"
          onClick={submit}
          disabled={selected.length === 0 || createConversation.isPending || (type === 'GROUP' && name.trim().length < 2)}
        >
          {t('startChat')}
        </Button>
      </DialogActions>
    </Dialog>
  )
}

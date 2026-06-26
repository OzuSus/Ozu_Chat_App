import Badge from '@mui/material/Badge'
import Button from '@mui/material/Button'
import IconButton from '@mui/material/IconButton'
import InputAdornment from '@mui/material/InputAdornment'
import List from '@mui/material/List'
import ListItemButton from '@mui/material/ListItemButton'
import ListItemText from '@mui/material/ListItemText'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import Tooltip from '@mui/material/Tooltip'
import Typography from '@mui/material/Typography'
import { LogOut, Plus, Search, Settings } from 'lucide-react'
import { useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'

import { useAuthActions } from '../hooks/useAuthActions'
import { formatMessageTime } from '../lib/format'
import { useAuthStore } from '../stores/authStore'
import { useUiStore } from '../stores/uiStore'
import type { Conversation } from '../types/domain'
import { AvatarView } from './AvatarView'

interface ConversationSidebarProps {
  conversations: Conversation[]
}

export const ConversationSidebar = ({ conversations }: ConversationSidebarProps) => {
  const { t } = useTranslation()
  const user = useAuthStore((state) => state.user)
  const activeConversationId = useUiStore((state) => state.activeConversationId)
  const setActiveConversationId = useUiStore((state) => state.setActiveConversationId)
  const setSettingsOpen = useUiStore((state) => state.setSettingsOpen)
  const setNewChatOpen = useUiStore((state) => state.setNewChatOpen)
  const { logout } = useAuthActions()
  const [query, setQuery] = useState('')

  const filteredConversations = useMemo(() => {
    const normalized = query.trim().toLowerCase()
    if (!normalized) {
      return conversations
    }
    return conversations.filter((conversation) =>
      conversationName(conversation, user?.id).toLowerCase().includes(normalized),
    )
  }, [conversations, query, user?.id])

  return (
    <aside className="conversation-sidebar">
      <Stack direction="row" className="account-row" sx={{ alignItems: 'center', justifyContent: 'space-between' }}>
        <Stack direction="row" spacing={1.25} sx={{ alignItems: 'center', minWidth: 0 }}>
          <Badge
            color="success"
            overlap="circular"
            variant="dot"
            anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
          >
            <AvatarView name={user?.displayName ?? 'Ozu'} src={user?.avatarUrl} />
          </Badge>
          <Stack sx={{ minWidth: 0 }}>
            <Typography variant="subtitle1" noWrap>
              {user?.displayName}
            </Typography>
            <Typography variant="caption" color="text.secondary" noWrap>
              @{user?.username}
            </Typography>
          </Stack>
        </Stack>
        <Stack direction="row" spacing={0.5}>
          <Tooltip title={t('settings')}>
            <IconButton size="small" onClick={() => setSettingsOpen(true)}>
              <Settings size={18} />
            </IconButton>
          </Tooltip>
          <Tooltip title={t('logout')}>
            <IconButton size="small" onClick={() => logout.mutate()}>
              <LogOut size={18} />
            </IconButton>
          </Tooltip>
        </Stack>
      </Stack>

      <Stack direction="row" className="sidebar-title" sx={{ alignItems: 'center', justifyContent: 'space-between' }}>
        <Typography variant="h6">{t('conversations')}</Typography>
        <Button size="small" variant="contained" startIcon={<Plus size={16} />} onClick={() => setNewChatOpen(true)}>
          {t('newChat')}
        </Button>
      </Stack>

      <TextField
        value={query}
        onChange={(event) => setQuery(event.target.value)}
        placeholder={t('search')}
        size="small"
        slotProps={{
          input: {
            startAdornment: (
              <InputAdornment position="start">
                <Search size={16} />
              </InputAdornment>
            ),
          },
        }}
      />

      <List className="conversation-list" disablePadding>
        {filteredConversations.map((conversation) => {
          const active = conversation.id === activeConversationId
          const name = conversationName(conversation, user?.id)
          const peer = conversation.members.find((member) => member.id !== user?.id)
          return (
            <ListItemButton
              key={conversation.id}
              selected={active}
              onClick={() => setActiveConversationId(conversation.id)}
              className="conversation-item"
            >
              <AvatarView name={name} src={conversation.avatarUrl ?? peer?.avatarUrl} />
              <ListItemText
                primary={<Typography noWrap>{name}</Typography>}
                secondary={
                  <Typography variant="body2" color="text.secondary" noWrap>
                    {conversation.lastMessage?.content || conversation.type.toLowerCase()}
                  </Typography>
                }
                sx={{ ml: 1.25 }}
              />
              <Typography variant="caption" color="text.secondary">
                {formatMessageTime(conversation.lastMessageAt)}
              </Typography>
            </ListItemButton>
          )
        })}
      </List>
    </aside>
  )
}

export const conversationName = (conversation: Conversation, currentUserId?: string): string => {
  if (conversation.type === 'GROUP') {
    return conversation.name ?? 'Group'
  }
  const peer = conversation.members.find((member) => member.id !== currentUserId)
  return peer?.displayName ?? conversation.members[0]?.displayName ?? 'Conversation'
}

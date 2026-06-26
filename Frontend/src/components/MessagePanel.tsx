import Box from '@mui/material/Box'
import Button from '@mui/material/Button'
import IconButton from '@mui/material/IconButton'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import Tooltip from '@mui/material/Tooltip'
import Typography from '@mui/material/Typography'
import { FileText, MoreHorizontal, Phone, Video } from 'lucide-react'
import { Virtuoso } from 'react-virtuoso'
import { useTranslation } from 'react-i18next'

import { useMessages } from '../hooks/useChatQueries'
import { apiAssetUrl, formatFileSize, formatLocalDateTime, formatMessageTime } from '../lib/format'
import { useAuthStore } from '../stores/authStore'
import type { Attachment, Conversation, Message } from '../types/domain'
import { AvatarView } from './AvatarView'
import { Composer } from './Composer'
import { conversationName } from './ConversationSidebar'

interface MessagePanelProps {
  conversation: Conversation | null
}

export const MessagePanel = ({ conversation }: MessagePanelProps) => {
  const { t } = useTranslation()
  const currentUser = useAuthStore((state) => state.user)
  const messages = useMessages(conversation?.id ?? null)
  const title = conversation ? conversationName(conversation, currentUser?.id) : t('noConversation')
  const peer = conversation?.members.find((member) => member.id !== currentUser?.id)

  if (!conversation) {
    return (
      <section className="message-panel empty-panel">
        <div className="empty-orbit">
          <div className="brand-mark">O</div>
        </div>
        <Typography variant="h5">{t('noConversation')}</Typography>
        <Typography color="text.secondary">Create a chat and messages will appear here in realtime.</Typography>
      </section>
    )
  }

  return (
    <section className="message-panel">
      <header className="chat-header">
        <Stack direction="row" spacing={1.25} sx={{ alignItems: 'center', minWidth: 0 }}>
          <AvatarView name={title} src={conversation.avatarUrl ?? peer?.avatarUrl} />
          <Stack sx={{ minWidth: 0 }}>
            <Typography variant="subtitle1" noWrap>
              {title}
            </Typography>
            <Typography variant="caption" color="text.secondary" noWrap>
              {conversation.members.length} {t('members')}
            </Typography>
          </Stack>
        </Stack>
        <Stack direction="row" spacing={0.5}>
          <Tooltip title="Voice call">
            <IconButton size="small">
              <Phone size={18} />
            </IconButton>
          </Tooltip>
          <Tooltip title="Video call">
            <IconButton size="small">
              <Video size={18} />
            </IconButton>
          </Tooltip>
          <Tooltip title="More">
            <IconButton size="small">
              <MoreHorizontal size={18} />
            </IconButton>
          </Tooltip>
        </Stack>
      </header>

      <Box className="messages-viewport">
        <Virtuoso
          data={messages.data ?? []}
          followOutput="smooth"
          itemContent={(_, message) => <MessageBubble message={message} own={message.sender.id === currentUser?.id} />}
        />
      </Box>

      <Composer conversationId={conversation.id} />
    </section>
  )
}

interface MessageBubbleProps {
  message: Message
  own: boolean
}

const MessageBubble = ({ message, own }: MessageBubbleProps) => (
  <div className={`message-row ${own ? 'own' : ''}`}>
    {!own ? <AvatarView name={message.sender.displayName} src={message.sender.avatarUrl} size={32} /> : null}
    <div className="bubble-wrap">
      {!own ? (
        <Typography variant="caption" color="text.secondary">
          {message.sender.displayName}
        </Typography>
      ) : null}
      <Paper className="message-bubble" elevation={0}>
        {message.status === 'RECALLED' ? (
          <Typography variant="body2" color="text.secondary">
            Message recalled
          </Typography>
        ) : (
          <>
            {message.content ? <Typography className="message-text">{message.content}</Typography> : null}
            {message.attachments.map((attachment) => (
              <AttachmentPreview key={attachment.id} attachment={attachment} />
            ))}
          </>
        )}
      </Paper>
      <Typography variant="caption" color="text.secondary" title={formatLocalDateTime(message.createdAt)}>
        {formatMessageTime(message.createdAt)}
      </Typography>
    </div>
  </div>
)

const AttachmentPreview = ({ attachment }: { attachment: Attachment }) => {
  if (attachment.kind === 'IMAGE' || attachment.kind === 'GIF' || attachment.kind === 'AVATAR') {
    return <img className="message-media" src={apiAssetUrl(attachment.url)} alt={attachment.originalName} loading="lazy" />
  }
  if (attachment.kind === 'VIDEO') {
    return <video className="message-media" src={apiAssetUrl(attachment.url)} controls preload="metadata" />
  }
  return (
    <Button
      className="file-card"
      href={apiAssetUrl(attachment.url)}
      target="_blank"
      rel="noreferrer"
      startIcon={<FileText size={18} />}
      variant="outlined"
    >
      <Stack sx={{ alignItems: 'flex-start', minWidth: 0 }}>
        <span>{attachment.originalName}</span>
        <small>{formatFileSize(attachment.size)}</small>
      </Stack>
    </Button>
  )
}

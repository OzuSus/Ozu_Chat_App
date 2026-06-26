import Box from '@mui/material/Box'
import CircularProgress from '@mui/material/CircularProgress'
import IconButton from '@mui/material/IconButton'
import Paper from '@mui/material/Paper'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import Tooltip from '@mui/material/Tooltip'
import Typography from '@mui/material/Typography'
import EmojiPicker, { type EmojiClickData } from 'emoji-picker-react'
import { File, Image, Laugh, Send, X } from 'lucide-react'
import { useMemo, useState } from 'react'
import { useDropzone } from 'react-dropzone'
import { useTranslation } from 'react-i18next'

import { useSendMessage, useUploadAttachment } from '../hooks/useChatQueries'
import { formatFileSize } from '../lib/format'
import type { Attachment, AttachmentKind, MessageType } from '../types/domain'

interface ComposerProps {
  conversationId: string | null
}

export const Composer = ({ conversationId }: ComposerProps) => {
  const { t } = useTranslation()
  const [content, setContent] = useState('')
  const [attachments, setAttachments] = useState<Attachment[]>([])
  const [emojiOpen, setEmojiOpen] = useState(false)
  const upload = useUploadAttachment()
  const sendMessage = useSendMessage(conversationId)

  const { getRootProps, getInputProps, open, isDragActive } = useDropzone({
    noClick: true,
    maxFiles: 5,
    onDrop: (files) => {
      files.forEach((file) => {
        upload.mutate(file, {
          onSuccess: (attachment) => setAttachments((items) => [...items, attachment]),
        })
      })
    },
  })

  const messageType = useMemo<MessageType>(() => {
    if (attachments.length === 0) {
      return content.trim().length <= 4 && /\p{Emoji}/u.test(content.trim()) ? 'EMOJI' : 'TEXT'
    }
    const firstKind = attachments[0]?.kind
    if (firstKind === 'IMAGE' || firstKind === 'AVATAR') {
      return 'IMAGE'
    }
    if (firstKind === 'VIDEO') {
      return 'VIDEO'
    }
    if (firstKind === 'GIF') {
      return 'GIF'
    }
    return 'FILE'
  }, [attachments, content])

  const send = () => {
    const trimmed = content.trim()
    if (!conversationId || (!trimmed && attachments.length === 0)) {
      return
    }
    sendMessage.mutate(
      {
        type: messageType,
        content: trimmed,
        attachmentIds: attachments.map((attachment) => attachment.id),
      },
      {
        onSuccess: () => {
          setContent('')
          setAttachments([])
          setEmojiOpen(false)
        },
      },
    )
  }

  const addEmoji = (emoji: EmojiClickData) => {
    setContent((value) => `${value}${emoji.emoji}`)
  }

  return (
    <Box className={`composer-drop ${isDragActive ? 'dragging' : ''}`} {...getRootProps()}>
      <input {...getInputProps()} />
      {emojiOpen ? (
        <Paper className="emoji-popover" elevation={8}>
          <EmojiPicker onEmojiClick={addEmoji} height={360} width="100%" lazyLoadEmojis />
        </Paper>
      ) : null}

      {attachments.length > 0 ? (
        <Stack direction="row" spacing={1} className="attachment-strip">
          {attachments.map((attachment) => (
            <Paper key={attachment.id} className="attachment-pill" elevation={0}>
              {iconForKind(attachment.kind)}
              <Stack sx={{ minWidth: 0 }}>
                <Typography variant="caption" noWrap>
                  {attachment.originalName}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  {formatFileSize(attachment.size)}
                </Typography>
              </Stack>
              <IconButton
                size="small"
                onClick={() => setAttachments((items) => items.filter((item) => item.id !== attachment.id))}
              >
                <X size={14} />
              </IconButton>
            </Paper>
          ))}
        </Stack>
      ) : null}

      <Paper className="composer" elevation={0}>
        <Tooltip title={t('attach')}>
          <IconButton onClick={open} disabled={!conversationId || upload.isPending}>
            {upload.isPending ? <CircularProgress size={18} /> : <File size={20} />}
          </IconButton>
        </Tooltip>
        <Tooltip title={t('emoji')}>
          <IconButton onClick={() => setEmojiOpen((value) => !value)} disabled={!conversationId}>
            <Laugh size={20} />
          </IconButton>
        </Tooltip>
        <TextField
          value={content}
          onChange={(event) => setContent(event.target.value)}
          onKeyDown={(event) => {
            if (event.key === 'Enter' && !event.shiftKey) {
              event.preventDefault()
              send()
            }
          }}
          placeholder={t('messagePlaceholder')}
          disabled={!conversationId}
          multiline
          maxRows={5}
          variant="standard"
          fullWidth
          slotProps={{ input: { disableUnderline: true } }}
        />
        <Tooltip title={t('send')}>
          <span>
            <IconButton
              color="primary"
              onClick={send}
              disabled={!conversationId || sendMessage.isPending || (!content.trim() && attachments.length === 0)}
            >
              <Send size={20} />
            </IconButton>
          </span>
        </Tooltip>
      </Paper>
    </Box>
  )
}

const iconForKind = (kind: AttachmentKind) => {
  if (kind === 'IMAGE' || kind === 'GIF' || kind === 'AVATAR') {
    return <Image size={18} />
  }
  return <File size={18} />
}

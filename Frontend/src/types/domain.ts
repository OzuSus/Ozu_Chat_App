export type ThemeMode = 'LIGHT' | 'DARK' | 'SYSTEM'

export type UserStatus = 'OFFLINE' | 'ONLINE' | 'AWAY'

export type UserRole = 'ROLE_USER' | 'ROLE_ADMIN'

export type ConversationType = 'PRIVATE' | 'GROUP'

export type MessageType =
  | 'TEXT'
  | 'IMAGE'
  | 'VIDEO'
  | 'GIF'
  | 'EMOJI'
  | 'FILE'
  | 'AUDIO'
  | 'STICKER'

export type MessageStatus = 'SENT' | 'DELIVERED' | 'SEEN' | 'DELETED' | 'RECALLED'

export type AttachmentKind =
  | 'IMAGE'
  | 'VIDEO'
  | 'GIF'
  | 'PDF'
  | 'DOCUMENT'
  | 'SPREADSHEET'
  | 'ARCHIVE'
  | 'FILE'
  | 'AVATAR'

export interface ThemePreference {
  mode: ThemeMode
  primaryColor: string
  secondaryColor: string
  accentColor: string
  fontSize: number
  borderRadius: number
}

export interface UserSummary {
  id: string
  username: string
  displayName: string
  avatarUrl: string | null
  status: UserStatus | null
  lastSeenAt: string | null
}

export interface UserProfile extends UserSummary {
  email: string
  bio: string
  language: 'en' | 'vi'
  theme: ThemePreference
  emailVerified: boolean
  roles: UserRole[]
  createdAt: string | null
  updatedAt: string | null
}

export interface Attachment {
  id: string
  originalName: string
  contentType: string | null
  size: number
  kind: AttachmentKind
  url: string
  width: number | null
  height: number | null
  durationMs: number | null
  createdAt: string | null
}

export interface MessageReaction {
  userId: string
  emoji: string
  createdAt: string
}

export interface Message {
  id: string
  conversationId: string
  sender: UserSummary
  type: MessageType
  status: MessageStatus
  content: string
  attachments: Attachment[]
  replyToMessageId: string | null
  reactions: MessageReaction[]
  editedAt: string | null
  deletedAt: string | null
  recalledAt: string | null
  createdAt: string | null
  updatedAt: string | null
}

export interface Conversation {
  id: string
  type: ConversationType
  name: string | null
  avatarUrl: string | null
  members: UserSummary[]
  createdBy: string
  lastMessage: Message | null
  lastMessageAt: string | null
  createdAt: string | null
  updatedAt: string | null
}

export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
  timestamp: string
}

export interface PageResponse<T> {
  items: T[]
  page: number
  size: number
  total: number
  hasNext: boolean
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  accessTokenExpiresAt: string
  refreshTokenExpiresAt: string
  user: UserProfile
}

export interface RegisterResponse {
  user: UserProfile
  devVerificationToken: string
}

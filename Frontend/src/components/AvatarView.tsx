import Avatar from '@mui/material/Avatar'

import { apiAssetUrl } from '../lib/format'

interface AvatarViewProps {
  name: string
  src?: string | null
  size?: number
}

export const AvatarView = ({ name, src, size = 42 }: AvatarViewProps) => {
  const initials = name
    .split(' ')
    .map((part) => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase()

  return (
    <Avatar
      src={src ? apiAssetUrl(src) : undefined}
      alt={name}
      sx={{
        width: size,
        height: size,
        fontSize: size < 40 ? 13 : 15,
        background: 'linear-gradient(135deg, var(--primary), var(--secondary))',
      }}
    >
      {initials}
    </Avatar>
  )
}

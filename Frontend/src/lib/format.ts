import dayjs from 'dayjs'

export const formatMessageTime = (value: string | null): string => {
  if (!value) {
    return ''
  }
  return dayjs(value).format('HH:mm')
}

export const formatLocalDateTime = (value: string | null): string => {
  if (!value) {
    return ''
  }
  return dayjs(value).format('DD/MM/YYYY HH:mm')
}

export const formatFileSize = (bytes: number): string => {
  if (bytes < 1024) {
    return `${bytes} B`
  }
  if (bytes < 1024 * 1024) {
    return `${(bytes / 1024).toFixed(1)} KB`
  }
  return `${(bytes / 1024 / 1024).toFixed(1)} MB`
}

export const apiAssetUrl = (url: string): string => {
  if (url.startsWith('http')) {
    return url
  }
  const apiUrl = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'
  return `${apiUrl.replace(/\/api\/?$/, '')}${url}`
}

# Ozu Chat App

Full-stack realtime chat app for web and mobile-sized screens.

## Structure

```text
OzuChatApp
├── Backend
│   └── ozu_chat_app_backend
└── Frontend
```

## Backend

Stack: Java 23, Spring Boot 4, Spring Security, Spring WebSocket/STOMP, MongoDB, JWT, Spring Mail, Maven.

Main local database:

```text
mongodb://localhost:27017/ozu_chat_app
```

Collections designed in the backend:

- `users`
- `conversations`
- `messages`
- `refresh_tokens`
- `email_verification_tokens`
- `attachments`
- `notifications`

Copy and edit the environment file:

```powershell
Copy-Item Backend\ozu_chat_app_backend\.env.example Backend\ozu_chat_app_backend\.env
```

Run backend:

```powershell
cd Backend\ozu_chat_app_backend
.\mvnw.cmd spring-boot:run
```

Run backend tests:

```powershell
cd Backend\ozu_chat_app_backend
.\mvnw.cmd test
```

## Frontend

Stack: React 19, Vite, TypeScript strict, MUI, TanStack Router, TanStack Query, React Hook Form, Zod, Axios, STOMP client, i18next, Dayjs, Framer Motion, Emoji Picker, React Dropzone, Zustand, React Virtuoso.

Copy and edit the environment file:

```powershell
Copy-Item Frontend\.env.example Frontend\.env
```

Run frontend:

```powershell
cd Frontend
npm install
npm run dev
```

Build frontend:

```powershell
cd Frontend
npm run build
```

## Local Auth Flow

1. Register a user.
2. In local development, the register response includes `devVerificationToken` so you can verify without SMTP.
3. Paste that token into the verify email tab.
4. Sign in after verification.

For production, configure SMTP and remove any public exposure of development-only tokens before release.

## Git Flow

Recommended branch flow:

```text
main
└── develop
    ├── feature/auth
    ├── feature/chat-realtime
    ├── feature/media-upload
    └── feature/deploy
```

Use conventional commits:

```text
feat: add jwt auth flow
fix: guard private conversation access
perf: virtualize message list
docs: add deployment guide
```

Target pull requests into `develop`. When the release is stable, merge `develop` into `main` and deploy.

## Deployment Notes

Good first deployment split:

- Frontend: Vercel, Netlify, or Cloudflare Pages.
- Backend: Render, Railway, Fly.io, or a VPS.
- Database: MongoDB Atlas for public internet deployment, or a secured self-hosted MongoDB.
- Files: local disk is fine for development; use S3-compatible storage for production media.

Set production environment variables for JWT secrets, MongoDB URI, CORS origins, SMTP, backend URL, and frontend URL.

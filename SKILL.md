# SKILL.md

# OZU CHAT APP

## Overview

Đây là project Chat Application Fullstack.

Mục tiêu của project là xây dựng một ứng dụng chat hiện đại tương tự Messenger / Discord / Telegram với UI đẹp, realtime nhanh, code sạch, dễ mở rộng và tối ưu hiệu năng.

Project gồm hai phần:

```
OzuChatApp
│
├── Backend
└── Frontend
```

---

# Tech Stack

## Frontend

* React 19
* Vite
* TypeScript
* Material UI (MUI)
* TanStack Router
* TanStack Query
* React Hook Form
* Zod
* Axios
* Socket.IO Client
* React i18next
* Dayjs
* Framer Motion
* Emoji Picker
* React Dropzone
* Zustand
* React Virtuoso (Virtual List)
* React Lazy
* React Suspense

---

## Backend

* Java 23
* Spring Boot 4
* Spring Security
* Spring WebSocket
* Socket.IO Java Server (hoặc STOMP nếu phù hợp)
* JWT Authentication
* Spring Validation
* Spring Mail
* MongoDB
* Lombok
* MapStruct
* Maven

---

## Database

MongoDB Local

```
mongodb://localhost:27017/ozu_chat_app
```

AI phải tự thiết kế Database theo nghiệp vụ.

Không hardcode schema.

Thiết kế có khả năng mở rộng.

---

# Authentication

Hệ thống phải có:

* Register
* Login
* Logout
* Refresh Token
* JWT Access Token
* JWT Refresh Token

Refresh token lưu database.

Password hash bằng BCrypt.

---

# Authorization

Phân quyền theo Role.

Ví dụ:

```
ROLE_USER

ROLE_ADMIN
```

API phải kiểm tra JWT.

Không cho phép truy cập nếu chưa xác thực.

---

# Email Verification

Khi đăng ký:

* gửi email
* verify account

User chưa verify:

* không được login

Token verify có thời hạn.

---

# User Profile

Mỗi user có:

* avatar
* username
* email
* password
* displayName
* bio
* language
* theme
* online status
* last seen
* createdAt
* updatedAt

Cho phép:

* update profile
* upload avatar
* remove avatar

---

# Chat Features

Ứng dụng phải hỗ trợ:

## Private Chat

1-1

---

## Group Chat

Có nhiều thành viên.

Admin group có thể:

* đổi tên
* đổi avatar
* thêm thành viên
* xóa thành viên

---

## Message Types

Hỗ trợ:

* text
* image
* video
* gif
* emoji
* file
* audio (future)
* sticker (future)

---

## Realtime

Realtime bằng WebSocket / Socket.IO.

Các sự kiện:

* user online
* user offline
* typing
* stop typing
* seen
* delivered
* new message
* edit message
* delete message
* recall message

---

# Message

Message phải hỗ trợ:

* edit
* delete
* recall
* reply
* forward (future)
* pin (future)
* reaction emoji

---

# Upload

Cho phép upload:

* image
* video
* pdf
* doc
* docx
* excel
* zip

Backend lưu metadata.

Không lưu Base64.

---

# Avatar

Cho phép:

* upload
* crop
* preview
* remove

---

# UI

UI theo phong cách:

Modern

Minimal

Creative

Glassmorphism

Rounded

Smooth Animation

Responsive

---

# Theme

Có:

Light

Dark

User có thể custom:

Primary Color

Secondary Color

Accent Color

Font Size

Border Radius

---

# Localization

Đa ngôn ngữ:

* English
* Vietnamese

Sử dụng react-i18next.

---

# Date Time

Toàn bộ datetime:

Hiển thị theo Local Time của client.

Backend lưu UTC.

Frontend convert.

Sử dụng Dayjs.

---

# Performance

Bắt buộc tối ưu.

Áp dụng:

* Virtual List
* Lazy Loading
* Code Splitting
* Image Lazy Loading
* Memoization
* React.memo
* useMemo
* useCallback
* Infinite Scroll
* Debounce Search
* Throttle Typing Event

Không render dư thừa.

---

# Notification

Có:

* unread count
* browser notification
* sound notification
* realtime notification

---

# Search

Cho phép:

* search user
* search conversation
* search message

---

# Security

Bắt buộc:

JWT

BCrypt

Input Validation

Rate Limit

CORS

CSRF nếu cần

XSS Protection

SQL Injection không áp dụng Mongo nhưng phải sanitize dữ liệu.

---

# Folder Structure

## Root OzuChatApp

```

Backend

Frontend

SKILL.md
```

---

# MongoDB Collections

AI phải tự thiết kế.

Ít nhất:

Users

Conversations

Messages

RefreshTokens

EmailVerificationTokens

Attachments

Notifications

---

# Coding Rules

TypeScript:

strict = true

Không dùng any.

Ưu tiên interface.

---

Java

Không dùng field injection.

Chỉ constructor injection.

DTO riêng.

Entity riêng.

Không expose Entity ra API.

---

API

RESTful.

Response chuẩn:

```
success

message

data

timestamp
```

---

# Frontend Rules

Không gọi API trực tiếp trong Component.

API nằm trong services.

Query nằm trong hooks.

State global dùng Zustand.

Form dùng React Hook Form.

Validation dùng Zod.

---

# Backend Rules

Controller

↓

Service

↓

Repository

Không bỏ qua tầng.

---

# Socket Events

Ví dụ:

```
connect

disconnect

join-room

leave-room

send-message

receive-message

typing

stop-typing

seen

online

offline
```

---

# UI Components

Reusable.

Ví dụ:

Button

Modal

Dialog

Avatar

ChatBubble

Sidebar

Header

MessageInput

EmojiPicker

ThemeSwitcher

LanguageSwitcher

UploadButton

VideoPlayer

ImageViewer

FileCard

Loading

Skeleton

---

# Design Principles

SOLID

KISS

DRY

Clean Code

Reusable Components

Feature First

Scalable

Maintainable

Readable

---

# Git Commit

Conventional Commits.

Ví dụ:

```
feat:

fix:

refactor:

style:

docs:

test:

perf:

build:
```

---

# AI Coding Requirements

Khi sinh code:

* luôn sinh code hoàn chỉnh
* không pseudo code
* không TODO
* không bỏ trống function
* code chạy được
* giải thích ngắn gọn nếu cần
* luôn ưu tiên hiệu năng
* luôn ưu tiên khả năng mở rộng
* luôn tuân thủ cấu trúc thư mục
* luôn giữ coding style thống nhất
* luôn tái sử dụng component
* không tạo code trùng lặp
* luôn kiểm tra type
* luôn xử lý exception
* luôn validate dữ liệu đầu vào

Nếu phải lựa chọn giữa code ngắn và code dễ bảo trì, hãy ưu tiên code dễ bảo trì.

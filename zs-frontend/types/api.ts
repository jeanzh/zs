// --- Auth ---
export interface RegisterRequest {
  nickname: string
  phone: string
  password: string
}

export interface LoginRequest {
  phone: string
  password: string
}

export interface AuthResponse {
  token: string
  userId: number
  nickname: string
}

// --- Paged ---
export interface PagedResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}

// --- Review ---
export interface ReviewItemResponse {
  id: number
  bookId: number
  rating: number
  content: string
  likes: number
  sourceUrl: string | null
  bookTitle: string
  bookAuthor: string
  createdAt: string
  userId: number
  userNickname: string
  userAvatarUrl: string | null
}

// --- Feed ---
export interface FeedItemResponse {
  feedId: number
  bookId: number
  bookTitle: string
  bookAuthor: string
  bookCoverUrl: string | null
  chapterId: number | null
  chapterTitle: string | null
  createdAt: string
}

// --- User ---
export interface UserInfoResponse {
  id: number
  nickname: string
  avatarUrl: string | null
  phone: string
  status: string
  createdAt: string
}

// --- Book Submit ---
export interface BookSubmitRequest {
  title: string
  author: string
  tags?: string[]
  summary?: string
  worldBackground?: string
  highlights?: string
  rating: number
  sourceUrl?: string
  bookUrl?: string
  latestUpdateUrl?: string
  reviewContent: string
}

export interface BookSubmitResponse {
  bookId: number
  title: string
  author: string
  tags: string[]
  rating: number
  reviewCount: number
  avgRating: number
  bookUrl: string | null
  latestUpdateUrl: string | null
  latestChapterTitle: string | null
  newlyCreated: boolean
  reviewId: number
}

// --- Admin ---
export interface AddChapterRequest {
  seq: number
  title: string
  sourceUrl?: string
  wordCount?: number
}

// --- Profile ---
export interface UpdateProfileRequest {
  nickname?: string
  avatarUrl?: string
}

export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
}

export interface UpdateReviewRequest {
  rating?: number
  content: string
}

// --- Book Library ---
export interface BookListItemResponse {
  id: number
  title: string
  author: string
  coverUrl: string | null
  tags: string[]
  summary: string | null
  rating: number
  reviewCount: number
  avgRating: number
  serialStatus: string
  totalWords: number | null
  latestChapterTitle: string | null
  bookUrl: string | null
  latestUpdateUrl: string | null
  createdAt: string
}

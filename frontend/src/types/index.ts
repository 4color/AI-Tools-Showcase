export interface User {
  id: number
  username: string
  email: string
  avatar?: string
  role: 'user' | 'admin'
  /** 组织/部门标识（后台管理） */
  officeId?: string
  createdAt: string
}

export interface Tool {
  id: number
  name: string
  description: string
  introduction?: string
  category: string
  tags: string[]
  url: string
  logoUrl?: string
  price: 'free' | 'freemium' | 'paid'
  rating: number
  pinned?: number
  reviewCount: number
  viewCount?: number
  features: string[]
  createdAt: string
  updatedAt: string
}

export interface Tutorial {
  id: number
  title: string
  description: string
  content: string
  /** 关联的工具ID，可选 */
  toolId?: number
  category?: string
  coverImage?: string
  difficulty: 'beginner' | 'intermediate' | 'advanced'
  duration?: string
  tags: string[]
  viewCount?: number
  rating?: number
  author?: {
    id: number
    username: string
    email: string
    avatar?: string
    role: 'user' | 'admin'
    createdAt: string
  }
  readTime?: number
  createdAt: string
  updatedAt?: string
}

export interface ApiInfo {
  id: number
  name: string
  provider: string
  description: string
  documentation: string
  pricing: 'free' | 'freemium' | 'paid'
  features: string[]
  category: string
  categoryId?: number
  logo?: string
  // API 详情页新增字段
  baseUrl?: string
  apiKeyUrl?: string
  models?: ApiModel[]
  endpoints?: ApiEndpoint[]
  pricingTable?: PricingPlan[]
  createdAt: string
  updatedAt?: string
}

export interface ApiModel {
  id: number
  apiId?: number
  name: string
  description: string
  contextLength: number
  capabilities: string[]
  recommended?: boolean
  /** 是否为图像生成模型（用于 Banner 图生成） */
  imageModel?: boolean
}

export interface ApiEndpoint {
  method: 'GET' | 'POST' | 'PUT' | 'DELETE'
  path: string
  description: string
  parameters?: ApiParam[]
  example?: {
    request: string
    response: string
  }
}

export interface ApiParam {
  name: string
  type: string
  required: boolean
  description: string
  default?: string
}

export interface PricingPlan {
  name: string
  price: number
  interval: 'month' | 'request' | 'token'
  features: string[]
  limits?: {
    requests?: number
    tokens?: number
  }
}

export interface Review {
  id: number
  user: User
  rating: number
  content: string
  createdAt: string
}

export interface SearchFilters {
  category?: string
  price?: string
  rating?: number
  tags?: string[]
}

export interface Prompt {
  id: number
  title: string
  content: string
  category: 'text' | 'image' | 'video' | 'code'
  creator: {
    id: number
    username: string
    avatar?: string
  }
  thumbnail?: string
  /** Banner/封面图虚拟路径（列表页头图） */
  coverImage?: string
  /** 使用方法说明（支持 Markdown） */
  usageGuide?: string
  /** 生成效果图虚拟路径（以提示词为 AI 生成来源） */
  effectImage?: string
  likes: number
  views: number
  commentCount: number
  tags: string[]
  description: string
  examples?: string[]
  createdAt: string
  updatedAt?: string
}

export interface PageResponse<T> {
  content: T[];
  total: number;
  page: number;
  size: number;
}

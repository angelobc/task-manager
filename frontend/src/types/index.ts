export interface User {
  id: string
  email: string
  fullName: string
  avatarUrl: string | null
  createdAt: string
}

export interface Project {
  id: string
  name: string
  description: string | null
  color: string | null
  archived: boolean
  ownerId: string
  createdAt: string
  updatedAt: string
}

export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE'
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH'

export interface Task {
  id: string
  projectId: string
  title: string
  description: string | null
  status: TaskStatus
  priority: TaskPriority
  dueDate: string | null
  position: number
  assigneeId: string | null
  createdById: string
  createdAt: string
  updatedAt: string
}

export interface AuthResponse {
  user: User
  accessToken: string
  refreshToken: string
}

export interface ApiResponse<T> {
  success: boolean
  message: string | null
  data: T
}

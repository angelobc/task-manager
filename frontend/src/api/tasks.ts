import { apiClient } from './client'
import { ApiResponse, Task, TaskPriority, TaskStatus } from '@/types'

export type CreateTaskPayload = {
  title: string
  description?: string
  status?: TaskStatus
  priority?: TaskPriority
  dueDate?: string
}

export type UpdateTaskPayload = {
  title: string
  description?: string
  status: TaskStatus
  priority: TaskPriority
  dueDate?: string
}

export type TaskFilters = {
  status?: TaskStatus
  priority?: TaskPriority
  search?: string
}

export const tasksApi = {
  getAll: (projectId: string, params?: TaskFilters) =>
    apiClient.get<ApiResponse<Task[]>>(`/projects/${projectId}/tasks`, { params }),

  create: (projectId: string, data: CreateTaskPayload) =>
    apiClient.post<ApiResponse<Task>>(`/projects/${projectId}/tasks`, data),

  update: (projectId: string, taskId: string, data: UpdateTaskPayload) =>
    apiClient.put<ApiResponse<Task>>(`/projects/${projectId}/tasks/${taskId}`, data),

  delete: (projectId: string, taskId: string) =>
    apiClient.delete(`/projects/${projectId}/tasks/${taskId}`),

  updateStatus: (projectId: string, taskId: string, status: TaskStatus) =>
    apiClient.patch<ApiResponse<Task>>(`/projects/${projectId}/tasks/${taskId}/status`, { status }),
}

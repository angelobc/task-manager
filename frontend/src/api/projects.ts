import { apiClient } from './client'
import { ApiResponse, Project } from '@/types'

export type ProjectPayload = {
  name: string
  description?: string
  color?: string
}

export const projectsApi = {
  getAll: () =>
    apiClient.get<ApiResponse<Project[]>>('/projects'),

  getById: (id: string) =>
    apiClient.get<ApiResponse<Project>>(`/projects/${id}`),

  create: (data: ProjectPayload) =>
    apiClient.post<ApiResponse<Project>>('/projects', data),

  update: (id: string, data: ProjectPayload) =>
    apiClient.put<ApiResponse<Project>>(`/projects/${id}`, data),

  delete: (id: string) =>
    apiClient.delete(`/projects/${id}`),

  toggleArchive: (id: string) =>
    apiClient.patch<ApiResponse<Project>>(`/projects/${id}/archive`),
}

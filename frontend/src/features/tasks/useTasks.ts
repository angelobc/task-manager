import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { tasksApi, CreateTaskPayload, UpdateTaskPayload, TaskFilters } from '@/api/tasks'
import { TaskStatus } from '@/types'

export const useTasks = (projectId: string, filters?: TaskFilters) =>
  useQuery({
    queryKey: ['tasks', projectId, filters],
    queryFn: () => tasksApi.getAll(projectId, filters).then((r) => r.data.data),
    enabled: !!projectId,
  })

export const useCreateTask = (projectId: string) => {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: CreateTaskPayload) => tasksApi.create(projectId, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['tasks', projectId] }),
  })
}

export const useUpdateTask = (projectId: string) => {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ taskId, data }: { taskId: string; data: UpdateTaskPayload }) =>
      tasksApi.update(projectId, taskId, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['tasks', projectId] }),
  })
}

export const useDeleteTask = (projectId: string) => {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (taskId: string) => tasksApi.delete(projectId, taskId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['tasks', projectId] }),
  })
}

export const useUpdateTaskStatus = (projectId: string) => {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ taskId, status }: { taskId: string; status: TaskStatus }) =>
      tasksApi.updateStatus(projectId, taskId, status),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['tasks', projectId] }),
  })
}

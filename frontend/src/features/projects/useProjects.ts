import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { projectsApi, ProjectPayload } from '@/api/projects'

export const useProjects = () =>
  useQuery({
    queryKey: ['projects'],
    queryFn: () => projectsApi.getAll().then((r) => r.data.data),
  })

export const useProject = (id: string) =>
  useQuery({
    queryKey: ['projects', id],
    queryFn: () => projectsApi.getById(id).then((r) => r.data.data),
    enabled: !!id,
  })

export const useCreateProject = () => {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: ProjectPayload) => projectsApi.create(data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['projects'] }),
  })
}

export const useUpdateProject = () => {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: ProjectPayload }) =>
      projectsApi.update(id, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['projects'] }),
  })
}

export const useDeleteProject = () => {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: string) => projectsApi.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['projects'] }),
  })
}

export const useToggleArchive = () => {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: string) => projectsApi.toggleArchive(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['projects'] }),
  })
}

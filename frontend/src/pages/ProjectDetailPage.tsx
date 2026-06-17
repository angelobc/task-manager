import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, Plus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { useProject } from '@/features/projects/useProjects'
import TaskList from '@/features/tasks/TaskList'
import TaskFilters from '@/features/tasks/TaskFilters'
import TaskModal from '@/features/tasks/TaskModal'
import { TaskFilters as Filters } from '@/api/tasks'

export default function ProjectDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { data: project, isLoading } = useProject(id!)
  const [isOpen, setIsOpen] = useState(false)
  const [filters, setFilters] = useState<Filters>({})

  if (isLoading) return <p className="text-muted-foreground">Cargando...</p>
  if (!project) return <p>Proyecto no encontrado</p>

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center gap-3">
          <Button variant="ghost" size="icon" onClick={() => navigate('/projects')}>
            <ArrowLeft size={18} />
          </Button>
          <div
            className="w-4 h-4 rounded-full shrink-0"
            style={{ backgroundColor: project.color || '#6366f1' }}
          />
          <h1 className="text-2xl font-bold">{project.name}</h1>
        </div>
        <Button onClick={() => setIsOpen(true)} className="gap-2">
          <Plus size={16} />
          Nueva tarea
        </Button>
      </div>

      <TaskFilters filters={filters} onChange={setFilters} />
      <TaskList projectId={id!} filters={filters} />
      <TaskModal projectId={id!} open={isOpen} onClose={() => setIsOpen(false)} />
    </div>
  )
}

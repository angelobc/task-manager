import { ClipboardList } from 'lucide-react'
import { useTasks } from './useTasks'
import TaskCard from './TaskCard'
import { TaskFilters } from '@/api/tasks'

interface Props {
  projectId: string
  filters?: TaskFilters
}

export default function TaskList({ projectId, filters }: Props) {
  const { data: tasks, isLoading } = useTasks(projectId, filters)

  if (isLoading) {
    return <p className="text-muted-foreground py-4">Cargando tareas...</p>
  }

  if (!tasks?.length) {
    return (
      <div className="flex flex-col items-center justify-center h-48 text-center mt-4">
        <ClipboardList size={40} className="text-muted-foreground mb-3" />
        <p className="text-muted-foreground font-medium">No hay tareas</p>
        <p className="text-sm text-muted-foreground">Crea la primera tarea del proyecto</p>
      </div>
    )
  }

  return (
    <div className="space-y-2 mt-4">
      {tasks.map((task) => (
        <TaskCard key={task.id} task={task} projectId={projectId} />
      ))}
    </div>
  )
}

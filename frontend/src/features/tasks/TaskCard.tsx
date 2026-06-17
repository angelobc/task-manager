import { useState } from 'react'
import { Calendar, MoreVertical, Pencil, Trash2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { useDeleteTask, useUpdateTaskStatus } from './useTasks'
import TaskModal from './TaskModal'
import { Task, TaskStatus } from '@/types'
import { cn } from '@/lib/utils'

const STATUS_LABELS: Record<TaskStatus, string> = {
  TODO: 'Por hacer',
  IN_PROGRESS: 'En progreso',
  DONE: 'Hecho',
}

const STATUS_NEXT: Record<TaskStatus, TaskStatus> = {
  TODO: 'IN_PROGRESS',
  IN_PROGRESS: 'DONE',
  DONE: 'TODO',
}

const STATUS_CLASSES: Record<TaskStatus, string> = {
  TODO: 'bg-slate-100 text-slate-700 hover:bg-slate-200',
  IN_PROGRESS: 'bg-blue-100 text-blue-700 hover:bg-blue-200',
  DONE: 'bg-green-100 text-green-700 hover:bg-green-200',
}

const PRIORITY_CLASSES: Record<string, string> = {
  LOW: 'bg-green-100 text-green-700',
  MEDIUM: 'bg-amber-100 text-amber-700',
  HIGH: 'bg-red-100 text-red-700',
}

const PRIORITY_LABELS: Record<string, string> = {
  LOW: 'Baja',
  MEDIUM: 'Media',
  HIGH: 'Alta',
}

export default function TaskCard({ task, projectId }: { task: Task; projectId: string }) {
  const [isEditOpen, setIsEditOpen] = useState(false)
  const { mutate: updateStatus } = useUpdateTaskStatus(projectId)
  const { mutate: deleteTask } = useDeleteTask(projectId)

  return (
    <>
      <div className="flex items-center gap-3 p-3 rounded-lg border bg-card hover:bg-accent/30 transition-colors">
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 flex-wrap">
            <span className="font-medium truncate">{task.title}</span>
            <Badge className={cn('text-xs shrink-0 border-0', PRIORITY_CLASSES[task.priority])}>
              {PRIORITY_LABELS[task.priority]}
            </Badge>
          </div>
          {task.dueDate && (
            <div className="flex items-center gap-1 mt-1 text-xs text-muted-foreground">
              <Calendar size={11} />
              <span>Vence: {new Date(task.dueDate + 'T12:00:00').toLocaleDateString('es-PE')}</span>
            </div>
          )}
        </div>

        <button
          onClick={() => updateStatus({ taskId: task.id, status: STATUS_NEXT[task.status] })}
          className={cn(
            'text-xs px-2 py-1 rounded-full font-medium transition-colors shrink-0',
            STATUS_CLASSES[task.status],
          )}
        >
          {STATUS_LABELS[task.status]}
        </button>

        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="icon" className="h-7 w-7 shrink-0">
              <MoreVertical size={14} />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuItem onClick={() => setIsEditOpen(true)} className="gap-2">
              <Pencil size={14} /> Editar
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem
              onClick={() => deleteTask(task.id)}
              className="gap-2 text-destructive focus:text-destructive"
            >
              <Trash2 size={14} /> Eliminar
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      <TaskModal projectId={projectId} task={task} open={isEditOpen} onClose={() => setIsEditOpen(false)} />
    </>
  )
}

import { Search, X } from 'lucide-react'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { TaskFilters as Filters } from '@/api/tasks'
import { TaskPriority, TaskStatus } from '@/types'

interface Props {
  filters: Filters
  onChange: (filters: Filters) => void
}

export default function TaskFilters({ filters, onChange }: Props) {
  const hasFilters = filters.status || filters.priority || filters.search

  return (
    <div className="flex gap-2 flex-wrap items-center">
      <div className="relative flex-1 min-w-48">
        <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <Input
          placeholder="Buscar tareas..."
          className="pl-8"
          value={filters.search || ''}
          onChange={(e) => onChange({ ...filters, search: e.target.value || undefined })}
        />
      </div>

      <Select
        value={filters.status || 'ALL'}
        onValueChange={(v) =>
          onChange({ ...filters, status: v === 'ALL' ? undefined : (v as TaskStatus) })
        }
      >
        <SelectTrigger className="w-40">
          <SelectValue placeholder="Estado" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="ALL">Todos los estados</SelectItem>
          <SelectItem value="TODO">Por hacer</SelectItem>
          <SelectItem value="IN_PROGRESS">En progreso</SelectItem>
          <SelectItem value="DONE">Hecho</SelectItem>
        </SelectContent>
      </Select>

      <Select
        value={filters.priority || 'ALL'}
        onValueChange={(v) =>
          onChange({ ...filters, priority: v === 'ALL' ? undefined : (v as TaskPriority) })
        }
      >
        <SelectTrigger className="w-40">
          <SelectValue placeholder="Prioridad" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="ALL">Todas las prioridades</SelectItem>
          <SelectItem value="LOW">Baja</SelectItem>
          <SelectItem value="MEDIUM">Media</SelectItem>
          <SelectItem value="HIGH">Alta</SelectItem>
        </SelectContent>
      </Select>

      {hasFilters && (
        <Button variant="outline" size="sm" onClick={() => onChange({})} className="gap-1">
          <X size={13} /> Limpiar
        </Button>
      )}
    </div>
  )
}

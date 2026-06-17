import { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { useCreateProject, useUpdateProject } from './useProjects'
import { Project } from '@/types'
import { cn } from '@/lib/utils'

const PRESET_COLORS = [
  '#6366f1', '#3b82f6', '#22c55e', '#f59e0b',
  '#ef4444', '#ec4899', '#8b5cf6', '#14b8a6',
]

const schema = z.object({
  name: z.string().min(1, 'El nombre es requerido').max(150),
  description: z.string().optional(),
  color: z.string().optional(),
})

type FormData = z.infer<typeof schema>

interface Props {
  open: boolean
  onClose: () => void
  project?: Project
}

export default function ProjectModal({ open, onClose, project }: Props) {
  const isEdit = !!project
  const { mutate: create, isPending: isCreating } = useCreateProject()
  const { mutate: update, isPending: isUpdating } = useUpdateProject()

  const { register, handleSubmit, setValue, watch, reset, formState: { errors } } =
    useForm<FormData>({
      resolver: zodResolver(schema),
      defaultValues: { color: PRESET_COLORS[0] },
    })

  const selectedColor = watch('color')

  useEffect(() => {
    if (open) {
      reset(
        project
          ? { name: project.name, description: project.description ?? '', color: project.color ?? PRESET_COLORS[0] }
          : { name: '', description: '', color: PRESET_COLORS[0] },
      )
    }
  }, [open, project, reset])

  const onSubmit = (data: FormData) => {
    if (isEdit) {
      update({ id: project.id, data }, { onSuccess: onClose })
    } else {
      create(data, { onSuccess: onClose })
    }
  }

  return (
    <Dialog open={open} onOpenChange={(o) => !o && onClose()}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{isEdit ? 'Editar proyecto' : 'Nuevo proyecto'}</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="space-y-1">
            <Label htmlFor="name">Nombre</Label>
            <Input id="name" placeholder="Nombre del proyecto" {...register('name')} />
            {errors.name && <p className="text-sm text-destructive">{errors.name.message}</p>}
          </div>
          <div className="space-y-1">
            <Label htmlFor="description">Descripción</Label>
            <Textarea id="description" placeholder="Descripción opcional" rows={3} {...register('description')} />
          </div>
          <div className="space-y-2">
            <Label>Color</Label>
            <div className="flex gap-2 flex-wrap">
              {PRESET_COLORS.map((color) => (
                <button
                  key={color}
                  type="button"
                  className={cn(
                    'w-7 h-7 rounded-full ring-offset-2 transition-all',
                    selectedColor === color && 'ring-2 ring-primary',
                  )}
                  style={{ backgroundColor: color }}
                  onClick={() => setValue('color', color)}
                />
              ))}
            </div>
          </div>
          <div className="flex justify-end gap-2 pt-2">
            <Button type="button" variant="outline" onClick={onClose}>Cancelar</Button>
            <Button type="submit" disabled={isCreating || isUpdating}>
              {isCreating || isUpdating ? 'Guardando...' : isEdit ? 'Actualizar' : 'Crear'}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  )
}

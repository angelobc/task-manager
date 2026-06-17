import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Archive, MoreVertical, Pencil, Trash2 } from 'lucide-react'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { useDeleteProject, useToggleArchive } from './useProjects'
import ProjectModal from './ProjectModal'
import { Project } from '@/types'

export default function ProjectCard({ project }: { project: Project }) {
  const navigate = useNavigate()
  const [isEditOpen, setIsEditOpen] = useState(false)
  const { mutate: deleteProject } = useDeleteProject()
  const { mutate: toggleArchive } = useToggleArchive()

  return (
    <>
      <Card
        className="cursor-pointer hover:shadow-md transition-shadow"
        onClick={() => navigate(`/projects/${project.id}`)}
      >
        <CardContent className="p-4">
          <div className="flex items-start justify-between gap-2">
            <div className="flex items-center gap-2 min-w-0">
              <div
                className="w-3 h-3 rounded-full shrink-0"
                style={{ backgroundColor: project.color || '#6366f1' }}
              />
              <h3 className="font-semibold truncate">{project.name}</h3>
            </div>
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-7 w-7 shrink-0"
                  onClick={(e) => e.stopPropagation()}
                >
                  <MoreVertical size={14} />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" onClick={(e) => e.stopPropagation()}>
                <DropdownMenuItem onClick={() => setIsEditOpen(true)} className="gap-2">
                  <Pencil size={14} /> Editar
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => toggleArchive(project.id)} className="gap-2">
                  <Archive size={14} />
                  {project.archived ? 'Desarchivar' : 'Archivar'}
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem
                  onClick={() => deleteProject(project.id)}
                  className="gap-2 text-destructive focus:text-destructive"
                >
                  <Trash2 size={14} /> Eliminar
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>

          {project.description && (
            <p className="text-sm text-muted-foreground mt-2 line-clamp-2">{project.description}</p>
          )}

          {project.archived && (
            <Badge variant="secondary" className="mt-3">Archivado</Badge>
          )}
        </CardContent>
      </Card>

      <ProjectModal open={isEditOpen} onClose={() => setIsEditOpen(false)} project={project} />
    </>
  )
}

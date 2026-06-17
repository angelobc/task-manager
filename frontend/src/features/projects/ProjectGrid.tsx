import { Folder } from 'lucide-react'
import { useProjects } from './useProjects'
import ProjectCard from './ProjectCard'

export default function ProjectGrid() {
  const { data: projects, isLoading } = useProjects()

  if (isLoading) {
    return <p className="text-muted-foreground">Cargando proyectos...</p>
  }

  if (!projects?.length) {
    return (
      <div className="flex flex-col items-center justify-center h-64 text-center">
        <Folder size={48} className="text-muted-foreground mb-4" />
        <p className="text-muted-foreground font-medium">No tienes proyectos aún</p>
        <p className="text-sm text-muted-foreground">Crea tu primer proyecto para empezar</p>
      </div>
    )
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
      {projects.map((project) => (
        <ProjectCard key={project.id} project={project} />
      ))}
    </div>
  )
}

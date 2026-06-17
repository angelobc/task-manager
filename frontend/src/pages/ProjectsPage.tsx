import { useState } from 'react'
import { Plus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import ProjectGrid from '@/features/projects/ProjectGrid'
import ProjectModal from '@/features/projects/ProjectModal'

export default function ProjectsPage() {
  const [isOpen, setIsOpen] = useState(false)

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">Mis Proyectos</h1>
        <Button onClick={() => setIsOpen(true)} className="gap-2">
          <Plus size={16} />
          Nuevo proyecto
        </Button>
      </div>
      <ProjectGrid />
      <ProjectModal open={isOpen} onClose={() => setIsOpen(false)} />
    </div>
  )
}

import { NavLink } from 'react-router-dom'
import { LayoutList } from 'lucide-react'
import { cn } from '@/lib/utils'

export default function Sidebar() {
  return (
    <aside className="w-56 border-r bg-card flex flex-col shrink-0">
      <div className="p-4 border-b">
        <h1 className="font-bold text-lg tracking-tight">Task Manager</h1>
      </div>
      <nav className="flex-1 p-2 space-y-1">
        <NavLink
          to="/projects"
          className={({ isActive }) =>
            cn(
              'flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors',
              isActive
                ? 'bg-primary text-primary-foreground'
                : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground',
            )
          }
        >
          <LayoutList size={18} />
          Proyectos
        </NavLink>
      </nav>
    </aside>
  )
}

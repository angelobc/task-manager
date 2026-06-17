import { ReactNode } from 'react'

export default function AuthLayout({ children }: { children: ReactNode }) {
  return (
    <div className="min-h-screen flex items-center justify-center bg-muted/30">
      <div className="w-full max-w-md px-4">
        <div className="mb-8 text-center">
          <h1 className="text-3xl font-bold">Task Manager</h1>
          <p className="text-muted-foreground mt-1">Gestiona tus proyectos y tareas</p>
        </div>
        {children}
      </div>
    </div>
  )
}

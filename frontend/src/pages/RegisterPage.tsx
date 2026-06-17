import AuthLayout from '@/components/shared/AuthLayout'
import RegisterForm from '@/features/auth/RegisterForm'

export default function RegisterPage() {
  return (
    <AuthLayout>
      <RegisterForm />
    </AuthLayout>
  )
}

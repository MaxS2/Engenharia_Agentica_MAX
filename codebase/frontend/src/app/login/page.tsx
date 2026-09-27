'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import Image from 'next/image';
import { useRouter, useSearchParams } from 'next/navigation';
import { Suspense, useState } from 'react';
import { useForm } from 'react-hook-form';
import { AxiosError } from 'axios';
import { LogIn } from 'lucide-react';
import { z } from 'zod';

import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useAuth } from '@/contexts/auth-context';

const loginSchema = z.object({
  username: z.string().min(3, 'Usuário precisa ter ao menos 3 caracteres'),
  password: z.string().min(4, 'Senha precisa ter ao menos 4 caracteres'),
});

type LoginFormValues = z.infer<typeof loginSchema>;

export default function LoginPage() {
  return (
    <Suspense fallback={null}>
      <LoginForm />
    </Suspense>
  );
}

function LoginForm() {
  const router = useRouter();
  const params = useSearchParams();
  const { login } = useAuth();
  const [submitError, setSubmitError] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: { username: '', password: '' },
  });

  const onSubmit = async (values: LoginFormValues) => {
    setSubmitError(null);
    try {
      await login(values.username, values.password);
      const next = params.get('next');
      router.replace(next && next.startsWith('/') ? next : '/');
    } catch (error) {
      if (error instanceof AxiosError && error.response?.status === 401) {
        setSubmitError('Usuário ou senha inválidos.');
      } else {
        setSubmitError('Não foi possível entrar. Tente novamente em instantes.');
      }
    }
  };

  return (
    <main className="flex min-h-screen flex-col items-center justify-center bg-background px-4 py-10">
      <div className="mb-6 flex flex-col items-center gap-3">
        <Image
          src="/logo.png"
          alt="PPT Nao Compila"
          width={96}
          height={96}
          className="rounded-xl shadow-sm"
          priority
        />
        <h1 className="text-2xl font-semibold text-primary">PPTNC Poke Deck</h1>
      </div>

      <Card className="w-full max-w-sm">
        <CardHeader>
          <CardTitle>Entrar</CardTitle>
          <CardDescription>Acesse com seu usuário e senha.</CardDescription>
        </CardHeader>
        <CardContent>
          <form className="space-y-4" onSubmit={handleSubmit(onSubmit)} noValidate>
            <div className="space-y-2">
              <Label htmlFor="username">Usuário</Label>
              <Input
                id="username"
                autoComplete="username"
                aria-invalid={errors.username ? 'true' : 'false'}
                {...register('username')}
              />
              {errors.username && (
                <p className="text-xs text-destructive">{errors.username.message}</p>
              )}
            </div>
            <div className="space-y-2">
              <Label htmlFor="password">Senha</Label>
              <Input
                id="password"
                type="password"
                autoComplete="current-password"
                aria-invalid={errors.password ? 'true' : 'false'}
                {...register('password')}
              />
              {errors.password && (
                <p className="text-xs text-destructive">{errors.password.message}</p>
              )}
            </div>
            {submitError && (
              <p className="rounded-md bg-destructive/10 px-3 py-2 text-sm text-destructive">{submitError}</p>
            )}
            <Button type="submit" className="w-full gap-2" disabled={isSubmitting}>
              <LogIn className="h-4 w-4" /> {isSubmitting ? 'Entrando...' : 'Entrar'}
            </Button>
          </form>
        </CardContent>
      </Card>

      <p className="mt-6 text-[10px] text-muted-foreground">PPT Não Compila © 2026</p>
    </main>
  );
}

'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { AxiosError } from 'axios';
import { Trash2, UserPlus } from 'lucide-react';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { z } from 'zod';

import { AppSidebar } from '@/components/app-sidebar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { useAuth } from '@/contexts/auth-context';
import { useDecks } from '@/hooks/use-decks';
import { createUser, deleteUser, listUsers } from '@/services/users-service';
import type { User } from '@/types/domain';

const createUserSchema = z.object({
  username: z
    .string()
    .min(3, 'Mínimo de 3 caracteres')
    .max(50, 'Máximo de 50 caracteres'),
  password: z
    .string()
    .min(4, 'Mínimo de 4 caracteres')
    .max(100, 'Máximo de 100 caracteres'),
  admin: z.boolean().default(false),
});

type CreateUserFormValues = z.infer<typeof createUserSchema>;

export default function AdminUsersPage() {
  const { user: currentUser } = useAuth();
  const { data: decks = [] } = useDecks();
  const [users, setUsers] = useState<User[]>([]);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [open, setOpen] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<CreateUserFormValues>({
    resolver: zodResolver(createUserSchema),
    defaultValues: { username: '', password: '', admin: false },
  });

  useEffect(() => {
    let alive = true;
    listUsers()
      .then((data) => {
        if (alive) setUsers(data);
      })
      .catch(() => {
        if (alive) setLoadError('Não foi possível carregar a lista de usuários.');
      });
    return () => {
      alive = false;
    };
  }, []);

  const onCreate = async (values: CreateUserFormValues) => {
    setSubmitError(null);
    try {
      const created = await createUser(values);
      setUsers((prev) => [...prev, created]);
      reset();
      setOpen(false);
      toast.success(`Usuário "${created.username}" criado.`);
    } catch (error) {
      if (error instanceof AxiosError && error.response?.status === 409) {
        setSubmitError('Já existe um usuário com esse nome.');
      } else {
        setSubmitError('Falha ao criar o usuário. Tente novamente.');
      }
    }
  };

  const onDelete = async (target: User) => {
    if (!confirm(`Remover o usuário "${target.username}"? Essa ação é irreversível.`)) return;
    try {
      await deleteUser(target.id);
      setUsers((prev) => prev.filter((u) => u.id !== target.id));
      toast.success(`Usuário "${target.username}" removido.`);
    } catch {
      toast.error('Não foi possível remover o usuário.');
    }
  };

  if (!currentUser) return null;

  return (
    <>
      <AppSidebar
        user={currentUser}
        decks={decks}
        selectedDeckId={decks[0]?.id ?? ''}
        onSelectDeck={() => {}}
      />

      <main className="flex-1 overflow-y-auto">
        <header className="flex items-center justify-between border-b bg-white/60 px-6 py-5 backdrop-blur">
          <div>
            <p className="text-xs uppercase tracking-wider text-muted-foreground">Painel do administrador</p>
            <h1 className="text-2xl font-semibold text-primary">Gerenciar usuários</h1>
          </div>
          <Button className="gap-2" onClick={() => setOpen(true)}>
            <UserPlus className="h-4 w-4" /> Novo usuário
          </Button>
        </header>

        <section className="p-6">
          {loadError && (
            <p className="mb-4 rounded-md bg-destructive/10 px-3 py-2 text-sm text-destructive">{loadError}</p>
          )}
          <div className="overflow-hidden rounded-lg border bg-white">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Usuário</TableHead>
                  <TableHead>Permissão</TableHead>
                  <TableHead className="text-right">Ações</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {users.map((user) => (
                  <TableRow key={user.id}>
                    <TableCell className="font-medium">{user.username}</TableCell>
                    <TableCell>
                      {user.admin ? (
                        <Badge variant="secondary">Admin</Badge>
                      ) : (
                        <Badge variant="outline">Usuário</Badge>
                      )}
                    </TableCell>
                    <TableCell className="text-right">
                      <Button
                        size="sm"
                        variant="outline"
                        className="gap-1 border-destructive text-destructive hover:bg-destructive/10 hover:text-destructive"
                        disabled={user.id === currentUser.id}
                        onClick={() => void onDelete(user)}
                      >
                        <Trash2 className="h-3.5 w-3.5" /> Remover
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        </section>
      </main>

      <Dialog
        open={open}
        onOpenChange={(value) => {
          setOpen(value);
          if (!value) {
            reset();
            setSubmitError(null);
          }
        }}
      >
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Adicionar novo usuário</DialogTitle>
          </DialogHeader>
          <form className="space-y-4" onSubmit={handleSubmit(onCreate)} noValidate>
            <div className="space-y-2">
              <Label htmlFor="new-username">Usuário</Label>
              <Input id="new-username" placeholder="ex: ash" {...register('username')} />
              {errors.username && (
                <p className="text-xs text-destructive">{errors.username.message}</p>
              )}
            </div>
            <div className="space-y-2">
              <Label htmlFor="new-password">Senha temporária</Label>
              <Input
                id="new-password"
                type="password"
                placeholder="mínimo 4 caracteres"
                {...register('password')}
              />
              {errors.password && (
                <p className="text-xs text-destructive">{errors.password.message}</p>
              )}
            </div>
            <label className="flex items-center gap-2 text-sm text-muted-foreground">
              <input type="checkbox" {...register('admin')} />
              Também é administrador
            </label>
            {submitError && (
              <p className="rounded-md bg-destructive/10 px-3 py-2 text-sm text-destructive">{submitError}</p>
            )}
            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => {
                  setOpen(false);
                  reset();
                  setSubmitError(null);
                }}
              >
                Cancelar
              </Button>
              <Button type="submit" disabled={isSubmitting}>
                {isSubmitting ? 'Criando...' : 'Criar'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </>
  );
}

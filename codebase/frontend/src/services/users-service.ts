import { api } from './api';

import type { User } from '@/types/domain';

export interface CreateUserPayload {
  username: string;
  password: string;
  admin: boolean;
}

export async function listUsers(): Promise<User[]> {
  const { data } = await api.get<User[]>('/users');
  return data;
}

export async function listRecipients(): Promise<User[]> {
  const { data } = await api.get<User[]>('/users/recipients');
  return data;
}

export async function createUser(payload: CreateUserPayload): Promise<User> {
  const { data } = await api.post<User>('/users', payload);
  return data;
}

export async function deleteUser(id: string): Promise<void> {
  await api.delete(`/users/${id}`);
}

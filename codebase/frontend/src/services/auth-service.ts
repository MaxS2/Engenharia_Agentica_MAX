import { api } from './api';

import type { User } from '@/types/domain';

export interface LoginPayload {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  expiresInMs: number;
  user: User;
}

export async function login(payload: LoginPayload): Promise<AuthResponse> {
  const { data } = await api.post<AuthResponse>('/auth/login', payload);
  return data;
}

export async function logout(): Promise<void> {
  try {
    await api.post('/auth/logout');
  } catch {
    // logout eh idempotente; ignorar 401 apos token expirado
  }
}

export async function fetchMe(): Promise<User> {
  const { data } = await api.get<User>('/auth/me');
  return data;
}

'use client';

import { useQueryClient } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';
import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react';

import { AUTH_EVENT_EXPIRED } from '@/lib/auth-storage';
import {
  fetchMe,
  login as loginRequest,
  logout as logoutRequest,
} from '@/services/auth-service';
import type { User } from '@/types/domain';

interface AuthContextValue {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (username: string, password: string) => Promise<User>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const router = useRouter();
  const queryClient = useQueryClient();
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    let alive = true;
    fetchMe()
      .then((me) => {
        if (alive) setUser(me);
      })
      .catch(() => {
        if (alive) setUser(null);
      })
      .finally(() => {
        if (alive) setIsLoading(false);
      });
    return () => {
      alive = false;
    };
  }, []);

  useEffect(() => {
    function handleExpired() {
      queryClient.clear();
      setUser(null);
      router.replace('/login');
    }
    window.addEventListener(AUTH_EVENT_EXPIRED, handleExpired);
    return () => window.removeEventListener(AUTH_EVENT_EXPIRED, handleExpired);
  }, [router, queryClient]);

  const login = useCallback(
    async (username: string, password: string) => {
      queryClient.clear();
      const response = await loginRequest({ username, password });
      setUser(response.user);
      return response.user;
    },
    [queryClient],
  );

  const logout = useCallback(async () => {
    await logoutRequest();
    queryClient.clear();
    setUser(null);
    router.replace('/login');
  }, [router, queryClient]);

  const value = useMemo<AuthContextValue>(
    () => ({ user, isAuthenticated: user !== null, isLoading, login, logout }),
    [user, isLoading, login, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth precisa estar dentro de <AuthProvider>');
  }
  return ctx;
}

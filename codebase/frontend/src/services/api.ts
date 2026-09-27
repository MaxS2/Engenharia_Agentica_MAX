import axios, { AxiosError } from 'axios';

import { AUTH_EVENT_EXPIRED } from '@/lib/auth-storage';

const baseURL = process.env.NEXT_PUBLIC_API_BASE_URL ?? 'http://localhost:8080/api/v1';

export const api = axios.create({
  baseURL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401 && typeof window !== 'undefined') {
      window.dispatchEvent(new Event(AUTH_EVENT_EXPIRED));
    }
    return Promise.reject(error);
  },
);

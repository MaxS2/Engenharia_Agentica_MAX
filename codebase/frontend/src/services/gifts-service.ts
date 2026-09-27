import { api } from './api';

import type { Gift } from '@/types/domain';

export interface SendGiftPayload {
  receiverId: string;
  pokemonId: number;
  originDeckId: string;
}

export async function listPendingGifts(): Promise<Gift[]> {
  const { data } = await api.get<Gift[]>('/gifts/pending');
  return data;
}

export async function sendGift(payload: SendGiftPayload): Promise<Gift> {
  const { data } = await api.post<Gift>('/gifts', payload);
  return data;
}

export async function acceptGift(giftId: string, deckId: string): Promise<void> {
  await api.patch(`/gifts/${giftId}`, { status: 'ACCEPTED', deckId });
}

export async function rejectGift(giftId: string): Promise<void> {
  await api.patch(`/gifts/${giftId}`, { status: 'REJECTED' });
}

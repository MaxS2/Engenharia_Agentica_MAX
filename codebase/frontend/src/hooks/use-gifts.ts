import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import { decksQueryKey } from '@/hooks/use-decks';
import {
  acceptGift,
  listPendingGifts,
  rejectGift,
  sendGift,
  type SendGiftPayload,
} from '@/services/gifts-service';
import { listRecipients } from '@/services/users-service';

export const pendingGiftsQueryKey = ['gifts', 'pending'] as const;
export const recipientsQueryKey = ['users', 'recipients'] as const;

export function usePendingGifts(enabled = true) {
  return useQuery({
    queryKey: pendingGiftsQueryKey,
    queryFn: listPendingGifts,
    enabled,
    staleTime: 30_000,
  });
}

export function useRecipients(enabled = true) {
  return useQuery({
    queryKey: recipientsQueryKey,
    queryFn: listRecipients,
    enabled,
    staleTime: 60_000,
  });
}

export function useSendGift() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: SendGiftPayload) => sendGift(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: decksQueryKey });
      queryClient.invalidateQueries({ queryKey: ['decks'] });
      queryClient.invalidateQueries({ queryKey: pendingGiftsQueryKey });
    },
  });
}

export function useAcceptGift() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: { giftId: string; deckId: string }) => acceptGift(payload.giftId, payload.deckId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: pendingGiftsQueryKey });
      queryClient.invalidateQueries({ queryKey: decksQueryKey });
      queryClient.invalidateQueries({ queryKey: ['decks'] });
    },
  });
}

export function useRejectGift() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (giftId: string) => rejectGift(giftId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: pendingGiftsQueryKey });
      queryClient.invalidateQueries({ queryKey: decksQueryKey });
      queryClient.invalidateQueries({ queryKey: ['decks'] });
    },
  });
}

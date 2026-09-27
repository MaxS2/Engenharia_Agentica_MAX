/**
 * Tipos de dominio espelhando os DTOs do backend (ver codebase/backend/src/main/java/com/pptnc/pokedeck/dto).
 * Em Sprints reais (4+) serao populados via fetch; aqui sao consumidos de mock data estatica.
 */

export interface User {
  id: string;
  username: string;
  admin: boolean;
}

export interface Deck {
  id: string;
  name: string;
  pokemonCount: number;
}

export interface PokemonSummary {
  id: number;
  name: string;
  imageUrl: string;
  types: string[];
  inSelectedDeck: boolean;
}

export interface PokemonStat {
  label: string;
  baseValue: number;
}

export interface PokemonDetail {
  id: number;
  name: string;
  imageUrl: string;
  types: string[];
  abilities: string[];
  height: number;
  weight: number;
  baseExperience: number;
  stats: PokemonStat[];
  inSelectedDeck: boolean;
}

export type GiftStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';

export interface Gift {
  id: string;
  sender: User;
  receiver: User;
  pokemonId: number;
  pokemonName: string;
  pokemonImageUrl: string;
  originDeckId: string;
  status: GiftStatus;
  createdAt: string;
  resolvedAt: string | null;
}

package com.pptnc.pokedeck.service;

import java.util.Locale;
import java.util.Map;

/**
 * Tradução dos termos da PokeAPI (inglês) para os rótulos PT-BR usados no frontend
 * (ver codebase/frontend/src/lib/pokemon-types.ts). Mantido sem acentos para alinhar
 * com os tokens ja presentes no código.
 */
final class PokemonTranslations {

    private static final Map<String, String> TYPES = Map.ofEntries(
        Map.entry("normal", "Normal"),
        Map.entry("fire", "Fogo"),
        Map.entry("water", "Agua"),
        Map.entry("electric", "Eletrico"),
        Map.entry("grass", "Grama"),
        Map.entry("ice", "Gelo"),
        Map.entry("fighting", "Lutador"),
        Map.entry("poison", "Venenoso"),
        Map.entry("ground", "Terrestre"),
        Map.entry("flying", "Voador"),
        Map.entry("psychic", "Psiquico"),
        Map.entry("bug", "Inseto"),
        Map.entry("rock", "Pedra"),
        Map.entry("ghost", "Fantasma"),
        Map.entry("dragon", "Dragao"),
        Map.entry("dark", "Sombrio"),
        Map.entry("steel", "Metalico"),
        Map.entry("fairy", "Fada")
    );

    private static final Map<String, String> STATS = Map.of(
        "hp", "HP",
        "attack", "Ataque",
        "defense", "Defesa",
        "special-attack", "Ataque Especial",
        "special-defense", "Defesa Especial",
        "speed", "Velocidade"
    );

    private PokemonTranslations() {
    }

    static String typeLabel(String englishName) {
        if (englishName == null) return "Normal";
        return TYPES.getOrDefault(englishName.toLowerCase(Locale.ROOT), capitalize(englishName));
    }

    static String statLabel(String englishName) {
        if (englishName == null) return "";
        return STATS.getOrDefault(englishName.toLowerCase(Locale.ROOT), capitalize(englishName));
    }

    static String capitalize(String raw) {
        if (raw == null || raw.isBlank()) return raw;
        String replaced = raw.replace('-', ' ').replace('_', ' ');
        return Character.toUpperCase(replaced.charAt(0)) + replaced.substring(1);
    }
}

package com.pptnc.pokedeck.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pptnc.pokedeck.dto.PageResponse;
import com.pptnc.pokedeck.dto.PokemonDetailDTO;
import com.pptnc.pokedeck.dto.PokemonStatDTO;
import com.pptnc.pokedeck.dto.PokemonSummaryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Facade para a PokeAPI (https://pokeapi.co). Mantém dois caches em memória:
 * - Índice completo (name -> id) carregado uma vez por execução.
 * - Detalhes por ID, populados sob demanda.
 *
 * Cache simples em ConcurrentHashMap conforme archicterure-details.md §6.
 * Só populamos entradas bem-sucedidas — 404 sobe como ResponseStatusException.
 */
@Service
public class PokeApiService {

    private static final Logger log = LoggerFactory.getLogger(PokeApiService.class);
    private static final Pattern POKEMON_URL_ID = Pattern.compile("/pokemon/(\\d+)/?");
    private static final String ARTWORK_TEMPLATE =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/%d.png";

    private final RestClient restClient;
    private final Map<Integer, PokemonDetailDTO> detailCache = new ConcurrentHashMap<>();
    private volatile List<PokemonIndexEntry> indexCache;

    public PokeApiService(@Value("${pokedeck.pokeapi.base-url:https://pokeapi.co/api/v2}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public PageResponse<PokemonSummaryDTO> search(String search, int page, int size) {
        int normalizedPage = Math.max(0, page);
        int normalizedSize = size <= 0 ? 20 : Math.min(size, 60);

        List<PokemonIndexEntry> filtered = filterIndex(search);
        int total = filtered.size();
        int from = Math.min(normalizedPage * normalizedSize, total);
        int to = Math.min(from + normalizedSize, total);

        List<PokemonSummaryDTO> items = new ArrayList<>(to - from);
        for (PokemonIndexEntry entry : filtered.subList(from, to)) {
            PokemonDetailDTO detail = detailById(entry.id());
            items.add(new PokemonSummaryDTO(
                detail.id(),
                detail.name(),
                detail.imageUrl(),
                detail.types(),
                false
            ));
        }
        return PageResponse.of(items, normalizedPage, normalizedSize, total);
    }

    public PokemonDetailDTO detailById(int id) {
        PokemonDetailDTO cached = detailCache.get(id);
        if (cached != null) return cached;

        PokeApiPokemon raw;
        try {
            raw = restClient.get()
                .uri("/pokemon/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(NOT_FOUND, "Pokemon nao encontrado na PokeAPI: " + id);
                })
                .body(PokeApiPokemon.class);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (RestClientException ex) {
            log.warn("Falha ao consultar PokeAPI para id {}: {}", id, ex.getMessage());
            throw new ResponseStatusException(BAD_GATEWAY, "PokeAPI indisponivel no momento");
        }
        if (raw == null) {
            throw new ResponseStatusException(NOT_FOUND, "Pokemon nao encontrado: " + id);
        }
        PokemonDetailDTO dto = toDetail(raw);
        detailCache.put(id, dto);
        return dto;
    }

    private List<PokemonIndexEntry> filterIndex(String search) {
        List<PokemonIndexEntry> all = loadIndex();
        if (search == null || search.isBlank()) return all;
        String needle = search.trim().toLowerCase(Locale.ROOT);
        return all.stream().filter(e -> e.name().contains(needle)).toList();
    }

    private List<PokemonIndexEntry> loadIndex() {
        List<PokemonIndexEntry> cached = indexCache;
        if (cached != null) return cached;
        synchronized (this) {
            if (indexCache != null) return indexCache;
            try {
                PokeApiList list = restClient.get()
                    .uri("/pokemon?limit=2000&offset=0")
                    .retrieve()
                    .body(PokeApiList.class);
                if (list == null || list.results() == null) {
                    throw new ResponseStatusException(BAD_GATEWAY, "Resposta invalida da PokeAPI");
                }
                List<PokemonIndexEntry> entries = new ArrayList<>(list.results().size());
                for (PokeApiNameUrl item : list.results()) {
                    Matcher m = POKEMON_URL_ID.matcher(item.url());
                    if (m.find()) {
                        entries.add(new PokemonIndexEntry(Integer.parseInt(m.group(1)), item.name()));
                    }
                }
                entries.sort((a, b) -> Integer.compare(a.id(), b.id()));
                indexCache = List.copyOf(entries);
                log.info("Indice PokeAPI carregado: {} pokemons.", indexCache.size());
                return indexCache;
            } catch (RestClientException ex) {
                log.warn("Falha ao carregar indice PokeAPI: {}", ex.getMessage());
                throw new ResponseStatusException(BAD_GATEWAY, "PokeAPI indisponivel no momento");
            }
        }
    }

    private PokemonDetailDTO toDetail(PokeApiPokemon raw) {
        List<String> types = raw.types() == null ? List.of() :
            raw.types().stream()
                .sorted((a, b) -> Integer.compare(a.slot(), b.slot()))
                .map(t -> PokemonTranslations.typeLabel(t.type().name()))
                .toList();

        List<String> abilities = raw.abilities() == null ? List.of() :
            raw.abilities().stream()
                .map(a -> PokemonTranslations.capitalize(a.ability().name()))
                .toList();

        List<PokemonStatDTO> stats = raw.stats() == null ? List.of() :
            raw.stats().stream()
                .map(s -> new PokemonStatDTO(PokemonTranslations.statLabel(s.stat().name()), s.baseStat()))
                .toList();

        String imageUrl = raw.sprites() != null && raw.sprites().other() != null
            && raw.sprites().other().officialArtwork() != null
            && raw.sprites().other().officialArtwork().frontDefault() != null
            ? raw.sprites().other().officialArtwork().frontDefault()
            : ARTWORK_TEMPLATE.formatted(raw.id());

        return new PokemonDetailDTO(
            raw.id(),
            raw.name(),
            imageUrl,
            types,
            abilities,
            raw.height(),
            raw.weight(),
            raw.baseExperience(),
            stats,
            false
        );
    }

    // -------- Mapeamento JSON da PokeAPI --------
    private record PokemonIndexEntry(int id, String name) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PokeApiList(List<PokeApiNameUrl> results) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PokeApiNameUrl(String name, String url) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PokeApiPokemon(
        int id,
        String name,
        int height,
        int weight,
        @com.fasterxml.jackson.annotation.JsonProperty("base_experience") int baseExperience,
        List<PokeApiType> types,
        List<PokeApiAbility> abilities,
        List<PokeApiStat> stats,
        PokeApiSprites sprites
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PokeApiType(int slot, PokeApiNameUrl type) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PokeApiAbility(PokeApiNameUrl ability) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PokeApiStat(
        @com.fasterxml.jackson.annotation.JsonProperty("base_stat") int baseStat,
        PokeApiNameUrl stat
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PokeApiSprites(PokeApiSpritesOther other) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PokeApiSpritesOther(
        @com.fasterxml.jackson.annotation.JsonProperty("official-artwork") PokeApiArtwork officialArtwork
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PokeApiArtwork(
        @com.fasterxml.jackson.annotation.JsonProperty("front_default") String frontDefault
    ) {
    }
}

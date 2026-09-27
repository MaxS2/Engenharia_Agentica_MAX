package com.pptnc.pokedeck.service;

import com.pptnc.pokedeck.domain.DeckEntity;
import com.pptnc.pokedeck.domain.UserEntity;
import com.pptnc.pokedeck.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Garante que o usuario master (business-rules-details.md §1.1) existe no primeiro startup.
 * Credenciais vem de `pokedeck.admin.username` e `pokedeck.admin.password` (default admin/admin).
 */
@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public AdminSeeder(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        @Value("${pokedeck.admin.username}") String adminUsername,
        @Value("${pokedeck.admin.password}") String adminPassword
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.existsByUsername(adminUsername)) {
            log.info("Admin '{}' ja existe; seeding ignorado.", adminUsername);
            return;
        }
        UserEntity admin = new UserEntity(
            UUID.randomUUID(),
            adminUsername,
            passwordEncoder.encode(adminPassword),
            true
        );
        admin.getDecks().add(new DeckEntity(UUID.randomUUID(), "Meu primeiro Deck", admin));
        userRepository.save(admin);
        log.info("Admin '{}' criado com sucesso (seed inicial).", adminUsername);
    }
}

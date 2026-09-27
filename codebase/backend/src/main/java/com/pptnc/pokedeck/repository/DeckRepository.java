package com.pptnc.pokedeck.repository;

import com.pptnc.pokedeck.domain.DeckEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeckRepository extends JpaRepository<DeckEntity, String> {

    List<DeckEntity> findAllByOwner_Id(String ownerId);
}

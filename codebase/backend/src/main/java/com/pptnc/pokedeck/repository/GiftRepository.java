package com.pptnc.pokedeck.repository;

import com.pptnc.pokedeck.domain.GiftEntity;
import com.pptnc.pokedeck.dto.GiftStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiftRepository extends JpaRepository<GiftEntity, String> {

    List<GiftEntity> findAllByReceiver_IdAndStatusOrderByCreatedAtAsc(String receiverId, GiftStatus status);

    void deleteAllBySender_IdOrReceiver_Id(String senderId, String receiverId);
}

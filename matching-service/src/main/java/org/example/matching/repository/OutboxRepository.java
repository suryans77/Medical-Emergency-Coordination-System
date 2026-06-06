package org.example.matching.repository;

import org.example.matching.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {

    // The background poller will use this to find unsent messages
    List<OutboxEvent> findByStatus(String status);
}
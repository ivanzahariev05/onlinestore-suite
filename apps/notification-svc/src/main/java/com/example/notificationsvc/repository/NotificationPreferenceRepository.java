package com.example.notificationsvc.repository;

import com.example.notificationsvc.model.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {

    Optional<NotificationPreference> findByUserId(UUID userId);

    List<NotificationPreference> findAllUsersIdByEnabled(boolean preferenceEnabled);
}

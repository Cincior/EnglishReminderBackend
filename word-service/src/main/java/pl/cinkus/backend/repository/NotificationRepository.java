package pl.cinkus.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cinkus.backend.entity.Notification;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}

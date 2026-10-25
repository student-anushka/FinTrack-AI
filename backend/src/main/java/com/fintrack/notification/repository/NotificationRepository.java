
package com.fintrack.notification.repository;

import com.fintrack.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserIdOrderByCreatedAtDesc(
            Long userId);

    Optional<Notification> findByIdAndUserId(
            Long notificationId,
            Long userId);

    long countByUserIdAndReadFalse(Long userId);

    List<Notification> findAllByUserIdAndReadFalse(
            Long userId);

    boolean existsByUserIdAndBudgetIdAndAlertLevel(
            Long userId,
            Long budgetId,
            String alertLevel);
}

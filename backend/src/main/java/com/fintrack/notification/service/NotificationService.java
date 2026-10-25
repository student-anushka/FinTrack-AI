
package com.fintrack.notification.service;

import com.fintrack.auth.entity.User;
import com.fintrack.auth.repository.UserRepository;
import com.fintrack.notification.dto.NotificationResponse;
import com.fintrack.notification.entity.Notification;
import com.fintrack.notification.repository.NotificationRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return notificationRepository
                .findAllByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return notificationRepository
                .countByUserIdAndReadFalse(user.getId());
    }

    public NotificationResponse markAsRead(
            Long notificationId,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        Notification notification = notificationRepository
                .findByIdAndUserId(notificationId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notification not found"));

        notification.setRead(true);

        return mapToResponse(
                notificationRepository.save(notification));
    }

    public void markAllAsRead(Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        List<Notification> unreadNotifications = notificationRepository
                .findAllByUserIdAndReadFalse(user.getId());

        unreadNotifications.forEach(
                notification -> notification.setRead(true));

        notificationRepository.saveAll(unreadNotifications);
    }

    public void deleteNotification(
            Long notificationId,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        Notification notification = notificationRepository
                .findByIdAndUserId(notificationId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notification not found"));

        notificationRepository.delete(notification);
    }

    /*
     * Internal method for other backend modules to create alerts.
     * We will connect budget and goal events after the core API works.
     */
    public NotificationResponse createNotification(
            Long userId,
            String type,
            String title,
            String message) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return mapToResponse(
                notificationRepository.save(notification));
    }

    private User getAuthenticatedUser(
            Authentication authentication) {

        if (authentication == null
                || authentication.getName() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"));
    }

    private NotificationResponse mapToResponse(
            Notification notification) {

        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt());
    }

    public void createBudgetAlertIfAbsent(
            Long userId,
            Long budgetId,
            String alertLevel,
            String title,
            String message) {

        boolean alreadyExists = notificationRepository.existsByUserIdAndBudgetIdAndAlertLevel(
                userId, budgetId, alertLevel);

        if (alreadyExists) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType("BUDGET_ALERT");
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setBudgetId(budgetId);
        notification.setAlertLevel(alertLevel);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }
}

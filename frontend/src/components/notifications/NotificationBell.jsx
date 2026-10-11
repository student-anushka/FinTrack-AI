
import { useCallback, useEffect, useState } from "react";
import notificationApi from "../../api/notificationApi";
import "./NotificationBell.css";

function formatDate(value) {
    if (!value) return "";

    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return "";

    return date.toLocaleString([], {
        dateStyle: "medium",
        timeStyle: "short",
    });
}

export default function NotificationBell() {
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [isOpen, setIsOpen] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState("");
    const [actionId, setActionId] = useState(null);
    const [isMarkingAll, setIsMarkingAll] = useState(false);

    const refreshUnreadCount = useCallback(async (signal) => {
        try {
            const data = await notificationApi.getUnreadCount({ signal });
            setUnreadCount(Number(data.unreadCount) || 0);
        } catch (err) {
            if (err.code !== "ERR_CANCELED") {
                console.error("Unable to load unread count:", err);
            }
        }
    }, []);

    const loadNotifications = useCallback(async (signal) => {
        setIsLoading(true);
        setError("");

        try {
            const data = await notificationApi.getNotifications({ signal });
            setNotifications(Array.isArray(data) ? data : []);
        } catch (err) {
            if (err.code !== "ERR_CANCELED") {
                setError(
                    err.response?.data?.message ||
                    "Unable to load notifications. Please try again."
                );
            }
        } finally {
            if (!signal?.aborted) {
                setIsLoading(false);
            }
        }
    }, []);

    useEffect(() => {
        const controller = new AbortController();

        async function loadInitialData() {
            await Promise.all([
                refreshUnreadCount(controller.signal),
                loadNotifications(controller.signal),
            ]);
        }

        loadInitialData();

        return () => controller.abort();
    }, [loadNotifications, refreshUnreadCount]);

    const handleOpen = async () => {
        const nextOpen = !isOpen;
        setIsOpen(nextOpen);

        if (nextOpen) {
            const controller = new AbortController();
            await Promise.all([
                loadNotifications(controller.signal),
                refreshUnreadCount(controller.signal),
            ]);
        }
    };

    const handleMarkRead = async (notification) => {
        if (notification.read) return;

        setActionId(notification.id);
        setError("");

        try {
            const updated = await notificationApi.markAsRead(notification.id);

            setNotifications((current) =>
                current.map((item) =>
                    item.id === notification.id ? updated : item
                )
            );

            setUnreadCount((count) => Math.max(0, count - 1));
        } catch (err) {
            setError(
                err.response?.data?.message ||
                "Unable to mark notification as read."
            );
        } finally {
            setActionId(null);
        }
    };

    const handleMarkAllRead = async () => {
        setIsMarkingAll(true);
        setError("");

        try {
            await notificationApi.markAllAsRead();

            setNotifications((current) =>
                current.map((item) => ({ ...item, read: true }))
            );

            setUnreadCount(0);
        } catch (err) {
            setError(
                err.response?.data?.message ||
                "Unable to mark all notifications as read."
            );
        } finally {
            setIsMarkingAll(false);
        }
    };

    const handleDelete = async (notificationId) => {
        setActionId(notificationId);
        setError("");

        try {
            const item = notifications.find(
                (notification) => notification.id === notificationId
            );

            await notificationApi.deleteNotification(notificationId);

            setNotifications((current) =>
                current.filter((notification) => notification.id !== notificationId)
            );

            if (item && !item.read) {
                setUnreadCount((count) => Math.max(0, count - 1));
            }
        } catch (err) {
            setError(
                err.response?.data?.message ||
                "Unable to delete notification."
            );
        } finally {
            setActionId(null);
        }
    };

    return (
        <div className="notification-root">
            <button
                type="button"
                className="notification-bell"
                onClick={handleOpen}
                aria-label={`Notifications, ${unreadCount} unread`}
                aria-expanded={isOpen}
            >
                <span aria-hidden="true">♧</span>
                {unreadCount > 0 && (
                    <span className="notification-badge">
                        {unreadCount > 99 ? "99+" : unreadCount}
                    </span>
                )}
            </button>

            {isOpen && (
                <>
                    <button
                        type="button"
                        className="notification-backdrop"
                        aria-label="Close notifications"
                        onClick={() => setIsOpen(false)}
                    />

                    <section
                        className="notification-panel"
                        aria-label="Notifications"
                    >
                        <header className="notification-panel-header">
                            <div>
                                <h2>Notifications</h2>
                                <p>{unreadCount} unread</p>
                            </div>

                            <button
                                type="button"
                                className="notification-text-button"
                                disabled={unreadCount === 0 || isMarkingAll}
                                onClick={handleMarkAllRead}
                            >
                                {isMarkingAll ? "Updating..." : "Mark all read"}
                            </button>
                        </header>

                        {error && (
                            <div className="notification-error" role="alert">
                                {error}
                            </div>
                        )}

                        {isLoading ? (
                            <p className="notification-state">Loading notifications...</p>
                        ) : notifications.length === 0 ? (
                            <div className="notification-state">
                                <span aria-hidden="true">🔔</span>
                                <strong>You're all caught up</strong>
                                <p>New budget and savings alerts will appear here.</p>
                            </div>
                        ) : (
                            <div className="notification-list">
                                {notifications.map((notification) => (
                                    <article
                                        key={notification.id}
                                        className={`notification-item ${notification.read ? "is-read" : "is-unread"
                                            }`}
                                    >
                                        <div className="notification-item-content">
                                            <div className="notification-item-title">
                                                {!notification.read && (
                                                    <span className="notification-unread-dot" />
                                                )}
                                                <strong>{notification.title}</strong>
                                            </div>

                                            <p>{notification.message}</p>

                                            <time dateTime={notification.createdAt}>
                                                {formatDate(notification.createdAt)}
                                            </time>
                                        </div>

                                        <div className="notification-item-actions">
                                            {!notification.read && (
                                                <button
                                                    type="button"
                                                    className="notification-text-button"
                                                    disabled={actionId === notification.id}
                                                    onClick={() => handleMarkRead(notification)}
                                                >
                                                    Read
                                                </button>
                                            )}

                                            <button
                                                type="button"
                                                className="notification-delete-button"
                                                aria-label={`Delete ${notification.title}`}
                                                disabled={actionId === notification.id}
                                                onClick={() => handleDelete(notification.id)}
                                            >
                                                Delete
                                            </button>
                                        </div>
                                    </article>
                                ))}
                            </div>
                        )}
                    </section>
                </>
            )}
        </div>
    );
}

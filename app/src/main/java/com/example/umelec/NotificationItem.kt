package com.example.umelec

/**
 * Data class representing a single notification item.
 *
 * This structure is designed to guide backend/database integration:
 * - The 'id' would be the primary key for updates (e.g., setting 'isRead' status).
 * - 'type' determines the icon and potentially the navigation destination.
 * - 'timestamp' is crucial for displaying the '10 mins ago' or 'Oct 11, 2025' format.
 */
data class NotificationItem(
    val id: String, // MUST use 'val' for public property access
    val title: String,
    val previewText: String,
    val type: NotificationType,
    val timestamp: Long,
    var isRead: Boolean
)

/**
 * Enum to define possible notification types, mapping to specific icons.
 */
enum class NotificationType {
    REMINDER,       // For election reminders (uses ic_notif_reminder)
    SUBMISSION,     // For successful vote submissions (uses ic_notif_submitted)
    GENERIC,        // Default for other types (uses a generic system info icon)
}
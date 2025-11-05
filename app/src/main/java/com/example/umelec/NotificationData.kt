package com.example.umelec

import java.util.concurrent.TimeUnit
import java.util.Date

// --- Data Classes: Defined Once Here ---

enum class NotificationType {
    REMINDER, SUBMISSION, GENERIC
}

/**
 * Defines the structure for a complete notification item, including the full text.
 */
data class NotificationItem(
    val id: String,
    val title: String,
    val previewText: String, // Used for list/dropdown view
    val fullText: String,    // Used for detail view (Notification2.kt)
    val type: NotificationType,
    val timestamp: Long,
    var isRead: Boolean
)


// --- The Unified Notification List (N001 - N009) ---

/**
 * This is the single source of truth for all notifications in the application.
 */
val allNotifications: MutableList<NotificationItem> = mutableListOf(
    // N001 - N005: Originally from Notification.kt's main list
    NotificationItem(
        id = "N001",
        title = "New Candidate Registration is Open!",
        previewText = "The window for candidate registration is now officially open! Submit your forms...",
        fullText = "The window for candidate registration is now officially open! All interested students are encouraged to submit their applications, including all required forms and a statement of intent, before the deadline on November 30th. For detailed requirements, please visit the election portal and review the latest guidelines.",
        type = NotificationType.REMINDER,
        timestamp = Date().time - TimeUnit.DAYS.toMillis(1),
        isRead = false // Keep N001 as unread for the example
    ),
    NotificationItem(
        id = "N002",
        title = "Critical System Update Deployed",
        previewText = "A critical system update has been successfully deployed to enhance security.",
        fullText = "A critical system update has been successfully deployed to enhance security and stability across the platform. Please check the updated guidelines regarding the campaigning rules, as some restrictions have been clarified. No immediate action is required.",
        type = NotificationType.GENERIC,
        timestamp = Date().time - TimeUnit.DAYS.toMillis(2),
        isRead = true
    ),
    NotificationItem(
        id = "N003",
        title = "System Maintenance Alert",
        previewText = "The system will undergo brief scheduled maintenance tonight from 1 AM to 3 AM.",
        fullText = "The system will undergo brief but important scheduled maintenance tonight from 1 AM to 3 AM. Access to voting and candidate profiles will be temporarily unavailable during this window. We apologize for any inconvenience.",
        type = NotificationType.GENERIC,
        timestamp = Date().time - TimeUnit.DAYS.toMillis(4),
        isRead = false
    ),
    NotificationItem(
        id = "N004",
        title = "Voting Period Starts Soon!",
        previewText = "Attention voters! Less than 24 hours left before the official voting period begins.",
        fullText = "Attention voters! Less than 24 hours left before the official voting period begins. Ensure you have your login credentials ready and familiarize yourself with the ballot before the deadline. Voting will open at 9:00 AM tomorrow morning.",
        type = NotificationType.REMINDER,
        timestamp = Date().time - TimeUnit.DAYS.toMillis(7),
        isRead = false
    ),
    NotificationItem(
        id = "N005",
        title = "Account Verified",
        previewText = "Congratulations! Your profile has been fully verified and is ready for voting.",
        fullText = "Congratulations! Your profile has been fully verified and is ready for voting. You can now browse all candidates and prepare to cast your vote once the voting period opens.",
        type = NotificationType.SUBMISSION,
        timestamp = Date().time - TimeUnit.DAYS.toMillis(10),
        isRead = true
    ),
    // N006 - N009: Originally from NotificationUtils.kt's dropdown list
    NotificationItem(
        id = "N006",
        title = "Election Reminder",
        previewText = "The voting period will officially end in 3 days. Cast your vote now!",
        fullText = "A final reminder: The voting period will officially end in 3 days. This is your last chance to participate and make your voice heard! Cast your vote now before the polls close on Friday.",
        type = NotificationType.REMINDER,
        timestamp = Date().time - TimeUnit.MINUTES.toMillis(15),
        isRead = false
    ),
    NotificationItem(
        id = "N007",
        title = "Vote Submitted Successfully",
        previewText = "Your ballot was successfully submitted on October 22, 2025.",
        fullText = "Confirmation: Your electronic ballot was successfully submitted and recorded on October 22, 2025, at 10:30 AM. Thank you for participating in the election process.",
        type = NotificationType.SUBMISSION,
        timestamp = Date().time - TimeUnit.HOURS.toMillis(3),
        isRead = true
    ),
    NotificationItem(
        id = "N008",
        title = "System Update",
        previewText = "A minor security patch has been deployed. Check the FAQ for details.",
        fullText = "A minor security patch has been deployed to fix several small bugs and improve overall performance. Check the FAQ section for details on the changes and any required browser updates.",
        type = NotificationType.GENERIC,
        timestamp = Date().time - TimeUnit.DAYS.toMillis(1),
        isRead = false
    ),
    NotificationItem(
        id = "N009",
        title = "New Candidate",
        previewText = "Alice Williams has been added to the Treasurer race.",
        fullText = "We have a new addition to the roster! Alice Williams has been officially added to the Treasurer race. Review her profile and campaign materials on the candidate page.",
        type = NotificationType.GENERIC,
        timestamp = Date().time - TimeUnit.DAYS.toMillis(5),
        isRead = false
    )
).toMutableList()
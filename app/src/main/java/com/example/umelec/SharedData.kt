package com.example.umelec

// Data class to represent a notification item (MOVED HERE)
data class NotificationItem(val title: String, val isRead: Boolean)

// If you have it, also move the FaqItem data class here
data class FaqItem(val question: String, val answer: String)

// ⭐️ NEW: Data structure for Candidate's detailed platform/comparison data
data class CandidatePlatformDetails(
    val candidateId: String,
    val name: String,
    val position: String,
    val courseInfo: String,
    val profilePictureResource: Int,
    val credentials: String,
    val advocacy: String
)
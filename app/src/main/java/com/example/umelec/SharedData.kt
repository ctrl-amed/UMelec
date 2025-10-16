package com.example.umelec

// Data class to represent a notification item (MOVED HERE)
data class NotificationItem(val title: String, val isRead: Boolean)

// If you have it, also move the FaqItem data class here
data class FaqItem(val question: String, val answer: String)
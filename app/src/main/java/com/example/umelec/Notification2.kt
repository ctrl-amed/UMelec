package com.example.umelec

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// ----------------------------------------------------------------------
// --- DATA STRUCTURE FOR FULL NOTIFICATIONS (Model) ---
// ----------------------------------------------------------------------
// This is the structure for the detailed notification data that is displayed here.
data class FullNotificationDetails(
    val id: String,
    val title: String,
    val body: String // Changed from 'previewText' to 'body' for the full view
)

class Notification2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification2)

        // 1. Get the Notification ID passed from the previous activity
        val notificationId = intent.getStringExtra("NOTIFICATION_ID")

        // 2. Setup UI and load data based on the ID
        if (notificationId != null) {
            setupBackNavigation()
            loadNotificationData(notificationId)
        } else {
            // Handle error case if ID is missing (should not happen if called from Notification.kt)
            Toast.makeText(this, "Error: Notification ID missing.", Toast.LENGTH_LONG).show()
            finish()
        }

        // 3. Setup the persistent footer navigation
        setupFooterNavigation()
    }

    // ----------------------------------------------------------------------
    // --- DATA LOADING LOGIC (Easy for backend integration) ---
    // ----------------------------------------------------------------------

    /**
     * Simulates fetching the full notification details from a database/backend.
     * This makes it easy to replace with a real database/API call later.
     */
    private fun fetchNotificationDetails(id: String): FullNotificationDetails? {
        // In a real app, you'd make an API call using the 'id'.
        // For now, we use a map for quick lookups.
        val dataMap = mapOf(
            "N001" to FullNotificationDetails(
                id = "N001",
                title = "Candidate Registration is Open!",
                body = "The window for candidate registration is now officially open! All interested parties must submit their applications, including all required documentation and a brief platform summary, by the deadline next Friday at 5 PM. Check the 'Candidates' tab for detailed requirements."
            ),
            "N002" to FullNotificationDetails(
                id = "N002",
                title = "New Message from Admin",
                body = "The administration has released updated guidelines regarding the campaigning rules. Please note that public gatherings are restricted to campus grounds only. Violation of these rules may result in immediate disqualification. Please review the FAQ section for details."
            ),
            "N003" to FullNotificationDetails(
                id = "N003",
                title = "System Maintenance Alert",
                body = "The system will undergo brief maintenance tonight from 1 AM to 3 AM. Access to voting and candidate registration will be temporarily unavailable during this window. We apologize for any inconvenience."
            ),
            "N004" to FullNotificationDetails(
                id = "N004",
                title = "Voting Period Starts Soon!",
                body = "The official voting period begins in less than 24 hours! Make sure you are logged in and ready to cast your ballot. You can only vote once, so review the candidates carefully before making your final selection."
            )
        )
        return dataMap[id]
    }

    /**
     * Fetches and displays the notification data to the TextViews.
     */
    private fun loadNotificationData(notificationId: String) {
        val details = fetchNotificationDetails(notificationId)

        if (details != null) {
            val titleTextView: TextView = findViewById(R.id.notification_title)
            val bodyTextView: TextView = findViewById(R.id.notification_preview_text)

            // Set the retrieved data to the views
            titleTextView.text = details.title
            bodyTextView.text = details.body
        } else {
            // Handle case where ID was found, but the data is not in the map/backend
            Toast.makeText(this, "Notification details not found for ID: $notificationId", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // ----------------------------------------------------------------------
    // --- TOP NAVIGATION LOGIC (Back Button) ---
    // ----------------------------------------------------------------------

    private fun setupBackNavigation() {
        val backButton: ImageButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            // Simply finish the current activity to return to the Notification list
            finish()
        }
    }

    // ----------------------------------------------------------------------
    // --- FOOTER NAVIGATION LOGIC (Kept as is) ---
    // ----------------------------------------------------------------------

    /**
     * Sets up click listeners for all elements in the footer navigation bar.
     */
    private fun setupFooterNavigation() {
        // Find all navigation items (LinearLayouts)
        val navHome: LinearLayout = findViewById(R.id.nav_home)
        val navVote: LinearLayout = findViewById(R.id.nav_vote)
        val navCandidates: LinearLayout = findViewById(R.id.nav_candidates)
        val navResults: LinearLayout = findViewById(R.id.nav_results)
        val navFaq: LinearLayout = findViewById(R.id.nav_faq)

        // Helper function to navigate to a new Activity
        val navigateTo = { activityClass: Class<*> ->
            val intent = Intent(this, activityClass)
            // Use this flag for smoother tab switching
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
        }

        // Set Click Listeners
        navHome.setOnClickListener { navigateTo(Homepage::class.java) }
        navVote.setOnClickListener { navigateTo(Vote::class.java) }
        navCandidates.setOnClickListener { navigateTo(Candidates::class.java) }
        navResults.setOnClickListener { navigateTo(Results::class.java) }
        navFaq.setOnClickListener { navigateTo(Faq::class.java) }
    }
}
package com.example.umelec

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.LayoutInflater
// 💡 REQUIRED IMPORT ADDED HERE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
// Assuming Notification2, Homepage, Vote, Candidates, Results, and Faq classes exist.

// ----------------------------------------------------------------------
// --- DATA STRUCTURE FOR NOTIFICATIONS (Model) ---
// ----------------------------------------------------------------------
data class FullNotificationData(
    val id: String, // Unique ID for navigation (e.g., to Notification2)
    val title: String,
    val previewText: String,
    var isRead: Boolean // Important: Must be 'var' to allow state changes
)

class Notification : AppCompatActivity() {

    // Variable to hold all notifications (Simulates your database/backend list)
    private lateinit var allNotifications: MutableList<FullNotificationData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // 1. Fetch initial data
        allNotifications = fetchAllNotifications().toMutableList()

        // 2. Populate UI with cards and update the counter
        populateNotifications()

        // 3. Setup header and footer
        setupBackNavigation()
        setupFooterNavigation()
    }

    // ----------------------------------------------------------------------
    // --- TOP NAVIGATION LOGIC (Back Button) ---
    // ----------------------------------------------------------------------

    private fun setupBackNavigation() {
        val backButton: ImageButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            finish()
        }
    }

    // ----------------------------------------------------------------------
    // --- DATA FETCHING (Simulated, replace this with your actual DB/API call) ---
    // ----------------------------------------------------------------------

    private fun fetchAllNotifications(): List<FullNotificationData> {
        return listOf(
            FullNotificationData(
                id = "N001",
                title = "Candidate Registration is Open!",
                previewText = "The window for candidate registration is now officially open...",
                isRead = true
            ),
            FullNotificationData(
                id = "N002",
                title = "New Message from Admin",
                previewText = "Please check the updated guidelines regarding the campaigning rules...",
                isRead = false
            ),
            FullNotificationData(
                id = "N003",
                title = "System Maintenance Alert",
                previewText = "The system will undergo brief maintenance tonight from 1 AM to 3 AM...",
                isRead = false
            ),
            FullNotificationData(
                id = "N004",
                title = "Voting Period Starts Soon!",
                previewText = "Less than 24 hours left before the official voting period begins...",
                isRead = true
            )
        )
    }

    // ----------------------------------------------------------------------
    // --- DYNAMIC POPULATION AND COUNTER LOGIC ---
    // ----------------------------------------------------------------------

    private fun populateNotifications() {
        // 💡 CHANGE 1: Find the parent container
        val notificationContainer: LinearLayout = findViewById(R.id.NotificationContainer)
        notificationContainer.removeAllViews()
        updateNotificationCounter()

        allNotifications.forEach { notification ->
            // 💡 CHANGE 2: Pass the parent container to the creation function
            val cardView = createNotificationCard(notification, notificationContainer)
            notificationContainer.addView(cardView)
        }
    }

    private fun updateNotificationCounter() {
        val counterTextView: TextView? = findViewById(R.id.number)

        counterTextView?.let { textView ->
            val totalCount = allNotifications.size
            val readCount = allNotifications.count { it.isRead }
            textView.text = "$readCount/$totalCount"
        }
    }

    /**
     * Programmatically inflates and configures a single notification card.
     */
    // 💡 CHANGE 3: Function now accepts the parent ViewGroup
    private fun createNotificationCard(notification: FullNotificationData, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(this)

        // 💡 CHANGE 4: Use the 3-argument inflate method to honor layout_marginBottom
        val cardView = inflater.inflate(R.layout.notification_card_item, parent, false)

        val titleTextView: TextView = cardView.findViewById(R.id.notification_title)
        val previewTextView: TextView = cardView.findViewById(R.id.notification_preview_text)

        titleTextView.text = notification.title
        previewTextView.text = notification.previewText

        cardView.setBackgroundResource(
            if (notification.isRead) R.drawable.notification_read_bg else R.drawable.notification_unread_bg
        )

        cardView.setOnClickListener { view ->
            if (!notification.isRead) {
                view.setBackgroundResource(R.drawable.notification_read_bg)

                notification.isRead = true
                updateNotificationCounter()

                // ⚠️ Update backend here
            }

            val intent = Intent(this, Notification2::class.java).apply {
                putExtra("NOTIFICATION_ID", notification.id)
            }
            startActivity(intent)
        }

        return cardView
    }

    // ----------------------------------------------------------------------
    // --- FOOTER NAVIGATION LOGIC (No change needed) ---
    // ----------------------------------------------------------------------

    private fun setupFooterNavigation() {
        val navHome: LinearLayout = findViewById(R.id.nav_home)
        val navVote: LinearLayout = findViewById(R.id.nav_vote)
        val navCandidates: LinearLayout = findViewById(R.id.nav_candidates)
        val navResults: LinearLayout = findViewById(R.id.nav_results)
        val navFaq: LinearLayout = findViewById(R.id.nav_faq)

        val navigateTo = { activityClass: Class<*> ->
            val intent = Intent(this, activityClass)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
        }

        navHome.setOnClickListener { navigateTo(Homepage::class.java) }
        navVote.setOnClickListener { navigateTo(Vote::class.java) }
        navCandidates.setOnClickListener { navigateTo(Candidates::class.java) }
        navResults.setOnClickListener { navigateTo(Results::class.java) }
        navFaq.setOnClickListener { navigateTo(Faq::class.java) }
    }
}
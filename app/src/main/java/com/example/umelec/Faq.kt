package com.example.umelec

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat // For resource color/drawable handling

// --- DATA STRUCTURE FOR FAQ ---

class Faq : AppCompatActivity() {

    // A list to hold your notification data (Simulated data)
    private val notifications = mutableListOf(
        NotificationItem("New message from John Doe", false),
        NotificationItem("Your post was liked by 5 people", true),
        NotificationItem("System update available", false)
    )

    // The current state of the notification dropdown
    private var isNotificationDropdownVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        // Initialize UI components and set up listeners
        setupUI()

        // Setup the persistent footer navigation
        setupFooterNavigation()

        // 🆕 POPULATE FAQS DYNAMICALLY
        populateFaqs()
    }

    // ----------------------------------------------------------------------
    // --- DYNAMIC FAQ POPULATION LOGIC ---
    // ----------------------------------------------------------------------

    private fun populateFaqs() {
        // 1. Get a reference to the main container
        val contentContainer: LinearLayout = findViewById(R.id.ContentContainer)

        // 2. Define your list of FAQs (EASY TO REPLACE WITH BACKEND DATA)
        val faqList = listOf(
            FaqItem(
                question = "How do I cast my vote?",
                answer = "You can cast your vote by navigating to the 'Vote' tab, selecting the candidates you prefer for each position, and submitting your ballot before the deadline."
            ),
            FaqItem(
                question = "When does the election start and end?",
                answer = "The voting period is displayed on the homepage. Please check the 'Election Information' card for the exact dates and times."
            ),

            // ⬇️ START: COMMENT OUT THE NEXT FOUR FAQS TO TEST SCROLLING ⬇️

            FaqItem(
                question = "Who are the candidates running?",
                answer = "The full list of candidates, along with their platforms and profiles, can be viewed in the 'Candidates' section of the application."
            ),
            FaqItem(
                question = "Can I change my vote after submitting?",
                answer = "No, for security and integrity reasons, once your vote is submitted, it is final and cannot be altered or withdrawn."
            ),
            FaqItem(
                question = "Where can I see the election results?",
                answer = "Live tallies and final results will be posted in the 'Results' section once the voting period has officially closed."
            ),

            // ⬆️ END: COMMENT OUT THE ABOVE FOUR FAQS TO TEST SCROLLING ⬆️

        )

        // 3. Clear existing children (if any) before adding the new ones.
        // We will remove the first child which is the sample FaqLayout in the XML,
        // and any subsequent view before the final placeholder.
        // It's safer to clear everything except the placeholder view and then rebuild.

        // Find the index of the placeholder view
        val placeholderIndex = contentContainer.childCount - 1
        val placeholderView = if (placeholderIndex >= 0) contentContainer.getChildAt(placeholderIndex) else null

        // Clear the container (keeping the placeholder safe if it exists)
        contentContainer.removeAllViews()

        // 4. Iterate and add the views
        faqList.forEach { faq ->
            val faqView = createFaqItemView(this, faq.question, faq.answer)
            contentContainer.addView(faqView)
        }

        // 5. Re-add the placeholder view (if it exists) to maintain padding/scrolling
        if (placeholderView != null) {
            contentContainer.addView(placeholderView)
        }
    }

    /**
     * Programmatically creates a single FAQ item (the equivalent of FaqLayout).
     * This avoids needing to create a separate XML layout file for the item.
     */
    private fun createFaqItemView(context: Context, question: String, answer: String): View {
        // Main container (FaqLayout equivalent)
        val faqLayout = LinearLayout(context).apply {
            id = View.generateViewId() // Generate a unique ID
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                // Margin for spacing between FAQ cards
                bottomMargin = 20.toPx()
            }
            orientation = LinearLayout.VERTICAL

            // Background properties matching faq_bg.xml and elevation from your XML
            background = ContextCompat.getDrawable(context, R.drawable.faq_bg)
            setPadding(16.toPx(), 16.toPx(), 16.toPx(), 16.toPx())
            elevation = 4f
        }

        // Question TextView
        val questionText = TextView(context).apply {
            id = View.generateViewId() // Generate a unique ID
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8.toPx()
            }
            text = question // 🎯 EASILY CHANGEABLE DATA POINT
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor("#0E0E2C"))
            // You'll need to use reflection or an extension function to set custom fonts if not available globally
            // For now, we omit the custom font setup here for simplicity.
        }
        faqLayout.addView(questionText)

        // Answer TextView
        val answerText = TextView(context).apply {
            id = View.generateViewId() // Generate a unique ID
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8.toPx()
            }
            text = answer // 🎯 EASILY CHANGEABLE DATA POINT
            textSize = 18f
            // Note: Your original XML had textStyle="bold" for the answer,
            // but usually answers are normal. I kept it as per your request but noted the difference.
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor("#4A4A68"))
            // Custom font omitted for simplicity
        }
        faqLayout.addView(answerText)

        return faqLayout
    }

    // ----------------------------------------------------------------------
    // --- BASE ACTIVITY LOGIC (from previous turn) ---
    // ----------------------------------------------------------------------

    /**
     * Initializes header elements: User Name, Profile Icon, and Notification Icon.
     */
    private fun setupUI() {
        val profileIcon: ImageView = findViewById(R.id.profileIcon)
        val notificationIcon: ImageView = findViewById(R.id.notificationIcon)



        // --- Profile Icon Click Listener ---
        profileIcon.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        // --- Notification Icon Click Listener (Toggle Dropdown) ---
        notificationIcon.setOnClickListener {
            toggleNotificationDropdown(notificationIcon)
        }
    }

    /**
     * Sets up click listeners for all elements in the footer navigation bar.
     */
    private fun setupFooterNavigation() {
        val navHome: LinearLayout = findViewById(R.id.nav_home)
        val navVote: LinearLayout = findViewById(R.id.nav_vote)
        val navCandidates: LinearLayout = findViewById(R.id.nav_candidates)
        val navResults: LinearLayout = findViewById(R.id.nav_results)
        val navFaq: LinearLayout = findViewById(R.id.nav_faq)

        val navigateTo = { activityClass: Class<*> ->
            if (activityClass != this::class.java) {
                val intent = Intent(this, activityClass)
                // Use FLAG_ACTIVITY_REORDER_TO_FRONT for better navigation flow
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
            }
        }

        navHome.setOnClickListener { navigateTo(Homepage::class.java) }
        navVote.setOnClickListener { navigateTo(Vote::class.java) }
        navCandidates.setOnClickListener { navigateTo(Candidates::class.java) }
        navResults.setOnClickListener { navigateTo(Results::class.java) }
        // navFaq.setOnClickListener { /* Already here */ }
    }

    /**
     * Toggles the visibility of the notification dropdown menu.
     */
    private fun toggleNotificationDropdown(anchorView: ImageView) {
        if (isNotificationDropdownVisible) {
            anchorView.setColorFilter(Color.parseColor("#FAFCFE"))
            isNotificationDropdownVisible = false
        } else {
            showNotificationDropdown(anchorView)
            isNotificationDropdownVisible = true
        }
    }

    /**
     * Creates and displays the custom notification dropdown menu.
     */
    private fun showNotificationDropdown(anchorView: ImageView) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // **IMPORTANT:** Ensure you have a file named 'notification_dropdown.xml' in your layout folder.
        val popupView = inflater.inflate(R.layout.notification_dropdown, null)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        popupWindow.setOnDismissListener {
            anchorView.setColorFilter(Color.parseColor("#FAFCFE"))
            isNotificationDropdownVisible = false
        }
        anchorView.setColorFilter(Color.parseColor("#FCBE6A"))

        val notificationContainer: LinearLayout = popupView.findViewById(R.id.notificationListContainer)
        val noNotificationText: TextView = popupView.findViewById(R.id.noNotificationTextView)

        notificationContainer.removeAllViews()

        if (notifications.isEmpty()) {
            noNotificationText.visibility = View.VISIBLE
        } else {
            noNotificationText.visibility = View.GONE
            notifications.take(3).forEach { item ->
                val tv = TextView(this).apply {
                    id = View.generateViewId()
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 8, 0, 8) }
                    text = item.title
                    textSize = 16f
                    setTextColor(Color.parseColor(if (item.isRead) "#333333" else "#FCBE6A"))
                    setOnClickListener {
                        val intent = Intent(this@Faq, Notification::class.java)
                        intent.putExtra("NOTIFICATION_TITLE", item.title)
                        startActivity(intent)
                        popupWindow.dismiss()
                    }
                }
                notificationContainer.addView(tv)
            }
        }

        val closeButton: ImageView = popupView.findViewById(R.id.closeDropdownButton)
        closeButton.setOnClickListener { popupWindow.dismiss() }

        val viewAllButton: TextView = popupView.findViewById(R.id.viewAllButton)
        viewAllButton.setOnClickListener {
            val intent = Intent(this, Notification::class.java)
            startActivity(intent)
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(anchorView, -300, 0)
    }

    // Utility extension function to convert DP to pixels
    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()
}
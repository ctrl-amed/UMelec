package com.example.umelec

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

// --- DATA STRUCTURES ---


class Faq : AppCompatActivity() {

    // 1. 💡 NEW: Initialize the reusable NotificationManager
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        // 2. 💡 NEW: Instantiate the NotificationManager
        notificationManager = NotificationManager(this)

        // Initialize UI components and set up listeners
        setupUI()

        // Setup the persistent footer navigation
        setupFooterNavigation()

        // POPULATE FAQS DYNAMICALLY
        populateFaqs()
    }

    // ----------------------------------------------------------------------
    // --- DYNAMIC FAQ POPULATION LOGIC ---
    // ----------------------------------------------------------------------

    private fun populateFaqs() {
        val contentContainer: LinearLayout = findViewById(R.id.ContentContainer)

        val faqList = listOf(
            FaqItem(
                question = "How do I cast my vote?",
                answer = "You can cast your vote by navigating to the 'Vote' tab, selecting the candidates you prefer for each position, and submitting your ballot before the deadline."
            ),
            FaqItem(
                question = "When does the election start and end?",
                answer = "The voting period is displayed on the homepage. Please check the 'Election Information' card for the exact dates and times."
            ),
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
        )

        val placeholderIndex = contentContainer.childCount - 1
        val placeholderView = if (placeholderIndex >= 0) contentContainer.getChildAt(placeholderIndex) else null

        contentContainer.removeAllViews()

        faqList.forEach { faq ->
            val faqView = createFaqItemView(this, faq.question, faq.answer)
            contentContainer.addView(faqView)
        }

        if (placeholderView != null) {
            contentContainer.addView(placeholderView)
        }
    }

    private fun createFaqItemView(context: Context, question: String, answer: String): View {
        val faqLayout = LinearLayout(context).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 20.toPx()
            }
            orientation = LinearLayout.VERTICAL
            background = ContextCompat.getDrawable(context, R.drawable.faq_bg)
            setPadding(16.toPx(), 16.toPx(), 16.toPx(), 16.toPx())
            elevation = 4f
        }

        val questionText = TextView(context).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8.toPx()
            }
            text = question
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor("#0E0E2C"))
        }
        faqLayout.addView(questionText)

        val answerText = TextView(context).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8.toPx()
            }
            text = answer
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor("#4A4A68"))
        }
        faqLayout.addView(answerText)

        return faqLayout
    }

    // ----------------------------------------------------------------------
    // --- BASE ACTIVITY LOGIC ---
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

        // 3. 💡 NEW: Notification Icon Click Listener (Uses the utility class)
        // This is where we call the reusable function
        notificationIcon.setOnClickListener {
            notificationManager.toggleNotificationDropdown(it as ImageView)
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
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
            }
        }

        navHome.setOnClickListener { navigateTo(Homepage::class.java) }
        navVote.setOnClickListener { navigateTo(Vote::class.java) }
        navCandidates.setOnClickListener { navigateTo(Candidates::class.java) }
        navResults.setOnClickListener { navigateTo(Results::class.java) }
    }

    // Utility extension function to convert DP to pixels
    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()
}
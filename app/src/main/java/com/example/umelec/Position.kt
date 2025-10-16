package com.example.umelec

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button // Import for the ViewAll button
import android.widget.ImageView // Base type for profile picture
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

// --- DATA STRUCTURE FOR CANDIDATES ---
// This is the model you would map your backend/database data to.
data class CandidateItem(
    val candidateId: String,
    val name: String,
    val position: String,
    val courseInfo: String,
    val previewText: String,
    val profilePictureResource: Int // Use R.drawable.your_image
)

class Position : AppCompatActivity() {

    // ⭐️ Variable to hold the position name passed from the previous activity
    private var currentPosition: String = "Position"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_position)

        // ⭐️ Retrieve the position name from the intent
        currentPosition = intent.getStringExtra("POSITION_NAME") ?: "Candidates"

        // 1. Setup the header title to display the position
        setupHeaderTitle()

        // 2. Setup the back button functionality
        setupBackNavigation()

        // 3. Populate the candidate cards dynamically
        populateCandidates()

        // 4. Setup the persistent footer navigation
        setupFooterNavigation()
    }

    // ----------------------------------------------------------------------
    // --- HEADER TITLE LOGIC ---
    // ----------------------------------------------------------------------

    /**
     * Finds the NameTitle TextView and sets its text based on the incoming Intent.
     */
    private fun setupHeaderTitle() {
        val nameTitle: TextView = findViewById(R.id.NameTitle)
        nameTitle.text = currentPosition
    }


    // ----------------------------------------------------------------------
    // --- TOP NAVIGATION LOGIC (Back Button) ---
    // ----------------------------------------------------------------------

    /**
     * Sets up the click listener for the back button to navigate to the previous screen.
     */
    private fun setupBackNavigation() {
        // Find the back button ID provided in your XML
        val backButton: ImageButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            finish() // This closes the current activity and returns to the previous one (likely Candidates.kt)
        }
    }

    // ----------------------------------------------------------------------
    // --- DYNAMIC CARD POPULATION LOGIC ---
    // ----------------------------------------------------------------------

    /**
     * Fetches candidate data (simulated) and dynamically creates cards.
     */
    private fun populateCandidates() {
        // 1. Get a reference to the main container
        val registerContainer: LinearLayout = findViewById(R.id.registerContainer)

        // ⭐️ SIMULATED BACKEND DATA ⭐️
        // NOTE: The 'position' field in CandidateItem can now use 'currentPosition'
        val candidates = listOf(
            CandidateItem(
                candidateId = "JANE_D",
                name = "Jane Doe",
                position = currentPosition, // Using the title from the intent
                courseInfo = "III - BCSAD",
                previewText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                profilePictureResource = R.drawable.profile // Replace with your actual drawable resource ID
            ),
            CandidateItem(
                candidateId = "JOHN_S",
                name = "John Smith",
                position = currentPosition, // Using the title from the intent
                courseInfo = "IV - BSIT",
                previewText = "Consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam...",
                profilePictureResource = R.drawable.profile// Replace with your actual drawable resource ID
            ),
            CandidateItem(
                candidateId = "JOHN_S",
                name = "John Smith",
                position = currentPosition, // Using the title from the intent
                courseInfo = "IV - BSIT",
                previewText = "Consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam...",
                profilePictureResource = R.drawable.profile// Replace with your actual drawable resource ID
            ),
            CandidateItem(
                candidateId = "SARAH_L",
                name = "Sarah Lee",
                position = currentPosition, // Using the title from the intent
                courseInfo = "II - BSBA",
                previewText = "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
                profilePictureResource = R.drawable.profile// Replace with your actual drawable resource ID
            )
            // Add more candidates for scrolling test if needed
        )

        registerContainer.removeAllViews() // Clear any existing placeholders

        // 2. Iterate and add the views
        candidates.forEach { candidate ->
            val cardView = createCandidateCardView(candidate, registerContainer)
            registerContainer.addView(cardView)
        }
    }

    /**
     * Inflates and populates a single candidate card with data and sets up the click listener.
     */
    private fun createCandidateCardView(candidate: CandidateItem, root: LinearLayout): View {
        // 1. Inflate the Card Layout
        val inflater = LayoutInflater.from(this)
        val cardView = inflater.inflate(R.layout.candidate_card_item, root, false)

        // 2. Find dynamic views within the inflated card (using IDs from the XML block)
        val profilePic: ImageView = cardView.findViewById(R.id.iv_profile_picture)
        val nameText: TextView = cardView.findViewById(R.id.tv_name)
        val positionText: TextView = cardView.findViewById(R.id.tv_position)
        val courseText: TextView = cardView.findViewById(R.id.tv_course_info)
        val previewText: TextView = cardView.findViewById(R.id.tv_preview_text)
        val viewAllButton: Button = cardView.findViewById(R.id.btnViewAll)

        // 3. Set Data (Changeable)
        nameText.text = candidate.name
        positionText.text = candidate.position
        courseText.text = candidate.courseInfo
        previewText.text = candidate.previewText

        // Load the profile picture resource
        profilePic.setImageResource(candidate.profilePictureResource)

        // 4. Set the View All button click behavior
        viewAllButton.setOnClickListener {
            // ⭐️ FIX: Animate color change and then navigate
            animateClickFeedback(
                view = it,
                originalBgResource = R.drawable.blue_rounded_button,
                navigationAction = {
                    val intent = Intent(this, Platform::class.java).apply {
                        // Pass data needed by the Platform activity (e.g., to load this specific candidate's platform)
                        putExtra("CANDIDATE_ID", candidate.candidateId)
                    }
                    startActivity(intent)
                }
            )
        }

        return cardView
    }

    // ----------------------------------------------------------------------
    // --- FOOTER NAVIGATION LOGIC (from previous step) ---
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

    // ----------------------------------------------------------------------
    // --- ANIMATION HELPER FUNCTIONS ---
    // ----------------------------------------------------------------------

    /**
     * Helper function to convert DP to pixels.
     */
    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()

    /**
     * Applies a temporary color change to FCBE6A for visual feedback.
     */
    private fun animateClickFeedback(view: View, originalBgResource: Int, navigationAction: () -> Unit) {
        // 1. Get the original background
        val originalBackground = view.background

        // 2. Create the temporary highlight background (#FCBE6A)
        val highlightColor = android.graphics.Color.parseColor("#FCBE6A")
        val highlightDrawable = android.graphics.drawable.GradientDrawable().apply {
            setColor(highlightColor)
            // Attempt to preserve the corner radius (using a default if we can't get it from the original)
            if (originalBackground is android.graphics.drawable.GradientDrawable) {
                cornerRadii = originalBackground.cornerRadii
            } else {
                cornerRadius = 8.toPx().toFloat() // Default corner radius
            }
        }

        // 3. Apply the temporary highlight color
        view.background = highlightDrawable

        // 4. Delay navigation and restore original color
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            // Restore the original background drawable
            view.background = android.content.ContextWrapper(view.context).baseContext.resources.getDrawable(originalBgResource, null)

            // Execute the navigation action
            navigationAction()
        }, 100) // 100ms delay for visual feedback
    }
}
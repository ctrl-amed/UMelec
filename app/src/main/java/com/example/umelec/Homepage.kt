package com.example.umelec

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton // Needed for the Vote Now button
import androidx.constraintlayout.widget.ConstraintLayout // Required for the ConstraintLayout ID


// Define the possible states for the election card UI
enum class ElectionState {
    ONGOING,
    NO_ELECTION,
    UPCOMING,
    ENDED
}

// Data class to easily handle election details for the ONGOING phase
data class ElectionDetails(val title: String, val period: String, val status: String)

// Define a data class to represent a notification item
data class NotificationItem(val title: String, val isRead: Boolean)


// Data class to represent a single candidate's information
data class Candidate(
    val name: String,
    val position: String,
    val photoResource: Int // Resource ID for the drawable/image (e.g., R.drawable.profile_pic)
)

// Data class to represent a candidate's result (including vote count)
data class ResultCandidate(
    val name: String,
    val position: String,
    val photoResource: Int, // Resource ID for the drawable/image
    val votes: Int // New field for the results card
)

// The main activity for the Homepage screen
class Homepage : AppCompatActivity() {

    // A list to hold your notification data. In a real app, this would come from a backend.
    private val notifications = mutableListOf(
        NotificationItem("New message from John Doe", false),
        NotificationItem("Your post was liked by 5 people", true),
        NotificationItem("System update available", false)
    )

    // The current state of the notification dropdown
    private var isNotificationDropdownVisible = false

    // Shared width variable for candidate and result preview items
    private var candidateItemWidth = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content view to the layout defined in res/layout/activity_homepage.xml
        setContentView(R.layout.activity_homepage)

        // Initialize UI components and set up listeners
        setupUI()

        // --- NEW ELECTION INITIALIZATION ---
        // Determine the current election status from the backend/database
        val currentElectionState = determineElectionState()
        // Update the UI based on the state
        updateElectionUI(currentElectionState)
        // -----------------------------------

        // --- NEW FOOTER NAVIGATION SETUP ---
        setupFooterNavigation() // <--- ADD THIS LINE
        // -----------------------------------
    }

    private fun setupUI() {
        // Find the views defined in your XML layout
        val nameTextView: TextView = findViewById(R.id.NameTitle)
        val profileIcon: ImageView = findViewById(R.id.profileIcon)
        val notificationIcon: ImageView = findViewById(R.id.notificationIcon)

        // --- Dynamic Greeting Implementation ---

        // **IMPORTANT:** This is where you would fetch the user's name
        // from your backend or local database (e.g., using SharedPreferences).
        // For demonstration, we'll use a hardcoded name.
        val userName = "Alice" // Replace with actual backend call
        nameTextView.text = userName

        // --- Profile Icon Click Listener ---

        // Make the profile icon clickable to navigate to the Profile screen.
        profileIcon.setOnClickListener {
            // Create an Intent to start the ProfileActivity class (assuming it's named Profile.kt)
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        // --- Notification Icon Click Listener (Toggle Dropdown) ---

        // The notification icon toggles the visibility of the notification dropdown.
        notificationIcon.setOnClickListener {
            toggleNotificationDropdown(notificationIcon)
        }
    }

    // --- ELECTION LOGIC START ---

    // Placeholder function to simulate fetching the election state and data
    private fun determineElectionState(): ElectionState {
        // **IMPORTANT:** Replace this with your actual backend call logic.
        // Use the desired state for testing:
        return ElectionState.ONGOING
        // return ElectionState.NO_ELECTION
        // return ElectionState.UPCOMING
        // return ElectionState.ENDED
    }

    /**
     * Hides all election-related dynamic views and resets them.
     */
    private fun resetElectionViews(
        ongoingLayout: LinearLayout,
        noElectionText: TextView,
        upcomingLayout: LinearLayout,
        electionEndedText: TextView,
        voteNowButton: AppCompatButton
    ) {
        ongoingLayout.visibility = View.GONE
        noElectionText.visibility = View.GONE
        upcomingLayout.visibility = View.GONE
        electionEndedText.visibility = View.GONE
        voteNowButton.isEnabled = false // Disable by default
        voteNowButton.alpha = 0.5f // Optional: Dim the button when disabled
    }

    /**
     * Updates the UI elements of the Election Information card based on the current state.
     * This function is designed to make it easy to manage data from the backend.
     */
    private fun updateElectionUI(state: ElectionState) {
        // Find all necessary views
        val ongoingLayout: LinearLayout = findViewById(R.id.OngoingLayout)
        val upcomingLayout: LinearLayout = findViewById(R.id.UpcomingLayout)
        val textNoElection: TextView = findViewById(R.id.textNoElection)
        val textElectionEnded: TextView = findViewById(R.id.textElectionEnded)
        val btnVoteNow: AppCompatButton = findViewById(R.id.btnVoteNow)

        // Candidate Preview Card Views
        val candidatesContainer: ConstraintLayout = findViewById(R.id.CandidateContainer)
        val textNoCandidates: TextView = findViewById(R.id.textNoCandidates)
        val textCandidatesEnded: TextView = findViewById(R.id.textCandidatesEnded)
        val btnViewAll: AppCompatButton = findViewById(R.id.btnViewAll)
        val candidateListContainer: LinearLayout = findViewById(R.id.candidateListContainer)

        // Result Preview Card Views
        val resultsContainer: ConstraintLayout = findViewById(R.id.ResultsContainer)
        val textLiveTallies: TextView = findViewById(R.id.textLiveTallies)
        val textNoResult: TextView = findViewById(R.id.textNoResult)
        val liveTallyLayout: LinearLayout = findViewById(R.id.LiveTallyLayout)
        val resultsLayout: LinearLayout = findViewById(R.id.ResultsLayout)
        val btnViewLiveTally: AppCompatButton = findViewById(R.id.btnViewLiveTally)
        val btnResults: AppCompatButton = findViewById(R.id.btnResults)
        val candidateListContainerResults: LinearLayout = findViewById(R.id.candidateListContainerResults)

        // Reset all views before setting the state-specific ones
        resetElectionViews(ongoingLayout, textNoElection, upcomingLayout, textElectionEnded, btnVoteNow)
        candidatesContainer.visibility = View.GONE
        textNoCandidates.visibility = View.GONE
        textCandidatesEnded.visibility = View.GONE
        btnViewAll.isEnabled = false
        btnViewAll.alpha = 0.5f
        resultsContainer.visibility = View.GONE
        textLiveTallies.visibility = View.GONE
        textNoResult.visibility = View.GONE
        liveTallyLayout.visibility = View.GONE
        resultsLayout.visibility = View.GONE
        btnViewLiveTally.isEnabled = false
        btnViewLiveTally.alpha = 0.5f
        btnResults.isEnabled = false
        btnResults.alpha = 0.5f


        // Views for ONGOING election data (for easy backend integration)
        val electionTitleValue: TextView = findViewById(R.id.electionTitleValue)
        val votingPeriodValue: TextView = findViewById(R.id.votingPeriodValue)
        val statusValue: TextView = findViewById(R.id.statusValue)

        // View for UPCOMING election date data
        val upcomingDateValue: TextView = findViewById(R.id.UpcomingDateValue)

        when (state) {
            ElectionState.ONGOING -> {
                // PHASE 1: ONGOING (Election Info Card)
                ongoingLayout.visibility = View.VISIBLE
                btnVoteNow.isEnabled = true
                btnVoteNow.alpha = 1.0f // Restore full opacity


                // **Backend Integration Point (ONGOING)**
                val electionData = fetchOngoingElectionData()
                electionTitleValue.text = electionData.title
                votingPeriodValue.text = electionData.period
                statusValue.text = electionData.status
                statusValue.setTextColor(Color.parseColor("#007F00")) // Green for Active

                // Set click listener for Vote Now button
                btnVoteNow.setOnClickListener {
                    // Assuming Vote.kt is Vote Activity
                    val intent = Intent(this, Vote::class.java)
                    startActivity(intent)
                }

                // PHASE 1 & 3: ONGOING and UPCOMING (Candidate Preview Card)
                candidatesContainer.visibility = View.VISIBLE
                // textCandidatesEnded and textNoCandidates are already hidden by reset

                btnViewAll.isEnabled = true
                btnViewAll.alpha = 1.0f

                // Populate and set up scrolling
                populateCandidateList(candidateListContainer, candidates)
                setupCandidateScrollControls()

                // Set View All button click listener
                btnViewAll.setOnClickListener {
                    // Assuming Candidates.kt is Candidates Activity
                    val intent = Intent(this, Candidates::class.java)
                    startActivity(intent)
                }

                // --- RESULT CARD LOGIC (PHASE 1: ONGOING) ---
                textLiveTallies.visibility = View.VISIBLE
                liveTallyLayout.visibility = View.VISIBLE
                btnViewLiveTally.isEnabled = true
                btnViewLiveTally.alpha = 1.0f

                btnViewLiveTally.setOnClickListener {
                    // Assuming Livetally.kt is Livetally Activity
                    val intent = Intent(this, Livetally::class.java)
                    startActivity(intent)
                }
            }
            ElectionState.NO_ELECTION -> {
                // PHASE 2: NO ELECTION (Election Info Card)
                textNoElection.visibility = View.VISIBLE
                // btnVoteNow remains disabled

                // PHASE 2: NO ELECTION (Candidate Preview Card)
                textNoCandidates.visibility = View.VISIBLE
                // btnViewAll remains disabled

                // --- RESULT CARD LOGIC (PHASE 2: NO ELECTION) ---
                textNoResult.visibility = View.VISIBLE
                resultsLayout.visibility = View.VISIBLE
                // btnResults is disabled by default
            }
            ElectionState.UPCOMING -> {
                // PHASE 3: UPCOMING (Election Info Card)
                upcomingLayout.visibility = View.VISIBLE

                // **Backend Integration Point (UPCOMING)**
                val upcomingDate = fetchUpcomingElectionDate()
                upcomingDateValue.text = upcomingDate
                // btnVoteNow remains disabled

                // PHASE 1 & 3: ONGOING and UPCOMING (Candidate Preview Card)
                candidatesContainer.visibility = View.VISIBLE
                // textCandidatesEnded and textNoCandidates are already hidden by reset

                btnViewAll.isEnabled = true
                btnViewAll.alpha = 1.0f

                // Populate and set up scrolling
                populateCandidateList(candidateListContainer, candidates)
                setupCandidateScrollControls()

                // Set View All button click listener
                btnViewAll.setOnClickListener {
                    // Assuming Candidates.kt is Candidates Activity
                    val intent = Intent(this, Candidates::class.java)
                    startActivity(intent)
                }

                // --- RESULT CARD LOGIC (PHASE 3: UPCOMING) ---
                textNoResult.visibility = View.VISIBLE
                resultsLayout.visibility = View.VISIBLE
                // btnResults is disabled by default
            }
            ElectionState.ENDED -> {
                // PHASE 4: ENDED (Election Info Card)
                // Show the "Voting opens on" layout as a container, then the "Election has ended" text
                upcomingLayout.visibility = View.VISIBLE
                textElectionEnded.visibility = View.VISIBLE
                // btnVoteNow remains disabled

                // PHASE 4: ENDED (Candidate Preview Card)
                textCandidatesEnded.visibility = View.VISIBLE
                // btnViewAll remains disabled

                // --- RESULT CARD LOGIC (PHASE 4: ENDED) ---
                resultsContainer.visibility = View.VISIBLE
                resultsLayout.visibility = View.VISIBLE
                btnResults.isEnabled = true
                btnResults.alpha = 1.0f

                // Populate and set up scrolling
                populateResultList(candidateListContainerResults, results)
                setupResultScrollControls()

                btnResults.setOnClickListener {
                    // Assuming Result.kt is Result Activity
                    val intent = Intent(this, Result::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    // --- Backend Data Simulation (Replace with actual backend calls) ---
    private fun fetchOngoingElectionData(): ElectionDetails {
        // **CODE IT FOR EASY BACKEND/DATABASE ACCESS**
        return ElectionDetails(
            title = "Student Council Leadership Election",
            period = "October 10 - 15, 2025",
            status = "Active"
        )
    }

    private fun fetchUpcomingElectionDate(): String {
        // **CODE IT FOR EASY BACKEND/DATABASE ACCESS**
        return "November 20, 2025"
    }

// --- ELECTION LOGIC END ---

    // --- CANDIDATE PREVIEW LOGIC START ---

    // The current candidate list (simulated data)
    private val candidates = listOf(
        Candidate("John Doe", "President", R.drawable.profile), // Use a placeholder drawable ID
        Candidate("Jane Smith", "VP", R.drawable.notification), // Use a placeholder drawable ID
        Candidate("Bob Johnson", "Secretary", R.drawable.profile),
        Candidate("Alice Williams", "Treasurer", R.drawable.notification),
        Candidate("Chris Lee", "Auditor", R.drawable.profile)
    )

    /**
     * Dynamically populates the HorizontalScrollView with candidate items.
     * @param container The LinearLayout inside the HorizontalScrollView.
     * @param candidates The list of candidates to display.
     */
    private fun populateCandidateList(container: LinearLayout, candidates: List<Candidate>) {
        // 1. Get the width of the main container (CandidateContainer) to calculate candidate item width
        val constraintLayout: ConstraintLayout = findViewById(R.id.CandidateContainer)
        constraintLayout.post {
            // Calculate the width for one candidate item (e.g., half the screen minus padding for arrows)
            val viewWidth = constraintLayout.width
            val arrowWidth = findViewById<ImageButton>(R.id.btnPrevCandidate).width +
                    findViewById<ImageButton>(R.id.btnNextCandidate).width +
                    (resources.getDimensionPixelSize(R.dimen.candidate_padding) * 2) // Add padding for safety

            // We want to show roughly two candidates at a time.
            candidateItemWidth = (viewWidth - arrowWidth) / 2

            // 2. Clear any existing views
            container.removeAllViews()

            // 3. Dynamically create and add a view for each candidate
            candidates.forEach { candidate ->
                val candidateItemView = createCandidateItemView(candidate)
                container.addView(candidateItemView)
            }
        }
    }

    /**
     * Creates a single candidate item view programmatically.
     */
    private fun createCandidateItemView(candidate: Candidate): View {
        // You must replace this with your actual candidate item XML if you have one.
        // For now, we recreate the structure from the XML dynamically.

        val context = this

        // Root LinearLayout for the candidate item
        val itemLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                candidateItemWidth.coerceAtLeast(200), // Ensures minimum width if calculations fail initially
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER_HORIZONTAL
            setPadding(8.toPx(), 8.toPx(), 8.toPx(), 8.toPx()) // Convert DP to pixels
        }

        // ImageView for the photo
        val photoView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(80.toPx(), 80.toPx())
            setImageResource(candidate.photoResource)
            contentDescription = "Candidate Photo"
            scaleType = ImageView.ScaleType.CENTER_CROP
            // Note: You need to handle image loading (e.g., rounded corners/image loading library) here
        }
        itemLayout.addView(photoView)

        // TextView for the Name
        val nameView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 4.toPx()
            }
            text = candidate.name
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor("#333333"))
            // Note: setting custom font programmatically is complex; relies on XML definition
        }
        itemLayout.addView(nameView)

        // TextView for the Position
        val positionView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = candidate.position
            textSize = 14f
            setTextColor(Color.parseColor("#333333"))
            // Note: setting custom font programmatically is complex; relies on XML definition
        }
        itemLayout.addView(positionView)

        return itemLayout
    }

    /**
     * Handles the click of the previous/next arrow buttons to scroll the candidate list.
     */
    private fun setupCandidateScrollControls() {
        val scrollView: HorizontalScrollView = findViewById(R.id.candidateScrollView)
        val btnPrev: ImageButton = findViewById(R.id.btnPrevCandidate)
        val btnNext: ImageButton = findViewById(R.id.btnNextCandidate)

        // Previous button logic: scroll left by the width of one candidate item
        btnPrev.setOnClickListener {
            scrollView.smoothScrollBy(-candidateItemWidth, 0)
        }

        // Next button logic: scroll right by the width of one candidate item
        btnNext.setOnClickListener {
            scrollView.smoothScrollBy(candidateItemWidth, 0)
        }

        // Initial check (hiding one button if list is short or at the start/end)
        scrollView.post {
            // You would typically monitor scroll position to hide/show buttons,
            // but for a fixed step scroll, enabling both is often simpler for a preview.
            // For now, we'll keep both visible unless the candidate list is very short.
        }
    }

    // Utility extension function to convert DP to pixels
    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()

// --- CANDIDATE PREVIEW LOGIC END ---

    // --- RESULT PREVIEW LOGIC START ---

    // The current result list (simulated data for ENDED phase)
    private val results = listOf(
        ResultCandidate("Alice Johnson", "President", R.drawable.profile, 520), // Winner
        ResultCandidate("Mark Chen", "VP", R.drawable.notification, 480),
        ResultCandidate("Maria Garcia", "Secretary", R.drawable.profile, 600),
        ResultCandidate("David Lee", "Treasurer", R.drawable.notification, 350)
    )

    /**
     * Dynamically populates the HorizontalScrollView with result items.
     */
    private fun populateResultList(container: LinearLayout, results: List<ResultCandidate>) {
        // 1. Get the width of the main container to calculate result item width (re-post to ensure it's measured)
        val constraintLayout: ConstraintLayout = findViewById(R.id.ResultsContainer)
        constraintLayout.post {
            val viewWidth = constraintLayout.width
            val arrowWidth = findViewById<ImageButton>(R.id.btnPrevCandidateResults).width +
                    findViewById<ImageButton>(R.id.btnNextCandidateResults).width +
                    (resources.getDimensionPixelSize(R.dimen.candidate_padding) * 2)

            // Ensure candidateItemWidth is calculated (it should be set by the Candidate Preview card, but good to ensure)
            if (candidateItemWidth == 0) {
                candidateItemWidth = (viewWidth - arrowWidth) / 2
            }

            // 2. Clear any existing views
            container.removeAllViews()

            // 3. Dynamically create and add a view for each result
            results.forEach { result ->
                val resultItemView = createResultItemView(result)
                container.addView(resultItemView)
            }
        }
    }

    /**
     * Creates a single result item view programmatically.
     */
    private fun createResultItemView(result: ResultCandidate): View {
        val context = this

        // Root LinearLayout for the result item (similar to candidate item)
        val itemLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                candidateItemWidth.coerceAtLeast(200),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER_HORIZONTAL
            setPadding(8.toPx(), 8.toPx(), 8.toPx(), 8.toPx())
        }

        // ImageView for the photo
        val photoView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(80.toPx(), 80.toPx())
            setImageResource(result.photoResource)
            contentDescription = "Candidate Photo"
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        itemLayout.addView(photoView)

        // TextView for the Name
        val nameView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 4.toPx() }
            text = result.name
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor("#333333"))
        }
        itemLayout.addView(nameView)

        // TextView for the Position
        val positionView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = result.position
            textSize = 14f
            setTextColor(Color.parseColor("#333333"))
        }
        itemLayout.addView(positionView)

        // TextView for the Votes (NEW ELEMENT)
        val votesView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = "${result.votes} Votes"
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor("#0039A6")) // Blue color for emphasis
        }
        itemLayout.addView(votesView)

        return itemLayout
    }

    /**
     * Handles the click of the previous/next arrow buttons for the results list.
     */
    private fun setupResultScrollControls() {
        val scrollView: HorizontalScrollView = findViewById(R.id.candidateScrollViewResults)
        val btnPrev: ImageButton = findViewById(R.id.btnPrevCandidateResults)
        val btnNext: ImageButton = findViewById(R.id.btnNextCandidateResults)

        // Previous button logic: scroll left by the width of one candidate item
        btnPrev.setOnClickListener {
            scrollView.smoothScrollBy(-candidateItemWidth, 0)
        }

        // Next button logic: scroll right by the width of one candidate item
        btnNext.setOnClickListener {
            scrollView.smoothScrollBy(candidateItemWidth, 0)
        }
    }

// --- RESULT PREVIEW LOGIC END ---

    // --- FOOTER NAVIGATION LOGIC START ---

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
            // Only start the activity if it's not the current one (to prevent unnecessary restarts)
            if (activityClass != this::class.java) {
                val intent = Intent(this, activityClass)
                startActivity(intent)
                // Optional: Add finish() if you don't want the user to return here via back button
                // finish()
            }
        }

        // Set Click Listeners

        // Home (Current Activity - No action needed unless reloading is desired)
        // We can keep this listener empty or make it re-initialize the current activity.
        navHome.setOnClickListener {
            // Since we are already on Homepage.kt, we typically do nothing or smooth scroll to top.
        }

        // Vote
        navVote.setOnClickListener {
            // Assuming Vote.kt is Vote Activity
            navigateTo(Vote::class.java)
        }

        // Candidates
        navCandidates.setOnClickListener {
            // Assuming Candidates.kt is Candidates Activity
            navigateTo(Candidates::class.java)
        }

        // Results
        navResults.setOnClickListener {
            // Assuming Results.kt is Results Activity
            navigateTo(Results::class.java)
        }

        // FAQs
        navFaq.setOnClickListener {
            // Assuming Faq.kt is Faq Activity
            navigateTo(Faq::class.java)
        }
    }

// --- FOOTER NAVIGATION LOGIC END ---

    /**
     * Toggles the visibility of the notification dropdown menu.
     * @param anchorView The view (the notification icon) to anchor the dropdown to.
     */
    private fun toggleNotificationDropdown(anchorView: ImageView) {
        if (isNotificationDropdownVisible) {
            // If visible, hide it (handled by the PopupWindow's dismiss on outside click,
            // but we'll use a local state to manage the icon color)
            // The logic below will handle creating/showing, so we just reset the icon color here
            anchorView.setColorFilter(Color.parseColor("#FAFCFE"))
            isNotificationDropdownVisible = false
        } else {
            // If hidden, show it
            showNotificationDropdown(anchorView)
            isNotificationDropdownVisible = true
        }
    }

    /**
     * Creates and displays the custom notification dropdown menu.
     * @param anchorView The view (the notification icon) to anchor the dropdown to.
     */
    private fun showNotificationDropdown(anchorView: ImageView) {
        // 1. Inflate the custom layout for the dropdown
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.notification_dropdown, null) // Assuming you'll create notification_dropdown.xml

        // 2. Create the PopupWindow
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // Allows the popup to be dismissed by clicking outside
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        // Set an onDismissListener to reset the notification icon color when the popup closes
        popupWindow.setOnDismissListener {
            anchorView.setColorFilter(Color.parseColor("#FAFCFE"))
            isNotificationDropdownVisible = false
        }

        // 3. Set the icon color to the clicked state (#FCBE6A)
        anchorView.setColorFilter(Color.parseColor("#FCBE6A"))

        // 4. Populate the dropdown content
        val notificationContainer: LinearLayout = popupView.findViewById(R.id.notificationListContainer)
        val noNotificationText: TextView = popupView.findViewById(R.id.noNotificationTextView)

        notificationContainer.removeAllViews() // Clear any existing views

        if (notifications.isEmpty()) {
            // Show "No notification here" if the list is empty
            noNotificationText.visibility = View.VISIBLE
        } else {
            noNotificationText.visibility = View.GONE
            // Dynamically add Notification Title TextViews
            notifications.take(3).forEach { item -> // Limit to show a few, then "View all"
                val tv = TextView(this).apply {
                    id = View.generateViewId() // Assign a unique ID programmatically
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        // Apply margin for spacing
                        setMargins(0, 8, 0, 8)
                    }
                    text = item.title
                    textSize = 16f
                    // Set color based on read status (FCBE6A if unread, or a default darker color)
                    setTextColor(Color.parseColor(if (item.isRead) "#333333" else "#FCBE6A"))
                    // Set up click listener for each notification title
                    setOnClickListener {
                        // **IMPORTANT:** You would update the notification's read status in the backend here.
                        // For now, we'll just navigate to the NotificationActivity.
                        val intent = Intent(this@Homepage, Notification::class.java)
                        intent.putExtra("NOTIFICATION_TITLE", item.title) // Pass data if needed
                        startActivity(intent)
                        popupWindow.dismiss() // Close the dropdown after clicking
                    }
                }
                notificationContainer.addView(tv)
            }
        }

        // 5. Set up the close (X) button
        val closeButton: ImageView = popupView.findViewById(R.id.closeDropdownButton)
        closeButton.setOnClickListener {
            popupWindow.dismiss()
        }

        // 6. Set up the "View all" button
        val viewAllButton: TextView = popupView.findViewById(R.id.viewAllButton)
        viewAllButton.setOnClickListener {
            val intent = Intent(this, Notification::class.java)
            startActivity(intent)
            popupWindow.dismiss()
        }

        // 7. Show the popup window
        // showAsDropDown positions the popup relative to the anchor view
        // Adding a negative x-offset can align it more to the right, near the icon
        popupWindow.showAsDropDown(anchorView, -300, 0)
    }
}
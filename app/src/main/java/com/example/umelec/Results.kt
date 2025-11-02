package com.example.umelec

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

// ⭐️ Data class for the leading candidate carousel content
data class LeadingCandidate(
    val position: String,
    val name: String,
    val votes: Int,
    val profileResId: Int // Resource ID for the drawable/image
)

// ⭐️ Enum to manage the state of the results card
enum class ResultCardState { ONGOING, NO_ELECTION, ENDED }

class Results : AppCompatActivity() {

    // 1. 💡 NEW: Declare the reusable NotificationManager
    private lateinit var notificationManager: NotificationManager

    // NOTE: Removed old local 'notifications' list and 'isNotificationDropdownVisible'

    // ⭐️ Simulated data for leading candidates
    private val leadingCandidates = listOf(
        LeadingCandidate(
            position = "Chairperson",
            name = "Mark Tan The Candidate With A Very Long Name", // Long name test
            votes = 2540,
            profileResId = R.drawable.profile // Use your placeholder image resource
        ),
        LeadingCandidate(
            position = "Treasurer",
            name = "Sarah Lee",
            votes = 1800,
            profileResId = R.drawable.profile // Use your placeholder image resource
        ),
        LeadingCandidate(
            position = "Public Relations Officer",
            name = "Alex Stone",
            votes = 1500,
            profileResId = R.drawable.profile // Use your placeholder image resource
        )
    )
    // ⭐️ Index tracks the current candidate being displayed
    private var currentCandidateIndex = 0

    // ⭐️ CHANGE THIS VALUE to test the different card messages:
    private var resultCardState = ResultCardState.ENDED


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // 2. 💡 NEW: Initialize the NotificationManager
        notificationManager = NotificationManager(this)

        setupHeaderIcons()
        setupFooterNavigation()

        setupAllResultsCards()
    }

    // ----------------------------------------------------------------------
    // --- RESULTS CARD LOGIC ---
    // ----------------------------------------------------------------------

    /**
     * **UPDATED:** Now inflates the reusable XML layout (leading_candidate_card.xml)
     * and sets the data on the child views.
     */
    private fun createLeadingCandidateView(candidate: LeadingCandidate): View {
        // Inflate the reusable layout for a single candidate card
        val cardView = LayoutInflater.from(this).inflate(
            R.layout.leading_candidate_card,
            null,
            false
        ) as LinearLayout

        // 1. Get references to the views
        // Note: ImageView is used here, but the underlying XML tag is CircleImageView
        val profilePic = cardView.findViewById<ImageView>(R.id.candidateProfilePicture)
        val nameText = cardView.findViewById<TextView>(R.id.candidateNameTextView)
        val positionText = cardView.findViewById<TextView>(R.id.candidatePositionTextView)
        val voteCountText = cardView.findViewById<TextView>(R.id.candidateVotesTextView)

        // 2. Set data
        profilePic.setImageResource(candidate.profileResId)
        nameText.text = candidate.name
        positionText.text = candidate.position

        val voteCountString = String.format("%,d Votes", candidate.votes)
        voteCountText.text = voteCountString

        return cardView
    }

    /**
     * Updates the candidate display area to show the single candidate at the current index.
     */
    private fun updateLeadingCandidateDisplay() {
        // ID for the container inside the HorizontalScrollView
        val container = findViewById<LinearLayout>(R.id.candidateDisplayArea)
        val btnPrev = findViewById<ImageButton>(R.id.btnPrevCandidate)
        val btnNext = findViewById<ImageButton>(R.id.btnNextCandidate)

        container?.removeAllViews()

        if (leadingCandidates.isEmpty()) return

        // Display the single candidate at the current index
        val candidate = leadingCandidates[currentCandidateIndex]
        container?.addView(createLeadingCandidateView(candidate))

        // Arrows are visible if there is more than one candidate
        val shouldShowArrows = leadingCandidates.size > 1
        btnPrev?.visibility = if (shouldShowArrows) View.VISIBLE else View.INVISIBLE
        btnNext?.visibility = if (shouldShowArrows) View.VISIBLE else View.INVISIBLE
    }

    /**
     * Manages the visibility of the messages or the carousel for the Leading Candidates Card.
     */
    private fun setupLeadingCandidatesCard() {
        val messageContainer = findViewById<LinearLayout>(R.id.leadingCandidatesMessageContainer)
        val noDataText = findViewById<TextView>(R.id.noElectionDataTextView)
        val endedText = findViewById<TextView>(R.id.electionEndedTextView)
        // CORRECTED ID: using leadingCandidatesCarouselContainer from your XML
        val controlsContainer = findViewById<LinearLayout>(R.id.leadingCandidatesCarouselContainer)
        val btnPrev = findViewById<ImageButton>(R.id.btnPrevCandidate)
        val btnNext = findViewById<ImageButton>(R.id.btnNextCandidate)

        when (resultCardState) {
            ResultCardState.NO_ELECTION, ResultCardState.ENDED -> {
                messageContainer?.visibility = View.VISIBLE
                noDataText?.visibility = if (resultCardState == ResultCardState.NO_ELECTION) View.VISIBLE else View.GONE
                endedText?.visibility = if (resultCardState == ResultCardState.ENDED) View.VISIBLE else View.GONE
                controlsContainer?.visibility = View.GONE
            }
            ResultCardState.ONGOING -> {
                if (leadingCandidates.isEmpty()) {
                    messageContainer?.visibility = View.VISIBLE
                    noDataText?.visibility = View.VISIBLE
                    endedText?.visibility = View.GONE
                    controlsContainer?.visibility = View.GONE
                    return
                }

                messageContainer?.visibility = View.GONE
                controlsContainer?.visibility = View.VISIBLE

                updateLeadingCandidateDisplay()

                // Re-implemented manual rotation logic
                btnPrev?.setOnClickListener {
                    currentCandidateIndex = (currentCandidateIndex - 1 + leadingCandidates.size) % leadingCandidates.size
                    updateLeadingCandidateDisplay()
                }

                btnNext?.setOnClickListener {
                    currentCandidateIndex = (currentCandidateIndex + 1) % leadingCandidates.size
                    updateLeadingCandidateDisplay()
                }
            }
        }
    }

    /**
     * Manages the Tallies Card and the Official Results Card based on the election state,
     * adding Intent navigation for the buttons.
     */
    private fun updateTalliesAndOfficialResultsCards() {
        // TALLIES CARD ELEMENTS: Use AppCompatButton
        val talliesTitle = findViewById<TextView>(R.id.talliesCardTitle)
        val talliesMessage = findViewById<TextView>(R.id.talliesCardMessage)
        val talliesButton = findViewById<AppCompatButton>(R.id.btnViewTallies)

        // OFFICIAL RESULTS CARD ELEMENTS: Use AppCompatButton
        val officialMessage = findViewById<TextView>(R.id.officialResultsCardMessage)
        val officialButton = findViewById<AppCompatButton>(R.id.btnViewOfficialResult)

        when (resultCardState) {
            ResultCardState.NO_ELECTION -> {
                // TALLIES CARD
                talliesTitle?.text = "Tallies"
                talliesMessage?.text = "No election data available."
                talliesButton?.visibility = View.GONE

                // OFFICIAL RESULTS CARD
                officialMessage?.text = "No Election data available."
                officialButton?.isEnabled = false
            }
            ResultCardState.ONGOING -> {
                // TALLIES CARD
                talliesTitle?.text = "Live Tallies"
                talliesMessage?.text = "Live tallies are available."
                talliesButton?.text = "View Live Tally"
                talliesButton?.visibility = View.VISIBLE
                talliesButton?.isEnabled = true

                // OFFICIAL RESULTS CARD
                officialMessage?.text = "Election is still ongoing."
                officialButton?.text = "View Result"
                officialButton?.isEnabled = false
            }
            ResultCardState.ENDED -> {
                // TALLIES CARD
                talliesTitle?.text = "Final Tallies"
                talliesMessage?.text = "Live tallies have been finalized."
                talliesButton?.text = "View Final Tallies"
                talliesButton?.visibility = View.VISIBLE
                talliesButton?.isEnabled = true

                // OFFICIAL RESULTS CARD
                officialMessage?.text = "The official and verified results are now available."
                officialButton?.text = "View Result"
                officialButton?.isEnabled = true
            }
        }

        // Set up button click listeners for navigation
        talliesButton?.setOnClickListener {
            // Ensure you have a Tallies.kt activity defined
            val intent = Intent(this, Tallies::class.java)
            startActivity(intent)
        }
        officialButton?.setOnClickListener {
            // Ensure you have an OfficialResults.kt activity defined
            val intent = Intent(this, OfficialResults::class.java)
            startActivity(intent)
        }
    }

    /**
     * Initializes all result cards by delegating setup based on the current state.
     */
    private fun setupAllResultsCards() {
        setupLeadingCandidatesCard()
        updateTalliesAndOfficialResultsCards()
    }

    // ----------------------------------------------------------------------
    // --- EXISTING LOGIC (HEADER, FOOTER) ---
    // ----------------------------------------------------------------------

    /**
     * Initializes header elements: Profile Icon and Notification Icon.
     */
    private fun setupHeaderIcons() {
        val profileIcon: ImageView? = findViewById(R.id.profileIcon)
        val notificationIcon: ImageView? = findViewById(R.id.notificationIcon)

        profileIcon?.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        // 3. 💡 NEW: Notification Icon Click Listener (Uses the utility class)
        notificationIcon?.setOnClickListener {
            if (it is ImageView) {
                // Call the reusable NotificationManager to toggle the dropdown
                notificationManager.toggleNotificationDropdown(it)
            }
        }
    }

    /**
     * Sets up click listeners for all elements in the footer navigation bar.
     */
    private fun setupFooterNavigation() {
        val navHome: LinearLayout? = findViewById(R.id.nav_home)
        val navVote: LinearLayout? = findViewById(R.id.nav_vote)
        val navCandidates: LinearLayout? = findViewById(R.id.nav_candidates)
        val navResults: LinearLayout? = findViewById(R.id.nav_results)
        val navFaq: LinearLayout? = findViewById(R.id.nav_faq)

        val navigateTo = { activityClass: Class<*> ->
            if (activityClass != this::class.java) {
                val intent = Intent(this, activityClass)
                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(intent)
            }
        }

        navHome?.setOnClickListener { navigateTo(Homepage::class.java) }
        navVote?.setOnClickListener { navigateTo(Vote::class.java) }
        navCandidates?.setOnClickListener { navigateTo(Candidates::class.java) }
        navResults?.setOnClickListener { /* Already here, do nothing */ }
        navFaq?.setOnClickListener { navigateTo(Faq::class.java) }
    }

    // NOTE: The old local notification methods (toggleNotificationDropdown and showNotificationDropdown)
    // have been successfully removed as their functionality is now handled by the imported NotificationManager.

    // NOTE: The toPx extension function is no longer needed in Results.kt
    // because the removed notification methods were the only ones using it.
}
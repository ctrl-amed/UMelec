package com.example.umelec

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


// Define the possible states for the election card UI
enum class ElectionState {
    ONGOING,
    NO_ELECTION,
    UPCOMING,
    ENDED
}

// Data class to easily handle election details for the ONGOING phase
data class ElectionDetails(val title: String, val period: String, val status: String)

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

    // 1. 💡 NEW: Declare the reusable NotificationManager
    private lateinit var notificationManager: NotificationManager

    // NOTE: Removed old local 'notifications' list and 'isNotificationDropdownVisible'

    // Shared width variable for candidate and result preview items
    private var candidateItemWidth = 0

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // 2. 💡 NEW: Initialize the NotificationManager
        notificationManager = NotificationManager(this)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize UI components and set up listeners
        setupUI()

        // --- NEW ELECTION INITIALIZATION ---
        val currentElectionState = determineElectionState()
        updateElectionUI(currentElectionState)
        // -----------------------------------

        // --- NEW FOOTER NAVIGATION SETUP ---
        setupFooterNavigation()
        // -----------------------------------
    }

    private fun setupUI() {
        val nameTextView: TextView = findViewById(R.id.NameTitle)
        val profileIcon: ImageView = findViewById(R.id.profileIcon)
        val notificationIcon: ImageView = findViewById(R.id.notificationIcon)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid

            db.collection("students").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val firstname = document.getString("firstname") ?: ""
                        nameTextView.text = firstname
                    } else {
                        nameTextView.text = "Unknown"
                    }
                }
                .addOnFailureListener {
                    nameTextView.text = "Error"
                }
        } else {
            nameTextView.text = "No user"
        }


        profileIcon.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        notificationIcon.setOnClickListener {
            notificationManager.toggleNotificationDropdown(it as ImageView)
        }
    }

    private fun determineElectionState(): ElectionState {
        return ElectionState.ONGOING
    }

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

        voteNowButton.isEnabled = false
        voteNowButton.alpha = 0.5f
    }

    private fun updateElectionUI(state: ElectionState) {
        val ongoingLayout: LinearLayout = findViewById(R.id.OngoingLayout)
        val upcomingLayout: LinearLayout = findViewById(R.id.UpcomingLayout)
        val textNoElection: TextView = findViewById(R.id.textNoElection)
        val textElectionEnded: TextView = findViewById(R.id.textElectionEnded)
        val btnVoteNow: AppCompatButton = findViewById(R.id.btnVoteNow)

        val candidatesContainer: ConstraintLayout = findViewById(R.id.CandidateContainer)
        val textNoCandidates: TextView = findViewById(R.id.textNoCandidates)
        val textCandidatesEnded: TextView = findViewById(R.id.textCandidatesEnded)
        val btnViewAll: AppCompatButton = findViewById(R.id.btnViewAll)
        val candidateListContainer: LinearLayout = findViewById(R.id.candidateListContainer)

        val resultsContainer: ConstraintLayout = findViewById(R.id.ResultsContainer)
        val textLiveTallies: TextView = findViewById(R.id.textLiveTallies)
        val textNoResult: TextView = findViewById(R.id.textNoResult)
        val liveTallyLayout: LinearLayout = findViewById(R.id.LiveTallyLayout)
        val resultsLayout: LinearLayout = findViewById(R.id.ResultsLayout)
        val btnViewLiveTally: AppCompatButton = findViewById(R.id.btnViewLiveTally)
        val btnResults: AppCompatButton = findViewById(R.id.btnResults)
        val candidateListContainerResults: LinearLayout = findViewById(R.id.candidateListContainerResults)

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

        val electionTitleValue: TextView = findViewById(R.id.electionTitleValue)
        val votingPeriodValue: TextView = findViewById(R.id.votingPeriodValue)
        val statusValue: TextView = findViewById(R.id.statusValue)
        val upcomingDateValue: TextView = findViewById(R.id.UpcomingDateValue)

        when (state) {
            ElectionState.ONGOING -> {
                ongoingLayout.visibility = View.VISIBLE
                btnVoteNow.isEnabled = true
                btnVoteNow.alpha = 1.0f

                val electionData = fetchOngoingElectionData()
                electionTitleValue.text = electionData.title
                votingPeriodValue.text = electionData.period
                statusValue.text = electionData.status
                statusValue.setTextColor(Color.parseColor("#007F00"))

                btnVoteNow.setOnClickListener {
                    val intent = Intent(this, Vote::class.java)
                    startActivity(intent)
                }

                candidatesContainer.visibility = View.VISIBLE
                btnViewAll.isEnabled = true
                btnViewAll.alpha = 1.0f
                populateCandidateList(candidateListContainer, candidates)
                setupCandidateScrollControls()

                btnViewAll.setOnClickListener {
                    val intent = Intent(this, Candidates::class.java)
                    startActivity(intent)
                }

                textLiveTallies.visibility = View.VISIBLE
                liveTallyLayout.visibility = View.VISIBLE
                btnViewLiveTally.isEnabled = true
                btnViewLiveTally.alpha = 1.0f

                btnViewLiveTally.setOnClickListener {
                    val intent = Intent(this, Results::class.java)
                    startActivity(intent)
                }
            }
            ElectionState.NO_ELECTION -> {
                textNoElection.visibility = View.VISIBLE
                textNoCandidates.visibility = View.VISIBLE
                textNoResult.visibility = View.VISIBLE
                resultsLayout.visibility = View.VISIBLE
            }
            ElectionState.UPCOMING -> {
                upcomingLayout.visibility = View.VISIBLE
                val upcomingDate = fetchUpcomingElectionDate()
                upcomingDateValue.text = upcomingDate

                candidatesContainer.visibility = View.VISIBLE
                btnViewAll.isEnabled = true
                btnViewAll.alpha = 1.0f
                populateCandidateList(candidateListContainer, candidates)
                setupCandidateScrollControls()

                btnViewAll.setOnClickListener {
                    val intent = Intent(this, Candidates::class.java)
                    startActivity(intent)
                }

                textNoResult.visibility = View.VISIBLE
                resultsLayout.visibility = View.VISIBLE
            }
            ElectionState.ENDED -> {
                upcomingLayout.visibility = View.VISIBLE
                textElectionEnded.visibility = View.VISIBLE
                textCandidatesEnded.visibility = View.VISIBLE
                resultsContainer.visibility = View.VISIBLE
                resultsLayout.visibility = View.VISIBLE
                btnResults.isEnabled = true
                btnResults.alpha = 1.0f
                populateResultList(candidateListContainerResults, results)
                setupResultScrollControls()

                btnResults.setOnClickListener {
                    val intent = Intent(this, Result::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun fetchOngoingElectionData(): ElectionDetails {
        return ElectionDetails(
            title = "Student Council Leadership Election",
            period = "October 10 - 15, 2025",
            status = "Active"
        )
    }

    private fun fetchUpcomingElectionDate(): String {
        return "November 20, 2025"
    }

    private val candidates = listOf(
        Candidate("John Doe", "President", R.drawable.profile),
        Candidate("Jane Smith", "VP", R.drawable.notification),
        Candidate("Bob Johnson", "Secretary", R.drawable.profile),
        Candidate("Alice Williams", "Treasurer", R.drawable.notification),
        Candidate("Chris Lee", "Auditor", R.drawable.profile)
    )

    private fun populateCandidateList(container: LinearLayout, candidates: List<Candidate>) {
        val constraintLayout: ConstraintLayout = findViewById(R.id.CandidateContainer)
        constraintLayout.post {
            val viewWidth = constraintLayout.width
            val arrowWidth = findViewById<ImageButton>(R.id.btnPrevCandidate).width +
                    findViewById<ImageButton>(R.id.btnNextCandidate).width +
                    (resources.getDimensionPixelSize(R.dimen.candidate_padding) * 2)

            candidateItemWidth = (viewWidth - arrowWidth) / 2
            container.removeAllViews()
            candidates.forEach { candidate ->
                val candidateItemView = createCandidateItemView(candidate)
                container.addView(candidateItemView)
            }
        }
    }

    private fun createCandidateItemView(candidate: Candidate): View {
        val context = this
        val itemLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                candidateItemWidth.coerceAtLeast(200),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER_HORIZONTAL
            setPadding(8.toPx(), 8.toPx(), 8.toPx(), 8.toPx())
        }

        val photoView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(80.toPx(), 80.toPx())
            setImageResource(candidate.photoResource)
            contentDescription = "Candidate Photo"
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        itemLayout.addView(photoView)

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
        }
        itemLayout.addView(nameView)

        val positionView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = candidate.position
            textSize = 14f
            setTextColor(Color.parseColor("#333333"))
        }
        itemLayout.addView(positionView)

        return itemLayout
    }

    private fun setupCandidateScrollControls() {
        val scrollView: HorizontalScrollView = findViewById(R.id.candidateScrollView)
        val btnPrev: ImageButton = findViewById(R.id.btnPrevCandidate)
        val btnNext: ImageButton = findViewById(R.id.btnNextCandidate)

        btnPrev.setOnClickListener {
            scrollView.smoothScrollBy(-candidateItemWidth, 0)
        }

        btnNext.setOnClickListener {
            scrollView.smoothScrollBy(candidateItemWidth, 0)
        }
    }

    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()

    private val results = listOf(
        ResultCandidate("Alice Johnson", "President", R.drawable.profile, 520),
        ResultCandidate("Mark Chen", "VP", R.drawable.notification, 480),
        ResultCandidate("Maria Garcia", "Secretary", R.drawable.profile, 600),
        ResultCandidate("David Lee", "Treasurer", R.drawable.notification, 350)
    )

    private fun populateResultList(container: LinearLayout, results: List<ResultCandidate>) {
        val constraintLayout: ConstraintLayout = findViewById(R.id.ResultsContainer)
        constraintLayout.post {
            val viewWidth = constraintLayout.width
            val arrowWidth = findViewById<ImageButton>(R.id.btnPrevCandidateResults).width +
                    findViewById<ImageButton>(R.id.btnNextCandidateResults).width +
                    (resources.getDimensionPixelSize(R.dimen.candidate_padding) * 2)

            if (candidateItemWidth == 0) {
                candidateItemWidth = (viewWidth - arrowWidth) / 2
            }

            container.removeAllViews()
            results.forEach { result ->
                val resultItemView = createResultItemView(result)
                container.addView(resultItemView)
            }
        }
    }

    private fun createResultItemView(result: ResultCandidate): View {
        val context = this
        val itemLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                candidateItemWidth.coerceAtLeast(200),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER_HORIZONTAL
            setPadding(8.toPx(), 8.toPx(), 8.toPx(), 8.toPx())
        }

        val photoView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(80.toPx(), 80.toPx())
            setImageResource(result.photoResource)
            contentDescription = "Candidate Photo"
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        itemLayout.addView(photoView)

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

        val votesView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = "${result.votes} Votes"
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor("#0039A6"))
        }
        itemLayout.addView(votesView)

        return itemLayout
    }

    private fun setupResultScrollControls() {
        val scrollView: HorizontalScrollView = findViewById(R.id.candidateScrollViewResults)
        val btnPrev: ImageButton = findViewById(R.id.btnPrevCandidateResults)
        val btnNext: ImageButton = findViewById(R.id.btnNextCandidateResults)

        btnPrev.setOnClickListener {
            scrollView.smoothScrollBy(-candidateItemWidth, 0)
        }

        btnNext.setOnClickListener {
            scrollView.smoothScrollBy(candidateItemWidth, 0)
        }
    }

    private fun setupFooterNavigation() {
        val navHome: LinearLayout = findViewById(R.id.nav_home)
        val navVote: LinearLayout = findViewById(R.id.nav_vote)
        val navCandidates: LinearLayout = findViewById(R.id.nav_candidates)
        val navResults: LinearLayout = findViewById(R.id.nav_results)
        val navFaq: LinearLayout = findViewById(R.id.nav_faq)

        val navigateTo = { activityClass: Class<*> ->
            if (activityClass != this::class.java) {
                val intent = Intent(this, activityClass)
                startActivity(intent)
            }
        }

        navHome.setOnClickListener { }
        navVote.setOnClickListener { navigateTo(Vote::class.java) }
        navCandidates.setOnClickListener { navigateTo(Candidates::class.java) }
        navResults.setOnClickListener { navigateTo(Results::class.java) }
        navFaq.setOnClickListener { navigateTo(Faq::class.java) }
    }
}

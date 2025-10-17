package com.example.umelec

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ColorDrawable

// --- DATA STRUCTURE FOR CANDIDATES ---
data class CandidateItem(
    val candidateId: String,
    val name: String,
    val position: String,
    val courseInfo: String,
    val previewText: String,
    val profilePictureResource: Int // Use R.drawable.your_image
)

class Position : AppCompatActivity() {

    private lateinit var allCandidates: List<CandidateItem>
    private var currentPosition: String = "Position"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_position)

        currentPosition = intent.getStringExtra("POSITION_NAME") ?: "Candidates"

        // 1. Fetch the data once
        allCandidates = getCandidatesForPosition(currentPosition)

        // 2. Setup the header title to display the position
        setupHeaderTitle()

        // 3. Setup the back button functionality
        setupBackNavigation()

        // 4. Populate the candidate cards dynamically (FIXED)
        populateCandidates()

        // 5. Setup the compare button logic (CRASH-PROOF)
        setupCompareButton()

        // 6. Setup the persistent footer navigation
        setupFooterNavigation()
    }

    // ----------------------------------------------------------------------
    // --- DYNAMIC CARD POPULATION LOGIC (FIXED) ---
    // ----------------------------------------------------------------------

    /**
     * Fetches candidate data and dynamically creates cards, ensuring static elements are preserved.
     */
    private fun populateCandidates() {
        val registerContainer: LinearLayout? = findViewById(R.id.registerContainer)

        // Exit if the container cannot be found (crash-proof)
        if (registerContainer == null) return

        // 1. Find and temporarily detach the static CompareLayout before clearing
        val compareLayout: LinearLayout? = registerContainer.findViewById(R.id.CompareLayout)

        // Safely detach the CompareLayout from the registerContainer
        if (compareLayout != null) {
            (compareLayout.parent as? LinearLayout)?.removeView(compareLayout)
        }

        // 2. Clear out the dynamic content and any static card templates (like cardContainer)
        registerContainer.removeAllViews()

        // 3. Add all dynamic candidate cards
        allCandidates.forEach { candidate ->
            val cardView = createCandidateCardView(candidate, registerContainer)
            registerContainer.addView(cardView)
        }

        // 4. Re-add the CompareLayout at the bottom
        if (compareLayout != null) {
            registerContainer.addView(compareLayout)
        }
    }


    // ----------------------------------------------------------------------
    // --- COMPARE BUTTON LOGIC ---
    // ----------------------------------------------------------------------

    /**
     * Finds the Compare button and sets its click listener to show the bottom sheet.
     */
    private fun setupCompareButton() {
        // Use AppCompatButton? and safe call ?. to prevent crashes if btnCompare is missing
        val compareButton: AppCompatButton? = findViewById(R.id.btnCompare)

        // Only proceed if the button exists
        compareButton?.setOnClickListener {
            // Apply the color change animation and then show the dialog
            animateClickFeedback(
                view = it,
                originalBgResource = R.drawable.blue_rounded_button,
                navigationAction = {
                    showCompareBottomSheet()
                }
            )
        }
    }

    // ... (All other functions remain the same as the previous, crash-proof version)

    // ----------------------------------------------------------------------
    // --- DATA FETCHING (SIMULATED) ---
    // ----------------------------------------------------------------------

    private fun getCandidatesForPosition(positionName: String): List<CandidateItem> {
        val profileResId = R.drawable.profile // Use a valid resource ID
        return listOf(
            CandidateItem(
                candidateId = "JANE_D",
                name = "Jane Doe",
                position = positionName,
                courseInfo = "III - BCSAD",
                previewText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit...",
                profilePictureResource = profileResId
            ),
            CandidateItem(
                candidateId = "JOHN_S",
                name = "John Smith",
                position = positionName,
                courseInfo = "IV - BSIT",
                previewText = "Consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...",
                profilePictureResource = profileResId
            ),
            CandidateItem(
                candidateId = "SARAH_L",
                name = "Sarah Lee",
                position = positionName,
                courseInfo = "II - BSBA",
                previewText = "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua...",
                profilePictureResource = profileResId
            ),
            CandidateItem(
                candidateId = "MARK_T",
                name = "Mark Tan",
                position = positionName,
                courseInfo = "I - BSED",
                previewText = "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
                profilePictureResource = profileResId
            )
        )
    }

    // ----------------------------------------------------------------------
    // --- BOTTOM SHEET LOGIC ---
    // ----------------------------------------------------------------------

    private fun showCompareBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_compare, null)
        bottomSheetDialog.setContentView(view)

        val selectedCandidates = mutableListOf<CandidateItem>()
        val selectedViews = mutableListOf<View>()

        val compareButton: AppCompatButton = view.findViewById(R.id.btnCompareInSheet)
        val container: LinearLayout = view.findViewById(R.id.candidateSelectionContainer)
        val titleTextView: TextView = view.findViewById(R.id.sheetTitle)

        titleTextView.text = "Select candidates to compare"
        compareButton.isEnabled = false

        allCandidates.forEach { candidate ->
            val candidateView = createCompareCandidateItem(candidate)
            container.addView(candidateView)

            candidateView.setOnClickListener { v ->
                val isSelected = selectedCandidates.contains(candidate)

                if (isSelected) {
                    selectedCandidates.remove(candidate)
                    selectedViews.remove(v)
                    v.background = ContextCompat.getDrawable(this, R.drawable.compare_candidate_unselected_bg)
                } else if (selectedCandidates.size < 2) {
                    selectedCandidates.add(candidate)
                    selectedViews.add(v)
                    v.background = ColorDrawable(Color.parseColor("#FCBE6A"))
                } else {
                    Toast.makeText(this, "You can only select a maximum of two candidates.", Toast.LENGTH_SHORT).show()
                }

                compareButton.isEnabled = selectedCandidates.size == 2
            }
        }

        compareButton.setOnClickListener {
            if (selectedCandidates.size == 2) {
                val intent = Intent(this, Comparison::class.java).apply {
                    putExtra("CANDIDATE_ID_1", selectedCandidates[0].candidateId)
                    putExtra("CANDIDATE_ID_2", selectedCandidates[1].candidateId)
                }
                bottomSheetDialog.dismiss()
                startActivity(intent)
            }
        }

        bottomSheetDialog.show()
    }

    private fun createCompareCandidateItem(candidate: CandidateItem): View {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.compare_candidate_item, null)

        val nameTextView: TextView? = view.findViewById(R.id.tv_name)
        val positionTextView: TextView? = view.findViewById(R.id.tv_position)
        val profileImageView: ImageView? = view.findViewById(R.id.iv_profile_picture)

        nameTextView?.text = candidate.name
        positionTextView?.text = candidate.position
        profileImageView?.setImageResource(candidate.profilePictureResource)

        view.background = ContextCompat.getDrawable(this, R.drawable.compare_candidate_unselected_bg)

        (view.layoutParams as? LinearLayout.LayoutParams)?.let {
            it.bottomMargin = 8.toPx()
            view.layoutParams = it
        }

        return view
    }

    // ----------------------------------------------------------------------
    // --- REMAINING HELPER FUNCTIONS ---
    // ----------------------------------------------------------------------

    private fun setupHeaderTitle() {
        val nameTitle: TextView? = findViewById(R.id.NameTitle)
        nameTitle?.text = currentPosition
    }

    private fun setupBackNavigation() {
        val backButton: ImageButton? = findViewById(R.id.btnBack)
        backButton?.setOnClickListener {
            finish()
        }
    }

    private fun createCandidateCardView(candidate: CandidateItem, root: LinearLayout?): View {
        val inflater = LayoutInflater.from(this)
        val cardView = inflater.inflate(R.layout.candidate_card_item, root, false) // Assumes candidate_card_item.xml exists

        val profilePic: ImageView? = cardView.findViewById(R.id.iv_profile_picture)
        val nameText: TextView? = cardView.findViewById(R.id.tv_name)
        val positionText: TextView? = cardView.findViewById(R.id.tv_position)
        val courseText: TextView? = cardView.findViewById(R.id.tv_course_info)
        val previewText: TextView? = cardView.findViewById(R.id.tv_preview_text)
        val viewAllButton: Button? = cardView.findViewById(R.id.btnViewAll)

        nameText?.text = candidate.name
        positionText?.text = candidate.position
        courseText?.text = candidate.courseInfo
        previewText?.text = candidate.previewText
        profilePic?.setImageResource(candidate.profilePictureResource)

        viewAllButton?.setOnClickListener {
            animateClickFeedback(
                view = it,
                originalBgResource = R.drawable.blue_rounded_button,
                navigationAction = {
                    val intent = Intent(this, Platform::class.java).apply {
                        putExtra("CANDIDATE_ID", candidate.candidateId)
                    }
                    startActivity(intent)
                }
            )
        }

        return cardView
    }

    private fun setupFooterNavigation() {
        val navHome: LinearLayout? = findViewById(R.id.nav_home)
        val navVote: LinearLayout? = findViewById(R.id.nav_vote)
        val navCandidates: LinearLayout? = findViewById(R.id.nav_candidates)
        val navResults: LinearLayout? = findViewById(R.id.nav_results)
        val navFaq: LinearLayout? = findViewById(R.id.nav_faq)

        val navigateTo = { activityClass: Class<*> ->
            val intent = Intent(this, activityClass)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
        }

        navHome?.setOnClickListener { navigateTo(Homepage::class.java) }
        navVote?.setOnClickListener { navigateTo(Vote::class.java) }
        navCandidates?.setOnClickListener { navigateTo(Candidates::class.java) }
        navResults?.setOnClickListener { navigateTo(Results::class.java) }
        navFaq?.setOnClickListener { navigateTo(Faq::class.java) }
    }

    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()

    private fun animateClickFeedback(view: View, originalBgResource: Int, navigationAction: () -> Unit) {
        val originalBackground = view.background
        val highlightColor = Color.parseColor("#FCBE6A")

        val highlightDrawable = GradientDrawable().apply {
            setColor(highlightColor)
            if (originalBackground is GradientDrawable) {
                cornerRadii = originalBackground.cornerRadii
            } else {
                cornerRadius = 8.toPx().toFloat()
            }
        }

        view.background = highlightDrawable

        Handler(Looper.getMainLooper()).postDelayed({
            view.background = ContextCompat.getDrawable(view.context, originalBgResource)
            navigationAction()
        }, 100)
    }
}
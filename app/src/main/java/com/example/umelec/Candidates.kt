package com.example.umelec

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat

// NOTE: Ensure the 'NotificationItem' data class is defined in a separate,
// shared file (like SharedData.kt) in this package to avoid 'Redeclaration' error.

// --- DATA STRUCTURE FOR POSITIONS ---
// This is the model you would map your backend/database data to.
data class PositionItem(val positionName: String)

class Candidates : AppCompatActivity() {

    // A list to hold your notification data (Simulated data)
    private val notifications = mutableListOf(
        NotificationItem("New message from John Doe", false),
        NotificationItem("Your post was liked by 5 people", true),
        NotificationItem("System update available", false)
    )

    private var isNotificationDropdownVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidates)

        // Initialize header UI components and set up listeners
        setupUI()

        // 🆕 POPULATE POSITIONS DYNAMICALLY
        populatePositions()

        // Setup the persistent footer navigation
        setupFooterNavigation()
    }

    // ----------------------------------------------------------------------
    // --- DYNAMIC POSITION POPULATION LOGIC ---
    // ----------------------------------------------------------------------

    private fun populatePositions() {
        // 1. Get a reference to the main container
        val positionContainer: LinearLayout = findViewById(R.id.PositionContainer)

        // 2. Define your list of Positions (EASY TO REPLACE WITH BACKEND DATA)
        val positionList = listOf(
            PositionItem(positionName = "Chairperson"),
            PositionItem(positionName = "Vice Chairperson"),
            PositionItem(positionName = "Secretary"),
            PositionItem(positionName = "Treasurer"),

            // ⬇️ START: COMMENT OUT THE NEXT POSITION TO TEST SCROLLING ⬇️
            PositionItem(positionName = "Public Relations Officer")
            // ⬆️ END: COMMENT OUT THE ABOVE POSITION TO TEST SCROLLING ⬆️
        )

        // 3. Clear existing children if necessary (e.g., if you had a sample in XML)
        positionContainer.removeAllViews()

        // 4. Iterate and add the views
        positionList.forEach { position ->
            val positionView = createPositionButtonView(this, position.positionName)
            positionContainer.addView(positionView)
        }
    }

    /**
     * Programmatically creates a single position button (the equivalent of btnPosition).
     */
    /**
     * Programmatically creates a single position button (the equivalent of btnPosition).
     */
    private fun createPositionButtonView(context: Context, positionName: String): View {
        // 1. Main container (btnPosition equivalent - LinearLayout)
        val btnPosition = LinearLayout(context).apply {
            id = View.generateViewId() // Generate a unique ID
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 10.toPx() // Spacing between buttons
            }
            orientation = LinearLayout.VERTICAL
            setPadding(12.toPx(), 12.toPx(), 12.toPx(), 12.toPx())
            gravity = android.view.Gravity.CENTER

            // Set the background drawable (blue_rounded_button)
            background = context.resources.getDrawable(R.drawable.blue_rounded_button, null)
            isClickable = true
            isFocusable = true
        }

        // 2. Position TextView
        val positionText = TextView(context).apply {
            id = R.id.Positions
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = positionName
            setTextColor(Color.WHITE)
            textSize = 20f

            // ⭐️ FIX: Load and set the custom font
            try {
                val typeface = ResourcesCompat.getFont(context, R.font.montserrat_bold)
                setTypeface(typeface)
            } catch (e: Exception) {
                // Fallback to default bold if the font resource is missing
                setTypeface(null, android.graphics.Typeface.BOLD)
                // Log the error if necessary for debugging
            }
        }
        btnPosition.addView(positionText)

        // 3. Click Listener for the Position Button
        btnPosition.setOnClickListener {
            // Apply the color change animation and navigate
            animateAndNavigate(
                view = it,
                originalBgResource = R.drawable.blue_rounded_button,
                targetActivity = Position::class.java,
                // ⭐️ NEW: Pass the position name to the next activity
                extraKey = "POSITION_NAME",
                extraValue = positionName
            )
        }

        return btnPosition
    }


    // ----------------------------------------------------------------------
    // --- HEADER AND COMPARISON BUTTON LOGIC ---
    // ----------------------------------------------------------------------

    /**
     * Initializes header elements: Profile Icon, Notification Icon, and Compare button.
     */
    private fun setupUI() {
        // Find the views defined in your XML layout
        val profileIcon: ImageView = findViewById(R.id.profileIcon)
        val notificationIcon: ImageView = findViewById(R.id.notificationIcon)

        // 🆕 Find the Compare button
        val compareButton: Button = findViewById(R.id.btnCompare)


        // --- Profile Icon Click Listener ---
        profileIcon.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        // --- Notification Icon Click Listener (Toggle Dropdown) ---
        notificationIcon.setOnClickListener {
            toggleNotificationDropdown(notificationIcon)
        }

        // --- Compare Button Click Listener ---
        compareButton.setOnClickListener {
            // Apply the color change animation and navigate to Comparison.kt
            animateAndNavigate(
                view = it,
                originalBgResource = R.drawable.blue_rounded_button,
                targetActivity = Comparison::class.java

            )
        }
    }


    /**
     * Helper function to provide visual feedback and then navigate to a new activity.
     * Applies a temporary color change to FCBE6A before navigating.
     */
    private fun animateAndNavigate(view: View, originalBgResource: Int, targetActivity: Class<*>,
        // ⭐️ NEW: Optional Intent Extra parameters
        extraKey: String? = null,
        extraValue: String? = null) {
        // 1. Get the original background (assuming it's a GradientDrawable with corners)
        val originalBackground = view.background

        // 2. Create the temporary highlight background (#FCBE6A)
        val highlightColor = Color.parseColor("#FCBE6A")
        val highlightDrawable = GradientDrawable().apply {
            setColor(highlightColor)
            // Attempt to preserve the corner radius from the original drawable
            if (originalBackground is GradientDrawable) {
                cornerRadii = originalBackground.cornerRadii
            } else {
                cornerRadius = 8.toPx().toFloat() // Default corner radius if not found
            }
        }

        // 3. Apply the temporary highlight color
        view.background = highlightDrawable

        // 4. Delay navigation and restore original color
        Handler(Looper.getMainLooper()).postDelayed({
            // Restore the original background drawable
            view.background = originalBackground

            // Navigate to the target activity
            val intent = Intent(this, targetActivity)
            // ⭐️ NEW: Add the extra to the intent if provided
            if (extraKey != null && extraValue != null) {
                intent.putExtra(extraKey, extraValue)
            }
            startActivity(intent)
        }, 100) // 100ms delay for visual feedback
    }

    // ----------------------------------------------------------------------
    // --- FOOTER AND NOTIFICATION LOGIC (from previous turn) ---
    // ----------------------------------------------------------------------

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
        navCandidates.setOnClickListener { /* Already here, do nothing */ }
        navResults.setOnClickListener { navigateTo(Results::class.java) }
        navFaq.setOnClickListener { navigateTo(Faq::class.java) }
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
                        val intent = Intent(this@Candidates, Notification::class.java)
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
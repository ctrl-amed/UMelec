package com.example.umelec

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.constraintlayout.widget.ConstraintLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class Castvote2 : AppCompatActivity() {

    private lateinit var reviewVoteContainer: LinearLayout
    private var unsavedChanges = true // Set to true initially since the vote is not "confirmed" yet
    private lateinit var reviewedPositions: List<String>
    private lateinit var reviewedCandidates: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_castvote2)

        // 1. Initialize views
        reviewVoteContainer = findViewById(R.id.ReviewVoteContainer)
        val electionTitle: TextView = findViewById(R.id.ElectionTitle)

        // 2. Set static UI elements
        electionTitle.text = "UMak Student Council \nElections 2025"
        findViewById<TextView>(R.id.setupTitle).text = "Step 2 of 2"
        findViewById<View>(R.id.progressBarFill).layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT // Progress to 100%

        // 3. Get Data from Intent
        // These keys must match the ones used in Castvote.kt
        reviewedPositions = intent.getStringArrayListExtra("positions") ?: emptyList()
        reviewedCandidates = intent.getStringArrayListExtra("candidates") ?: emptyList()

        // 4. Inflate the Review List
        inflateReviewList()

        // 5. Setup Navigation and Confirmation
        setupBackNavigation()
        setupFooterNavigation()
        setupConfirmButton()
        setupBackToBallotButton()
        setupModernBackPressHandler()
    }

    // ----------------------------------------------------------------------
    // --- UI INFLATION LOGIC (Omitted for brevity, unchanged) ---
    // ----------------------------------------------------------------------

    private fun inflateReviewList() {
        val inflater = LayoutInflater.from(this)

        // CLEAR: Remove the template views inside the ReviewVoteContainer
        reviewVoteContainer.removeAllViews()

        // Loop through the data received from Castvote.kt
        for (i in reviewedPositions.indices) {
            val positionTitle = reviewedPositions[i]
            val candidateName = reviewedCandidates[i]

            // Inflate the ReviewlistContainer template (from activity_castvote2.xml)
            // Note: We use the ReviewlistContainer as the root of the single review item
            val reviewItemView = inflater.inflate(R.layout.review_list_item, reviewVoteContainer, false) as LinearLayout

            reviewItemView.findViewById<TextView>(R.id.ReviewPositionTitle).text = "$positionTitle:"

            val candidateTextView = reviewItemView.findViewById<TextView>(R.id.ReviewCandidateName)
            candidateTextView.text = candidateName

            // Optional: Set color for Abstain votes
            if (candidateName == "Abstain") {
                // IMPORTANT: Ensure you have R.color.error_red defined in your colors.xml
                candidateTextView.setTextColor(ContextCompat.getColor(this, R.color.error_red))
            } else {
                candidateTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
            }

            reviewVoteContainer.addView(reviewItemView)
        }
    }


    // ----------------------------------------------------------------------
    // --- DIALOG STYLING HELPER ---
    // ----------------------------------------------------------------------

    /**
     * Creates an AlertDialog with transparent background, centered gravity, and custom touch outside behavior.
     * This integrates the design from Login.kt.
     */
    private fun createStyledAlertDialog(dialogView: View, isCancellable: Boolean = true): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        // Apply custom styling for a cleaner look
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER)
        // Set touch outside behavior
        dialog.setCanceledOnTouchOutside(isCancellable)

        return dialog
    }

    // ----------------------------------------------------------------------
    // --- CONFIRMATION AND NAVIGATION LOGIC ---
    // ----------------------------------------------------------------------

    private fun setupConfirmButton() {
        findViewById<AppCompatButton>(R.id.btnConfirm).setOnClickListener {
            showFinalConfirmationDialog()
        }
    }

    private fun setupBackToBallotButton() {
        findViewById<AppCompatButton>(R.id.btnBacktoBallot).setOnClickListener {
            // Treat going back as leaving with unsaved changes, even if we know the votes
            handleExit(Castvote::class.java)
        }
    }


    /**
     * Displays the FINAL CONFIRMATION dialog before submitting the vote.
     */
    private fun showFinalConfirmationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_toast_question, null)
        // Use the styled helper. This dialog can be dismissed by touching outside (default behavior).
        val alertDialog = createStyledAlertDialog(dialogView)

        dialogView.findViewById<TextView>(R.id.toast_title).text = "Submit your vote?"
        dialogView.findViewById<TextView>(R.id.toast_value).text = "This action cannot be undone."
        dialogView.findViewById<AppCompatButton>(R.id.btn_action_primary).text = "Cancel"
        dialogView.findViewById<AppCompatButton>(R.id.btn_action_secondary).text = "Confirm"

        dialogView.findViewById<AppCompatButton>(R.id.btn_action_primary).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<AppCompatButton>(R.id.btn_action_secondary).setOnClickListener {
            alertDialog.dismiss()
            // Action revised: Show the recorded dialog with proof of submission
            showRecordedDialog()
        }

        alertDialog.show()
    }


    /**
     * Displays the VOTE RECORDED receipt dialog using custom_toast_recorded.xml.
     */
    private fun showRecordedDialog() {
        // Vote is recorded, set this flag to prevent "Unsaved Changes" on exit
        unsavedChanges = false

        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_toast_recorded, null)
        // Use the styled helper. This dialog CANNOT be dismissed by touching outside.
        val alertDialog = createStyledAlertDialog(dialogView, isCancellable = false)

        // --- MOCK DATA GENERATION ---
        // TODO: Replace this mock logic with actual data fetching from the backend after submission.
        val refCode = "ELEC-2025-" + (1000000 + (Math.random() * 9000000).toInt())
        val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        val refDate = dateFormat.format(Date())
        val refSignature = "0x" + (1..16).map { Integer.toHexString((Math.random() * 256).toInt()).padStart(2, '0') }.joinToString("").take(8) + "..."

        // --- Set Mock Data ---
        dialogView.findViewById<TextView>(R.id.ReferenceCode).text = refCode
        dialogView.findViewById<TextView>(R.id.ReferenceDate).text = refDate
        dialogView.findViewById<TextView>(R.id.ReferenceSignature).text = refSignature

        // --- Button Handlers ---

        // Download PDF Button (btn_action_primary)
        dialogView.findViewById<AppCompatButton>(R.id.btn_action_primary).setOnClickListener {
            // TODO: Implement PDF generation/download logic here
            Toast.makeText(this, "Your PDF receipt has been downloaded.", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()

            // Navigate away after final action
            navigateTo(Homepage::class.java, isFinalExit = true)
        }

        // Send to Email Button (btn_action_secondary)
        dialogView.findViewById<AppCompatButton>(R.id.btn_action_secondary).setOnClickListener {
            // TODO: Implement email sending logic here
            alertDialog.dismiss() // Dismiss the recorded dialog
            showSuccessToastDialog() // Show the success dialog

            // Navigation is handled inside showSuccessToastDialog
        }

        alertDialog.show()
    }


    /**
     * Displays a SUCCESS dialog when the user chooses to email the receipt.
     */
    private fun showSuccessToastDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_toast_success, null)
        // Use the styled helper. This final dialog should be dismissable by touching outside.
        val alertDialog = createStyledAlertDialog(dialogView)

        // --- Custom Text Revisions ---
        dialogView.findViewById<TextView>(R.id.toast_title).text = "Sent Successfully"
        dialogView.findViewById<TextView>(R.id.toast_value).text = "Check your email."

        // Make sure btn_action is visible, and set its text to "Ok"
        val btnAction = dialogView.findViewById<AppCompatButton>(R.id.btn_action)
        btnAction.visibility = View.VISIBLE
        btnAction.text = "Ok"

        // Setup dismiss and final navigation
        btnAction.setOnClickListener {
            alertDialog.dismiss()
            // Final exit: navigate to Homepage and clear all voting activities
            navigateTo(Homepage::class.java, isFinalExit = true)
        }

        // Set background color for the strip (if needed, though it's set in XML via backgroundTint)
        // dialogView.findViewById<View>(R.id.color_strip).setBackgroundColor(Color.parseColor("#27A688"))

        alertDialog.show()
    }

    /**
     * Triggers the Unsaved Changes warning dialog if the vote is not confirmed, otherwise proceeds.
     */
    private fun handleExit(targetActivity: Class<*>? = null) {
        if (unsavedChanges) {
            showUnsavedChangesDialog(targetActivity)
        } else if (targetActivity != null) {
            navigateTo(targetActivity)
        } else {
            finish()
        }
    }

    /**
     * Displays the WARNING dialog when leaving the review screen before final confirmation.
     */
    private fun showUnsavedChangesDialog(targetActivity: Class<*>? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_toast_warning, null)
        // Use the styled helper. This dialog can be dismissed by touching outside.
        val alertDialog = createStyledAlertDialog(dialogView)

        dialogView.findViewById<TextView>(R.id.toast_title).text = "Vote Unsaved"
        dialogView.findViewById<TextView>(R.id.toast_value).text = "Are you sure you want to leave?"
        dialogView.findViewById<AppCompatButton>(R.id.btn_action_primary).text = "Leave"
        dialogView.findViewById<AppCompatButton>(R.id.btn_action_secondary).text = "Stay"

        dialogView.findViewById<View>(R.id.color_strip).setBackgroundColor(Color.parseColor("#F5A304"))

        dialogView.findViewById<AppCompatButton>(R.id.btn_action_primary).setOnClickListener {
            // Leave / Proceed with navigation or close
            unsavedChanges = false
            alertDialog.dismiss()
            if (targetActivity != null) {
                navigateTo(targetActivity)
            } else {
                finish()
            }
        }

        dialogView.findViewById<AppCompatButton>(R.id.btn_action_secondary).setOnClickListener {
            // Stay / Cancel
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun navigateTo(activityClass: Class<*>, isFinalExit: Boolean = false) {
        val intent = Intent(this, activityClass)

        if (isFinalExit) {
            // Use flags to clear the activity stack and go to the root activity (Homepage)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            // Default navigation behavior
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
        }
    }


    // --- STANDARD NAVIGATION HANDLERS ---
    private fun setupBackNavigation() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            handleExit()
        }
    }

    private fun setupModernBackPressHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleExit()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun setupFooterNavigation() {
        val navItems = listOf(
            R.id.nav_home to Homepage::class.java,
            R.id.nav_vote to Vote::class.java,
            R.id.nav_candidates to Candidates::class.java,
            R.id.nav_results to Results::class.java,
            R.id.nav_faq to Faq::class.java
        )

        navItems.forEach { (id, target) ->
            findViewById<LinearLayout>(id).setOnClickListener {
                if (id == R.id.nav_vote) {
                    return@setOnClickListener // Already on the voting flow
                }
                handleExit(target)
            }
        }
    }
}

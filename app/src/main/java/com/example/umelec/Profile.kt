package com.example.umelec

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton // Required for the logout button

class Profile : AppCompatActivity() {

    // Declare all UI elements
    private lateinit var emailValue: TextView
    private lateinit var studentIdValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var collegeValue: TextView
    private lateinit var statusValue: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var btnLogout: AppCompatButton // ⭐️ Added Logout Button ⭐️

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // 1. Initialize all the UI elements by finding them by their ID
        initializeViews()

        // 2. Set up the back button and logout button listeners
        setupListeners()

        // 3. Populate the profile data
        populateProfileData()
    }

    private fun initializeViews() {
        // Find the TextViews for profile data
        emailValue = findViewById(R.id.EmailValue)
        studentIdValue = findViewById(R.id.StudentIDValue)
        yearValue = findViewById(R.id.YearValue)
        collegeValue = findViewById(R.id.CollegeValue)
        statusValue = findViewById(R.id.statusValue)

        // Find the ImageButton
        btnBack = findViewById(R.id.btnBack)

        // ⭐️ Initialize the Logout Button ⭐️
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun setupListeners() {
        // Set an OnClickListener for the back button
        btnBack.setOnClickListener {
            finish()
        }

        // ⭐️ Set an OnClickListener for the Logout button ⭐️
        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    /**
     * ⭐️ New Function: Displays the alert dialog for logout confirmation. ⭐️
     */
    private fun showLogoutConfirmationDialog() {
        // Use AlertDialog.Builder to create the dialog
        AlertDialog.Builder(this)
            .setTitle("Log Out Confirmation")
            .setMessage("Are you sure you want to log out of your account?")
            // Set the "Log Out" action
            .setPositiveButton("Log Out") { dialog, which ->
                // User clicked the Log Out button
                performLogout()
            }
            // Set the "Cancel" action
            .setNegativeButton("Cancel") { dialog, which ->
                // User clicked the Cancel button, just dismiss the dialog
                dialog.dismiss()
            }
            .setIcon(android.R.drawable.ic_dialog_alert) // Optional: Add a simple warning icon
            .show() // Display the dialog to the user
    }

    /**
     * ⭐️ New Function: Handles the actual logout process. ⭐️
     */
    private fun performLogout() {
        // 1. Clear user session/credentials (IMPORTANT for actual backend integration)
        //    * DB/BACKEND GUIDE: Implement logic here to clear local stored tokens,
        //      session data, or user preferences (e.g., using SharedPreferences or Jetpack DataStore).

        // 2. Navigate back to the main login/landing activity (assuming MainActivity.kt is your entry point)
        val intent = Intent(this, MainActivity::class.java)

        // Add flags to clear the activity stack so the user cannot press 'back'
        // and return to the logged-in profile screens.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)

        // Finish the current Profile activity
        finish()
    }


    /**
     * This function demonstrates how to set the text on your TextViews.
     */
    fun populateProfileData() {
        // Example data structure (you'd replace this with your actual data source)
        val userProfile = mapOf(
            "email" to "user.new@example.com",
            "studentId" to "S98765432",
            "year" to "4th year",
            "college" to "CITCS",
            "status" to "Ineligible"
        )

        // Set the text of each TextView using the retrieved data
        emailValue.text = userProfile["email"]
        studentIdValue.text = userProfile["studentId"]
        yearValue.text = userProfile["year"]
        collegeValue.text = userProfile["college"]
        statusValue.text = userProfile["status"]
    }
}
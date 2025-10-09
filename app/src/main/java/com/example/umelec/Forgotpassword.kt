package com.example.umelec

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.app.AlertDialog // <-- IMPORTANT: Add this import

class Forgotpassword : AppCompatActivity() {


    // Define the key for passing data between activities
    companion object {
        const val EXTRA_EMAIL_ADDRESS = "com.example.umelec.EMAIL_ADDRESS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)

        // =====================================================================
        // 🚨 1. CONSTANTS, VIEWS, AND HELPER FUNCTIONS (INITIALIZATION) 🚨
        // =====================================================================

        // Define color constants
        val COLOR_PRIMARY_BLUE = Color.parseColor("#0039A6") // Focus/Typing/Info
        val COLOR_ERROR_RED = Color.parseColor("#D32F2F")     // Error
        val COLOR_SUCCESS_GREEN = Color.parseColor("#4CAF50") // Valid
        val COLOR_HINT_GRAY = Color.parseColor("#8C8CA1")     // Default Hint

        // --- EMAIL VIEWS ---
        val layoutEmail = findViewById<TextInputLayout>(R.id.textInputLayoutEmail)
        val inputEmail = findViewById<TextInputEditText>(R.id.inputEmail)
        val emailRequirementsContainer = findViewById<View>(R.id.EmailRequirements)
        val reqEmail = findViewById<TextView>(R.id.reqEmail)

        // --- BUTTONS ---
        val btnSendVerification = findViewById<Button>(R.id.btnSendVerification)
        val btnBack = findViewById<ImageButton>(R.id.btnBack) // Assuming a back button ID

        // --- HELPER FUNCTIONS ---
        fun isFieldNotEmpty(text: String): Boolean {
            return text.trim().isNotEmpty()
        }

        fun isUmakEmail(email: String): Boolean {
            return email.trim().endsWith("@umak.edu.ph", ignoreCase = true)
        }

        // Function to update the Send Verification button's enabled/disabled state
        fun updateButtonState() {
            val emailText = inputEmail.text.toString()
            // Button is enabled only if the field is NOT empty
            btnSendVerification.isEnabled = isFieldNotEmpty(emailText)
        }

        // Function to show the error dialog without resetting data
        fun showInvalidEmailDialog() {
            AlertDialog.Builder(this)
                .setTitle("Invalid Email")
                .setMessage("The email must end with @umak.edu.ph.")
                .setPositiveButton("OK") { dialog, which ->
                    // Dialog is dismissed, user remains on the screen with data intact
                }
                .show()
        }

        // =====================================================================
        // 🚨 2. INITIAL SETUP & LISTENERS 🚨
        // =====================================================================

        // BACK BUTTON SETUP
        btnBack.setOnClickListener {
            finish()
        }

        // Initial state: Button is disabled
        btnSendVerification.isEnabled = false

        // ---------------------------------------------------------------------
        // 🔹 EMAIL FOCUS CHANGE HANDLING (Blur/Empty Check)
        // (Simplified to only check for empty/not empty)
        // ---------------------------------------------------------------------

        // 🔹 Hide helper text by default
        emailRequirementsContainer.visibility = View.GONE

        inputEmail.setOnFocusChangeListener { _, hasFocus ->
            val email = inputEmail.text.toString().trim()
            val isFilled = isFieldNotEmpty(email)

            if (hasFocus) {
                // --- WHEN FOCUSED ---
                emailRequirementsContainer.visibility = View.VISIBLE
                if (isFilled) {
                    // ✅ FILLED & FOCUSED: Show success state
                    reqEmail.setTextColor(COLOR_SUCCESS_GREEN)
                    reqEmail.text = "Input is ready"
                    layoutEmail.boxStrokeColor = COLOR_SUCCESS_GREEN
                } else {
                    // 🩶 EMPTY & FOCUSED: Show hint state
                    reqEmail.setTextColor(COLOR_HINT_GRAY)
                    reqEmail.text = "Field is required"
                    layoutEmail.boxStrokeColor = COLOR_PRIMARY_BLUE
                }
            } else {
                // --- WHEN UN-FOCUSED (BLUR) ---
                if (email.isEmpty()) {
                    // ❌ EMPTY on BLUR: Show required error
                    emailRequirementsContainer.visibility = View.VISIBLE
                    reqEmail.setTextColor(COLOR_ERROR_RED)
                    reqEmail.text = "Field is required"
                    layoutEmail.boxStrokeColor = COLOR_ERROR_RED
                } else {
                    // ✅ NOT EMPTY on BLUR: Hide helper text, KEEP GREEN BORDER
                    emailRequirementsContainer.visibility = View.GONE
                    layoutEmail.boxStrokeColor = COLOR_SUCCESS_GREEN
                }
            }
            updateButtonState()
        }

        // ---------------------------------------------------------------------
        // 🔹 EMAIL REAL-TIME VALIDATION (Typing/Input Check)
        // (Simplified to only check for empty/not empty, as requested)
        // ---------------------------------------------------------------------

        inputEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString().trim()

                when {
                    // ✅ NOT EMPTY (Valid for typing status)
                    isFieldNotEmpty(text) -> {
                        reqEmail.setTextColor(COLOR_SUCCESS_GREEN)
                        //reqEmail.text = "Input is ready"
                        layoutEmail.boxStrokeColor = COLOR_SUCCESS_GREEN
                        emailRequirementsContainer.visibility = View.VISIBLE
                    }

                    // 🩶 EMPTY (Default typing state)
                    else -> {
                        reqEmail.setTextColor(COLOR_HINT_GRAY)
                        reqEmail.text = "Field is required"
                        layoutEmail.boxStrokeColor = COLOR_PRIMARY_BLUE
                        emailRequirementsContainer.visibility = View.VISIBLE
                    }
                }
                updateButtonState()
            }
        })

        // =====================================================================
        // 🚨 3. SEND VERIFICATION BUTTON CLICK LISTENER 🚨
        // =====================================================================

        // --- UPDATED CLICK LISTENER ---
        btnSendVerification.setOnClickListener {
            val email = inputEmail.text.toString().trim()

            // ... your existing validation checks before sending ...

            if (isUmakEmail(email)) { // Assuming isUmakEmail is your check for @umak.edu.ph
                // SUCCESS: Go to Verification activity and PASS the email
                val intent = Intent(this, Verification::class.java).apply {
                    // Use the constant key and the email value
                    putExtra(EXTRA_EMAIL_ADDRESS, email)
                }
                startActivity(intent)
            } else {
                // Condition 2: Show error dialog and stay on screen
                showInvalidEmailDialog()
            }
        }
    }
}
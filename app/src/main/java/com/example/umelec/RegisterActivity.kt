package com.example.umelec

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.app.AlertDialog

class RegisterActivity : AppCompatActivity() {

    // =========================================================================
    // 1. CLASS-LEVEL PROPERTIES
    // =========================================================================
    private var allValidationsPassed = false
    // Retaining specialChars definition
    val specialChars = "!@#$%^&*-+=()_`~[]{}|\\:;\"'<,>.?/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // =====================================================================
        // 2. VIEW INITIALIZATION (FIND VIEW BY ID)
        // =====================================================================

        // 🔹 Buttons and Navigation
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnNext = findViewById<Button>(R.id.btnNext)

        // 🔹 Email Fields
        val inputEmail = findViewById<TextInputEditText>(R.id.inputEmail)
        val EmailRequirements = findViewById<View>(R.id.EmailRequirements)
        val reqEmail = findViewById<TextView>(R.id.reqEmail)
        // Note: textInputLayoutEmail (layoutEmail) is not found, but is used below.

        // 🔹 Password Fields
        val layoutPassword = findViewById<TextInputLayout>(R.id.textInputLayoutPassword)
        val inputPassword = findViewById<TextInputEditText>(R.id.inputPassword)
        val passwordRequirements = findViewById<View>(R.id.passwordRequirements)

        // 🔹 Confirm Password Fields
        val layoutConfirmPassword = findViewById<TextInputLayout>(R.id.textInputLayoutConfirmPassword)
        val inputConfirmPassword = findViewById<TextInputEditText>(R.id.inputConfirmPassword)
        val ConfirmpasswordRequirements = findViewById<View>(R.id.ConfirmpasswordRequirements)

        // 🔹 Password Requirement Texts
        val reqLength = findViewById<TextView>(R.id.reqLength)

        // 🚨 UPDATED VIEWS: Using new IDs from your XML
        val reqMixedcase = findViewById<TextView>(R.id.reqMixedcase)
        val reqSpecial = findViewById<TextView>(R.id.reqSpecial)
        val reqNumber = findViewById<TextView>(R.id.reqNumber)
        // Note: reqUppercase is no longer used, replaced by reqMixedcase

        val reqMatch = findViewById<TextView>(R.id.reqMatch)

        // 🔹 Redundant/Duplicated variable declaration (kept for non-modification constraint)
        val specialChars = "!@#$%^&*-+=()_`~[]{}|\\:;\"'<,>.?/"

        // =====================================================================
        // 3. CONFIRM PASSWORD WATCHER DECLARATION (FIXED UNRESOLVED REFERENCE)
        // =====================================================================
        // 🚨 FIX 1: Declaring the variable here so it can be referenced in passwordWatcher
        // later in the file, resolving the "Unresolved reference" error.
        lateinit var confirmPasswordWatcher: TextWatcher

        // =====================================================================
        // 4. INITIAL SETUP AND HELPER FUNCTION (Previously Section 3)
        // =====================================================================

        // Initial State Setup
        btnNext.isEnabled = false
        passwordRequirements.visibility = View.GONE
        ConfirmpasswordRequirements.visibility = View.GONE
        EmailRequirements.visibility = View.GONE

        // Function to show the "Email already registered" dialog
        fun showEmailAlreadyRegisteredDialog() {
            // Using android.app.AlertDialog
            AlertDialog.Builder(this)
                .setTitle("Registration Error")
                .setMessage("Email address already in use. Please use a different email or log in.")
                .setPositiveButton("OK") { dialog, which ->
                    // User stays on the current activity, allowing them to fix the email input
                    dialog.dismiss()
                }
                .setCancelable(true) // Allow dismissal by tapping outside/back button
                .show()
        }


        // 🔹 Helper to update Next button state
        fun updateNextButtonState() {
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString()
            val confirmPassword = inputConfirmPassword.text.toString()

            val isEmailValid = email.endsWith("@umak.edu.ph", ignoreCase = true)

            // 🚨 UPDATED VALIDATION LOGIC 🚨
            val isPasswordValid = password.length >= 8 &&
                    password.any { it.isUpperCase() } && // Check for uppercase
                    password.any { it.isLowerCase() } && // Check for lowercase
                    password.any { it in specialChars } &&
                    password.any { it.isDigit() } // Check for number

            val isConfirmMatch = password == confirmPassword && confirmPassword.isNotEmpty()

            // Enable or disable button; background changes automatically (if set up via XML selector)
            btnNext.isEnabled = isEmailValid && isPasswordValid && isConfirmMatch
        }

        // 🔹 Back and Next Button Click Listeners
        btnBack.setOnClickListener { finish() }

        btnNext.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString()

            // =========================================================================
            // 🚨 BACKEND SIMULATION / INTEGRATION POINT 🚨
            // =========================================================================

            // --- SIMULATION START ---
            val REGISTERED_EMAIL_SIMULATION = "test@umak.edu.ph"

            if (email.equals(REGISTERED_EMAIL_SIMULATION, ignoreCase = true)) {
                // FAILURE CASE: Email is already registered
                showEmailAlreadyRegisteredDialog()

            } else {
                // SUCCESS CASE: Proceed to the next registration step (RegisterActivity2)
                val intent = Intent(this, RegisterActivity2::class.java)
                startActivity(intent)
            }
            // --- SIMULATION END ---
            // =========================================================================
        }

        // =====================================================================
        // 5. EMAIL FIELD LOGIC (Previously Section 4)
        // =====================================================================

        // 🔹 Focus Change Listener for Email
        inputEmail.setOnFocusChangeListener { _, hasFocus ->
            val email = inputEmail.text.toString()
            if (hasFocus) {
                EmailRequirements.visibility = View.VISIBLE
            } else {
                if (email.isEmpty()) {
                    EmailRequirements.visibility = View.GONE
                } else if (email.endsWith("@umak.edu.ph", ignoreCase = true)) {
                    reqEmail.setTextColor(Color.parseColor("#4CAF50"))
                    reqEmail.text = "✓ Valid UMak email (@umak.edu.ph)"
                    EmailRequirements.visibility = View.GONE
                } else {
                    reqEmail.setTextColor(Color.parseColor("#D32F2F"))
                    reqEmail.text = "• Please use your UMak email (@umak.edu.ph)"
                }
            }
        }

        // 🔹 Text Watcher for Email (Live Validation)
        inputEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = inputEmail.text.toString()

                if (email.isEmpty()) {
                    reqEmail.setTextColor(Color.parseColor("#8C8CA1"))
                    reqEmail.text = "• Please use your UMak email (@umak.edu.ph)"
                } else if (email.endsWith("@umak.edu.ph", ignoreCase = true)) {
                    reqEmail.setTextColor(Color.parseColor("#4CAF50"))
                    reqEmail.text = "✓ Valid UMak email (@umak.edu.ph)"
                } else {
                    reqEmail.setTextColor(Color.parseColor("#D32F2F"))
                    reqEmail.text = "• Please use your UMak email (@umak.edu.ph)"
                }

                updateNextButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // =====================================================================
        // 6. PASSWORD FIELD LOGIC (Previously Section 5)
        // =====================================================================

        // 🔹 Focus Change Listener for Password (No change needed here)
        inputPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                passwordRequirements.visibility = View.VISIBLE
            } else {
                val password = inputPassword.text.toString()
                if (password.isEmpty()) {
                    passwordRequirements.visibility = View.GONE
                    layoutPassword.boxStrokeColor = Color.parseColor("#9E9E9E") // Gray
                } else if (allValidationsPassed) {
                    passwordRequirements.visibility = View.GONE
                    layoutPassword.boxStrokeColor = Color.parseColor("#4CAF50") // Green
                } else {
                    layoutPassword.boxStrokeColor = Color.parseColor("#D32F2F") // Red
                }
            }
        }

        // 🔹 Text Watcher for Password (Live Validation)
        val passwordWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = inputPassword.text.toString()

                // New flags for the four requirements
                var isLengthValid = false
                var isMixedCaseValid = false
                var isSpecialValid = false
                var isNumberValid = false

                // Check 1: Length (>= 8 characters)
                if (password.length >= 8) {
                    reqLength.setTextColor(Color.parseColor("#4CAF50"))
                    reqLength.text = "✓ Must be at least 8 characters"
                    isLengthValid = true
                } else {
                    reqLength.setTextColor(Color.parseColor("#D32F2F"))
                    reqLength.text = "• Must be at least 8 characters"
                }

                // Check 2: Mixed Case (Upper and Lower)
                val hasUppercase = password.any { it.isUpperCase() }
                val hasLowercase = password.any { it.isLowerCase() }
                if (hasUppercase && hasLowercase) {
                    reqMixedcase.setTextColor(Color.parseColor("#4CAF50"))
                    reqMixedcase.text = "✓ Mixed case"
                    isMixedCaseValid = true
                } else {
                    reqMixedcase.setTextColor(Color.parseColor("#D32F2F"))
                    reqMixedcase.text = "• Mixed case (Uppercase and Lowercase)"
                }

                // Check 3: Special character
                if (password.any { it in specialChars }) {
                    reqSpecial.setTextColor(Color.parseColor("#4CAF50"))
                    reqSpecial.text = "✓ Must contain a special character"
                    isSpecialValid = true
                } else {
                    reqSpecial.setTextColor(Color.parseColor("#D32F2F"))
                    reqSpecial.text = "• Must contain a special character"
                }

                // Check 4: Number
                if (password.any { it.isDigit() }) {
                    reqNumber.setTextColor(Color.parseColor("#4CAF50"))
                    reqNumber.text = "✓ Must contain a number"
                    isNumberValid = true
                } else {
                    reqNumber.setTextColor(Color.parseColor("#D32F2F"))
                    reqNumber.text = "• Must contain a number"
                }

                // Update final validation status
                allValidationsPassed = isLengthValid && isMixedCaseValid && isSpecialValid && isNumberValid
                updateNextButtonState()

                // IMPORTANT: Ensure confirm password logic is re-run immediately after
                // password changes to check for a match.
                // 🚨 FIX 2: We can now reference confirmPasswordWatcher because it was declared earlier.
                inputConfirmPassword.text?.let { confirmPasswordWatcher.afterTextChanged(it) }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        inputPassword.addTextChangedListener(passwordWatcher)

        // =====================================================================
        // 7. CONFIRM PASSWORD FIELD LOGIC (Previously Section 6)
        // =====================================================================

        // 🔹 Focus Change Listener for Confirm Password (No change)
        inputConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                ConfirmpasswordRequirements.visibility = View.VISIBLE
            } else {
                val password = inputPassword.text.toString()
                val confirmPassword = inputConfirmPassword.text.toString()

                if (confirmPassword.isEmpty()) {
                    ConfirmpasswordRequirements.visibility = View.GONE
                    layoutConfirmPassword.boxStrokeColor = Color.parseColor("#9E9E9E") // Gray
                } else if (password == confirmPassword) {
                    ConfirmpasswordRequirements.visibility = View.GONE
                    reqMatch.setTextColor(Color.parseColor("#4CAF50"))
                    reqMatch.text = "✓ Passwords match"
                    layoutConfirmPassword.boxStrokeColor = Color.parseColor("#4CAF50") // Green
                } else {
                    ConfirmpasswordRequirements.visibility = View.VISIBLE
                    reqMatch.setTextColor(Color.parseColor("#D32F2F"))
                    reqMatch.text = "• Passwords must match"
                    layoutConfirmPassword.boxStrokeColor = Color.parseColor("#D32F2F") // Red
                }
            }
        }

        // 🔹 Text Watcher for Confirm Password (Live Validation)
        // 🚨 FIX 3: Assigning the value to the previously declared lateinit variable.
        confirmPasswordWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = inputPassword.text.toString()
                val confirmPassword = inputConfirmPassword.text.toString()

                if (confirmPassword.isEmpty()) {
                    // Note: reqMatch.visibility = View.GONE here might hide only the text,
                    // while ConfirmpasswordRequirements is the parent view.
                    reqMatch.visibility = View.GONE
                    layoutConfirmPassword.boxStrokeColor = Color.parseColor("#9E9E9E") // Gray
                } else if (password == confirmPassword) {
                    reqMatch.visibility = View.VISIBLE
                    reqMatch.setTextColor(Color.parseColor("#4CAF50"))
                    reqMatch.text = "✓ Passwords match"
                    layoutConfirmPassword.boxStrokeColor = Color.parseColor("#4CAF50") // Green
                } else {
                    reqMatch.visibility = View.VISIBLE
                    reqMatch.setTextColor(Color.parseColor("#D32F2F"))
                    reqMatch.text = "• Passwords must match"
                    layoutConfirmPassword.boxStrokeColor = Color.parseColor("#D32F2F") // Red
                }

                updateNextButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        inputConfirmPassword.addTextChangedListener(confirmPasswordWatcher)

        // 🔹 Redundant Email TextWatcher for UpdateNextButtonState (No change)
        inputEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateNextButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}

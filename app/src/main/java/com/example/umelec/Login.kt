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
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.app.AlertDialog


class Login : AppCompatActivity() {

    // 🚨 SIMULATED CREDENTIALS (Replace with your actual authentication logic)
    private val CORRECT_EMAIL = "@umak.edu.ph"
    private val CORRECT_PASSWORD = "a"
    // NEW: Placeholder for the user's name (easy to change for the backend)
    private val CORRECT_USER_NAME = "Juan Dela Cruz"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


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

        // --- PASSWORD VIEWS ---
        val layoutPassword = findViewById<TextInputLayout>(R.id.textInputLayoutPassword)
        val inputPassword = findViewById<TextInputEditText>(R.id.inputPassword)
        val passwordRequirementsContainer = findViewById<View>(R.id.PasswordRequirements)
        val reqPassword = findViewById<TextView>(R.id.reqPassword)

        // --- BUTTONS ---
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val forgotPassword = findViewById<TextView>(R.id.ForgotPassword)
        // ⭐️ New: Register Button ⭐️
        val registerButton = findViewById<TextView>(R.id.RegisterButton)

        // --- HELPER FUNCTIONS ---
        fun isEmailValid(email: String): Boolean {
            // Requires non-empty AND ends with the specific domain
            return email.isNotEmpty() && email.endsWith(CORRECT_EMAIL, ignoreCase = true)
        }

        fun isPasswordValid(password: String): Boolean {
            // Only checks if the password string is NOT empty
            return password.isNotEmpty()
        }

        // Function to update the Login button's enabled/disabled state
        fun updateLoginButtonState() {
            val emailText = inputEmail.text.toString().trim()
            val passwordText = inputPassword.text.toString()

            val allFieldsValid = isEmailValid(emailText) && isPasswordValid(passwordText)

            btnLogin.isEnabled = allFieldsValid
        }

        // Function to show the error dialog without resetting data
        fun showLoginErrorDialog() {
            // Using android.app.AlertDialog as per your original import
            AlertDialog.Builder(this)
                .setTitle("Login Failed")
                .setMessage("Invalid Credentials. Please try again.")
                .setPositiveButton("OK") { dialog, which ->
                    // Dialog is dismissed, user remains on the Login screen with data intact
                }
                .show()
        }

        // NEW: Function to show the success dialog and navigate to Homepage
        fun showLoginSuccessDialog(userName: String) {
            // 1. Show the Toast message
            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

            // 2. Display the custom AlertDialog
            // Using android.app.AlertDialog as per your original import
            AlertDialog.Builder(this)
                .setTitle("Login Successful")
                .setMessage("Welcome back, $userName!") // Use the changeable name here
                .setPositiveButton("Continue to Homepage") { dialog, which ->
                    // 3. Navigation to Homepage.kt upon clicking the button
                    val intent = Intent(this, Homepage::class.java)
                    // Clear the back stack so the user can't return to Login by pressing back
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish() // Finish the current login activity
                }
                .setCancelable(false) // Prevent dismissal
                .show()
        }


        // =====================================================================
        // 🚨 2. INITIAL SETUP & LISTENERS 🚨
        // =====================================================================

        // BACK BUTTON SETUP
        btnBack.setOnClickListener {
            //val intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
            finish() // optional — use if you don’t want to come back here when pressing back again
        }

        // FORGOT PASSWORD BUTTON SETUP
        forgotPassword.setOnClickListener {
            val intent = Intent(this, Forgotpassword::class.java)
            startActivity(intent)
        }

        // ⭐️ REGISTER BUTTON SETUP ⭐️
        registerButton.setOnClickListener {
            // Start the RegisterActivity when the button is clicked
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Initial state: Button is disabled
        btnLogin.isEnabled = false


        // ---------------------------------------------------------------------
        // 🔹 EMAIL FOCUS CHANGE HANDLING (Blur/Empty Check)
        // ---------------------------------------------------------------------

        // 🔹 Hide helper text by default
        emailRequirementsContainer.visibility = View.GONE

        inputEmail.setOnFocusChangeListener { _, hasFocus ->
            val email = inputEmail.text.toString().trim()
            val isValid = isEmailValid(email)

            if (hasFocus) {
                // --- WHEN FOCUSED ---
                emailRequirementsContainer.visibility = View.VISIBLE

                if (isValid) {
                    // ✅ VALID & FOCUSED: Show success state
                    reqEmail.setTextColor(COLOR_SUCCESS_GREEN)
                    reqEmail.text = "Email format is correct"
                    layoutEmail.boxStrokeColor = COLOR_SUCCESS_GREEN // SET GREEN
                } else {
                    // 🩶 NOT VALID/TYPING & FOCUSED: Show hint state
                    reqEmail.setTextColor(COLOR_HINT_GRAY)
                    reqEmail.text = "• Please use your UMak email ($CORRECT_EMAIL)"
                    layoutEmail.boxStrokeColor = COLOR_PRIMARY_BLUE // SET BLUE
                }
            } else {
                // --- WHEN UN-FOCUSED (BLUR) ---
                if (email.isEmpty()) {
                    // ❌ EMPTY on BLUR: Show required error
                    emailRequirementsContainer.visibility = View.VISIBLE
                    reqEmail.setTextColor(COLOR_ERROR_RED)
                    reqEmail.text = "Field is required"
                    layoutEmail.boxStrokeColor = COLOR_ERROR_RED // SET RED
                } else if (isValid) {
                    // ✅ VALID on BLUR: Hide helper text, KEEP GREEN BORDER
                    emailRequirementsContainer.visibility = View.GONE
                    layoutEmail.boxStrokeColor = COLOR_SUCCESS_GREEN // SET GREEN
                } else {
                    // ❌ INVALID on BLUR: Keep error visible
                    emailRequirementsContainer.visibility = View.VISIBLE
                    reqEmail.setTextColor(COLOR_ERROR_RED)
                    reqEmail.text = "• Email must end with $CORRECT_EMAIL"
                    layoutEmail.boxStrokeColor = COLOR_ERROR_RED // Ensure it's red
                }
            }
            updateLoginButtonState()
        }

        // ---------------------------------------------------------------------
        // 🔹 EMAIL REAL-TIME VALIDATION (Typing/Input Check)
        // ---------------------------------------------------------------------

        inputEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString().trim()

                // 2. 🔹 Validation Logic
                when {
                    // ✅ VALID MATCH: Complete and correct domain
                    isEmailValid(text) -> {
                        reqEmail.setTextColor(COLOR_SUCCESS_GREEN)
                        reqEmail.text = "Email format is correct"
                        layoutEmail.boxStrokeColor = COLOR_SUCCESS_GREEN
                        emailRequirementsContainer.visibility = View.VISIBLE
                    }

                    // ❌ INVALID FORMAT: Contains text but doesn't end with required domain
                    text.isNotEmpty() && !text.endsWith(CORRECT_EMAIL, ignoreCase = true) -> {
                        reqEmail.setTextColor(COLOR_ERROR_RED)
                        reqEmail.text = "• Email must end with $CORRECT_EMAIL"
                        layoutEmail.boxStrokeColor = COLOR_ERROR_RED
                        emailRequirementsContainer.visibility = View.VISIBLE
                    }

                    // 🩶 DEFAULT TYPING STATE: Empty or still typing (focused state)
                    else -> {
                        reqEmail.setTextColor(COLOR_HINT_GRAY)
                        reqEmail.text = "• Please use your UMak email ($CORRECT_EMAIL)"
                        layoutEmail.boxStrokeColor = COLOR_PRIMARY_BLUE
                        emailRequirementsContainer.visibility = View.VISIBLE
                    }
                }
                updateLoginButtonState()
            }
        })

        // ---------------------------------------------------------------------
        // 🔹 PASSWORD FOCUS CHANGE HANDLING (Blur/Empty Check)
        // ---------------------------------------------------------------------

        // 🔹 Hide helper text by default
        passwordRequirementsContainer.visibility = View.GONE

        inputPassword.setOnFocusChangeListener { _, hasFocus ->
            val password = inputPassword.text.toString()
            val isValid = isPasswordValid(password) // Checks if it's not empty

            if (hasFocus) {
                // --- WHEN FOCUSED ---
                passwordRequirementsContainer.visibility = View.VISIBLE

                if (isValid) {
                    // ✅ VALID (Not Empty) & FOCUSED: Show success state
                    reqPassword.setTextColor(COLOR_SUCCESS_GREEN)
                    reqPassword.text = "Input is ready"
                    layoutPassword.boxStrokeColor = COLOR_SUCCESS_GREEN // SET GREEN
                } else {
                    // 🩶 NOT VALID (Empty) & FOCUSED: Show hint state
                    reqPassword.setTextColor(COLOR_HINT_GRAY)
                    reqPassword.text = "Field is required"
                    layoutPassword.boxStrokeColor = COLOR_PRIMARY_BLUE // SET BLUE
                }
            } else {
                // --- WHEN UN-FOCUSED (BLUR) ---
                if (password.isEmpty()) {
                    // ❌ EMPTY on BLUR: Show required error
                    passwordRequirementsContainer.visibility = View.VISIBLE
                    reqPassword.setTextColor(COLOR_ERROR_RED)
                    reqPassword.text = "Field is required"
                    layoutPassword.boxStrokeColor = COLOR_ERROR_RED // SET RED
                } else {
                    // ✅ VALID (Not Empty) on BLUR: Hide helper text, KEEP GREEN BORDER
                    passwordRequirementsContainer.visibility = View.GONE
                    layoutPassword.boxStrokeColor = COLOR_SUCCESS_GREEN // SET GREEN
                }
            }
            updateLoginButtonState()
        }

        // ---------------------------------------------------------------------
        // 🔹 PASSWORD REAL-TIME VALIDATION (Typing/Input Check)
        // ---------------------------------------------------------------------

        inputPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()

                // 2. 🔹 Validation Logic
                when {
                    // ✅ NOT EMPTY (Valid)
                    isPasswordValid(text) -> {
                        reqPassword.setTextColor(COLOR_SUCCESS_GREEN)
                        reqPassword.text = "Input is ready"
                        layoutPassword.boxStrokeColor = COLOR_SUCCESS_GREEN
                        passwordRequirementsContainer.visibility = View.VISIBLE
                    }

                    // 🩶 EMPTY (Default typing state)
                    else -> {
                        reqPassword.setTextColor(COLOR_HINT_GRAY)
                        reqPassword.text = "Field is required"
                        layoutPassword.boxStrokeColor = COLOR_PRIMARY_BLUE
                        passwordRequirementsContainer.visibility = View.VISIBLE
                    }
                }
                updateLoginButtonState()
            }
        })

        // =====================================================================
        // 🚨 3. LOGIN BUTTON CLICK LISTENER (Final Step) 🚨
        // =====================================================================

        btnLogin.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString()

            // ⚠️ SIMULATED LOGIN CHECK ⚠️
            if (email.endsWith(CORRECT_EMAIL, ignoreCase = true) && password == CORRECT_PASSWORD) {
                // UPDATED: Show custom success dialog instead of navigating directly
                showLoginSuccessDialog(CORRECT_USER_NAME)
            } else {
                // FAILED LOGIN: Show custom error dialog and stay on screen
                showLoginErrorDialog()
            }
        }
    }
}
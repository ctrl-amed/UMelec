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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog // Import for AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

// NOTE: Assuming you have a LoginActivity.kt file
class LoginActivity : AppCompatActivity() {
    // Empty class declaration for compilation in this context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // You would normally set your login layout here
        // setContentView(R.layout.activity_login)
    }
}

class Resetpassword : AppCompatActivity() {

    val specialChars = "!@#$%^&*-+=()_`~[]{}|\\:;\"'<,>.?/"

    private lateinit var btnConfirm: Button
    private lateinit var btnBack: ImageButton

    // New Password Fields
    private lateinit var layoutNewPassword: TextInputLayout
    private lateinit var inputNewPassword: TextInputEditText
    private lateinit var passwordRequirements: View
    private lateinit var reqLength: TextView
    private lateinit var reqMixedcase: TextView
    private lateinit var reqSpecial: TextView
    private lateinit var reqNumber: TextView

    // Confirm Password Fields
    private lateinit var layoutConfirmPassword: TextInputLayout
    private lateinit var inputConfirmPassword: TextInputEditText
    private lateinit var confirmPasswordRequirements: View
    private lateinit var reqMatch: TextView

    private var allPasswordValidationsPassed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetpassword)

        // =====================================================================
        // 1. VIEW INITIALIZATION (FIND VIEW BY ID)
        // =====================================================================

        // Buttons
        btnBack = findViewById(R.id.btnBack)
        btnConfirm = findViewById(R.id.btnConfirm)

        // New Password Fields
        layoutNewPassword = findViewById(R.id.textInputLayoutPassword)
        inputNewPassword = findViewById(R.id.inputPassword)
        passwordRequirements = findViewById(R.id.passwordRequirements)

        // Password Requirement Texts
        reqLength = findViewById(R.id.reqLength)
        reqMixedcase = findViewById(R.id.reqMixedcase)
        reqSpecial = findViewById(R.id.reqSpecial)
        reqNumber = findViewById(R.id.reqNumber)

        // Confirm Password Fields
        layoutConfirmPassword = findViewById(R.id.textInputLayoutConfirmPassword)
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword)
        confirmPasswordRequirements = findViewById(R.id.ConfirmpasswordRequirements)
        reqMatch = findViewById(R.id.reqMatch)

        // =====================================================================
        // 2. INITIAL STATE & LISTENERS
        // =====================================================================

        btnConfirm.isEnabled = false
        passwordRequirements.visibility = View.GONE
        confirmPasswordRequirements.visibility = View.GONE

        btnBack.setOnClickListener { finish() }

        // NEW FIX: Replace direct navigation with Toast and AlertDialog
        btnConfirm.setOnClickListener {


            // 2. Display an AlertDialog for user guidance
            AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage("Password reset successful. Please login again with your new password.")
                .setPositiveButton("OK") { dialog, which ->
                    // 3. Navigate to LoginActivity upon clicking OK
                    val intent = Intent(this, Login::class.java)
                    // Clear the back stack so the user cannot press back to reset password screen
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setCancelable(false) // Prevent dialog dismissal by outside touch/back button
                .show()
        }

        // Set up the TextWatchers and Focus Listeners (rest of the file remains the same)
        inputNewPassword.addTextChangedListener(passwordWatcher)
        inputConfirmPassword.addTextChangedListener(confirmPasswordWatcher)
        inputNewPassword.setOnFocusChangeListener(passwordFocusListener)
        inputConfirmPassword.setOnFocusChangeListener(confirmPasswordFocusListener)
    }

    // =========================================================================
    // 3. HELPER FUNCTIONS AND LISTENERS (UNCHANGED VALIDATION LOGIC)
    // =========================================================================

    /**
     * Updates the state of the 'Confirm' button based on all validations.
     */
    private fun updateSaveButtonState() {
        val newPassword = inputNewPassword.text.toString()
        val confirmPassword = inputConfirmPassword.text.toString()

        // Check if the new password meets all complex requirements
        val isPasswordValid = newPassword.length >= 8 &&
                newPassword.any { it.isUpperCase() } &&
                newPassword.any { it.isLowerCase() } &&
                newPassword.any { it.isDigit() } &&
                newPassword.any { it in specialChars }

        // Check if confirm password matches the new valid password
        val isConfirmMatch = isPasswordValid && (newPassword == confirmPassword) && confirmPassword.isNotEmpty()

        btnConfirm.isEnabled = isConfirmMatch
    }

    /**
     * Live validation logic for the New Password field.
     */
    private val passwordWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val password = inputNewPassword.text.toString()

            var isLengthValid = false
            var isMixedcaseValid = false
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

            // Check 2: Mixed Case (Uppercase AND Lowercase)
            val hasUpper = password.any { it.isUpperCase() }
            val hasLower = password.any { it.isLowerCase() }
            if (hasUpper && hasLower) {
                reqMixedcase.setTextColor(Color.parseColor("#4CAF50"))
                reqMixedcase.text = "✓ Mixed case"
                isMixedcaseValid = true
            } else {
                reqMixedcase.setTextColor(Color.parseColor("#D32F2F"))
                reqMixedcase.text = "• Mixed case"
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

            allPasswordValidationsPassed = isLengthValid && isMixedcaseValid && isSpecialValid && isNumberValid

            // Update border color based on validation status
            if (inputNewPassword.isFocused) {
                layoutNewPassword.boxStrokeColor = if (allPasswordValidationsPassed) {
                    Color.parseColor("#4CAF50")
                } else {
                    Color.parseColor("#D32F2F")
                }
            } else if (password.isEmpty()) {
                layoutNewPassword.boxStrokeColor = Color.parseColor("#9E9E9E")
            } else if (allPasswordValidationsPassed) {
                layoutNewPassword.boxStrokeColor = Color.parseColor("#4CAF50")
            } else {
                layoutNewPassword.boxStrokeColor = Color.parseColor("#D32F2F")
            }

            // Also trigger update on confirm password validation since the match condition depends on the new password
            confirmPasswordWatcher.afterTextChanged(inputConfirmPassword.text)
            updateSaveButtonState()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    /**
     * Live validation logic for the Confirm Password field.
     */
    private val confirmPasswordWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val newPassword = inputNewPassword.text.toString()
            val confirmPassword = inputConfirmPassword.text.toString()

            val passwordsMatch = newPassword == confirmPassword
            val areBothNonEmpty = newPassword.isNotEmpty() && confirmPassword.isNotEmpty()

            if (!areBothNonEmpty) {
                reqMatch.visibility = View.VISIBLE
                reqMatch.setTextColor(Color.parseColor("#8C8CA1"))
                reqMatch.text = "• Passwords must match"
                layoutConfirmPassword.boxStrokeColor = Color.parseColor("#9E9E9E")
            } else if (passwordsMatch) {
                reqMatch.visibility = View.VISIBLE
                reqMatch.setTextColor(Color.parseColor("#4CAF50"))
                reqMatch.text = "✓ Passwords match"
                layoutConfirmPassword.boxStrokeColor = Color.parseColor("#4CAF50")
            } else {
                reqMatch.visibility = View.VISIBLE
                reqMatch.setTextColor(Color.parseColor("#D32F2F"))
                reqMatch.text = "• Passwords must match"
                layoutConfirmPassword.boxStrokeColor = Color.parseColor("#D32F2F")
            }

            updateSaveButtonState()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    /**
     * Focus listener for the New Password field (shows/hides requirements).
     */
    private val passwordFocusListener = View.OnFocusChangeListener { _, hasFocus ->
        val password = inputNewPassword.text.toString()
        if (hasFocus) {
            passwordRequirements.visibility = View.VISIBLE
        } else {
            if (password.isEmpty() || allPasswordValidationsPassed) {
                passwordRequirements.visibility = View.GONE
            }
            // Re-apply box color based on validation when focus leaves
            layoutNewPassword.boxStrokeColor = if (password.isEmpty()) {
                Color.parseColor("#9E9E9E")
            } else if (allPasswordValidationsPassed) {
                Color.parseColor("#4CAF50")
            } else {
                Color.parseColor("#D32F2F")
            }
        }
    }

    /**
     * Focus listener for the Confirm Password field (shows/hides match requirement).
     */
    private val confirmPasswordFocusListener = View.OnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            confirmPasswordRequirements.visibility = View.VISIBLE
        } else {
            val confirmPassword = inputConfirmPassword.text.toString()
            val newPassword = inputNewPassword.text.toString()
            val passwordsMatch = newPassword == confirmPassword

            if (confirmPassword.isEmpty() || passwordsMatch) {
                confirmPasswordRequirements.visibility = View.GONE
            }

            // Re-apply box color based on match when focus leaves
            layoutConfirmPassword.boxStrokeColor = if (confirmPassword.isEmpty()) {
                Color.parseColor("#9E9E9E")
            } else if (passwordsMatch) {
                Color.parseColor("#4CAF50")
            } else {
                Color.parseColor("#D32F2F")
            }
        }
    }
}
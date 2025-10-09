package com.example.umelec

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Color
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.app.AlertDialog // For the success/failure dialogs
import android.widget.ImageButton

class Verificationcode : AppCompatActivity() {

    // Define color constants (Same as your other activities)
    private val COLOR_PRIMARY_BLUE = Color.parseColor("#0039A6")
    private val COLOR_ERROR_RED = Color.parseColor("#D32F2F")
    private val COLOR_SUCCESS_GREEN = Color.parseColor("#4CAF50")
    private val COLOR_HINT_GRAY = Color.parseColor("#8C8CA1")

    // Define Views globally within the class for easier access in helper functions
    private lateinit var otpInputs: List<TextInputEditText>
    private lateinit var btnSendVerification: Button
    private lateinit var textOTPtimer: TextView
    private lateinit var countDownTimer: CountDownTimer

    // 🚨 SIMULATED CORRECT OTP (Replace with logic that checks against a sent code)
    private val CORRECT_OTP = "123456"
    private val TIMER_DURATION_SECONDS = 60L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verificationcode) // Assuming your layout is activity_verification_code

        val btnBack = findViewById<ImageButton>(R.id.btnBack) // Assuming a back button ID
        btnBack.setOnClickListener {
            finish()
        }

        // =====================================================================
        // 🚨 1. INITIALIZATION 🚨
        // =====================================================================

        // Initialize OTP Input Fields
        otpInputs = listOf(
            findViewById(R.id.input1),
            findViewById(R.id.input2),
            findViewById(R.id.input3),
            findViewById(R.id.input4),
            findViewById(R.id.input5),
            findViewById(R.id.input6)
        )

        btnSendVerification = findViewById(R.id.btnSendVerification)
        textOTPtimer = findViewById(R.id.textOTPtimer)

        // Set initial button state to disabled
        btnSendVerification.isEnabled = false

        // =====================================================================
        // 🚨 2. OTP INPUT AUTOMATION AND VALIDATION 🚨
        // =====================================================================

        setupOtpListeners()

        // =====================================================================
        // 🚨 3. TIMER AND RESEND CODE LOGIC 🚨
        // =====================================================================

        startResendTimer()
        textOTPtimer.setOnClickListener {
            // Only clickable when the timer is finished and showing "Resend code"
            if (textOTPtimer.text == "Resend code") {
                showResendSuccessDialog()
            }
        }

        // =====================================================================
        // 🚨 4. CONFIRM BUTTON CLICK LOGIC 🚨
        // =====================================================================

        btnSendVerification.setOnClickListener {
            handleOtpConfirmation()
        }
    }

    // ---------------------------------------------------------------------
    // 🔹 HELPER FUNCTIONS 🔹
    // ---------------------------------------------------------------------

    private fun checkAllFieldsFilled(): Boolean {
        return otpInputs.all { it.text?.isNotEmpty() == true }
    }

    private fun handleOtpConfirmation() {
        val enteredCode = otpInputs.joinToString("") { it.text.toString() }

        if (enteredCode == CORRECT_OTP) {
            showVerificationSuccessDialog()
        } else {
            showVerificationFailureDialog()
        }
    }

    private fun updateButtonState() {
        btnSendVerification.isEnabled = checkAllFieldsFilled()
    }

    private fun clearOtpFields() {
        otpInputs.forEach { it.setText("") }
        otpInputs.first().requestFocus()
    }

    // ---------------------------------------------------------------------
    // 🔹 DIALOG AND TOASTS 🔹
    // ---------------------------------------------------------------------

    private fun showVerificationSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Code Verified Successfully")
            .setMessage("You can now reset your password.")
            .setPositiveButton("Confirm") { dialog, which ->
                // Proceed to Resetpassword.kt
                val intent = Intent(this, Resetpassword::class.java) // Assuming class name is Resetpassword
                startActivity(intent)
                finish() // Close current activity
            }
            .setCancelable(false) // User must click confirm
            .show()
    }

    private fun showVerificationFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Verification Failed")
            .setMessage("Invalid OTP code entered.")
            .setPositiveButton("OK") { dialog, which ->
                // Go back to the activity and reset the invalid OTP
                clearOtpFields()
            }
            .setCancelable(false)
            .show()
    }

    private fun showResendSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Resend Successful")
            .setMessage("OTP code resent successfully.")
            .setPositiveButton("OK") { dialog, which ->
                // Goes back to the activity and restarts the timer
                startResendTimer()
            }
            .setCancelable(false)
            .show()
    }

    // ---------------------------------------------------------------------
    // 🔹 OTP LISTENERS (Auto-Focus and Delete) 🔹
    // ---------------------------------------------------------------------

    private fun setupOtpListeners() {
        otpInputs.forEachIndexed { index, input ->

            // Limit input to 1 character
            input.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    updateButtonState() // Update button state after any change

                    val text = s.toString()
                    if (text.length == 1 && index < otpInputs.size - 1) {
                        // Automatically move focus to the next field
                        otpInputs[index + 1].requestFocus()
                    }
                    if (text.length > 1) {
                        // Keep only the first character
                        input.setText(text.substring(0, 1))
                        input.setSelection(1)
                    }
                }
            })

            // Handle backspace key press to move focus back
            input.setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    // Check if the current field is empty AND it's not the first field
                    if (input.text.isNullOrEmpty() && index > 0) {
                        // Move focus to the previous field
                        otpInputs[index - 1].requestFocus()
                        // Ensure the cursor is at the end of the previous field
                        otpInputs[index - 1].setSelection(otpInputs[index - 1].text?.length ?: 0)
                        return@setOnKeyListener true // Consume the backspace event
                    } else if (input.text?.isNotEmpty() == true) {
                        // If there is text, let the default behavior (deleting one char) happen.
                        return@setOnKeyListener false
                    }
                }
                false
            }
        }
    }

    // ---------------------------------------------------------------------
    // 🔹 TIMER LOGIC 🔹
    // ---------------------------------------------------------------------

    private fun startResendTimer() {
        // Set to Gray, not clickable, and start showing time
        textOTPtimer.setTextColor(COLOR_HINT_GRAY)
        textOTPtimer.isClickable = false

        countDownTimer = object : CountDownTimer(TIMER_DURATION_SECONDS * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                textOTPtimer.text = "Resend code in $seconds seconds"
            }

            override fun onFinish() {
                // Set to Blue, clickable, and show "Resend code" text
                textOTPtimer.setTextColor(Color.parseColor("#318CE7")) // Use the blue color from XML
                textOTPtimer.text = "Resend code"
                textOTPtimer.isClickable = true
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }
}
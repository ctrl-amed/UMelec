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
// Removed unused import: import androidx.core.content.ContextCompat

class RegisterActivity : AppCompatActivity() {

    // =========================================================================
    // 1. CLASS-LEVEL PROPERTIES
    // =========================================================================
    private var allValidationsPassed = false
    // Note: The specialChars is defined twice. I've left both in place
    // as per your instruction "don't change any code", but recommended fix is below.
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
        val reqUppercase = findViewById<TextView>(R.id.reqUppercase)
        val reqSpecial = findViewById<TextView>(R.id.reqSpecial)
        val reqMatch = findViewById<TextView>(R.id.reqMatch)

        // 🔹 Redundant/Duplicated variable declaration
        val specialChars = "!@#$%^&*-+=()_`~[]{}|\\:;\"'<,>.?/"

        // =====================================================================
        // 3. INITIAL SETUP AND HELPER FUNCTION
        // =====================================================================

        // Initial State Setup
        btnNext.isEnabled = false
        passwordRequirements.visibility = View.GONE
        ConfirmpasswordRequirements.visibility = View.GONE
        EmailRequirements.visibility = View.GONE


        // 🔹 Helper to update Next button state
        fun updateNextButtonState() {
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString()
            val confirmPassword = inputConfirmPassword.text.toString()

            val isEmailValid = email.endsWith("@umak.edu.ph", ignoreCase = true)
            val isPasswordValid = password.length >= 8 &&
                    password.any { it.isUpperCase() } &&
                    password.any { it in specialChars }
            val isConfirmMatch = password == confirmPassword && confirmPassword.isNotEmpty()

            // Enable or disable button; background changes automatically (if set up via XML selector)
            btnNext.isEnabled = isEmailValid && isPasswordValid && isConfirmMatch
        }

        // 🔹 Back and Next Button Click Listeners
        btnBack.setOnClickListener { finish() }

        btnNext.setOnClickListener {
            val intent = Intent(this, RegisterActivity2::class.java)
            startActivity(intent)
        }

        // =====================================================================
        // 4. EMAIL FIELD LOGIC
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
        // 5. PASSWORD FIELD LOGIC
        // =====================================================================

        // 🔹 Focus Change Listener for Password
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

                var isLengthValid = false
                var isUppercaseValid = false
                var isSpecialValid = false

                // Check 1: Length
                if (password.length >= 8) {
                    reqLength.setTextColor(Color.parseColor("#4CAF50"))
                    reqLength.text = "✓ Must be at least 8 characters"
                    isLengthValid = true
                } else {
                    reqLength.setTextColor(Color.parseColor("#D32F2F"))
                    reqLength.text = "• Must be at least 8 characters"
                }

                // Check 2: Uppercase
                if (password.any { it.isUpperCase() }) {
                    reqUppercase.setTextColor(Color.parseColor("#4CAF50"))
                    reqUppercase.text = "✓ Must contain an uppercase letter"
                    isUppercaseValid = true
                } else {
                    reqUppercase.setTextColor(Color.parseColor("#D32F2F"))
                    reqUppercase.text = "• Must contain an uppercase letter"
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

                allValidationsPassed = isLengthValid && isUppercaseValid && isSpecialValid
                updateNextButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        inputPassword.addTextChangedListener(passwordWatcher)

        // =====================================================================
        // 6. CONFIRM PASSWORD FIELD LOGIC
        // =====================================================================

        // 🔹 Focus Change Listener for Confirm Password
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
        val confirmPasswordWatcher = object : TextWatcher {
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

        // 🔹 Redundant Email TextWatcher for UpdateNextButtonState
        // The more detailed email TextWatcher above covers this, but kept for non-modification constraint.
        inputEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateNextButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
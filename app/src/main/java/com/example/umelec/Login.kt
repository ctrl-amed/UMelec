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
import android.view.Gravity
import android.view.LayoutInflater
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import android.view.inputmethod.InputMethodManager
import android.content.Context

class Login : AppCompatActivity() {

    // Firebase Auth Helper (No hardcoded credentials)

    // Define color constants (Used only for requirements TextView text colors)
    private val COLOR_PRIMARY_BLUE = Color.parseColor("#00537A")
    private val COLOR_ERROR_RED = Color.parseColor("#D33131")
    private val COLOR_SUCCESS_GREEN = Color.parseColor("#27A688")
    private val COLOR_HINT_GRAY = Color.parseColor("#5C5C77")

    // Define field layouts outside onCreate
    private lateinit var layoutEmail: TextInputLayout
    private lateinit var layoutPassword: TextInputLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        // =====================================================================
        // 🚨 1. VIEWS AND HELPER FUNCTIONS (INITIALIZATION) 🚨
        // =====================================================================

        // --- EMAIL VIEWS ---
        layoutEmail = findViewById(R.id.textInputLayoutEmail)
        val inputEmail = findViewById<TextInputEditText>(R.id.inputEmail)
        val emailRequirementsContainer = findViewById<View>(R.id.EmailRequirements)
        val reqEmail = findViewById<TextView>(R.id.reqEmail)

        // --- PASSWORD VIEWS ---
        layoutPassword = findViewById(R.id.textInputLayoutPassword)
        val inputPassword = findViewById<TextInputEditText>(R.id.inputPassword)
        val passwordRequirementsContainer = findViewById<View>(R.id.PasswordRequirements)
        val reqPassword = findViewById<TextView>(R.id.reqPassword)

        // --- BUTTONS ---
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val forgotPassword = findViewById<TextView>(R.id.ForgotPassword)
        val registerButton = findViewById<TextView>(R.id.RegisterButton)

        /**
         * 🟢 NEW HELPER: Hides the keyboard and clears focus from any active view.
         */
        fun hideKeyboardAndClearFocus() {
            // 1. Hide the keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            // 2. Clear focus from the currently focused view (like the EditText)
            currentFocus?.clearFocus()
        }

        // --- HELPER FUNCTIONS ---
        fun isEmailValid(email: String): Boolean {
            return email.isNotEmpty() && email.endsWith("@umak.edu.ph", ignoreCase = true)
        }

        fun isPasswordValid(password: String): Boolean {
            return password.isNotEmpty()
        }

        fun updateLoginButtonState() {
            val emailText = inputEmail.text.toString().trim()
            val passwordText = inputPassword.text.toString()

            val allFieldsValid = isEmailValid(emailText) && isPasswordValid(passwordText)

            btnLogin.isEnabled = allFieldsValid
        }

        fun showLoginSuccessDialog(userEmail: String, fullName: String? = null) {
            val firstName = fullName?.split(" ")?.first() ?: userEmail.substringBefore("@")

            val layoutInflater = LayoutInflater.from(this)
            val dialogView = layoutInflater.inflate(R.layout.custom_toast_success, null)

            val builder = AlertDialog.Builder(this)
            builder.setView(dialogView)
            val dialog = builder.create()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.setCanceledOnTouchOutside(false)

            dialogView.findViewById<TextView>(R.id.toast_title).text = "Login Success"
            dialogView.findViewById<TextView>(R.id.toast_value).text = "Welcome back, $firstName!"

            val btnAction = dialogView.findViewById<Button>(R.id.btn_action)
            btnAction.text = "Continue to Homepage"
            btnAction.setOnClickListener {
                dialog.dismiss()

                val intent = Intent(this, Homepage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            dialog.show()
        }


        fun clearValidationState(layout: TextInputLayout) {
            layout.error = null // Clears RED border/error text
            layout.isActivated = false // Clears GREEN border
        }

        fun showLoginErrorDialog() {
            val layoutInflater = LayoutInflater.from(this)
            val dialogView = layoutInflater.inflate(R.layout.custom_toast_error, null)

            val builder = AlertDialog.Builder(this)
            builder.setView(dialogView)
            val dialog = builder.create()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.setCanceledOnTouchOutside(false)

            dialogView.findViewById<TextView>(R.id.toast_title).text = "Login Error"
            dialogView.findViewById<TextView>(R.id.toast_value).text = "Invalid credentials. Please try again."

            dialogView.findViewById<ImageButton>(R.id.btn_close).setOnClickListener {
                dialog.dismiss()
                clearValidationState(layoutEmail)
                clearValidationState(layoutPassword)
            }

            // Trigger Red border via standard Material Error property (used as a visual flag)
            layoutEmail.error = " "
            layoutPassword.error = " "

            dialog.show()
        }


        // =====================================================================
        // 🚨 2. INITIAL SETUP & LISTENERS 🚨
        // =====================================================================

        //btnBack.setOnClickListener { finish() }
        btnBack.setOnClickListener {
            // Check if the current activity is the only one in the task.
            // If it is, calling finish() will close the app.
            // In this case, we explicitly launch MainActivity.

            // NOTE: If you are using fragments, this check might need refinement,
            // but for simple activities, this is often sufficient.
            if (isTaskRoot) {
                val intent = Intent(this, MainActivity::class.java)
                // Add flags to clear any lingering activities and start fresh,
                // mimicking a clean start.
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                // 2. 🛑 Disable animation when STARTING MainActivity
                //overridePendingTransition(0, 0)
            } else {
                // Normal behavior: Go back to the previous activity in the stack.
                finish()

                // 3. 🛑 Disable animation when FINISHING Login
                //overridePendingTransition(0, 0)
            }
        }


        forgotPassword.setOnClickListener {
            startActivity(Intent(this, Forgotpassword::class.java))
        }
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.isEnabled = false
        emailRequirementsContainer.visibility = View.GONE
        passwordRequirementsContainer.visibility = View.GONE

        // 🌟 NEW BEHAVIOR: CLICK OUTSIDE TO UNFOCUS/HIDE KEYBOARD 🌟
        val rootView = findViewById<View>(android.R.id.content)
        rootView.setOnClickListener {
            hideKeyboardAndClearFocus()
            clearValidationState(layoutEmail)
            clearValidationState(layoutPassword)
        }

        // ---------------------------------------------------------------------
        // 🔹 EMAIL FOCUS CHANGE HANDLING
        // ---------------------------------------------------------------------

        inputEmail.setOnFocusChangeListener { _, hasFocus ->
            val email = inputEmail.text.toString().trim()
            val isValid = isEmailValid(email)

            if (hasFocus) {
                // --- WHEN FOCUSED (Typing) ---
                emailRequirementsContainer.visibility = View.VISIBLE
                // Border colors handled by TextWatcher while focused
            } else {
                // --- WHEN UN-FOCUSED (BLUR) ---
                when {
                    email.isEmpty() -> {
                        // ❌ EMPTY on BLUR: Show required error, trigger RED border
                        emailRequirementsContainer.visibility = View.VISIBLE
                        reqEmail.setTextColor(COLOR_ERROR_RED)
                        reqEmail.text = "• Field is required"
                        layoutEmail.error = " " // Triggers RED border
                        layoutEmail.isActivated = false
                    }
                    isValid -> {
                        // ✅ VALID on BLUR: Hide helper text, trigger GREEN BORDER
                        emailRequirementsContainer.visibility = View.GONE

                        layoutEmail.error = null // Clear red border
                        layoutEmail.isActivated = true // Triggers GREEN border
                    }
                    else -> {
                        // ❌ INVALID on BLUR: Keep error visible, trigger RED border
                        emailRequirementsContainer.visibility = View.VISIBLE
                        reqEmail.setTextColor(COLOR_ERROR_RED)
                        reqEmail.text = "• Please use your UMak email (@umak.edu.ph)"
                        layoutEmail.error = " " // Triggers RED border
                        layoutEmail.isActivated = false
                    }
                }
            }
            updateLoginButtonState()
        }

        // ---------------------------------------------------------------------
        // 🔹 EMAIL REAL-TIME VALIDATION (LIVE BORDER FEEDBACK IMPLEMENTED)
        // ---------------------------------------------------------------------

        inputEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Clear any error/success state before validating the new text
                clearValidationState(layoutEmail)
            }

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString().trim()
                val isValid = isEmailValid(text)

                // 2. 🔹 Validation Logic
                when {
                    // ✅ VALID MATCH: Complete and correct domain
                    isValid -> {
                    reqEmail.setTextColor(COLOR_SUCCESS_GREEN)
                    reqEmail.text = "✓ Please use your UMak email (@umak.edu.ph)"
                    emailRequirementsContainer.visibility = View.VISIBLE

                    // 🟢 LIVE FEEDBACK: Set to Green border
                    //layoutEmail.error = null        // Clear red border
                    layoutEmail.boxStrokeColor = COLOR_SUCCESS_GREEN
                    layoutEmail.isActivated = true  // Triggers GREEN border
                }

                // ❌ INVALID FORMAT: Contains text but doesn't end with required domain
                text.isNotEmpty() && !text.endsWith("@umak.edu.ph", ignoreCase = true) -> {
                    reqEmail.setTextColor(COLOR_ERROR_RED)
                    reqEmail.text = "• Please use your UMak email (@umak.edu.ph)"
                        emailRequirementsContainer.visibility = View.VISIBLE

                        // 🔴 LIVE FEEDBACK: Set to Red border
                        layoutEmail.isActivated = false // Clear green border
                        layoutEmail.error = " "         // Triggers RED border
                    }

                    // 🩶 DEFAULT TYPING STATE: Empty or still typing
                    else -> {
                        reqEmail.setTextColor(COLOR_HINT_GRAY)
                        reqEmail.text = "• Please use your UMak email (@umak.edu.ph)"
                        emailRequirementsContainer.visibility = View.VISIBLE

                        // Reset to default/primary color border while actively typing
                        layoutEmail.isActivated = false
                        layoutEmail.error = null
                    }
                }
                updateLoginButtonState()
            }
        })

        // ---------------------------------------------------------------------
        // 🔹 PASSWORD FOCUS CHANGE HANDLING (NO CHANGE)
        // ---------------------------------------------------------------------

        inputPassword.setOnFocusChangeListener { _, hasFocus ->
            val password = inputPassword.text.toString()
            val isValid = isPasswordValid(password)

            if (hasFocus) {
                clearValidationState(layoutPassword)
            } else {
                when {
                    password.isEmpty() -> {
                        passwordRequirementsContainer.visibility = View.VISIBLE
                        reqPassword.setTextColor(COLOR_ERROR_RED)
                        reqPassword.text = "• Field is required"
                        layoutPassword.error = " "
                        layoutPassword.isActivated = false
                    }
                    isValid -> {
                        passwordRequirementsContainer.visibility = View.GONE
                        layoutPassword.error = null
                        layoutPassword.isActivated = true
                    }
                }
            }
            updateLoginButtonState()
        }

        // ---------------------------------------------------------------------
        // 🔹 PASSWORD REAL-TIME VALIDATION (NO CHANGE)
        // ---------------------------------------------------------------------

        inputPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearValidationState(layoutPassword)
                passwordRequirementsContainer.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                updateLoginButtonState()
            }
        })

        // =====================================================================
        // 🚨 3. LOGIN BUTTON CLICK LISTENER (Final Step) 🚨
        // =====================================================================

        btnLogin.setOnClickListener {
            hideKeyboardAndClearFocus()

            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString()

            // Validate inputs
            if (!isEmailValid(email) || !isPasswordValid(password)) {
                showLoginErrorDialog()
                return@setOnClickListener
            }

            // Disable button during login
            btnLogin.isEnabled = false

            // Firebase Authentication
            FirebaseAuthHelper.signIn(
                email = email,
                password = password,
                onSuccess = { user ->
                    layoutEmail.error = null
                    layoutPassword.error = null

                    // Fetch firstname from Firestore
                    val uid = user.uid
                    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    db.collection("users").document(uid)
                        .get()
                        .addOnSuccessListener { document ->
                            val firstName = document.getString("firstname") ?: "User"
                            showLoginSuccessDialog(firstName) // pass firstName only
                        }
                        .addOnFailureListener {
                            // fallback if fetch fails
                            showLoginSuccessDialog("User")
                        }
                },
                onFailure = { errorMessage ->
                    btnLogin.isEnabled = true
                    showLoginErrorDialog()
                }
            )
        }

    }
}
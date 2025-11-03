package com.example.umelec

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Base64
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity3 : AppCompatActivity() {

    // Views
    private lateinit var btnBack: ImageButton
    private lateinit var signaturePad: SignaturePad
    private lateinit var btnClearSignature: AppCompatButton
    private lateinit var tvReviewTnC: TextView
    private lateinit var cbAgree: CheckBox
    private lateinit var btnNext: Button

    private lateinit var firestore: FirebaseFirestore

    // State
    private var isSignatureDrawn: Boolean = false
    private var isTermsAgreed: Boolean = false

    private val LOGIN_ACTIVITY_CLASS = Login::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register3)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // --- ⭐️ SECTION 1: GET ALL DATA FROM INTENT ⭐️ ---
        // Retrieve previous registration data
        val studentID = intent.getStringExtra("studentID") ?: ""
        val firstname = intent.getStringExtra("firstname") ?: ""
        val lastname = intent.getStringExtra("lastname") ?: ""
        val gender = intent.getStringExtra("gender") ?: ""
        val year = intent.getStringExtra("year") ?: ""
        val college = intent.getStringExtra("college") ?: ""

        // ⭐️ ADDED: Get the UID and email passed from RegisterActivity 1 & 2
        val authUID = intent.getStringExtra("AUTH_UID")
        val email = intent.getStringExtra("email")
        // --- ⭐️ END SECTION 1 ⭐️ ---


        // Initialize Views
        btnBack = findViewById(R.id.btnBack)
        signaturePad = findViewById(R.id.signaturePad)
        btnClearSignature = findViewById(R.id.btnClearSignature)
        tvReviewTnC = findViewById(R.id.tvReviewTnC)
        cbAgree = findViewById(R.id.cbAgree)
        btnNext = findViewById(R.id.btnNext)

        // Setup initial state
        updateNextButtonState()
        btnClearSignature.isEnabled = false

        // Listeners
        btnBack.setOnClickListener { finish() }

        tvReviewTnC.setOnClickListener {
            Toast.makeText(this, "TnC in working", Toast.LENGTH_SHORT).show()
        }

        cbAgree.setOnCheckedChangeListener { _, isChecked ->
            isTermsAgreed = isChecked
            updateNextButtonState()
        }

        signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {}
            override fun onSigned() {
                isSignatureDrawn = true
                btnClearSignature.isEnabled = true
                updateNextButtonState()
            }
            override fun onClear() {
                isSignatureDrawn = false
                btnClearSignature.isEnabled = false
                updateNextButtonState()
            }
        })

        btnClearSignature.setOnClickListener { signaturePad.clear() }


        // --- ⭐️ SECTION 2: THE FULLY CORRECTED BUTTON LOGIC ⭐️ ---
        btnNext.setOnClickListener {
            if (isSignatureDrawn && isTermsAgreed) {

                // --- 1. (THE FIX) VALIDATE THE PASSED UID ---
                // We use the authUID passed from the intent, NOT auth.currentUser
                if (authUID == null || email == null) {
                    // Safety check: If UID or email is missing, the registration flow broke.
                    Toast.makeText(this, "Error: Registration session lost. Please start over.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, LOGIN_ACTIVITY_CLASS)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    return@setOnClickListener
                }

                // --- 2. CONVERT SIGNATURE (Your code was correct) ---
                val signatureBitmap = signaturePad.getSignatureBitmap()
                val byteArrayOutputStream = ByteArrayOutputStream()
                signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
                val signatureBase64 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)

                // --- 3. PREPARE PAYLOAD (Using variables from onCreate) ---
                val userData = hashMapOf(
                    "studentID" to studentID,
                    "firstname" to firstname,
                    "lastname" to lastname,
                    "gender" to gender,
                    "year" to year,
                    "college" to college,
                    "email" to email, // <-- ⭐️ USE THE PASSED EMAIL
                    "signature_data" to signatureBase64,
                    "agreement_status" to isTermsAgreed,
                    "registration_complete" to true,
                    "registration_timestamp" to System.currentTimeMillis()
                )

                // --- 4. (THE FIX) Save to Firestore using the PASSED AUTH UID ---
                firestore.collection("students")
                    .document(authUID) // <-- ⭐️ USE THE PASSED UID
                    .set(userData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Registration completed!", Toast.LENGTH_SHORT).show()
                        showRegistrationSuccessDialog()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to save registration: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
        // --- ⭐️ END SECTION 2 ⭐️ ---
    }

    private fun updateNextButtonState() {
        btnNext.isEnabled = isSignatureDrawn && isTermsAgreed
    }

    private fun showRegistrationSuccessDialog() {
        val layoutInflater = LayoutInflater.from(this)
        val dialogView = layoutInflater.inflate(R.layout.custom_toast_success, null)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(false)

        dialogView.findViewById<TextView>(R.id.toast_title).text = "Registration Success"
        dialogView.findViewById<TextView>(R.id.toast_value).text = "Your account has been created."

        val btnAction = dialogView.findViewById<Button>(R.id.btn_action)
        btnAction.text = "Continue to Login"

        btnAction.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, LOGIN_ACTIVITY_CLASS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        dialog.show()
    }
}
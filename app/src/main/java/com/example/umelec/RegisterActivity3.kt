package com.example.umelec

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Base64
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.github.gcacace.signaturepad.views.SignaturePad
import java.io.ByteArrayOutputStream

// REMINDER: You MUST add the following dependency to your app/build.gradle file:
// implementation 'com.github.gcacace:signature-pad:1.2.1'

class RegisterActivity3 : AppCompatActivity() {

    // Views
    private lateinit var btnBack: ImageButton
    private lateinit var signaturePad: SignaturePad // Updated to use the actual SignaturePad component
    private lateinit var btnClearSignature: AppCompatButton
    private lateinit var tvReviewTnC: TextView
    private lateinit var cbAgree: CheckBox
    private lateinit var btnNext: Button

    // State
    private var isSignatureDrawn: Boolean = false
    private var isTermsAgreed: Boolean = false

    // Assumed target class for successful registration
    private val LOGIN_ACTIVITY_CLASS = Login::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register3)

        // 1. Initialize Views
        btnBack = findViewById(R.id.btnBack)
        signaturePad = findViewById(R.id.signaturePad) // Initialized as SignaturePad
        btnClearSignature = findViewById(R.id.btnClearSignature)
        tvReviewTnC = findViewById(R.id.tvReviewTnC)
        cbAgree = findViewById(R.id.cbAgree)
        btnNext = findViewById(R.id.btnNext)

        // 2. Setup Initial State
        updateNextButtonState()
        btnClearSignature.isEnabled = false


        // 3. Set Listeners
        btnBack.setOnClickListener {
            finish() // Goes back to the previous activity (RegisterActivity2)
        }

        tvReviewTnC.setOnClickListener {
            // Shows a Toast as requested
            Toast.makeText(this, "TnC in working", Toast.LENGTH_SHORT).show()
        }

        cbAgree.setOnCheckedChangeListener { _, isChecked ->
            isTermsAgreed = isChecked
            updateNextButtonState()
        }

        // 4. Working Signature Pad Logic
        signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                // Not needed for state tracking, but useful for UX hints
            }

            override fun onSigned() {
                // Triggered when the user starts drawing and lifts their finger
                isSignatureDrawn = true
                btnClearSignature.isEnabled = true
                updateNextButtonState()
            }

            override fun onClear() {
                // Triggered when signaturePad.clear() is called
                isSignatureDrawn = false
                btnClearSignature.isEnabled = false
                updateNextButtonState()
            }
        })

        // 5. Clear Signature Button Logic
        btnClearSignature.setOnClickListener {
            signaturePad.clear() // Clears the canvas and triggers the onClear listener
        }

        // 6. Next Button Logic (Data Processing)
        btnNext.setOnClickListener {
            if (isSignatureDrawn && isTermsAgreed) {
                // --- BACKEND/DATABASE GUIDANCE START ---

                // 1. Capture the signature as a high-quality Bitmap
                val signatureBitmap: Bitmap = signaturePad.getSignatureBitmap()

                // 2. Convert Bitmap to a suitable format for transfer (e.g., Base64 String)
                val byteArrayOutputStream = ByteArrayOutputStream()
                // Use JPEG for smaller file size, quality 90 is a good balance
                signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                val signatureBase64String: String = Base64.encodeToString(byteArray, Base64.DEFAULT)

                // 3. 🚨 FAKE DATA LOGGING for Backend Team 🚨
                // At this point, you would send signatureBase64String along with
                // all other registration data (ID, name, etc.) to your backend API.
                println("--- FAKE DATABASE CALL ---")
                println("Signature captured and converted to Base64 String.")
                println("Payload for Backend:")
                println("  - signature_data: ${signatureBase64String.substring(0, 50)}... [Base64 of JPEG image]")
                println("  - agreement_status: $isTermsAgreed")
                println("  - (Other user data from previous steps)")

                // 4. Proceed to success dialog after successful API response (simulated here)
                // --- BACKEND/DATABASE GUIDANCE END ---

                showRegistrationSuccessDialog()
            }
        }
    }

    /**
     * Updates the enabled state of the Next button based on required conditions.
     */
    private fun updateNextButtonState() {
        // Button is enabled ONLY when a signature is drawn AND the terms are checked
        btnNext.isEnabled = isSignatureDrawn && isTermsAgreed
    }

    /**
     * Custom AlertDialog for Registration Success.
     */
    private fun showRegistrationSuccessDialog() {
        val layoutInflater = LayoutInflater.from(this)
        // Ensure you have R.layout.custom_toast_success in your resources
        val dialogView = layoutInflater.inflate(R.layout.custom_toast_success, null)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        // Set dialog properties
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(false)

        // Set custom title and value
        dialogView.findViewById<TextView>(R.id.toast_title).text = "Registration Success"
        dialogView.findViewById<TextView>(R.id.toast_value).text = "Your account has been created."

        val btnAction = dialogView.findViewById<Button>(R.id.btn_action)
        btnAction.text = "Continue to Login"

        btnAction.setOnClickListener {
            dialog.dismiss()

            // Proceed to Login Activity
            val intent = Intent(this, LOGIN_ACTIVITY_CLASS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        dialog.show()
    }
}
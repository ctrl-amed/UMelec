package com.example.umelec

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.view.Gravity
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout

class Profile : AppCompatActivity() {

    // Declare all UI elements
    private lateinit var emailValue: TextView
    private lateinit var studentIdValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var collegeValue: TextView
    private lateinit var statusValue: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var btnLogout: AppCompatButton

    // ⭐️ NEW: Declare TextViews for the new profile fields ⭐️
    private lateinit var profileAcronym: TextView
    private lateinit var moduleValue: TextView
    private lateinit var genderValue: TextView
    private lateinit var nameTitle: TextView // Add full name TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is logged in
        if (!FirebaseAuthHelper.isUserLoggedIn()) {
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_profile)

        // Initialize UI elements
        initializeViews()
        setupListeners()
        populateProfileData()
    }

    private fun initializeViews() {
        emailValue = findViewById(R.id.EmailValue)
        studentIdValue = findViewById(R.id.StudentIDValue)
        yearValue = findViewById(R.id.YearValue)
        collegeValue = findViewById(R.id.CollegeValue)
        statusValue = findViewById(R.id.statusValue)
        btnBack = findViewById(R.id.btnBack)
        btnLogout = findViewById(R.id.btnLogout)
        profileAcronym = findViewById(R.id.profileAcronym)
        moduleValue = findViewById(R.id.moduleValue)
        genderValue = findViewById(R.id.GenderValue)
        nameTitle = findViewById(R.id.nameTitle) // Initialize full name TextView
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }
        btnLogout.setOnClickListener { showLogoutConfirmationDialog() }
    }

    private fun showCustomSuccessToast() {
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.custom_toast_success, null)
        val titleText: TextView = layout.findViewById(R.id.toast_title)
        val valueText: TextView = layout.findViewById(R.id.toast_value)
        val actionButton: AppCompatButton = layout.findViewById(R.id.btn_action)

        titleText.text = "Logged out successfully."
        valueText.text = "Goodbye!"
        actionButton.visibility = View.GONE

        with (Toast(applicationContext)) {
            duration = Toast.LENGTH_SHORT
            setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)
            view = layout
            show()
        }
    }

    private fun showLogoutConfirmationDialog() {
        val customView = LayoutInflater.from(this).inflate(R.layout.custom_toast_question, null)
        val titleText: TextView = customView.findViewById(R.id.toast_title)
        val valueText: TextView = customView.findViewById(R.id.toast_value)
        val btnCancel: AppCompatButton = customView.findViewById(R.id.btn_action_primary)
        val btnConfirm: AppCompatButton = customView.findViewById(R.id.btn_action_secondary)

        titleText.text = "Are you sure you want to logout?"
        valueText.visibility = View.GONE
        btnCancel.text = "Cancel"
        btnConfirm.text = "Confirm"

        val dialog = AlertDialog.Builder(this)
            .setView(customView)
            .setCancelable(true)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnConfirm.setOnClickListener {
            showCustomSuccessToast()
            dialog.dismiss()
            performLogout()
        }

        dialog.show()
    }

    private fun performLogout() {
        FirebaseAuthHelper.signOut()
        FirebaseAuthHelper.clearTemporaryCredentials(this)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun populateProfileData() {
        val currentUser = FirebaseAuthHelper.getCurrentUser()
        if (currentUser == null) {
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        emailValue.text = currentUser.email ?: ""

        FirebaseAuthHelper.getUserDataFromFirestore(
            userId = currentUser.uid,
            onSuccess = { userData ->
                if (userData != null) {
                    val firstname = userData["firstname"] as? String ?: ""
                    val lastname = userData["lastname"] as? String ?: ""

                    // ✅ Set full name
                    nameTitle.text = "$firstname $lastname".trim()
                    profileAcronym.text = "${firstname.take(1)}${lastname.take(1)}".uppercase()
                    moduleValue.text = userData["moduleValue"] as? String ?: "Voter"
                    genderValue.text = userData["gender"] as? String ?: ""

                    studentIdValue.text = userData["studentId"] as? String ?: ""
                    yearValue.text = userData["year"] as? String ?: ""
                    collegeValue.text = userData["college"] as? String ?: ""
                    statusValue.text = userData["status"] as? String ?: "Ineligible"
                } else {
                    // Defaults
                    nameTitle.text = currentUser.email?.substringBefore("@") ?: "User"
                    profileAcronym.text = currentUser.email?.substring(0, 2)?.uppercase() ?: "UN"
                    moduleValue.text = "Voter"
                    genderValue.text = ""
                    studentIdValue.text = ""
                    yearValue.text = ""
                    collegeValue.text = ""
                    statusValue.text = "Ineligible"
                }
            },
            onFailure = {
                nameTitle.text = currentUser.email?.substringBefore("@") ?: "User"
                profileAcronym.text = currentUser.email?.substring(0, 2)?.uppercase() ?: "UN"
                moduleValue.text = "Voter"
                genderValue.text = ""
                studentIdValue.text = ""
                yearValue.text = ""
                collegeValue.text = ""
                statusValue.text = "Ineligible"
            }
        )
    }
}

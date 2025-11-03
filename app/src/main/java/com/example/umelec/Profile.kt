package com.example.umelec

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Profile : AppCompatActivity() {

    private lateinit var emailValue: TextView
    private lateinit var studentIdValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var collegeValue: TextView
    private lateinit var statusValue: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var btnLogout: AppCompatButton

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views and listeners
        initializeViews()
        setupListeners()

        // Fetch and display the current user's info
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
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }
        btnLogout.setOnClickListener { showLogoutConfirmationDialog() }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Log Out Confirmation")
            .setMessage("Are you sure you want to log out of your account?")
            .setPositiveButton("Log Out") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun performLogout() {
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun populateProfileData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid

            // Fetch from Firestore "students" collection
            db.collection("students").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        emailValue.text = document.getString("email") ?: "N/A"
                        studentIdValue.text = document.getString("studentID") ?: "N/A"
                        yearValue.text = document.getString("year") ?: "N/A"
                        collegeValue.text = document.getString("college") ?: "N/A"
                        statusValue.text = document.getString("status") ?: "Active"
                    } else {
                        emailValue.text = "N/A"
                        studentIdValue.text = "N/A"
                        yearValue.text = "N/A"
                        collegeValue.text = "N/A"
                        statusValue.text = "N/A"
                    }
                }
                .addOnFailureListener {
                    emailValue.text = "Error loading"
                    studentIdValue.text = "-"
                    yearValue.text = "-"
                    collegeValue.text = "-"
                    statusValue.text = "-"
                }
        } else {
            emailValue.text = "Not logged in"
            studentIdValue.text = "-"
            yearValue.text = "-"
            collegeValue.text = "-"
            statusValue.text = "-"
        }
    }
}

package com.example.umelec

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Profile : AppCompatActivity() {

    private lateinit var emailValue: TextView
    private lateinit var studentIdValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var collegeValue: TextView
    private lateinit var statusValue: TextView
    private lateinit var btnBack: ImageButton

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

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
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }
    }

    private fun populateProfileData() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    emailValue.text = document.getString("email") ?: ""
                    studentIdValue.text = document.getString("student_id") ?: ""
                    yearValue.text = document.getString("year") ?: ""
                    collegeValue.text = document.getString("college") ?: ""

                    // ✅ Use the correct Firestore field: voting_status
                    val votingStatus = document.getLong("voting_status")?.toInt() ?: 0
                    statusValue.text = if (votingStatus == 0) "Not Voted" else "Already Voted"
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load profile: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}

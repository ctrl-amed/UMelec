package com.example.umelec

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Profile : AppCompatActivity() {

    // Declare your TextViews here so they can be accessed throughout the class (optional but often helpful)
    private lateinit var emailValue: TextView
    private lateinit var studentIdValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var collegeValue: TextView
    private lateinit var statusValue: TextView
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure this layout file name is correct. In your XML, the context is .RegisterActivity,
        // but your class is Profile. The layout should probably be activity_profile.xml.
        setContentView(R.layout.activity_profile) // Assuming your XML file is named activity_profile.xml

        // 1. Initialize all the UI elements by finding them by their ID
        initializeViews()

        // 2. Set up the back button listener
        setupListeners()

        // 3. Populate the profile data (easy for backend/database integration)
        //    *This is where you'd typically fetch data from a database or intent*
        populateProfileData()
    }

    private fun initializeViews() {
        // Find the TextViews for profile data
        emailValue = findViewById(R.id.EmailValue)
        studentIdValue = findViewById(R.id.StudentIDValue)
        yearValue = findViewById(R.id.YearValue)
        collegeValue = findViewById(R.id.CollegeValue)
        statusValue = findViewById(R.id.statusValue)

        // Find the ImageButton
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupListeners() {
        // Set an OnClickListener for the back button
        btnBack.setOnClickListener {
            // When the user clicks btnBack, this will finish the current activity
            // and return to the previous activity on the back stack.
            finish()
        }
    }

    /**
     * This function demonstrates how to set the text on your TextViews.
     * In a real application, you would replace the hardcoded strings
     * with data retrieved from a database, API, or passed via an Intent.
     */
    fun populateProfileData() {
        // Example data structure (you'd replace this with your actual data source)
        val userProfile = mapOf(
            "email" to "user.new@example.com", // This line changes the email value
            "studentId" to "S98765432",        // This line changes the Student ID value
            "year" to "4th year",              // This line changes the Year value
            "college" to "CITCS",              // This line changes the College value
            "status" to "Ineligible"           // This line changes the Status value
        )

        // Set the text of each TextView using the retrieved data
        emailValue.text = userProfile["email"]
        studentIdValue.text = userProfile["studentId"]
        yearValue.text = userProfile["year"]
        collegeValue.text = userProfile["college"]
        statusValue.text = userProfile["status"]

        // For actual database integration (e.g., using Firebase, Room, or a REST API),
        // you would call your data fetching logic here and then update the TextViews
        // inside the data fetch success callback.
    }
}
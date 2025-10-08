package com.example.umelec

import android.widget.ImageButton
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Filter
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Button // NOTE: Added missing import for Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.content.Intent // NOTE: Added missing import for Intent (for btnConfirm)


class RegisterActivity2 : AppCompatActivity() {
    // Declare the constants here
    companion object {
        const val MIN_NAME_LENGTH = 2
        const val MAX_NAME_LENGTH = 50
        // NOTE: Regex cannot be 'const val', so use 'val' here
        val VALID_NAME_PATTERN = Regex("^[a-zA-Z\\s'-]+\$")
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register2)


        // 🔹 Back button
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        // =====================================================================
        // 🚨 1. GLOBAL CONSTANTS AND VIEW INITIALIZATION BLOCK 🚨
        // =====================================================================

        // Define color constants for readability (Recommended Android practice)
        val COLOR_PRIMARY_BLUE = Color.parseColor("#0039A6") // Focus/Typing/Info
        val COLOR_ERROR_RED = Color.parseColor("#D32F2F")     // Error
        val COLOR_SUCCESS_GREEN = Color.parseColor("#4CAF50") // Valid
        val COLOR_HINT_GRAY = Color.parseColor("#8C8CA1")     // Default Hint

        // Define the required pattern: 1 uppercase letter followed by 8 digits
        val VALID_ID_PATTERN = Regex("^[A-Z][0-9]{8}\$")

        // ---------------------------------------------------------------------
        // 1.1 Text Field View Initialization
        // ---------------------------------------------------------------------
        val layoutStudentID = findViewById<TextInputLayout>(R.id.textInputLayoutStudentID)
        val inputStudentID = findViewById<TextInputEditText>(R.id.inputStudentID)
        val studentIDRequirementsContainer = findViewById<View>(R.id.StudentIDRequirements)
        val reqStudentID = findViewById<TextView>(R.id.reqStudentID)

        val layoutFirstname = findViewById<TextInputLayout>(R.id.textInputLayoutFirstname)
        val inputFirstname = findViewById<TextInputEditText>(R.id.inputFirstname)
        val firstnameRequirementsContainer = findViewById<View>(R.id.FirstnameRequirements)
        val reqFirstname = findViewById<TextView>(R.id.reqFirstname)

        val layoutLastname = findViewById<TextInputLayout>(R.id.textInputLayoutLastname)
        val inputLastname = findViewById<TextInputEditText>(R.id.inputLastname)
        val lastnameRequirementsContainer = findViewById<View>(R.id.LastnameRequirements)
        val reqLastname = findViewById<TextView>(R.id.reqLastname)

        // ---------------------------------------------------------------------
        // 1.2 Dropdown View Initialization and Setup
        // ---------------------------------------------------------------------
        val layoutCollege = findViewById<TextInputLayout>(R.id.textInputLayoutCollege)
        val inputCollege = findViewById<AutoCompleteTextView>(R.id.inputCollege)
        val collegeRequirementsContainer = findViewById<View>(R.id.CollegeRequirements)
        val reqCollege = findViewById<TextView>(R.id.reqCollege)

        val colleges = listOf(
            "College of Liberal Arts and Sciences (CLAS)",
            "College of Human Kinetics (CHK)",
            "College of Continuing, Advanced and Professional Studies (CCAPS)",
        "College of Business and Financial Science (CBFS)",
        "Institute of Arts and Design (IAD)",
        "College of Innovative Teacher Education (CITE)",
        "College of Computing and Information Sciences (CCIS)",
        "Institute of Technical Education and Skills Training (ITEST)"
        )

        val collegeAdapter = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            colleges
        )
        inputCollege.setAdapter(collegeAdapter)

        val layoutYear = findViewById<TextInputLayout>(R.id.textInputLayoutYear)
        val inputYear = findViewById<AutoCompleteTextView>(R.id.inputYear)
        val yearRequirementsContainer = findViewById<View>(R.id.YearRequirements)
        val reqYear = findViewById<TextView>(R.id.reqYear)

        val yearLevels = listOf(
            "1st Year",
        "2nd Year",
        "3rd Year",
        "4th Year"
        )

        val yearAdapter = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            yearLevels
        )
        inputYear.setAdapter(yearAdapter)

        // ---------------------------------------------------------------------
        // 1.3 Button Initialization
        // ---------------------------------------------------------------------
        val btnConfirm = findViewById<Button>(R.id.btnConfirm)
        btnConfirm.isEnabled = false // Initial State Setup [cite: 2]


        // =====================================================================
        // 🚨 2. VALIDATION HELPER FUNCTIONS BLOCK 🚨
        // (Must be defined before they are used in listeners)
        // =====================================================================

        // Firstname validation check function
        fun isFirstnameValid(name: String): Boolean {
            return name.isNotEmpty() &&
                    name.length >= MIN_NAME_LENGTH &&
            name.length <= MAX_NAME_LENGTH &&
                    VALID_NAME_PATTERN.matches(name)
        }

        // Lastname validation check function
        fun isLastnameValid(name: String): Boolean {
            return name.isNotEmpty() &&
                    name.length >= MIN_NAME_LENGTH &&
            name.length <= MAX_NAME_LENGTH &&
                    VALID_NAME_PATTERN.matches(name)
        }

        // College validation check function
        fun isCollegeValid(selection: String): Boolean {
            return selection.isNotEmpty() && colleges.contains(selection)
        }

        // Year validation check function
        fun isYearValid(selection: String): Boolean {
            return selection.isNotEmpty() && yearLevels.contains(selection)
        }

        // Helper function to update Confirm button state
        fun updateConfirmButtonState() {
            // 1. Get current values from all fields
            val studentID = inputStudentID.text.toString().trim()
            val firstname = inputFirstname.text.toString().trim()
            val lastname = inputLastname.text.toString().trim()
            val collegeSelection = inputCollege.text.toString().trim()
            val yearSelection = inputYear.text.toString().trim()

            // 2. Check validity
            val isIDValid = VALID_ID_PATTERN.matches(studentID)
            val isFirstnameValid = isFirstnameValid(firstname)
            val isLastnameValid = isLastnameValid(lastname)
            val isCollegeValid = isCollegeValid(collegeSelection)
            val isYearValid = isYearValid(yearSelection)

            // 3. Combine all valid checks
            val allFieldsValid = isIDValid && isFirstnameValid && isLastnameValid && isCollegeValid && isYearValid

            // 4. Set the button state
            btnConfirm.isEnabled = allFieldsValid
        }


        // =====================================================================
        // 🚨 3. VALIDATION LISTENERS BLOCK 🚨
        // =====================================================================

        // ---------------------------------------------------------------------
        // 🔹 STUDENT ID VALIDATION LISTENERS 
        // ---------------------------------------------------------------------
        studentIDRequirementsContainer.visibility = View.GONE

        inputStudentID.setOnFocusChangeListener { _, hasFocus ->
            val studentID = inputStudentID.text.toString().trim()
            val isValid = VALID_ID_PATTERN.matches(studentID)

            if (hasFocus) {
                studentIDRequirementsContainer.visibility = View.VISIBLE
                if (isValid) {
                    reqStudentID.setTextColor(COLOR_SUCCESS_GREEN)
                    reqStudentID.text = "Valid Student ID"
                    layoutStudentID.boxStrokeColor = COLOR_SUCCESS_GREEN
                } else {
                    reqStudentID.setTextColor(COLOR_HINT_GRAY)
                    reqStudentID.text = "Valid ID: K12345678"
                    layoutStudentID.boxStrokeColor = COLOR_PRIMARY_BLUE
                }
            } else {
                if (studentID.isEmpty()) {
                    studentIDRequirementsContainer.visibility = View.VISIBLE
                    reqStudentID.setTextColor(COLOR_ERROR_RED)
                    reqStudentID.text = "Field is required"
                    layoutStudentID.boxStrokeColor = COLOR_ERROR_RED
                } else if (isValid) {
                    studentIDRequirementsContainer.visibility = View.GONE
                    layoutStudentID.boxStrokeColor = COLOR_SUCCESS_GREEN
                } else {
                    studentIDRequirementsContainer.visibility = View.VISIBLE
                    layoutStudentID.boxStrokeColor = COLOR_ERROR_RED
                }
            }
            // Update button state on blur/focus change
            updateConfirmButtonState()
        }

        inputStudentID.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                var text = s.toString().trim()

                // 1. 🔠 Auto uppercase
                if (text != text.uppercase()) {
                    inputStudentID.removeTextChangedListener(this)
                    inputStudentID.setText(text.uppercase())
                    inputStudentID.setSelection(inputStudentID.text?.length ?: 0)
                    inputStudentID.addTextChangedListener(this)
                    return
                }

                // 2. 🔹 Validation Logic
                when {
                    // 🚫 ID is too long
                    text.length > 9 -> {
                        reqStudentID.setTextColor(COLOR_ERROR_RED)
                        reqStudentID.text = "Invalid Student ID (too long)"
                        layoutStudentID.boxStrokeColor = COLOR_ERROR_RED
                    }

                    // ✅ ID is complete AND matches the pattern
                    VALID_ID_PATTERN.matches(text) -> {
                        reqStudentID.setTextColor(COLOR_SUCCESS_GREEN)
                        reqStudentID.text = "Valid Student ID"
                        layoutStudentID.boxStrokeColor = COLOR_SUCCESS_GREEN
                        studentIDRequirementsContainer.visibility = View.VISIBLE
                    }

                    // ❌ ID has started but does not follow the pattern
                    text.isNotEmpty() && !text.matches(Regex("^[A-Z]?[0-9]{0,8}\$")) -> {
                        val letterCount = text.count { it.isLetter() }
                        val digitCount = text.count { it.isDigit() }

                        if (letterCount > 1 || (letterCount == 1 && !text.first().isLetter()) || digitCount > 8) {
                            reqStudentID.setTextColor(COLOR_ERROR_RED)
                            reqStudentID.text = "Invalid Student ID (Format: 1 Letter, 8 Digits)"
                            layoutStudentID.boxStrokeColor = COLOR_ERROR_RED
                            studentIDRequirementsContainer.visibility = View.VISIBLE
                        } else {
                            reqStudentID.setTextColor(COLOR_HINT_GRAY)
                            reqStudentID.text = "Valid ID: K12345678"
                            layoutStudentID.boxStrokeColor = COLOR_PRIMARY_BLUE
                            studentIDRequirementsContainer.visibility = View.VISIBLE
                        }
                    }

                    // 🩶 Still typing, following the pattern (less than 9)
                    text.isNotEmpty() && text.length < 9 -> {
                        reqStudentID.setTextColor(COLOR_HINT_GRAY)
                        reqStudentID.text = "Valid ID: K12345678"
                        layoutStudentID.boxStrokeColor = COLOR_PRIMARY_BLUE
                        studentIDRequirementsContainer.visibility = View.VISIBLE
                    }

                    // Fallback for empty/other scenarios while focused
                    else -> {
                        reqStudentID.setTextColor(COLOR_HINT_GRAY)
                        reqStudentID.text = "Valid ID: K12345678"
                        layoutStudentID.boxStrokeColor = COLOR_PRIMARY_BLUE
                        studentIDRequirementsContainer.visibility = View.VISIBLE
                    }
                }
                // Update button state after any change
                updateConfirmButtonState()
            }
        })


        // ---------------------------------------------------------------------
        // 🔹 FIRSTNAME VALIDATION LISTENERS
        // ---------------------------------------------------------------------
        firstnameRequirementsContainer.visibility = View.GONE

        inputFirstname.setOnFocusChangeListener { _, hasFocus ->
            val firstname = inputFirstname.text.toString().trim()
            val isValid = isFirstnameValid(firstname)

            if (hasFocus) {
                firstnameRequirementsContainer.visibility = View.VISIBLE
                if (isValid) {
                    reqFirstname.setTextColor(COLOR_SUCCESS_GREEN)
                    reqFirstname.text = "Valid Firstname"
                    layoutFirstname.boxStrokeColor = COLOR_SUCCESS_GREEN // SET GREEN
                } else {
                    reqFirstname.setTextColor(COLOR_HINT_GRAY)
                    reqFirstname.text = "Name must be 2-${MAX_NAME_LENGTH} letters, spaces, hyphens ('-') or apostrophes (')."
                    layoutFirstname.boxStrokeColor = COLOR_PRIMARY_BLUE // SET BLUE
                }
            } else {
                if (firstname.isEmpty()) {
                    firstnameRequirementsContainer.visibility = View.VISIBLE
                    reqFirstname.setTextColor(COLOR_ERROR_RED)
                    reqFirstname.text = "Field is required"
                    layoutFirstname.boxStrokeColor = COLOR_ERROR_RED
                } else if (isValid) {
                    firstnameRequirementsContainer.visibility = View.GONE
                    layoutFirstname.boxStrokeColor = COLOR_SUCCESS_GREEN
                } else {
                    firstnameRequirementsContainer.visibility = View.VISIBLE
                    layoutFirstname.boxStrokeColor = COLOR_ERROR_RED
                }
            }
            // Update button state on blur/focus change
            updateConfirmButtonState()
        }

        inputFirstname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                var text = s.toString()

                // 1. 🔠 Auto capitalize first letter of each word
                val capitalizedText = text.lowercase().split(' ').joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

                if (text != capitalizedText) {
                    inputFirstname.removeTextChangedListener(this)
                    inputFirstname.setText(capitalizedText)
                    inputFirstname.setSelection(capitalizedText.length)
                    inputFirstname.addTextChangedListener(this)
                    return
                }

                // 2. 🔹 Validation Logic
                when {
                    // 🚫 Too short
                    text.length < MIN_NAME_LENGTH -> {
                        reqFirstname.setTextColor(COLOR_ERROR_RED)
                        reqFirstname.text = "Firstname must be at least ${MIN_NAME_LENGTH} characters."
                        layoutFirstname.boxStrokeColor = COLOR_ERROR_RED
                    }

                    // 🚫 Too long
                    text.length > MAX_NAME_LENGTH -> {
                        reqFirstname.setTextColor(COLOR_ERROR_RED)
                        reqFirstname.text = "Firstname must be no more than ${MAX_NAME_LENGTH} characters."
                        layoutFirstname.boxStrokeColor = COLOR_ERROR_RED
                    }

                    // 🚫 Invalid characters
                    !VALID_NAME_PATTERN.matches(text) -> {
                        reqFirstname.setTextColor(COLOR_ERROR_RED)
                        reqFirstname.text = "Only letters, spaces, hyphens, and apostrophes are allowed."
                        layoutFirstname.boxStrokeColor = COLOR_ERROR_RED
                    }

                    // ✅ All good
                    isFirstnameValid(text) -> {
                        reqFirstname.setTextColor(COLOR_SUCCESS_GREEN)
                        reqFirstname.text = "Valid Firstname"
                        layoutFirstname.boxStrokeColor = COLOR_SUCCESS_GREEN
                        firstnameRequirementsContainer.visibility = View.VISIBLE
                    }

                    // 🩶 Default typing state
                    else -> {
                        reqFirstname.setTextColor(COLOR_HINT_GRAY)
                        reqFirstname.text = "Name must be 2-${MAX_NAME_LENGTH} letters, spaces, hyphens ('-') or apostrophes (')."
                        layoutFirstname.boxStrokeColor = COLOR_PRIMARY_BLUE
                        firstnameRequirementsContainer.visibility = View.VISIBLE
                    }
                }
                // Update button state after any change
                updateConfirmButtonState()
            }
        })

        // ---------------------------------------------------------------------
        // 🔹 LASTNAME VALIDATION LISTENERS
        // ---------------------------------------------------------------------
        lastnameRequirementsContainer.visibility = View.GONE

        inputLastname.setOnFocusChangeListener { _, hasFocus ->
            val lastname = inputLastname.text.toString().trim()
            val isValid = isLastnameValid(lastname)

            if (hasFocus) {
                lastnameRequirementsContainer.visibility = View.VISIBLE
                if (isValid) {
                    reqLastname.setTextColor(COLOR_SUCCESS_GREEN)
                    reqLastname.text = "Valid Lastname"
                    layoutLastname.boxStrokeColor = COLOR_PRIMARY_BLUE
                } else {
                    reqLastname.setTextColor(COLOR_HINT_GRAY)
                    reqLastname.text = "Name must be 2-${MAX_NAME_LENGTH} letters, spaces, hyphens ('-') or apostrophes (')."
                    layoutLastname.boxStrokeColor = COLOR_PRIMARY_BLUE
                }
            } else {
                if (lastname.isEmpty()) {
                    lastnameRequirementsContainer.visibility = View.VISIBLE
                    reqLastname.setTextColor(COLOR_ERROR_RED)
                    reqLastname.text = "Field is required"
                    layoutLastname.boxStrokeColor = COLOR_ERROR_RED
                } else if (isValid) {
                    lastnameRequirementsContainer.visibility = View.GONE
                    layoutLastname.boxStrokeColor = COLOR_SUCCESS_GREEN
                } else {
                    lastnameRequirementsContainer.visibility = View.VISIBLE
                    layoutLastname.boxStrokeColor = COLOR_ERROR_RED
                }
            }
            // Update button state on blur/focus change
            updateConfirmButtonState()
        }

        inputLastname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                var text = s.toString()

                // 1. 🔠 Auto capitalize first letter of each word
                val capitalizedText = text.lowercase().split(' ').joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

                if (text != capitalizedText) {
                    inputLastname.removeTextChangedListener(this)
                    inputLastname.setText(capitalizedText)
                    inputLastname.setSelection(capitalizedText.length)
                    inputLastname.addTextChangedListener(this)
                    return
                }

                // 2. 🔹 Validation Logic
                when {
                    // 🚫 Too short
                    text.length < MIN_NAME_LENGTH -> {
                        reqLastname.setTextColor(COLOR_ERROR_RED)
                        reqLastname.text = "Lastname must be at least ${MIN_NAME_LENGTH} characters."
                        layoutLastname.boxStrokeColor = COLOR_ERROR_RED
                    }

                    // 🚫 Too long
                    text.length > MAX_NAME_LENGTH -> {
                        reqLastname.setTextColor(COLOR_ERROR_RED)
                        reqLastname.text = "Lastname must be no more than ${MAX_NAME_LENGTH} characters."
                        layoutLastname.boxStrokeColor = COLOR_ERROR_RED
                    }

                    // 🚫 Invalid characters
                    !VALID_NAME_PATTERN.matches(text) -> {
                        reqLastname.setTextColor(COLOR_ERROR_RED)
                        reqLastname.text = "Only letters, spaces, hyphens, and apostrophes are allowed."
                        layoutLastname.boxStrokeColor = COLOR_ERROR_RED
                    }

                    // ✅ All good
                    isLastnameValid(text) -> {
                        reqLastname.setTextColor(COLOR_SUCCESS_GREEN)
                        reqLastname.text = "Valid Lastname"
                        layoutLastname.boxStrokeColor = COLOR_SUCCESS_GREEN
                        lastnameRequirementsContainer.visibility = View.VISIBLE
                    }

                    // 🩶 Default typing state
                    else -> {
                        reqLastname.setTextColor(COLOR_HINT_GRAY)
                        reqLastname.text = "Name must be 2-${MAX_NAME_LENGTH} letters, spaces, hyphens ('-') or apostrophes (')."
                        layoutLastname.boxStrokeColor = COLOR_PRIMARY_BLUE
                        lastnameRequirementsContainer.visibility = View.VISIBLE
                    }
                }
                // Update button state after any change
                updateConfirmButtonState()
            }
        })

        // ---------------------------------------------------------------------
        // 🔹 COLLEGE DROPDOWN VALIDATION LISTENERS
        // ---------------------------------------------------------------------
        collegeRequirementsContainer.visibility = View.GONE
        inputCollege.setOnFocusChangeListener { _, hasFocus ->
            val collegeSelection = inputCollege.text.toString().trim()
            val isValid = isCollegeValid(collegeSelection)

            if (hasFocus) {
                collegeRequirementsContainer.visibility = View.VISIBLE
                layoutCollege.boxStrokeColor = COLOR_PRIMARY_BLUE

                if (isValid) {
                    reqCollege.setTextColor(COLOR_SUCCESS_GREEN)
                    reqCollege.text = "College selected"
                } else {
                    reqCollege.setTextColor(COLOR_HINT_GRAY)
                    reqCollege.text = "Select a college from the list"
                }
            } else {
                if (collegeSelection.isEmpty()) {
                    collegeRequirementsContainer.visibility = View.VISIBLE
                    reqCollege.setTextColor(COLOR_ERROR_RED)
                    reqCollege.text = "Field is required"
                    layoutCollege.boxStrokeColor = COLOR_ERROR_RED
                } else if (isValid) {
                    collegeRequirementsContainer.visibility = View.GONE
                    layoutCollege.boxStrokeColor = COLOR_SUCCESS_GREEN
                } else {
                    collegeRequirementsContainer.visibility = View.VISIBLE
                    layoutCollege.boxStrokeColor = COLOR_ERROR_RED
                }
            }
            // Update button state on blur/focus change
            updateConfirmButtonState()
        }

        inputCollege.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString().trim()

                if (text.isEmpty()) {
                    reqCollege.setTextColor(COLOR_HINT_GRAY)
                    reqCollege.text = "Select a college from the list"
                    layoutCollege.boxStrokeColor = COLOR_PRIMARY_BLUE
                    collegeRequirementsContainer.visibility = View.VISIBLE
                } else if (colleges.contains(text)) {
                    reqCollege.setTextColor(COLOR_SUCCESS_GREEN)
                    reqCollege.text = "College selected"
                    layoutCollege.boxStrokeColor = COLOR_SUCCESS_GREEN
                    collegeRequirementsContainer.visibility = View.VISIBLE
                } else {
                    reqCollege.setTextColor(COLOR_ERROR_RED)
                    reqCollege.text = "Invalid selection. Please choose from the options."
                    layoutCollege.boxStrokeColor = COLOR_ERROR_RED
                    collegeRequirementsContainer.visibility = View.VISIBLE
                }
                // Update button state after any change
                updateConfirmButtonState()
            }
        })

        // ---------------------------------------------------------------------
        // 🔹 YEAR DROPDOWN VALIDATION LISTENERS
        // ---------------------------------------------------------------------
        yearRequirementsContainer.visibility = View.GONE

        inputYear.setOnFocusChangeListener { _, hasFocus ->
            val yearSelection = inputYear.text.toString().trim()
            val isValid = isYearValid(yearSelection)

            if (hasFocus) {
                yearRequirementsContainer.visibility = View.VISIBLE
                layoutYear.boxStrokeColor = COLOR_PRIMARY_BLUE

                if (isValid) {
                    reqYear.setTextColor(COLOR_SUCCESS_GREEN)
                    reqYear.text = "Year level selected"
                } else {
                    reqYear.setTextColor(COLOR_HINT_GRAY)
                    reqYear.text = "Select a year level from the options"
                }
            } else {
                if (yearSelection.isEmpty()) {
                    yearRequirementsContainer.visibility = View.VISIBLE
                    reqYear.setTextColor(COLOR_ERROR_RED)
                    reqYear.text = "Field is required"
                    layoutYear.boxStrokeColor = COLOR_ERROR_RED
                } else if (isValid) {
                    yearRequirementsContainer.visibility = View.GONE
                    layoutYear.boxStrokeColor = COLOR_SUCCESS_GREEN
                } else {
                    yearRequirementsContainer.visibility = View.VISIBLE
                    layoutYear.boxStrokeColor = COLOR_ERROR_RED
                }
            }
            // Update button state on blur/focus change
            updateConfirmButtonState()
        }

        inputYear.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString().trim()

                if (text.isEmpty()) {
                    reqYear.setTextColor(COLOR_HINT_GRAY)
                    reqYear.text = "Select a year level from the options"
                    layoutYear.boxStrokeColor = COLOR_PRIMARY_BLUE
                    yearRequirementsContainer.visibility = View.VISIBLE
                } else if (yearLevels.contains(text)) {
                    reqYear.setTextColor(COLOR_SUCCESS_GREEN)
                    reqYear.text = "Year level selected"
                    layoutYear.boxStrokeColor = COLOR_SUCCESS_GREEN
                    yearRequirementsContainer.visibility = View.VISIBLE
                } else {
                    reqYear.setTextColor(COLOR_ERROR_RED)
                    reqYear.text = "Invalid selection. Please choose from the options."
                    layoutYear.boxStrokeColor = COLOR_ERROR_RED
                    yearRequirementsContainer.visibility = View.VISIBLE
                }
                // Update button state after any change
                updateConfirmButtonState()
            }
        })

        // ---------------------------------------------------------------------
        // 🔹 BUTTON CLICK LISTENER (Final step)
        // ---------------------------------------------------------------------

        btnConfirm.setOnClickListener {
            // Navigate to RegistrationSuccessful activity
            // NOTE: You will need to create the RegistrationSuccessful class
            val intent = Intent(this, RegistrationSuccessful::class.java)
            startActivity(intent)
        }
    }
}
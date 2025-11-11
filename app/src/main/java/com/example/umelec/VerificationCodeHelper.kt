package com.example.umelec

import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.random.Random

/**
 * Helper class for managing password reset verification codes
 */
object VerificationCodeHelper {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private const val VERIFICATION_CODES_COLLECTION = "passwordResetCodes"
    private const val CODE_EXPIRY_MINUTES = 10L
    
    /**
     * Generate a 6-digit verification code
     */
    fun generateCode(): String {
        return String.format("%06d", Random.nextInt(0, 1000000))
    }
    
    /**
     * Save verification code to Firestore with expiration
     * 
     * @param email User's email address
     * @param code 6-digit verification code
     * @param onSuccess Callback when code is saved successfully
     * @param onFailure Callback when code saving fails
     */
    fun saveVerificationCode(
        email: String,
        code: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val expirationTime = Calendar.getInstance().apply {
            add(Calendar.MINUTE, CODE_EXPIRY_MINUTES.toInt())
        }.time
        
        val codeData = hashMapOf<String, Any>(
            "email" to email,
            "code" to code,
            "createdAt" to Date(),
            "expiresAt" to expirationTime,
            "used" to false
        )
        
        // Use email as document ID to ensure one code per email at a time
        firestore.collection(VERIFICATION_CODES_COLLECTION)
            .document(email)
            .set(codeData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to save verification code")
            }
    }
    
    /**
     * Verify verification code
     * 
     * @param email User's email address
     * @param code Verification code to verify
     * @param onSuccess Callback when code is valid
     * @param onFailure Callback when code is invalid or expired
     */
    fun verifyCode(
        email: String,
        code: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection(VERIFICATION_CODES_COLLECTION)
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val data = document.data
                    val storedCode = data?.get("code") as? String
                    val expiresAtTimestamp = data?.get("expiresAt")
                    val expiresAt = when (expiresAtTimestamp) {
                        is com.google.firebase.Timestamp -> expiresAtTimestamp.toDate()
                        is Date -> expiresAtTimestamp
                        else -> null
                    }
                    val used = data?.get("used") as? Boolean ?: false
                    
                    val now = Date()
                    
                    when {
                        used -> {
                            onFailure("This code has already been used")
                        }
                        expiresAt == null || now.after(expiresAt) -> {
                            onFailure("This code has expired. Please request a new code.")
                        }
                        storedCode != code -> {
                            onFailure("Invalid verification code")
                        }
                        else -> {
                            // Mark code as used
                            document.reference.update("used", true)
                                .addOnSuccessListener {
                                    onSuccess()
                                }
                                .addOnFailureListener { exception ->
                                    onFailure(exception.message ?: "Failed to verify code")
                                }
                        }
                    }
                } else {
                    onFailure("No verification code found for this email. Please request a new code.")
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to verify code")
            }
    }
    
    /**
     * Resend verification code (delete old and create new)
     * 
     * @param email User's email address
     * @param code New 6-digit verification code
     * @param onSuccess Callback when code is saved successfully
     * @param onFailure Callback when code saving fails
     */
    fun resendCode(
        email: String,
        code: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Delete old code if exists, then save new one
        firestore.collection(VERIFICATION_CODES_COLLECTION)
            .document(email)
            .delete()
            .addOnSuccessListener {
                // Save new code
                saveVerificationCode(email, code, onSuccess, onFailure)
            }
            .addOnFailureListener {
                // Even if delete fails, try to save new code (will overwrite)
                saveVerificationCode(email, code, onSuccess, onFailure)
            }
    }
}


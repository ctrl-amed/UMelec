package com.example.umelec

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay for a few seconds then check authentication state
        android.os.Handler().postDelayed({
            // Check if user is logged in
            if (FirebaseAuthHelper.isUserLoggedIn()) {
                // User is logged in, go to Homepage
                startActivity(Intent(this, Homepage::class.java))
            } else {
                // User is not logged in, go to MainActivity (login/register screen)
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }, 2000)  // 2-second splash
    }
}

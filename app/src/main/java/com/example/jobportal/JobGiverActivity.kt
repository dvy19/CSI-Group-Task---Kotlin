package com.example.jobportal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class JobGiverActivity : AppCompatActivity() {

    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.job_giver_home)
        preferences = AppPreferences(this)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val tvTokenInfo = findViewById<TextView>(R.id.tvTokenInfo)

        val addJob=findViewById<Button>(R.id.add_job)

        addJob.setOnClickListener(){
            val intent=Intent(this, AddJobActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Get saved tokens
        val accessToken = preferences.getAccessToken()
        val refreshToken = preferences.getRefreshToken()

        tvWelcome.text = "Welcome! You're logged in."

        // Show token info (for debugging)
        tvTokenInfo.text = """
            Access Token: ${accessToken?.take(20)}...
            Refresh Token: ${refreshToken?.take(20)}...
        """.trimIndent()

        println("DEBUG: Access Token: $accessToken")
        println("DEBUG: Refresh Token: $refreshToken")
    }
}
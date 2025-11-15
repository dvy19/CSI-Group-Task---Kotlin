package com.example.jobportal

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class JobSeekerActivity : AppCompatActivity() {

    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.job_giver_home)

        preferences = AppPreferences(this)




    }
}
package com.example.jobportal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AfterLoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.after_login)

        val profile_update=findViewById<Button>(R.id.profile_update)
        profile_update.setOnClickListener(){
            val intent= Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

}
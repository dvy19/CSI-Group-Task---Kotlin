package com.example.jobportal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottom_nav_view)

        // Set HomeFragment as default when app starts
        replaceFragment(HomeFragment())

        // Setup Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_add -> replaceFragment(AddFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
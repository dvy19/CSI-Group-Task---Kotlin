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
        replaceFragment(HomeFragment(), "Home")

        // Setup Bottom Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment(), "Home")
                R.id.nav_add -> replaceFragment(AddFragment(), "Add")
                R.id.nav_profile -> replaceFragment(ProfileFragment(), "Profile")
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)
            .addToBackStack(tag) // Add this line for back navigation
            .commit()
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 1) {
            // If there are fragments in back stack, pop them
            fragmentManager.popBackStack()

            // Update bottom navigation after back press
            updateBottomNavigationAfterBack()
        } else {
            // If no more fragments in back stack, close the app
            super.onBackPressed()
        }
    }

    private fun updateBottomNavigationAfterBack() {
        // Get the current fragment tag
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        val currentTag = currentFragment?.tag ?: "Home"

        // Update bottom navigation based on current fragment
        when (currentTag) {
            "Home" -> bottomNavigationView.selectedItemId = R.id.nav_home
            "Add" -> bottomNavigationView.selectedItemId = R.id.nav_add
            "Profile" -> bottomNavigationView.selectedItemId = R.id.nav_profile
        }
    }
}
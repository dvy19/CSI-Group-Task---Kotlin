package com.example.jobportal

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class HomeActivity : AppCompatActivity() {

    // Correct property declarations
    private lateinit var navHome: View
    private lateinit var iconHome: ImageView
    private lateinit var textHome: TextView

    private lateinit var navPost: View
    private lateinit var iconPost: ImageView
    private lateinit var textPost: TextView

    private lateinit var navNotifications: View
    private lateinit var iconNotifications: ImageView
    private lateinit var textNotifications: TextView

    private lateinit var navJobs: View
    private lateinit var iconJobs: ImageView
    private lateinit var textJobs: TextView

    private lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        // Initialize all views
        initializeViews()

        // Set initial fragment
        navigateToFragment(HomeFragment(), "Home")
        setActiveTab(navHome, iconHome, textHome)

        // Set up click listeners for bottom navigation
        setupBottomNavigation()
    }

    private fun initializeViews() {
        // Home tab views
        navHome = findViewById(R.id.navHome)
        iconHome = findViewById(R.id.iconHome)
        textHome = findViewById(R.id.textHome)

        // Post/Jobs tab views
        navPost = findViewById(R.id.navPost)
        iconPost = findViewById(R.id.iconPost)
        textPost = findViewById(R.id.textPost)

        // Notifications/Chat tab views
        navNotifications = findViewById(R.id.navNotifications)
        iconNotifications = findViewById(R.id.iconNotifications)
        textNotifications = findViewById(R.id.textNotifications)

        // Jobs/Profile tab views
        navJobs = findViewById(R.id.navJobs)
        iconJobs = findViewById(R.id.iconJobs)
        textJobs = findViewById(R.id.textJobs)
    }

    private fun setupBottomNavigation() {
        // Home Tab
        navHome.setOnClickListener {
            navigateToFragment(HomeFragment(), "Home")
            setActiveTab(navHome, iconHome, textHome)
            resetOtherTabs()
        }

        // Jobs Tab
        navPost.setOnClickListener {
            navigateToFragment(JobFragment(), "Jobs")
            setActiveTab(navPost, iconPost, textPost)
            resetOtherTabs()
        }

        // Chat Tab
        navNotifications.setOnClickListener {
            navigateToFragment(ChatFragment(), "Chat")
            setActiveTab(navNotifications, iconNotifications, textNotifications)
            resetOtherTabs()
        }

        // Profile Tab
        navJobs.setOnClickListener {
            navigateToFragment(ProfileFragment(), "Profile")
            setActiveTab(navJobs, iconJobs, textJobs)
            resetOtherTabs()
        }
    }

    private fun navigateToFragment(fragment: Fragment, tag: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Check if fragment already exists
        val existingFragment = fragmentManager.findFragmentByTag(tag)

        if (existingFragment != null) {
            // If fragment exists, show it and hide current
            if (::currentFragment.isInitialized) {
                fragmentTransaction.hide(currentFragment)
            }
            fragmentTransaction.show(existingFragment)
            currentFragment = existingFragment
        } else {
            // If fragment doesn't exist, add it
            if (::currentFragment.isInitialized) {
                fragmentTransaction.hide(currentFragment)
            }
            // Use proper fragment container (you'll need to add this to XML)
            fragmentTransaction.add(R.id.scrollView, fragment, tag)
            currentFragment = fragment
        }

        fragmentTransaction.commit()
    }

    private fun setActiveTab(tab: View, icon: ImageView, text: TextView) {
        // Set active color
        icon.setColorFilter(Color.parseColor("#1976D2"))
        text.setTextColor(Color.parseColor("#1976D2"))
    }

    private fun resetOtherTabs() {
        // Reset Home tab if not active
        if (currentFragment !is HomeFragment) {
            iconHome.setColorFilter(Color.parseColor("#666666"))
            textHome.setTextColor(Color.parseColor("#666666"))
        }

        // Reset Jobs tab if not active
        if (currentFragment !is JobFragment) {
            iconPost.setColorFilter(Color.parseColor("#666666"))
            textPost.setTextColor(Color.parseColor("#666666"))
        }

        // Reset Chat tab if not active
        if (currentFragment !is ChatFragment) {
            iconNotifications.setColorFilter(Color.parseColor("#666666"))
            textNotifications.setTextColor(Color.parseColor("#666666"))
        }

        // Reset Profile tab if not active
        if (currentFragment !is ProfileFragment) {
            iconJobs.setColorFilter(Color.parseColor("#666666"))
            textJobs.setTextColor(Color.parseColor("#666666"))
        }
    }
}
package com.example.jobportal

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

private val android: Any

class HomeActivity : AppCompatActivity() {

    private lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        // Set initial fragment
        navigateToFragment(HomeFragment(), "Home")
        setActiveTab(navHome, iconHome, textHome)

        // Set up click listeners for bottom navigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        // Home Tab
        navHome.setOnClickListener {
            navigateToFragment(HomeFragment(), "Home")
            setActiveTab(navHome, iconHome, textHome)
            resetOtherTabs()
        }

        // Jobs Tab (Note: Based on your XML, this is actually the Post tab with Jobs text)
        navPost.setOnClickListener {
            navigateToFragment(JobFragment(), "Jobs")
            setActiveTab(navPost, iconPost, textPost)
            resetOtherTabs()
        }

        // Chat Tab (Note: Based on your XML, this is the Notifications tab with Chat text)
        navNotifications.setOnClickListener {
            navigateToFragment(ChatFragment(), "Chat")
            setActiveTab(navNotifications, iconNotifications, textNotifications)
            resetOtherTabs()
        }

        // Profile Tab (Note: Based on your XML, this is the Jobs tab with Profile text)
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
            fragmentTransaction.add(R.id.fragment_container, fragment, tag)
            currentFragment = fragment
        }

        fragmentTransaction.commit()
    }

    private fun setActiveTab(tab: android.view.View, icon: android.widget.ImageView, text: android.widget.TextView) {
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
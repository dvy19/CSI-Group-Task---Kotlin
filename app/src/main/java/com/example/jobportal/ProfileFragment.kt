package com.example.jobportal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var appPreferences: AppPreferences
    private lateinit var profileRepository: ProfileRepository

    // Views
    private lateinit var progressBar: ProgressBar
    private lateinit var coverImage: ImageView
    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileProfession: TextView
    private lateinit var profileLocation: TextView
    private lateinit var profileBio: TextView
    private lateinit var statsPostsCount: TextView
    private lateinit var statsFollowersCount: TextView
    private lateinit var statsFollowingCount: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileSkills: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnEditProfile: ImageView
    private lateinit var btnFollow: Button
    private lateinit var btnMessage: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize AppPreferences
        appPreferences = AppPreferences(requireContext())
        profileRepository = ProfileRepository()

        // Find views
        initializeViews(view)

        // Debug token status
        debugTokenStatus()

        // Set up click listeners
        setupClickListeners()

        // Load profile data
        loadProfileData()
    }

    private fun initializeViews(view: View) {
        progressBar = view.findViewById(R.id.progressBar)
        coverImage = view.findViewById(R.id.coverImage)
        profileImage = view.findViewById(R.id.profileImage)
        profileName = view.findViewById(R.id.profileName)
        profileProfession = view.findViewById(R.id.profileProfession)
        profileLocation = view.findViewById(R.id.profileLocation)
        profileBio = view.findViewById(R.id.profileBio)
        statsPostsCount = view.findViewById(R.id.statsPostsCount)
        statsFollowersCount = view.findViewById(R.id.statsFollowersCount)
        statsFollowingCount = view.findViewById(R.id.statsFollowingCount)
        profileEmail = view.findViewById(R.id.profileEmail)
        profileSkills = view.findViewById(R.id.profileSkills)
        btnBack = view.findViewById(R.id.btnBack)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnFollow = view.findViewById(R.id.btnFollow)
        btnMessage = view.findViewById(R.id.btnMessage)
    }

    private fun debugTokenStatus() {
        val token = appPreferences.getAccessToken()
        println("DEBUG TOKEN STATUS:")
        println("Token exists: ${!token.isNullOrEmpty()}")
        println("Token length: ${token?.length ?: 0}")
        println("Token preview: ${token?.take(20)}...")
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        btnEditProfile.setOnClickListener {
            Toast.makeText(context, "Edit Profile clicked", Toast.LENGTH_SHORT).show()
        }

        btnFollow.setOnClickListener {
            Toast.makeText(context, "Follow clicked", Toast.LENGTH_SHORT).show()
        }

        btnMessage.setOnClickListener {
            Toast.makeText(context, "Message clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfileData() {
        val token = appPreferences.getAccessToken()

        if (token.isNullOrEmpty()) {
            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
            println("DEBUG: Token is null or empty")
            return
        }

        println("DEBUG: Token found, loading profile...")
        showLoading(true)

        lifecycleScope.launch {
            try {
                println("DEBUG: Making API call...")
                val result = profileRepository.getProfile(token)

                result.fold(
                    onSuccess = { profile ->
                        println("DEBUG: Profile loaded successfully: ${profile.getFullName()}")
                        showLoading(false)
                        updateUI(profile)
                    },
                    onFailure = { error ->
                        println("DEBUG: Profile load failed: ${error.message}")
                        showLoading(false)
                        handleError(error)
                    }
                )
            } catch (e: Exception) {
                println("DEBUG: Exception in loadProfileData: ${e.message}")
                showLoading(false)
                handleError(e)
            }
        }
    }

    private fun updateUI(profile: SeekerProfileResponse) {
        // Load cover image (uncomment when you have Glide setup)
        /*
        if (!profile.coverImage.isNullOrEmpty()) {
            Glide.with(this)
                .load(profile.coverImage)
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(coverImage)
        }

        // Load profile image
        if (!profile.profile_image.isNullOrEmpty()) {
            Glide.with(this)
                .load(profile.profile_image)
                .placeholder(R.drawable.user_img)
                .error(R.drawable.user_img)
                .circleCrop()
                .into(profileImage)
        }
        */

        // Set text data
        profileName.text = profile.getFullName()
        profileProfession.text = profile.profession ?: "Not specified"
        profileLocation.text = profile.location ?: "Location not set"
        profileBio.text = profile.bio ?: "No bio available"
        profileEmail.text = profile.email
        profileSkills.text = profile.getSkillsString()

        // Set stats
        statsPostsCount.text = profile.postsCount?.toString() ?: "0"
        statsFollowersCount.text = formatCount(profile.followersCount ?: 0)
        statsFollowingCount.text = profile.followingCount?.toString() ?: "0"

        // Show success message
        Toast.makeText(context, "Profile loaded successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun formatCount(count: Int): String {
        return when {
            count >= 1000000 -> String.format("%.1fM", count / 1000000.0)
            count >= 1000 -> String.format("%.1fK", count / 1000.0)
            else -> count.toString()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleError(error: Throwable) {
        val message = when {
            error.message?.contains("401") == true -> {
                // Clear tokens and redirect to login
                appPreferences.clearTokens()
                "Session expired. Please login again."
            }
            error.message?.contains("404") == true -> "Profile not found."
            error.message?.contains("Network error") == true -> "Check your internet connection"
            error.message?.contains("timeout") == true -> "Request timeout. Please try again."
            error.message?.contains("Empty response") == true -> "No profile data received"
            else -> "Error loading profile: ${error.message ?: "Unknown error"}"
        }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        println("ERROR: $message")
    }
}
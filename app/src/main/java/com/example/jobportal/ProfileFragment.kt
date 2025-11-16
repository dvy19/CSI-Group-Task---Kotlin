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
import com.bumptech.glide.Glide
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

        // Initialize
        appPreferences = AppPreferences(requireContext())
        profileRepository = ProfileRepository()

        // Find views
        initializeViews(view)

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
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            val result = profileRepository.getProfile(token)

            result.fold(
                onSuccess = { profile ->
                    showLoading(false)
                    println("DEBUG: Profile loaded successfully: $profile")
                    updateUI(profile)
                },
                onFailure = { error ->
                    showLoading(false)
                    println("DEBUG: Profile loading failed: ${error.message}")
                    handleError(error)
                }
            )
        }
    }

    private fun updateUI(profile: ProfileResponse) {
        println("DEBUG: Updating UI with profile data")
        println("DEBUG: Profile image URL: ${profile.profile_image}")
        println("DEBUG: Education: ${profile.education_text}")
        println("DEBUG: Skills: ${profile.skills}")

        // Load profile image
        if (!profile.profile_image.isNullOrEmpty()) {
            Glide.with(this)
                .load(profile.profile_image)
                .placeholder(R.drawable.user_img)
                .error(R.drawable.user_img)
                .circleCrop()
                .into(profileImage)
        }

        // Load education image (if you have an ImageView for it in your layout)
        if (!profile.education_image.isNullOrEmpty()) {
            // Uncomment if you have an education image view
            // Glide.with(this).load(profile.education_image).into(educationImageView)
        }

        // Set text data based on your actual ProfileResponse fields
        profileName.text = profile.user?.username ?: "User"
        profileEmail.text = profile.user?.email ?: "Email not available"

        // Use the actual fields from your ProfileResponse
        profileBio.text = profile.experience ?: "No experience listed"
        profileSkills.text = profile.skills ?: "No skills listed"

        // If you have these fields in your layout, update them
        // profileEducation.text = profile.education_text ?: "No education listed"
        // profileLanguages.text = profile.languages ?: "No languages listed"

        // These fields don't exist in your API, so set default values or hide them
        profileProfession.text = profile.role ?: "Job Seeker"
        profileLocation.text = "Location not set" // Not in your API

        // Stats - not in your API, set defaults or hide these views
        statsPostsCount.text = "0"
        statsFollowersCount.text = "0"
        statsFollowingCount.text = "0"
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
                appPreferences.clearTokens()
                "Session expired. Please login again."
            }
            error.message?.contains("404") == true -> "Profile not found."
            error.message?.contains("timeout") == true -> "Request timeout. Please try again."
            else -> "Error loading profile: ${error.message}"
        }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
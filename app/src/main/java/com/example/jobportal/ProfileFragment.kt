package com.example.jobportal

import android.content.Intent
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

        println("DEBUG: ProfileFragment - onViewCreated")

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
        println("DEBUG: ProfileFragment - loadProfileData called")

        val token = appPreferences.getAccessToken()

        println("DEBUG: ProfileFragment - Token is null: ${token == null}")
        println("DEBUG: ProfileFragment - Token is empty: ${token?.isEmpty()}")

        if (token.isNullOrEmpty()) {
            println("DEBUG: ProfileFragment - No token found, redirecting to login")
            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()

            // Redirect to login
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            return
        }

        println("DEBUG: ProfileFragment - Token found, loading profile")
        showLoading(true)

        lifecycleScope.launch {
            try {
                println("DEBUG: ProfileFragment - Calling repository.getProfile()")
                val result = profileRepository.getProfile(token)

                result.fold(
                    onSuccess = { profile ->
                        showLoading(false)
                        println("DEBUG: ProfileFragment - Profile loaded successfully")
                        println("DEBUG: ProfileFragment - Profile data: $profile")
                        updateUI(profile)
                    },
                    onFailure = { error ->
                        showLoading(false)
                        println("DEBUG: ProfileFragment - Profile loading failed")
                        println("DEBUG: ProfileFragment - Error: ${error.message}")
                        println("DEBUG: ProfileFragment - Stack trace: ${error.stackTraceToString()}")
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

    private fun updateUI(profile: ProfileResponse) {
        println("DEBUG: ProfileFragment - Updating UI with profile data")
        println("DEBUG: ProfileFragment - Profile image URL: ${profile.profile_image}")
        println("DEBUG: ProfileFragment - Education: ${profile.education_text}")
        println("DEBUG: ProfileFragment - Experience: ${profile.experience}")
        println("DEBUG: ProfileFragment - Skills: ${profile.skills}")
        println("DEBUG: ProfileFragment - Languages: ${profile.languages}")
        println("DEBUG: ProfileFragment - Role: ${profile.role}")
        println("DEBUG: ProfileFragment - User: ${profile.user}")

        // Load profile image
        if (!profile.profile_image.isNullOrEmpty()) {
            println("DEBUG: ProfileFragment - Loading profile image from: ${profile.profile_image}")
            Glide.with(this)
                .load(profile.profile_image)
                .placeholder(R.drawable.user_img)
                .error(R.drawable.user_img)
                .circleCrop()
                .into(profileImage)
        } else {
            println("DEBUG: ProfileFragment - No profile image URL available")
        }

        // Set text data based on your actual ProfileResponse fields
        profileName.text = profile.user?.username ?: "User"
        profileEmail.text = profile.user?.email ?: "Email not available"

        // Use the actual fields from your ProfileResponse
        profileBio.text = profile.experience ?: "No experience listed"
        profileSkills.text = profile.skills ?: "No skills listed"

        // These fields don't exist in your API, so set default values
        profileProfession.text = profile.role ?: "Job Seeker"
        profileLocation.text = "Location not set"

        // Stats - not in your API, set defaults
        statsPostsCount.text = "0"
        statsFollowersCount.text = "0"
        statsFollowingCount.text = "0"

        println("DEBUG: ProfileFragment - UI update completed")

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
        println("DEBUG: ProfileFragment - Loading indicator: ${if (isLoading) "VISIBLE" else "GONE"}")
    }

    private fun handleError(error: Throwable) {
        val message = when {
            error.message?.contains("401") == true -> {
                println("DEBUG: ProfileFragment - 401 error, clearing tokens")
                appPreferences.clearTokens()
                "Session expired. Please login again."
            }
            error.message?.contains("404") == true -> {
                println("DEBUG: ProfileFragment - 404 error")
                "Profile not found."
            }
            error.message?.contains("Network error") == true -> {
                println("DEBUG: ProfileFragment - Network error")
                "Check your internet connection"
            }
            error.message?.contains("timeout") == true -> {
                println("DEBUG: ProfileFragment - Timeout error")
                "Request timeout. Please try again."
            }
            error.message?.contains("Empty response") == true -> {
                println("DEBUG: ProfileFragment - Empty response")
                "No profile data received"
            }
            else -> {
                println("DEBUG: ProfileFragment - Other error")
                "Error loading profile: ${error.message ?: "Unknown error"}"
            }
        }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

        // If 401, redirect to login
        if (error.message?.contains("401") == true) {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
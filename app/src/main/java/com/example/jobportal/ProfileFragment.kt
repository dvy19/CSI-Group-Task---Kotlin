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

    private lateinit var preferenceManager: PreferenceManager
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
        preferenceManager = PreferenceManager(requireContext())
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
            // Navigate to edit profile screen
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
        val token = preferenceManager.getAuthToken()

        if (token.isNullOrEmpty()) {
            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            profileRepository.getProfile(token).fold(
                onSuccess = { profile ->
                    showLoading(false)
                    updateUI(profile)
                },
                onFailure = { error ->
                    showLoading(false)
                    Toast.makeText(
                        context,
                        "Error: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }

    private fun ProfileRepository.getProfile(token: String) {}

    private fun updateUI(profile: ProfileResponse) {
        // Load cover image
        if (!profile.coverImage.isNullOrEmpty()) {
            Glide.with(this)
                .load(profile.coverImage)
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(coverImage)
        }

        // Load profile image
        if (!profile.profilePicture.isNullOrEmpty()) {
            Glide.with(this)
                .load(profile.profilePicture)
                .placeholder(R.drawable.user_img)
                .error(R.drawable.user_img)
                .circleCrop()
                .into(profileImage)
        }

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
}

private fun handleError(error: Throwable) {
    val message = when {
        error.message?.contains("401") == true -> "Session expired. Please login again."
        error.message?.contains("404") == true -> "Profile not found."
        error.message?.contains("timeout") == true -> "Request timeout. Please try again."
        else -> "Error loading profile: ${error.message}"
    }

    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}
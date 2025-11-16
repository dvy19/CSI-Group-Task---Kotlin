package com.example.jobportal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.database.Cursor
import java.io.File

class SeekerProfileActivity : AppCompatActivity() {

    private lateinit var preferences: AppPreferences

    // Text fields
    private lateinit var experienceEditText: EditText
    private lateinit var educationEditText: EditText
    private lateinit var skillsEditText: EditText
    private lateinit var languagesEditText: EditText
    private lateinit var submitButton: Button
    private var progressBar: ProgressBar? = null

    // File selection variables
    private var profileImageUri: Uri? = null
    private var educationImageUri: Uri? = null
    private var resumeFileUri: Uri? = null
    private var resumeImageUri: Uri? = null

    // Buttons
    private lateinit var uploadProfileImageButton: Button
    private lateinit var uploadEducationImageButton: Button
    private lateinit var uploadResumeButton: Button
    private lateinit var uploadResumeImageButton: Button

    // File name display
    private lateinit var resumeFileNameText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.job_seeker_details)

        preferences = AppPreferences(this)

        println("DEBUG: ProfileActivity onCreate")

        // Initialize views
        initializeViews()

        // Check if user is logged in
        val token = preferences.getAccessToken()
        if (token == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setupClickListeners()
    }

    private fun initializeViews() {
        experienceEditText = findViewById(R.id.experienceEditText)
        educationEditText = findViewById(R.id.educationEditText)
        skillsEditText = findViewById(R.id.skillsEditText)
        languagesEditText = findViewById(R.id.languagesEditText)
        submitButton = findViewById(R.id.submitButton)
        progressBar = findViewById(R.id.progressBar)

        uploadProfileImageButton = findViewById(R.id.uploadProfileImageButton)
        uploadEducationImageButton = findViewById(R.id.uploadEducationImageButton)
        uploadResumeButton = findViewById(R.id.uploadResumeButton)
        uploadResumeImageButton = findViewById(R.id.uploadResumeImageButton)
        resumeFileNameText = findViewById(R.id.resumeFileNameText)
    }

    private fun setupClickListeners() {
        submitButton.setOnClickListener {
            println("DEBUG: Update Profile button clicked")
            updateProfile()
        }

        uploadProfileImageButton.setOnClickListener {
            // Open file picker for image
            openFilePicker("image/*", 1001)
        }

        uploadEducationImageButton.setOnClickListener {
            // Open file picker for education image
            openFilePicker("image/*", 1002)
        }

        uploadResumeButton.setOnClickListener {
            // Open file picker for resume file (PDF, DOC, etc.)
            openFilePicker("*/*", 1003)
        }

        uploadResumeImageButton.setOnClickListener {
            // Open file picker for resume image
            openFilePicker("image/*", 1004)
        }
    }

    private fun openFilePicker(mimeType: String, requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = mimeType
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            val uri = data.data
            when (requestCode) {
                1001 -> {
                    profileImageUri = uri
                    Toast.makeText(this, "Profile image selected", Toast.LENGTH_SHORT).show()
                }
                1002 -> {
                    educationImageUri = uri
                    Toast.makeText(this, "Education image selected", Toast.LENGTH_SHORT).show()
                }
                1003 -> {
                    resumeFileUri = uri
                    if (uri != null) {
                        val fileName = getFileName(uri)
                        resumeFileNameText.text = "Selected: $fileName"
                    } else {
                        resumeFileNameText.text = "No file selected"
                    }
                }
                1004 -> {
                    resumeImageUri = uri
                    Toast.makeText(this, "Resume image selected", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        return try {
            var result: String? = null
            if (uri.scheme == "content") {
                val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val displayNameIndex = it.getColumnIndex("_display_name")
                        if (displayNameIndex != -1) {
                            result = it.getString(displayNameIndex)
                        }
                    }
                }
            }
            if (result == null) {
                result = uri.path?.let { path ->
                    path.substring(path.lastIndexOf('/') + 1)
                }
            }
            result ?: "file_${System.currentTimeMillis()}"
        } catch (e: Exception) {
            "file_${System.currentTimeMillis()}"
        }
    }

    private fun updateProfile() {
        val education = educationEditText.text.toString()
        val experience = experienceEditText.text.toString()
        val skills = skillsEditText.text.toString()
        val languages = languagesEditText.text.toString()
        val token = preferences.getAccessToken()

        if (!validateInput(education, experience, skills, languages)) {
            return
        }

        // Show progress bar
        progressBar?.visibility = ProgressBar.VISIBLE
        submitButton.isEnabled = false

        // Convert text fields to RequestBody
        val educationBody = RequestBody.create("text/plain".toMediaTypeOrNull(), education)
        val experienceBody = RequestBody.create("text/plain".toMediaTypeOrNull(), experience)
        val skillsBody = RequestBody.create("text/plain".toMediaTypeOrNull(), skills)
        val languagesBody = RequestBody.create("text/plain".toMediaTypeOrNull(), languages)
        val roleBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "jobseeker")

        // Convert file URIs to MultipartBody.Part
        val profileImagePart = createMultipartPart("profile_image", profileImageUri)
        val educationImagePart = createMultipartPart("education_image", educationImageUri)
        val resumePart = createMultipartPart("resume", resumeFileUri)
        val resumeImagePart = createMultipartPart("resume_image", resumeImageUri)

        println("DEBUG: Making Profile API call")
        SignUpRetrofitClient.profileInstance.updateProfile(
            token = "Bearer $token",
            educationText = educationBody,
            experience = experienceBody,
            languages = languagesBody,
            skills = skillsBody,
            role = roleBody,
            profile_image = profileImagePart,
            education_image = educationImagePart,
            resume = resumePart,
            resume_image = resumeImagePart
        ).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                // Hide progress bar
                progressBar?.visibility = ProgressBar.GONE
                submitButton.isEnabled = true

                println("DEBUG: Profile API response received - Success: ${response.isSuccessful}")

                if (response.isSuccessful) {

                    submitButton.setOnClickListener {
                        val intent = Intent(this@SeekerProfileActivity , HomeActivity::class.java)
                        startActivity(intent)
                    }

                    val profileResponse = response.body()

                    if (profileResponse != null) {
                        println("DEBUG: Profile updated successfully!")

                        Toast.makeText(
                            this@SeekerProfileActivity,
                            "Profile Updated Successfully!",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()

                    } else {
                        Toast.makeText(
                            this@SeekerProfileActivity,
                            "Profile update failed: Invalid response",
                            Toast.LENGTH_LONG
                        ).show()
                    }






                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Please check your inputs"
                        401 -> {
                            preferences.clearTokens()
                            "Session expired. Please login again"
                        }
                        415 -> "Unsupported file type"
                        else -> "Update failed: ${response.code()}"
                    }

                    Toast.makeText(
                        this@SeekerProfileActivity,
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()

                    if (response.code() == 401) {
                        val intent = Intent(this@SeekerProfileActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                progressBar?.visibility = ProgressBar.GONE
                submitButton.isEnabled = true

                println("DEBUG: Profile API call failed: ${t.message}")
                Toast.makeText(
                    this@SeekerProfileActivity,
                    "Network error: ${t.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun createMultipartPart(partName: String, uri: Uri?): MultipartBody.Part? {
        return uri?.let {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val file = File(cacheDir, "temp_${System.currentTimeMillis()}")
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                MultipartBody.Part.createFormData(partName, getFileName(uri), requestFile)
            } catch (e: Exception) {
                println("DEBUG: Error creating multipart for $partName: ${e.message}")
                null
            }
        }
    }

    private fun validateInput(
        education: String,
        experience: String,
        skills: String,
        languages: String
    ): Boolean {
        if (education.isEmpty()) {
            Toast.makeText(this, "Please enter education details", Toast.LENGTH_SHORT).show()
            return false
        }

        if (experience.isEmpty()) {
            Toast.makeText(this, "Please enter experience", Toast.LENGTH_SHORT).show()
            return false
        }

        if (skills.isEmpty()) {
            Toast.makeText(this, "Please enter skills", Toast.LENGTH_SHORT).show()
            return false
        }

        if (languages.isEmpty()) {
            Toast.makeText(this, "Please enter languages", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        println("DEBUG: ProfileActivity onDestroy")
    }
}
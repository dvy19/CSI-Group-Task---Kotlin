package com.example.jobportal

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.InputStream
import java.io.FileOutputStream

class CommunityPostActivity : AppCompatActivity() {

    private lateinit var etPostContent: EditText
    private lateinit var tvCharCount: TextView
    private lateinit var btnSelectImage: Button
    private lateinit var tvSelectedImage: TextView
    private lateinit var ivImagePreview: ImageView
    private lateinit var btnPost: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView

    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 101
    private lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.community_post) // Make sure this matches your XML file name

        // Initialize AppPreferences
        appPreferences = AppPreferences(this)

        initializeViews()
        setupClickListeners()
        setupCharacterCounter()
    }

    private fun initializeViews() {
        etPostContent = findViewById(R.id.etPostContent)
        tvCharCount = findViewById(R.id.tvCharCount)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        tvSelectedImage = findViewById(R.id.tvSelectedImage)
        ivImagePreview = findViewById(R.id.ivSelectedImage) // Fixed ID to match XML
        btnPost = findViewById(R.id.btnCreatePost) // Fixed ID to match XML
        progressBar = findViewById(R.id.progressBar)
        tvStatus = findViewById(R.id.tvStatus)

        // Check if user is authenticated using AppPreferences
        if (!isUserAuthenticated()) {
            showError("Please login to create posts")
            finish()
            return
        }
    }

    private fun setupClickListeners() {
        btnSelectImage.setOnClickListener {
            selectImageFromGallery()
        }

        btnPost.setOnClickListener {
            createPost()
        }
    }

    private fun setupCharacterCounter() {
        etPostContent.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                val currentLength = s?.length ?: 0
                tvCharCount.text = "$currentLength/500 characters"
            }
        })
    }

    private fun isUserAuthenticated(): Boolean {
        val accessToken = appPreferences.getAccessToken()
        return !accessToken.isNullOrEmpty()
    }

    private fun getAccessToken(): String {
        return appPreferences.getAccessToken() ?: ""
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            tvSelectedImage.text = "Image selected"
            ivImagePreview.setImageURI(selectedImageUri)
            ivImagePreview.visibility = android.view.View.VISIBLE
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun createPost() {
        val content = etPostContent.text.toString().trim()

        // Validation
        if (content.isEmpty()) {
            showError("Please enter post content")
            return
        }

        if (content.length > 500) {
            showError("Post content should be less than 500 characters")
            return
        }

        // Show loading
        showLoading(true)
        tvStatus.visibility = android.view.View.GONE

        // Get access token from AppPreferences
        val accessToken = getAccessToken()
        if (accessToken.isEmpty()) {
            showError("Authentication failed. Please login again.")
            showLoading(false)
            return
        }

        // Create content part
        val contentPart = MultipartBody.Part.createFormData("content", content)

        // Create image part if image is selected - use "image" field name as per API docs
        var imagePart: MultipartBody.Part? = null
        selectedImageUri?.let { uri ->
            try {
                val file = getFileFromUri(uri)
                if (file != null && file.exists() && file.length() > 0) {
                    val requestFile = RequestBody.create("image/*".toMediaType(), file)
                    // CORRECTED: Use "image" as field name to match API documentation
                    imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                } else {
                    showError("Failed to process selected image")
                    showLoading(false)
                    return
                }
            } catch (e: Exception) {
                showError("Error processing image: ${e.message}")
                showLoading(false)
                return
            }
        }

        // Make API call using the correct instance name
        val call = SignUpRetrofitClient.communityPostInstance.createPost(
            token = "Bearer $accessToken",
            content = contentPart,
            image = imagePart
        )

        call.enqueue(object : Callback<CommunityPostResponse> {
            override fun onResponse(call: Call<CommunityPostResponse>, response: Response<CommunityPostResponse>) {
                showLoading(false)

                if (response.isSuccessful) {
                    val postResponse = response.body()
                    showSuccess("Post created successfully!")
                    clearForm()
                    setResult(Activity.RESULT_OK)
                    finish()
                    // Optionally finish activity or go back
                    // finish()
                } else {
                    when (response.code()) {
                        400 -> showError("Invalid post data")
                        401 -> {
                            showError("Authentication failed. Please login again.")
                            appPreferences.clearTokens()
                        }
                        500 -> showError("Server error. Please try again.")
                        else -> showError("Failed to create post: ${response.code()} - ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<CommunityPostResponse>, t: Throwable) {
                showLoading(false)
                showError("Network error: ${t.message}")
            }
        })
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
        btnPost.isEnabled = !show
        btnSelectImage.isEnabled = !show
        etPostContent.isEnabled = !show
    }

    private fun showError(message: String) {
        tvStatus.text = message
        tvStatus.setTextColor(resources.getColor(android.R.color.holo_red_dark, theme))
        tvStatus.visibility = android.view.View.VISIBLE

        // Hide error after 3 seconds
        tvStatus.postDelayed({
            tvStatus.visibility = android.view.View.GONE
        }, 3000)
    }

    private fun showSuccess(message: String) {
        tvStatus.text = message
        tvStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark, theme))
        tvStatus.visibility = android.view.View.VISIBLE

        // Hide success message after 3 seconds
        tvStatus.postDelayed({
            tvStatus.visibility = android.view.View.GONE
        }, 3000)
    }

    private fun clearForm() {
        etPostContent.text.clear()
        selectedImageUri = null
        tvSelectedImage.text = "No image selected"
        ivImagePreview.visibility = android.view.View.GONE
        tvCharCount.text = "0/500 characters"
    }
}
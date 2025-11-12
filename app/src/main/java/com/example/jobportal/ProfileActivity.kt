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
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var educationText: EditText
    private lateinit var experienceText: EditText
    private lateinit var languagesText: EditText
    private lateinit var skillsText: EditText
    private lateinit var roleText: EditText
    private lateinit var submitButton: Button
    private lateinit var selectEducationImageButton: Button
    private lateinit var selectResumeImageButton: Button
    private lateinit var selectResumeDocumentButton: Button




    private var selectedEducationImageUri: Uri? = null
    private var selectedResumeImageUri: Uri? = null
    private var selectedResumeDocumentUri: Uri? = null
    private lateinit var apiInterface: ApiInterface

    // Request codes for different file types
    companion object {
        private const val REQUEST_EDUCATION_IMAGE = 100
        private const val REQUEST_RESUME_IMAGE = 101
        private const val REQUEST_RESUME_DOCUMENT = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personal_info)

        // Initialize API interface
        apiInterface = RetrofitClient.getClient()

        // Find views
        educationText = findViewById(R.id.education)
        experienceText = findViewById(R.id.experience)
        languagesText = findViewById(R.id.language)
        skillsText = findViewById(R.id.skill)
        roleText = findViewById(R.id.job_role)
        submitButton = findViewById(R.id.submit_info)
        selectEducationImageButton = findViewById(R.id.selectEducationImageButton)
        selectResumeImageButton = findViewById(R.id.selectResumeImageButton)
        selectResumeDocumentButton = findViewById(R.id.selectResumeDocumentButton)


        // Set click listeners
        selectEducationImageButton.setOnClickListener {
            selectImageFromGallery(REQUEST_EDUCATION_IMAGE)
        }

        selectResumeImageButton.setOnClickListener {
            selectImageFromGallery(REQUEST_RESUME_IMAGE)
        }

        selectResumeDocumentButton.setOnClickListener {
            selectDocumentFromGallery()
        }

        submitButton.setOnClickListener {
            submitProfileData()
        }
    }


        //select the image form the gallery, with the requestCode to  identify which request it is

    private fun selectImageFromGallery(requestCode: Int) {

        //asking to get some content from device
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"     //content should be of image type jpg, png etc.
        startActivityForResult(intent, requestCode)
    }


    //function dedicated to only choose resume
    private fun selectDocumentFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)

        intent.type = "*/*"             //all file types, any type can be selected
        intent.addCategory(Intent.CATEGORY_OPENABLE)            //files which can be opened by file, not those of system or protected files
        // Specify MIME types for documents
        val mimeTypes ="application/pdf" //only accept and show these kind of files.
        startActivityForResult(intent, REQUEST_RESUME_DOCUMENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                when (requestCode) {
                    REQUEST_EDUCATION_IMAGE -> {
                        selectedEducationImageUri = uri
                        //educationImageText.text = "Education image selected: ${getFileName(uri)}"
                        Toast.makeText(this, "Education image selected", Toast.LENGTH_SHORT).show()
                    }
                    REQUEST_RESUME_IMAGE -> {
                        selectedResumeImageUri = uri
                        //resumeImageText.text = "Resume image selected: ${getFileName(uri)}"
                        Toast.makeText(this, "Resume image selected", Toast.LENGTH_SHORT).show()
                    }
                    REQUEST_RESUME_DOCUMENT -> {
                        selectedResumeDocumentUri = uri
                        //resumeDocumentText.text = "Resume document selected: ${getFileName(uri)}"
                        Toast.makeText(this, "Resume document selected", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val displayNameIndex = cursor.getColumnIndex("_display_name")
                    if (displayNameIndex != -1) {
                        result = cursor.getString(displayNameIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "unknown_file"
    }

    private fun submitProfileData() {
        val token = "Bearer ${getSavedToken()}"

        // Get input values
        val education = educationText.text.toString().trim()
        val experience = experienceText.text.toString().trim()
        val languages = languagesText.text.toString().trim()
        val skills = skillsText.text.toString().trim()
        val role = roleText.text.toString().trim()

        // Validate inputs
        if (education.isEmpty() || experience.isEmpty() || languages.isEmpty() || skills.isEmpty() || role.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Disable button during API call
        submitButton.isEnabled = false

        // Prepare multipart data
        val educationTextBody = education.toRequestBody("text/plain".toMediaType())
        val experienceBody = experience.toRequestBody("text/plain".toMediaType())
        val languagesBody = languages.toRequestBody("text/plain".toMediaType())
        val skillsBody = skills.toRequestBody("text/plain".toMediaType())
        val roleBody = role.toRequestBody("text/plain".toMediaType())

        // Handle optional files
        val educationImagePart = selectedEducationImageUri?.let { uri ->
            val file = uriToFile(uri, "education_image")
            MultipartBody.Part.createFormData(
                "education_image",   //name of variable where to store in server
                file.name,  // actual file name
                file.asRequestBody("image/*".toMediaType()) // defines content type
            )
        }

        val resumeImagePart = selectedResumeImageUri?.let { uri ->
            val file = uriToFile(uri, "resume_image")
            MultipartBody.Part.createFormData(
                "resume_image",
                file.name,
                file.asRequestBody("image/*".toMediaType())
            )
        }

        val resumeDocumentPart = selectedResumeDocumentUri?.let { uri ->
            val file = uriToFile(uri, "resume_document")
            MultipartBody.Part.createFormData(
                "resume",
                file.name,
                file.asRequestBody("*/*".toMediaType())
            )
        }

        // Make API call with ALL parameters including optional ones
        //prepares all data into one object, and prepare to send
        val call = apiInterface.updateProfile(
            token = token,
            educationText = educationTextBody,
            experience = experienceBody,
            languages = languagesBody,
            skills = skillsBody,
            role = roleBody,
            education_image = educationImagePart,
            resume_image = resumeImagePart,
            resume = resumeDocumentPart
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                submitButton.isEnabled = true

                if (response.isSuccessful) {
                    Toast.makeText(this@ProfileActivity, "Profile updated successfully!", Toast.LENGTH_LONG).show()
                    clearForm()
                } else {
                    when (response.code()) {
                        401 -> {
                            Toast.makeText(this@ProfileActivity, "Authentication failed. Please login again.", Toast.LENGTH_LONG).show()
                            clearToken()
                        }
                        400 -> Toast.makeText(this@ProfileActivity, "Bad request. Check your input.", Toast.LENGTH_LONG).show()
                        else -> Toast.makeText(this@ProfileActivity, "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                submitButton.isEnabled = true
                Toast.makeText(this@ProfileActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getSavedToken(): String {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("token", "") ?: ""
    }

    private fun clearToken() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        sharedPreferences.edit().remove("token").apply()
    }

    private fun uriToFile(uri: Uri, prefix: String): File {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val fileExtension = getFileExtension(uri)
        val fileName = "${prefix}_${System.currentTimeMillis()}.$fileExtension"
        val file = File(cacheDir, fileName)

        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file
    }

    private fun getFileExtension(uri: Uri): String {
        val mimeType = contentResolver.getType(uri)
        return when {
            mimeType == null -> "jpg"
            mimeType.contains("pdf") -> "pdf"
            mimeType.contains("word") -> "doc"
            mimeType.contains("image") -> "jpg"
            else -> "file"
        }
    }

    private fun clearForm() {
        educationText.text.clear()
        experienceText.text.clear()
        languagesText.text.clear()
        skillsText.text.clear()
        roleText.text.clear()
        selectedEducationImageUri = null
        selectedResumeImageUri = null
        selectedResumeDocumentUri = null
        //educationImageText.text = "No image selected"
        //resumeImageText.text = "No resume image selected"
        //resumeDocumentText.text = "No resume document selected"
    }
}
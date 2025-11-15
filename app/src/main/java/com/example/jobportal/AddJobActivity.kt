package com.example.jobportal

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class AddJobActivity : AppCompatActivity() {

    private lateinit var etJobTitle: EditText
    private lateinit var etJobDescription: EditText
    private lateinit var etLocation: EditText
    private lateinit var etCompanyName: EditText
    private lateinit var etJobType: EditText
    private lateinit var etSalary: EditText
    private lateinit var etCategory: EditText
    private lateinit var etJobTags: EditText
    private lateinit var etRequiredExperience: EditText
    private lateinit var etRequiredSkills: EditText
    private lateinit var etRequiredEducation: EditText
    private lateinit var etRequiredLanguages: EditText
    private lateinit var etAuthorEmail: EditText
    private lateinit var etAuthorName: EditText
    private lateinit var btnSelectLogo: Button
    private lateinit var btnCreateJob: Button
    private lateinit var progressBar: ProgressBar

    private var companyLogoUri: Uri? = null
    private lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_job)

        // Initialize AppPreferences
        appPreferences = AppPreferences(this)

        // Initialize views
        initViews()

        // Check if user is authenticated
        if (!isUserAuthenticated()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupClickListeners()
    }

    private fun initViews() {
        etJobTitle = findViewById(R.id.etJobTitle)
        etJobDescription = findViewById(R.id.etJobDescription)
        etLocation = findViewById(R.id.etLocation)
        etCompanyName = findViewById(R.id.etCompanyName)
        etJobType = findViewById(R.id.etJobType)
        etSalary = findViewById(R.id.etSalary)
        etCategory = findViewById(R.id.etCategory)
        etJobTags = findViewById(R.id.etJobTags)
        etRequiredExperience = findViewById(R.id.etRequiredExperience)
        etRequiredSkills = findViewById(R.id.etRequiredSkills)
        etRequiredEducation = findViewById(R.id.etRequiredEducation)
        etRequiredLanguages = findViewById(R.id.etRequiredLanguages)
        etAuthorEmail = findViewById(R.id.etAuthorEmail)
        etAuthorName = findViewById(R.id.etAuthorName)
        btnSelectLogo = findViewById(R.id.btnSelectLogo)
        btnCreateJob = findViewById(R.id.btnCreateJob)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun isUserAuthenticated(): Boolean {
        val token = appPreferences.getAccessToken()
        return !token.isNullOrEmpty()
    }

    private fun setupClickListeners() {
        btnSelectLogo.setOnClickListener {
            selectCompanyLogo()
        }

        btnCreateJob.setOnClickListener {
            createJob()
        }
    }

    private fun selectCompanyLogo() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            companyLogoUri = data.data
            Toast.makeText(this, "Logo selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createJob() {
        if (!validateInputs()) {
            return
        }

        progressBar.visibility = ProgressBar.VISIBLE
        btnCreateJob.isEnabled = false

        val accessToken = "Bearer ${appPreferences.getAccessToken()}"

        // Create multipart parts
        val jobTitlePart = createPartFromString("job_title", etJobTitle.text.toString())
        val jobDescPart = createPartFromString("job_description", etJobDescription.text.toString())
        val locationPart = createPartFromString("location", etLocation.text.toString())
        val companyNamePart = createPartFromString("company_name", etCompanyName.text.toString())
        val jobTypePart = createPartFromString("job_type", etJobType.text.toString())
        val salaryPart = createPartFromString("salary", etSalary.text.toString())
        val categoryPart = createPartFromString("category", etCategory.text.toString())
        val jobTagsPart = createPartFromString("job_tags", etJobTags.text.toString())
        val reqExpPart = createPartFromString("required_experience", etRequiredExperience.text.toString())
        val reqSkillsPart = createPartFromString("required_skills", etRequiredSkills.text.toString())
        val reqEducationPart = createPartFromString("required_education", etRequiredEducation.text.toString())
        val reqLanguagesPart = createPartFromString("required_languages", etRequiredLanguages.text.toString())
        val authorEmailPart = createPartFromString("author_email", etAuthorEmail.text.toString())
        val authorNamePart = createPartFromString("author_name", etAuthorName.text.toString())

        val logoPart = companyLogoUri?.let { uri ->
            createFilePartFromUri(uri, "company_logo")
        }

        // Make API call
        // Make API call with corrected field names
        val call = SignUpRetrofitClient.jobInstance.createJob(
            token = accessToken,
            jobTitle = jobTitlePart,
            jobDescription = jobDescPart,
            location = locationPart,
            companyName = companyNamePart,
            jobType = jobTypePart,
            salary = salaryPart,
            category = categoryPart,
            jobTags = jobTagsPart,
            requiredExperience = reqExpPart,
            requiredSkills = reqSkillsPart,
            requiredEducation = reqEducationPart,
            requiredLanguages = reqLanguagesPart,
            authorEmail = authorEmailPart,
            authorName = authorNamePart,
            company_logo = logoPart
        )

        call.enqueue(object : Callback<JobCreateResponse> {
            override fun onResponse(call: Call<JobCreateResponse>, response: Response<JobCreateResponse>) {
                progressBar.visibility = ProgressBar.GONE
                btnCreateJob.isEnabled = true

                if (response.isSuccessful) {
                    val jobResponse = response.body()
                    Toast.makeText(this@AddJobActivity, "Job created successfully!", Toast.LENGTH_SHORT).show()
                    finish() // Close activity after successful creation
                } else {
                    Toast.makeText(this@AddJobActivity, "Failed to create job: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JobCreateResponse>, t: Throwable) {
                progressBar.visibility = ProgressBar.GONE
                btnCreateJob.isEnabled = true
                Toast.makeText(this@AddJobActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun validateInputs(): Boolean {
        if (etJobTitle.text.toString().trim().isEmpty()) {
            etJobTitle.error = "Job title is required"
            return false
        }
        if (etJobDescription.text.toString().trim().isEmpty()) {
            etJobDescription.error = "Job description is required"
            return false
        }
        if (etLocation.text.toString().trim().isEmpty()) {
            etLocation.error = "Location is required"
            return false
        }
        if (etCompanyName.text.toString().trim().isEmpty()) {
            etCompanyName.error = "Company name is required"
            return false
        }
        // Add more validations as needed
        return true
    }

    private fun createPartFromString(partName: String, value: String): RequestBody {
        return RequestBody.create("text/plain".toMediaType(), value)
    }

    private fun createFilePartFromUri(uri: Uri, partName: String): MultipartBody.Part {
        val file = File(cacheDir, "company_logo.jpg")
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        val requestFile = RequestBody.create("image/*".toMediaType(), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1001
    }
}
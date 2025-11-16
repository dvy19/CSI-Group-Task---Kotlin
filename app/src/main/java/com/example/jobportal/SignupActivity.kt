package com.example.jobportal

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        // Initialize SharedPreferences helper
        preferences = AppPreferences(this)

        println("DEBUG: SignupActivity onCreate")

        val Fullname = findViewById<EditText>(R.id.etFullName)
        val mail = findViewById<EditText>(R.id.etEmail)
        val Password = findViewById<EditText>(R.id.etPassword)
        val ConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val radioJobSeeker = findViewById<RadioButton>(R.id.radioJobSeeker)
        val radioJobGiver = findViewById<RadioButton>(R.id.radioJobGiver)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        val toLogin=findViewById<TextView>(R.id.tvLogin)

        toLogin.setOnClickListener(){

            val intent=Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }


        btnSignUp.setOnClickListener {
            println("DEBUG: Signup button clicked")

            val fullName = Fullname.text.toString()
            val email = mail.text.toString()
            val password = Password.text.toString()
            val password2 = ConfirmPassword.text.toString()

            // Get selected role
            val role = if (radioJobGiver.isChecked) "job_giver" else "jobseeker"

            // REMOVED: The intent creation and startActivity calls from here
            // They should only be called after successful signup

            if (!validateInput(fullName, email, password, password2)) {
                return@setOnClickListener
            }

            // Show progress bar
            progressBar.visibility = ProgressBar.VISIBLE
            btnSignUp.isEnabled = false

            val request = SignupRequest(
                full_name = fullName,
                email = email,
                password = password,
                password2 = password2,
                role = role  // Add the role
            )

            println("DEBUG: Making API call")
            SignUpRetrofitClient.signupInstance.signup(request)
                .enqueue(object : Callback<SignupResponse> {
                    override fun onResponse(
                        call: Call<SignupResponse>,
                        response: Response<SignupResponse>
                    ) {
                        // Hide progress bar
                        progressBar.visibility = ProgressBar.GONE
                        btnSignUp.isEnabled = true

                        println("DEBUG: API response received - Success: ${response.isSuccessful}")

                        if (response.isSuccessful) {
                            val signupResponse = response.body()

                            if (signupResponse != null) {
                                // ✅ SAVE THE TOKENS HERE!
                                preferences.saveTokens(
                                    accessToken = signupResponse.access,
                                    refreshToken = signupResponse.refresh
                                )

                                println("DEBUG: Tokens saved successfully!")
                                println("DEBUG: Access Token: ${signupResponse.access}")
                                println("DEBUG: Refresh Token: ${signupResponse.refresh}")

                                Toast.makeText(
                                    this@SignupActivity,
                                    "Signup Successful!",
                                    Toast.LENGTH_LONG
                                ).show()

                                // Navigate to next activity based on role - MOVED THIS HERE
                                val intent = if (role == "job_giver") {
                                    Intent(this@SignupActivity, MainActivity::class.java)
                                } else {
                                    Intent(this@SignupActivity, SeekerProfileActivity::class.java)
                                }
                                startActivity(intent)
                                finish() // Close signup activity

                            } else {
                                Toast.makeText(
                                    this@SignupActivity,
                                    "Signup failed: Invalid response",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        } else {
                            // Handle different error cases
                            val errorMessage = when (response.code()) {
                                400 -> "Email already exists or invalid data"
                                500 -> "Server error, please try again later"
                                else -> "Signup failed: ${response.code()}"
                            }

                            Toast.makeText(
                                this@SignupActivity,
                                errorMessage,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                        // Hide progress bar
                        progressBar.visibility = ProgressBar.GONE
                        btnSignUp.isEnabled = true

                        println("DEBUG: API call failed: ${t.message}")
                        Toast.makeText(
                            this@SignupActivity,
                            "Network error: ${t.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }








        val loginBtn=findViewById<TextView>(R.id.tvLogin)
        loginBtn.setOnClickListener{
            val intent=Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

    }

    private fun validateInput(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show()
            return false
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty() || password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        println("DEBUG: SignupActivity onDestroy")
    }
}
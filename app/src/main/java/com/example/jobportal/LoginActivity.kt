package com.example.jobportal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jobportal.SignupActivity
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)

        // Initialize SharedPreferences helper
        preferences = AppPreferences(this)

        println("DEBUG: LoginActivity onCreate")

        val etEmail = findViewById<EditText>(R.id.mail)
        val etPassword = findViewById<EditText>(R.id.password)
        val btnLogin = findViewById<Button>(R.id.login_button)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvSignUpRedirect = findViewById<TextView>(R.id.tvSignUpRedirect)

        val radioJobSeeker = findViewById<RadioButton>(R.id.radioJobSeeker)
        val radioJobGiver = findViewById<RadioButton>(R.id.radioJobGiver)

        // Redirect to Signup
        tvSignUpRedirect.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            println("DEBUG: Login button clicked")

            // Moved role selection INSIDE the click listener
            val role = when {
                radioJobGiver.isChecked -> "job_giver"
                radioJobSeeker.isChecked -> "job_seeker"  // Fixed typo: was "jobseeker"
                else -> {
                    Toast.makeText(this@LoginActivity, "Please select a role", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (!validateInput(email, password)) {
                return@setOnClickListener
            }

            // Show progress bar
            progressBar.visibility = ProgressBar.VISIBLE
            btnLogin.isEnabled = false

            val request = LoginRequest(
                email = email,
                password = password
            )

            println("DEBUG: Making login API call for role: $role")
            SignUpRetrofitClient.loginInstance.login(request)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        // Hide progress bar
                        progressBar.visibility = ProgressBar.GONE
                        btnLogin.isEnabled = true

                        println("DEBUG: Login API response received - Success: ${response.isSuccessful}")

                        if (response.isSuccessful) {
                            val loginResponse = response.body()

                            if (loginResponse != null) {
                                // ✅ SAVE THE TOKENS HERE!
                                preferences.saveTokens(
                                    accessToken = loginResponse.access,
                                    refreshToken = loginResponse.refresh
                                )

                                println("DEBUG: Login successful - Tokens saved!")
                                println("DEBUG: Access Token: ${loginResponse.access}")
                                println("DEBUG: Refresh Token: ${loginResponse.refresh}")
                                println("DEBUG: Navigating to activity for role: $role")

                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login Successful!",
                                    Toast.LENGTH_LONG
                                ).show()

                                val intent = if (role == "job_giver") {
                                    Intent(this@LoginActivity, MainActivity::class.java)
                                } else {
                                    Intent(this@LoginActivity, HomeActivity::class.java)
                                }
                                startActivity(intent)
                                finish()

                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login failed: Invalid response",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        } else {
                            // Handle different error cases
                            val errorMessage = when (response.code()) {
                                400 -> "Missing or invalid fields"
                                401 -> "Invalid email or password"
                                500 -> "Server error, please try again later"
                                else -> "Login failed: ${response.code()}"
                            }

                            Toast.makeText(
                                this@LoginActivity,
                                errorMessage,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        // Hide progress bar
                        progressBar.visibility = ProgressBar.GONE
                        btnLogin.isEnabled = true

                        println("DEBUG: Login API call failed: ${t.message}")
                        Toast.makeText(
                            this@LoginActivity,
                            "Network error: ${t.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty() || password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        println("DEBUG: LoginActivity onDestroy")
    }
}
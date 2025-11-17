package com.example.jobportal

import android.content.Intent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
<<<<<<< HEAD
import com.example.jobportal.SignupActivity
=======
>>>>>>> 2ef3b147b7564784bd1a0f25008c45ed4df9043c
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var preferences: AppPreferences
    private var isLoading = false

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

        // Setup password toggle functionality
        setupPasswordToggle(etPassword)

        // Redirect to Signup
        tvSignUpRedirect.setOnClickListener {
            if (!isLoading) {
                val intent = Intent(this, SignupActivity::class.java)
                startActivity(intent)
            }
        }

        btnLogin.setOnClickListener {
            if (isLoading) return@setOnClickListener

            println("DEBUG: Login button clicked")

            val role = when {
                radioJobGiver.isChecked -> "job_giver"
                radioJobSeeker.isChecked -> "job_seeker"
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

            // Start loading state
            setLoadingState(true, progressBar, btnLogin, etEmail, etPassword, radioJobSeeker, radioJobGiver, tvSignUpRedirect)

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
                        // End loading state
                        setLoadingState(false, progressBar, btnLogin, etEmail, etPassword, radioJobSeeker, radioJobGiver, tvSignUpRedirect)

                        println("DEBUG: Login API response received - Success: ${response.isSuccessful}")
                        println("DEBUG: Response code: ${response.code()}")

                        if (response.isSuccessful) {
                            val loginResponse = response.body()

                            if (loginResponse != null) {
                                println("DEBUG: Login response body received")
                                println("DEBUG: Access Token from API: ${loginResponse.access}")
                                println("DEBUG: Refresh Token from API: ${loginResponse.refresh}")

                                // Save the tokens
                                preferences.saveTokens(
                                    accessToken = loginResponse.access,
                                    refreshToken = loginResponse.refresh
                                )

                                // Save the user role for future reference
                                preferences.saveUserRole(role)

                                // Verify tokens were saved
                                val savedToken = preferences.getAccessToken()
                                println("DEBUG: Verification - Token retrieved after saving: $savedToken")

                                println("DEBUG: Login successful - All data saved!")

                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login Successful!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = if (role == "job_giver") {
                                    Intent(this@LoginActivity, MainActivity::class.java)
                                } else {
                                    Intent(this@LoginActivity, HomeActivity::class.java)
                                }

                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()

                            } else {
                                println("DEBUG: Login response body is NULL")
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login failed: Invalid response",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        } else {
                            // Handle different error cases
                            println("DEBUG: Login failed - Response code: ${response.code()}")

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
                        // End loading state
                        setLoadingState(false, progressBar, btnLogin, etEmail, etPassword, radioJobSeeker, radioJobGiver, tvSignUpRedirect)

                        println("DEBUG: Login API call failed")
                        println("DEBUG: Error: ${t.message}")
                        println("DEBUG: Stack trace: ${t.stackTraceToString()}")

                        Toast.makeText(
                            this@LoginActivity,
                            "Network error: ${t.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }

    private fun setLoadingState(
        loading: Boolean,
        progressBar: ProgressBar,
        loginButton: Button,
        etEmail: EditText,
        etPassword: EditText,
        radioJobSeeker: RadioButton,
        radioJobGiver: RadioButton,
        tvSignUpRedirect: TextView
    ) {
        isLoading = loading

        // Show/hide progress bar
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE

        // Enable/disable login button
        loginButton.isEnabled = !loading
        loginButton.alpha = if (loading) 0.5f else 1.0f

        // Enable/disable input fields
        etEmail.isEnabled = !loading
        etPassword.isEnabled = !loading
        radioJobSeeker.isEnabled = !loading
        radioJobGiver.isEnabled = !loading
        tvSignUpRedirect.isEnabled = !loading
        tvSignUpRedirect.alpha = if (loading) 0.5f else 1.0f
    }

    private fun setupPasswordToggle(passwordEditText: EditText) {
        // Set initial state (hidden password)
        passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0)

        passwordEditText.setOnTouchListener { v, event ->
            if (isLoading) return@setOnTouchListener true // Prevent toggle during loading

            val drawableRight = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (passwordEditText.right - passwordEditText.compoundDrawables[drawableRight].bounds.width())) {
                    togglePasswordVisibility(passwordEditText)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun togglePasswordVisibility(editText: EditText) {
        val selection = editText.selectionEnd
        if (editText.transformationMethod == PasswordTransformationMethod.getInstance()) {
            // Show password
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            // Change drawable to eye off icon
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0)
        } else {
            // Hide password
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            // Change drawable to eye icon
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0)
        }
        editText.setSelection(selection)
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
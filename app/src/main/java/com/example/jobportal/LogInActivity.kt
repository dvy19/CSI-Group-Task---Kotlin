package com.example.jobportal

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogInActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)

        emailEditText = findViewById(R.id.mail)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login_button)


        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        val request = LoginRequest(email, password)

        LoginRetrofit.instance.login(request)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        Toast.makeText(applicationContext, "Signup Successful!", Toast.LENGTH_LONG).show()

                        val accessToken = loginResponse?.access
                        val refreshToken = loginResponse?.refresh
                    } else {
                        //statusTextView.text = "Login Failed: ${response.code()}"

                        Toast.makeText(applicationContext, "Login Failed: ${response.code()}",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    //statusTextView.text = "Error: ${t.message}"
                    Toast.makeText(applicationContext, "Error: ${t.message}",Toast.LENGTH_SHORT).show()
                }
            })
    }
}

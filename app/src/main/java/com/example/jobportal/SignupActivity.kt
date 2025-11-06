package com.example.jobportal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)




        val Fullname = findViewById<EditText>(R.id.full_name)
        val mail = findViewById<EditText>(R.id.mail)
        val Password = findViewById<EditText>(R.id.password)
        val ConfirmPassword = findViewById<EditText>(R.id.confirm_password)
        val btnSignUp = findViewById<Button>(R.id.btn_signup)

        btnSignUp.setOnClickListener {
            val fullName = Fullname.text.toString()
            val email = mail.text.toString()
            val password = Password.text.toString()
            val password2 = ConfirmPassword.text.toString()

            if (password != password2) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Split full name into first and last
            val parts = fullName.split(" ")
            val firstName = parts.getOrNull(0) ?: ""
            val lastName = parts.getOrNull(1) ?: ""

            val request = SignupRequest(
                full_name = fullName,
                email = email,
                password = password,
                password2 = password2,

            )

            SignUpRetrofitClient.instance.signup(request)
                .enqueue(object : Callback<SignupResponse> {
                    override fun onResponse(
                        call: Call<SignupResponse>,
                        response: Response<SignupResponse>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(applicationContext, "Signup Successful!", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@SignupActivity, HomeActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext, "Signup Failed: ${response.code()}", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "Error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                })
        }







        val loginBtn=findViewById<TextView>(R.id.login_btn)
        loginBtn.setOnClickListener{
            val intent=Intent(this, LogInActivity::class.java)
            startActivity(intent)

        }
    }
}

package com.example.fooddeliveryapp.LoginActivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fooddeliveryapp.Activity.MainActivity
import com.example.fooddeliveryapp.ApiManagers.ApiManagers
import com.example.fooddeliveryapp.DataModel.RegisterUserRequest
import com.example.fooddeliveryapp.DataModel.RegisterUserResponse
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.databinding.ActivityRegisterBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    var isPasswordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("AccessToken", null)

        if (accessToken != null) {
            // User is already logged in, proceed to main screen
            Toast.makeText(this, "You logged in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Optional: Finish the launcher activity
        } else {
            // User is not logged in, show login screen
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show()
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.ivPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                binding.inputPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.ivPassword.setImageResource(R.drawable.eye_open)
            }else {
                binding.inputPass.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.ivPassword.setImageResource(R.drawable.eye_crossed)
            }

            binding.inputPass.setSelection(binding.inputPass.text.length)
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.inputName.text.toString().trim()
            val email = binding.inputEmail.text.toString().trim()
            val phone = binding.inputPhone.text.toString().trim()
            val password = binding.inputPass.text.toString()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "These fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else {
                registerUser(name, email, phone, password)
            }
        }
    }

    private fun registerUser(name: String, email: String, phone: String, password: String) {
        val request = RegisterUserRequest(name, email, phone, password)
        ApiManagers.authService.registerUser(request).enqueue(object :
            Callback<RegisterUserResponse> {
            override fun onResponse(
                call: Call<RegisterUserResponse?>,
                response: Response<RegisterUserResponse?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()?.data?.createdUser
                    val accessToken = response.body()?.data?.accessToken
                    val refreshToken = response.body()?.data?.refreshToken

                    val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    editor.putString("username", user?.name)
                    editor.putString("phone", user?.phone)
                    editor.putString("email", user?.email)
                    editor.putString("role", user?.role)
                    editor.putString("AccessToken", accessToken)
                    editor.putString("refreshToken", refreshToken)
                    editor.apply()

                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    Toast.makeText(this@RegisterActivity, "Logged in successfully", Toast.LENGTH_SHORT).show()
                }else{
                    try {
                        val errorBodyString = response.errorBody()?.string()
                        Log.e("RegisterActivity", "Login error body: $errorBodyString")

                        val errorJson = errorBodyString?.let { JSONObject(it) }
                        val errorMessage = errorJson?.optString("message", "Login failed") ?: "Login failed"

                        Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()

                    } catch (e: Exception) {
                        Log.e("LoginActivity", "Error parsing error body", e)
                        Toast.makeText(this@RegisterActivity, "Unexpected error occurred", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<RegisterUserResponse?>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
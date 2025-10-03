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
import com.example.fooddeliveryapp.DataModel.LoginUserRequest
import com.example.fooddeliveryapp.DataModel.LoginUserResponse
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.databinding.ActivityLoginBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.PI

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    var isPasswordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("AccessToken", null)

        // Check if the intent has "logout" extra, to prevent auto-login after logout
        val isLogout = intent.getBooleanExtra("logout", false)

        if (accessToken != null && !isLogout) {
            // User is already logged in, proceed to main screen
            Toast.makeText(this, "You logged in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Optional: Finish the launcher activity
        } else {
            // User is not logged in, show login screen
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.ivPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                binding.etPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.ivPassword.setImageResource(R.drawable.eye_open)
            }else {
                binding.etPass.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.ivPassword.setImageResource(R.drawable.eye_crossed)
            }

            binding.etPass.setSelection(binding.etPass.text.length)
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPass.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }else{
                loginUser(email, password)
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val request = LoginUserRequest(email, password)

        ApiManagers.authService.loginUser(request).enqueue(object : Callback<LoginUserResponse> {
            override fun onResponse(
                call: Call<LoginUserResponse?>,
                response: Response<LoginUserResponse?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val accessToken = response.body()?.data?.accessToken
                    val user = response.body()?.data?.loggedInUser

                    val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("username", user?.name)
                    editor.putString("phone", user?.phone)
                    editor.putString("email", user?.email)
                    editor.putString("role", user?.role)
                    editor.putString("AccessToken", accessToken)
                    editor.apply()

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    Toast.makeText(this@LoginActivity, "Logged in successfully", Toast.LENGTH_SHORT).show()
                }else {
                    try {
                        val errorBodyString = response.errorBody()?.string()
                        Log.e("RegisterActivity", "Login error body: $errorBodyString")

                        val errorJson = errorBodyString?.let { JSONObject(it) }
                        val errorMessage = errorJson?.optString("message", "Login failed") ?: "Login failed"

                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e("LoginActivity", "Error parsing error body", e)
                        Toast.makeText(this@LoginActivity, "Unexpected error occurred", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(
                call: Call<LoginUserResponse?>,
                t: Throwable
            ) {
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }
}
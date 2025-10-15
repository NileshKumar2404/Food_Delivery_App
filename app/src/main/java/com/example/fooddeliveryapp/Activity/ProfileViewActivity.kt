package com.example.fooddeliveryapp.Activity

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.ImageViewCompat
import com.example.fooddeliveryapp.LoginActivity.LoginActivity
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.databinding.ActivityProfileViewBinding

class ProfileViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        highlightBottomNav(binding.profileBtn.id)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        val username = sharedPreferences.getString("username", "username")
        val email = sharedPreferences.getString("email", "email")

        binding.tvProfileName.text = username
        binding.tvProfileEmail.text = email

        setupListeners()
    }
    private fun setupListeners() {
        binding.llMyAddresses.setOnClickListener {
            startActivity(Intent(this, SavedAddressActivity::class.java))
        }

        binding.llLogout.setOnClickListener {
            logoutUser()
        }

        binding.homeBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.llFavorites.setOnClickListener {
            startActivity(Intent(this, FavouriteActivity::class.java))
        }

        binding.favouriteBtn.setOnClickListener {
            startActivity(Intent(this, FavouriteActivity::class.java))
        }

        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
            finish()
        }

        binding.llOrders.setOnClickListener {
            startActivity(Intent(this, MyOrderActivity::class.java))
            finish()
        }
    }
    private fun logoutUser() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("logout", true)
        startActivity(intent)
        finish()
    }
    private fun highlightBottomNav(activeButtonId: Int) {
        val inactiveColor = ContextCompat.getColor(this, R.color.black)
        val activeColor = ContextCompat.getColor(this, R.color.brand_600)

        // All your nav buttons
        val buttons = listOf(
            binding.homeBtn,
            binding.cartBtn,
            binding.favouriteBtn,
            binding.profileBtn
        )

        // Loop and tint
        buttons.forEach { button ->
            val color = if (button.id == activeButtonId) activeColor else inactiveColor
            ImageViewCompat.setImageTintList(button, ColorStateList.valueOf(color))
        }
    }

}
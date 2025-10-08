package com.example.fooddeliveryapp.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fooddeliveryapp.Adapter.MenuAdapter
import com.example.fooddeliveryapp.AuthInterceptor.AuthInterceptor
import com.example.fooddeliveryapp.DataModel.AddtoCartModelResponse
import com.example.fooddeliveryapp.DataModel.AddtoCartRequest
import com.example.fooddeliveryapp.DataModel.GetAllMenuItemsModelResponse
import com.example.fooddeliveryapp.DataModel.MenuItemsContainer
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.Routes.ApiService
import com.example.fooddeliveryapp.URLs.RetrofitInstance
import com.example.fooddeliveryapp.databinding.ActivityMenuItemBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log

class MenuItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuItemBinding
    private lateinit var adapter: MenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMenuItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBackMenuItem.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        setupRecyclerView()
        fetchAllMenuItems()
    }
    private fun setupRecyclerView() {
        adapter = MenuAdapter(emptyList()) { menuItem ->
            addToCart(menuItem._id, 1)
        }
        binding.rvMenu.layoutManager = LinearLayoutManager(this)
        binding.rvMenu.adapter = adapter
    }
    private fun fetchAllMenuItems() {
        showLoading(true)

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val authInterceptor = AuthInterceptor.AuthInterceptor(sharedPreferences)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(RetrofitInstance.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val apiServiceWithInterceptor = retrofit.create(ApiService::class.java)

        apiServiceWithInterceptor.getAllMenuItems().enqueue(object : Callback<GetAllMenuItemsModelResponse> {
            override fun onResponse(
                call: Call<GetAllMenuItemsModelResponse?>,
                response: Response<GetAllMenuItemsModelResponse?>
            ) {
                if (response.isSuccessful) {
                    showLoading(false)
                    binding.rvMenu.visibility = View.VISIBLE
                    val data = response.body()!!.data
                    Log.e("All menuitems: ", "$data")
                    adapter.updateList(data)
                } else{
                    Toast.makeText(this@MenuItemActivity, "Failed to fetch all menu items", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetAllMenuItemsModelResponse?>, t: Throwable) {
                showLoading(false)
                binding.tvMenu.visibility = View.VISIBLE
                Log.e("All menuitems: ", "${t.localizedMessage}")
                Toast.makeText(this@MenuItemActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun addToCart(menuItemId: String?, qty: Int) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val authInterceptor = AuthInterceptor.AuthInterceptor(sharedPreferences)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(RetrofitInstance.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val apiServiceWithInterceptor = retrofit.create(ApiService::class.java)

        val request = AddtoCartRequest(menuItemId!!, qty)

        apiServiceWithInterceptor.addToCart(request).enqueue(object : Callback<AddtoCartModelResponse> {
            override fun onResponse(
                call: Call<AddtoCartModelResponse?>,
                response: Response<AddtoCartModelResponse?>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MenuItemActivity, "Added to cart", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this@MenuItemActivity, "Failed to add in cart", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AddtoCartModelResponse?>, t: Throwable) {
                Log.e("Cart", "Error adding to cart: ${t.localizedMessage}")
                Toast.makeText(this@MenuItemActivity, "${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbMenu.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
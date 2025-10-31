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
import com.example.fooddeliveryapp.Adapter.RestaurantAdapter
import com.example.fooddeliveryapp.AuthInterceptor.AuthInterceptor
import com.example.fooddeliveryapp.DataModel.CreatedRestaurants
import com.example.fooddeliveryapp.DataModel.FavouriteRestaurantModelResponse
import com.example.fooddeliveryapp.DataModel.GetAllRestaurantResponse
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.Routes.ApiService
import com.example.fooddeliveryapp.URLs.RetrofitInstance
import com.example.fooddeliveryapp.databinding.ActivityAllRestaurantBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AllRestaurantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllRestaurantBinding
    private var adapter: RestaurantAdapter ?= null
    private val restaurantLists = mutableListOf<CreatedRestaurants>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAllRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadRestaurants()
        setUpRecyclerView()
        setUpListeners()
    }

    private fun setUpRecyclerView() {
        binding.rvRestaurantItems.layoutManager = LinearLayoutManager(this)
        adapter = RestaurantAdapter(restaurantLists) { restaurant ->
            addRestaurantInFavourite(restaurant._id)
        }
        binding.rvRestaurantItems.adapter = adapter
    }
    private fun setUpListeners() {
        binding.restRefreshLayout.setOnRefreshListener {
            loadRestaurants()
        }

        binding.cartBtnRestaurant.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    private fun loadRestaurants() {
        binding.progressBarRestaurant.visibility = View.VISIBLE

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

        apiServiceWithInterceptor.getAllRestaurant().enqueue(object : Callback<GetAllRestaurantResponse> {
            override fun onResponse(
                call: Call<GetAllRestaurantResponse?>,
                response: Response<GetAllRestaurantResponse?>
            ) {
                if (response.isSuccessful) {
                    binding.progressBarRestaurant.visibility = View.GONE
                    binding.rvRestaurantItems.visibility = View.VISIBLE
                    binding.restRefreshLayout.isRefreshing = false

                    val data = response.body()!!.data

                    adapter?.updateList(data.restaurants)
                    restaurantLists.clear()
                    restaurantLists.addAll(data.restaurants)
                    Toast.makeText(this@AllRestaurantActivity, "Restaurant added as favourite", Toast.LENGTH_SHORT).show()
                    adapter?.notifyDataSetChanged()
                }else {
                    Toast.makeText(this@AllRestaurantActivity, "Failed to load restaurants", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetAllRestaurantResponse?>, t: Throwable) {
                binding.progressBarRestaurant.visibility = View.GONE
                binding.rvRestaurantItems.visibility = View.GONE
                binding.restRefreshLayout.isRefreshing = false

                Log.e("All Restaurant", t.localizedMessage)
                Toast.makeText(this@AllRestaurantActivity, "Failed to load restaurants", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun addRestaurantInFavourite(restaurantId: String) {
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

        apiServiceWithInterceptor.addFavouriteRestaurant(restaurantId).enqueue(object : Callback<FavouriteRestaurantModelResponse> {
            override fun onResponse(
                call: Call<FavouriteRestaurantModelResponse?>,
                response: Response<FavouriteRestaurantModelResponse?>
            ) {
                if (response.isSuccessful) {
                    val favouriteRestaurantResponse = response.body()
                    if (favouriteRestaurantResponse != null) {
//                        updateHeartIcon()
                        Toast.makeText(this@AllRestaurantActivity, "Restaurant added as favourite", Toast.LENGTH_SHORT).show()
                    }
                }else {
                    Toast.makeText(this@AllRestaurantActivity, "Failed to add in favourites", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FavouriteRestaurantModelResponse?>, t: Throwable) {
                Log.e("Add in Favourite", t.localizedMessage)
                Toast.makeText(this@AllRestaurantActivity, "Failed to add in favourites", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
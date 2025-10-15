package com.example.fooddeliveryapp.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fooddeliveryapp.Adapter.FavouriteMenuAdapter
import com.example.fooddeliveryapp.Adapter.FavouriteRestaurantAdapter
import com.example.fooddeliveryapp.AuthInterceptor.AuthInterceptor
import com.example.fooddeliveryapp.DataModel.FavouriteMenuItem
import com.example.fooddeliveryapp.DataModel.FavouriteRestaurant
import com.example.fooddeliveryapp.DataModel.GetListOfFavouritesResponse
import com.example.fooddeliveryapp.DataModel.RemoveFavouriteMenuItemResponse
import com.example.fooddeliveryapp.DataModel.RemoveFavouriteRestaurantResponse
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.Routes.ApiService
import com.example.fooddeliveryapp.URLs.RetrofitInstance
import com.example.fooddeliveryapp.databinding.ActivityFavouriteBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FavouriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteBinding
    private var restaurantAdapter: FavouriteRestaurantAdapter? = null
    private var menuAdapter: FavouriteMenuAdapter? = null
    private var favouriteRestaurants = mutableListOf<FavouriteRestaurant>()
    private var favouriteMenuItems = mutableListOf<FavouriteMenuItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupTabs()
        setupBottomNav()
        loadFavourites()
    }
    private fun setupRecyclerView() {
        binding.rvFavouriteRestaurants.layoutManager = LinearLayoutManager(this)
        restaurantAdapter = FavouriteRestaurantAdapter(favouriteRestaurants) {restaurant, i ->
            removeRestaurantFromFavourite(restaurant, i)
        }
        binding.rvFavouriteRestaurants.adapter = restaurantAdapter

        binding.rvFavouriteMenuItems.layoutManager = LinearLayoutManager(this)
        menuAdapter = FavouriteMenuAdapter(favouriteMenuItems) {menuItem, i ->
            removeMenuItemFromFavourite(menuItem, i)
        }
        binding.rvFavouriteMenuItems.adapter = menuAdapter
    }
    private fun setupTabs() {
        binding.tvRestaurant.setOnClickListener {
            showRestaurants()
        }

        binding.tvMenuItem.setOnClickListener {
            showMenuItems()
        }

        binding.favouriteRefresh.setOnRefreshListener {
            loadFavourites()
        }
    }
    private fun setupBottomNav() {
        binding.favouriteBtn.setColorFilter(getColor(R.color.brand_600))
        binding.homeBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileViewActivity::class.java))
            finish()
        }
        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, MenuItemActivity::class.java))
            finish()
        }
    }
    private fun loadFavourites() {
        binding.errorPb.visibility = View.VISIBLE
        binding.errorTv.visibility = View.GONE

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

        apiServiceWithInterceptor.getFavourite().enqueue(object : Callback<GetListOfFavouritesResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<GetListOfFavouritesResponse?>,
                response: Response<GetListOfFavouritesResponse?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    binding.favouriteRefresh.isRefreshing = false

                    binding.errorPb.visibility = View.GONE

                    val data = response.body()!!.data
                    favouriteRestaurants.clear()
                    favouriteRestaurants.addAll(data.favouriteRestaurants)

                    favouriteMenuItems.clear()
                    favouriteMenuItems.addAll(data.favouriteMenuItems)

                    restaurantAdapter?.notifyDataSetChanged()
                    menuAdapter?.notifyDataSetChanged()

                    if (binding.rvFavouriteRestaurants.visibility == View.VISIBLE) {
                        toggleEmptyState(favouriteRestaurants.isEmpty())
                    } else {
                        toggleEmptyState(favouriteMenuItems.isEmpty())
                    }
                }else {
                    showError("Failed to load favourites")
                    Toast.makeText(this@FavouriteActivity, "Failed to get favourites", Toast.LENGTH_SHORT).show()
                    Log.e("FavouriteActivity", "Failed to get favourites: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GetListOfFavouritesResponse?>, t: Throwable) {
                binding.favouriteRefresh.isRefreshing = false
                binding.errorPb.visibility = View.GONE
                showError(t.message ?: "Error occurred")
                Log.e("FavouriteActivity", "Error: ${t.message}")
            }
        })
    }
    private fun toggleEmptyState(isEmpty: Boolean) {
        binding.errorTv.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
    private fun showError(msg: String) {
        binding.errorTv.text = msg
        binding.errorTv.visibility = View.VISIBLE
    }
    private fun showRestaurants() {
        binding.tvRestaurant.setTextColor(getColor(R.color.brand_600))
        binding.tvMenuItem.setTextColor(getColor(R.color.ink_500))
        binding.rvFavouriteRestaurants.visibility = View.VISIBLE
        binding.rvFavouriteMenuItems.visibility = View.GONE
        toggleEmptyState(favouriteRestaurants.isEmpty())
    }
    private fun showMenuItems() {
        binding.tvMenuItem.setTextColor(getColor(R.color.brand_600))
        binding.tvRestaurant.setTextColor(getColor(R.color.ink_500))
        binding.rvFavouriteMenuItems.visibility = View.VISIBLE
        binding.rvFavouriteRestaurants.visibility = View.GONE
        toggleEmptyState(favouriteMenuItems.isEmpty())
    }
    private fun removeRestaurantFromFavourite(restaurant: FavouriteRestaurant, position: Int) {
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

        apiServiceWithInterceptor.removeFavouriteRestaurant(restaurant._id).enqueue(object : Callback<RemoveFavouriteRestaurantResponse> {
            override fun onResponse(
                call: Call<RemoveFavouriteRestaurantResponse?>,
                response: Response<RemoveFavouriteRestaurantResponse?>
            ) {
                if (response.isSuccessful) {
                    restaurantAdapter?.removeAt(position)
                    toggleEmptyState(favouriteRestaurants.isEmpty())
                    Toast.makeText(this@FavouriteActivity, "Removed from favourites", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@FavouriteActivity, "Failed to remove from favourites", Toast.LENGTH_SHORT).show()
                    Log.e("RemoveFavouriteActivity", "Failed to remove from favourites: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RemoveFavouriteRestaurantResponse?>, t: Throwable) {
                Toast.makeText(this@FavouriteActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun removeMenuItemFromFavourite(menuItems: FavouriteMenuItem, position: Int) {
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

        apiServiceWithInterceptor.removeFavouriteMenuItem(menuItems._id).enqueue(object : Callback<RemoveFavouriteMenuItemResponse> {
            override fun onResponse(
                call: Call<RemoveFavouriteMenuItemResponse?>,
                response: Response<RemoveFavouriteMenuItemResponse?>
            ) {
                if (response.isSuccessful) {
                    menuAdapter?.removeAt(position)
                    toggleEmptyState(favouriteMenuItems.isEmpty())
                    Toast.makeText(this@FavouriteActivity, "Removed from favourites", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@FavouriteActivity, "Failed to remove from favourites", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RemoveFavouriteMenuItemResponse?>, t: Throwable) {
                Toast.makeText(this@FavouriteActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
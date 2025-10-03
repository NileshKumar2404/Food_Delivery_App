package com.example.fooddeliveryapp.Activity

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fooddeliveryapp.Adapter.HeroBannerAdapter
import com.example.fooddeliveryapp.Adapter.RecommendedAdapter
import com.example.fooddeliveryapp.Adapter.TopRatedRestaurantAdapter
import com.example.fooddeliveryapp.AuthInterceptor.AuthInterceptor
import com.example.fooddeliveryapp.DataModel.AllRestaurants
import com.example.fooddeliveryapp.DataModel.FeaturedRestaurantResponse
import com.example.fooddeliveryapp.DataModel.GetAllRestaurantResponse
import com.example.fooddeliveryapp.DataModel.SearchRestaurantResponse
import com.example.fooddeliveryapp.DataModel.TopRatedRestaurantResponse
//import com.example.fooddeliveryapp.DataModel.TopRatedRestaurants
import com.example.fooddeliveryapp.DataModel.featuredRestaurantsList
import com.example.fooddeliveryapp.DataModel.listtopRatedRestaurants
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.Routes.ApiService
import com.example.fooddeliveryapp.URLs.RetrofitInstance
import com.example.fooddeliveryapp.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //Adapters
    private lateinit var heroBannerAdapter: HeroBannerAdapter
    private lateinit var recommendedAdapter: RecommendedAdapter
    private lateinit var trustedAdapter: TopRatedRestaurantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        highlightBottomNav(binding.homeBtn.id)

        setupAdapters()
        loadHeroBanners()
        loadTrustedPicks()
        loadRecommended()
        setupListeners()

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "username")

        binding.tvUserName.text = username

        binding.etSearch.setOnEditorActionListener { _, _, _, ->
            val query = binding.etSearch.text.toString().trim()
            if (query.isNotEmpty()) searchRestaurants(query)
            true
        }
    }
    private fun setupListeners() {
        binding.ivAvatar.setOnClickListener {
            startActivity(Intent(this, ProfileViewActivity::class.java))
            finish()
        }

        binding.favouriteBtn.setOnClickListener {
            startActivity(Intent(this, FavouriteActivity::class.java))
        }

        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileViewActivity::class.java))
        }

        binding.ivLocation.setOnClickListener {
            startActivity(Intent(this, AddressActivity::class.java))
            finish()
        }
    }
    private fun setupAdapters() {
        heroBannerAdapter = HeroBannerAdapter(emptyList())
        binding.vpHeroBanner.adapter = heroBannerAdapter

        binding.rvTrusted.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        trustedAdapter = TopRatedRestaurantAdapter(emptyList()) { restaurant: listtopRatedRestaurants ->
            Toast.makeText(this, "Clicked add on ${restaurant.name}", Toast.LENGTH_SHORT).show() // Replace with your logic
        }
        binding.rvTrusted.adapter = trustedAdapter

        binding.rvRecommended.layoutManager = LinearLayoutManager(this)
        recommendedAdapter = RecommendedAdapter(emptyList()) { menuItem ->
            Toast.makeText(this, "Clicked add on ${menuItem.name}", Toast.LENGTH_SHORT).show() // Replace with your logic
        }
        binding.rvRecommended.adapter = recommendedAdapter
    }
    private fun loadHeroBanners() {
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

        apiServiceWithInterceptor.featuredRestaurants().enqueue(object : Callback<FeaturedRestaurantResponse> {
            override fun onResponse(
                call: Call<FeaturedRestaurantResponse?>,
                response: Response<FeaturedRestaurantResponse?>
            ) {
                if (response.isSuccessful) {
                    val featured = response.body()!!.data.featuredRestaurants
                    Log.e("Response", "response: ${response.body()!!.data.featuredRestaurants}")
                    heroBannerAdapter.updateItems(featured)
                } else {
                    Toast.makeText(this@MainActivity, "Failed to show banner", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FeaturedRestaurantResponse?>, t: Throwable) {
                Log.e("Mainactivity", "on failure: ${t.message}")
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun loadTrustedPicks() {
        binding.pbTrusted.visibility = View.VISIBLE

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

        apiServiceWithInterceptor.topRatedRestaurants().enqueue(object : Callback<TopRatedRestaurantResponse> {
            override fun onResponse(
                call: Call<TopRatedRestaurantResponse?>,
                response: Response<TopRatedRestaurantResponse?>
            ) {
                if (response.isSuccessful) {
                    binding.pbTrusted.visibility = View.GONE

                    val topRated = response.body()!!.data.topRatedRestaurants
                    print(topRated)
                    Log.e("Top rated Response", "response: ${topRated}")
                    trustedAdapter.updateList(topRated)
                }else{
                    Toast.makeText(this@MainActivity, "Failed to load restaurants", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TopRatedRestaurantResponse?>, t: Throwable) {
                binding.pbTrusted.visibility = View.GONE
                binding.rvTrusted.visibility = View.GONE

                Log.e("TrustedPicks", "on failure: ${t.message}")
                Toast.makeText(this@MainActivity, "Failed to load trusted picks", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun loadRecommended() {
        binding.pbRecommended.visibility = View.VISIBLE

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
                    binding.pbRecommended.visibility = View.GONE

                    val recommended = response.body()!!.data.restaurants
                    recommendedAdapter.updateList(recommended)
                }else {
                    Toast.makeText(this@MainActivity, "Failed to load restaurants", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetAllRestaurantResponse?>, t: Throwable) {
                binding.pbRecommended.visibility = View.GONE
                binding.rvRecommended.visibility = View.GONE

                Log.e("Recommended", "on failure: ${t.message}")
                Toast.makeText(this@MainActivity, "Failed to load recommended", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun searchRestaurants(query: String) {
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

        apiServiceWithInterceptor.searchRestaurants(query).enqueue(object : Callback<SearchRestaurantResponse> {
            override fun onResponse(
                call: Call<SearchRestaurantResponse?>,
                response: Response<SearchRestaurantResponse?>
            ) {
                if (response.isSuccessful) {
                    val restaurants = response.body()!!.data.restaurants
                    Log.e("Search Response", "response: ${restaurants}")
                }else {
                    Toast.makeText(this@MainActivity, "No restaurants found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SearchRestaurantResponse?>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
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
import com.bumptech.glide.Glide
import com.example.fooddeliveryapp.AuthInterceptor.AuthInterceptor
import com.example.fooddeliveryapp.DataModel.AddFavouriteMenuItemResponse
import com.example.fooddeliveryapp.DataModel.AddtoCartModelResponse
import com.example.fooddeliveryapp.DataModel.AddtoCartRequest
import com.example.fooddeliveryapp.DataModel.GetMenuItemsModelResponse
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.Routes.ApiService
import com.example.fooddeliveryapp.URLs.RetrofitInstance
import com.example.fooddeliveryapp.databinding.ActivityFoodDetailsBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FoodDetailsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: android.content.SharedPreferences
    private var isFavourite = false
    private lateinit var binding: ActivityFoodDetailsBinding
    private var menuItemId: String? = null
    private var restaurantId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFoodDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        setUpListeners()
    }
    private fun setUpListeners() {
        var currentQty = 1
        binding.tvQty.text = currentQty.toString()

        binding.btnPlus.setOnClickListener {
            currentQty++
            binding.tvQty.text = currentQty.toString()
//            updateTotalPrice(currentQty)
        }

        binding.btnMinus.setOnClickListener {
            if (currentQty > 1) {
                currentQty--
                binding.tvQty.text = currentQty.toString()
//                updateTotalPrice(currentQty)
            }
        }

        binding.btnFoodDetailsBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnAdd.setOnClickListener {
            menuItemId?.let {
                addToCart(it, currentQty)
            } ?: Toast.makeText(this, "No menu item id provided", Toast.LENGTH_SHORT).show()
        }

        binding.heartBtn.setOnClickListener {
            if(isFavourite) {
//                removeFromFavourites(menuItemId)
            } else {
                addToFavourite(menuItemId)
            }
        }

        menuItemId = intent.getStringExtra("MENU_ITEM_ID")

        menuItemId?.let {
            val set = sharedPreferences.getStringSet("favourite_menuItem", emptySet()) ?: emptyList()
            isFavourite = set.contains(it)
            updateHeartIcon()

            fetchMenuDetails(it) }
            ?: Toast.makeText(this, "No menu item id provided", Toast.LENGTH_SHORT).show()

        binding.btnBuyNow.setOnClickListener {
            if (restaurantId == null) {
                Toast.makeText(this, "Restaurant id is not available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, BuyNowActivity::class.java)
            intent.putExtra("menuItemId", menuItemId)
            intent.putExtra("restaurantId", restaurantId)
            intent.putExtra("quantity", currentQty)
            startActivity(intent)
        }
    }
    private fun addToFavourite(menuItemId: String?) {
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

        apiServiceWithInterceptor.addFavouriteMenuItem(menuItemId!!).enqueue(object : Callback<AddFavouriteMenuItemResponse> {
            override fun onResponse(
                call: Call<AddFavouriteMenuItemResponse?>,
                response: Response<AddFavouriteMenuItemResponse?>
            ) {
                if (response.isSuccessful) {
                    isFavourite = true
                    saveFavouriteLocally(menuItemId, true)
                    updateHeartIcon()
                    Toast.makeText(this@FoodDetailsActivity, "Added to favourites", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this@FoodDetailsActivity, "Failed to add in favourites", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AddFavouriteMenuItemResponse?>, t: Throwable) {
                Log.e("Favourite", "Error adding to favourites: ${t.localizedMessage}")
                Toast.makeText(this@FoodDetailsActivity, "${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun saveFavouriteLocally(menuItemId: String, add: Boolean) {
        val set = sharedPreferences.getStringSet("favourite_menuItem", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        if(add) set.add(menuItemId) else set.remove(menuItemId)
        sharedPreferences.edit().putStringSet("favourite_menuItem", set).apply()
    }
    private fun updateHeartIcon() {
        if (isFavourite) {
            binding.heartBtn.setImageResource(R.drawable.heart_filled)
        } else{
            binding.heartBtn.setImageResource(R.drawable.heart)
        }
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
                    Toast.makeText(this@FoodDetailsActivity, "Added to cart", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this@FoodDetailsActivity, "Failed to add in cart", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AddtoCartModelResponse?>, t: Throwable) {
                Log.e("Cart", "Error adding to cart: ${t.localizedMessage}")
                Toast.makeText(this@FoodDetailsActivity, "${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun fetchMenuDetails(menuItemId: String) {
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

        apiServiceWithInterceptor.getMenuItemById(menuItemId).enqueue(object : Callback<GetMenuItemsModelResponse> {
            override fun onResponse(
                call: Call<GetMenuItemsModelResponse?>,
                response: Response<GetMenuItemsModelResponse?>
            ) {
                showLoading(false)
                if (response.isSuccessful && response.body() != null) {
                    val menuItem = response.body()!!.data.menuItem
                    restaurantId = menuItem.restaurant._id
                    populateUI(
                        menuItem.name,
                        menuItem.description,
                        menuItem.price,
                        menuItem.ratings,
                        menuItem.image,
                        menuItem.restaurant.name
                    )
                }else {
                    Toast.makeText(this@FoodDetailsActivity, "Failed to load details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetMenuItemsModelResponse?>, t: Throwable) {
                showLoading(false)
                Log.e("FoodDetailsActivity", "Error fetching menu details: ${t.localizedMessage}")
                Toast.makeText(this@FoodDetailsActivity, "Failed to load food details", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun populateUI(name: String, description: String, price: String, rating: Double, image: String, restaurantName: String) {
        binding.tvTitle.text = name
        binding.tvDesc.text = description
        binding.tvTotal.text = price
        binding.tvRating.text = rating.toString()
        binding.tvSub.text = restaurantName

        Glide.with(binding.ivFood)
            .load(image)
            .into(binding.ivFood)
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbDetails.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun updateTotalPrice(qty: Int) {
        val price = binding.tvTotal.text.toString().replace("Rs", "").toDoubleOrNull() ?: 0.0
        val total = price * qty
        binding.tvTotal.text = "$%.2f".format(total)
    }
}
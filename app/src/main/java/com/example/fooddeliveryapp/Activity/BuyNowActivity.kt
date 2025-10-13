package com.example.fooddeliveryapp.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.fooddeliveryapp.AuthInterceptor.AuthInterceptor
import com.example.fooddeliveryapp.DataModel.GetMenuItemsModelResponse
import com.example.fooddeliveryapp.DataModel.Items
import com.example.fooddeliveryapp.DataModel.PlaceOrderData
import com.example.fooddeliveryapp.DataModel.PlaceOrderRequest
import com.example.fooddeliveryapp.DataModel.PlaceOrderResponse
import com.example.fooddeliveryapp.DataModel.SavedAddress
import com.example.fooddeliveryapp.DataModel.SavedAddressResponse
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.Routes.ApiService
import com.example.fooddeliveryapp.URLs.RetrofitInstance
import com.example.fooddeliveryapp.databinding.ActivityBuyNowBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BuyNowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuyNowBinding
    private var selectedAddressId: String? = null
    private var menuItemId: String? = null
    private var restaurantId: String? = null
    private var quantity: Int? = null
    private var selectedPaymentMethod: String = "COD"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBuyNowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupIntentData()
        setupListeners()
        fetchUserAddress()
    }
    private val selectAddressLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedAddress = result.data?.getStringExtra("selectedAddressId")
            val selectedAddressText = result.data?.getStringExtra("selectedAddressText")
            if (selectedAddress != null) {
                selectedAddressId = selectedAddress
                binding.tvDeliveryAddress.text = selectedAddressText ?: "Address fetched successfully"
            }
        }
    }
    private fun setupIntentData() {
        menuItemId = intent.getStringExtra("menuItemId")
        restaurantId = intent.getStringExtra("restaurantId")
        quantity = intent.getIntExtra("quantity", 1)

       if (menuItemId == null || restaurantId == null) {
           Toast.makeText(this, "Invalid order details", Toast.LENGTH_SHORT).show()
           finish()
           return
       } else {
           fetchMenuDetails(menuItemId!!)
       }
    }
    private fun setupListeners() {
        binding.apply {
            btnBackPlaceOrder.setOnClickListener {
                startActivity(Intent(this@BuyNowActivity, MainActivity::class.java))
                finish()
            }

            paymentOptions.setOnCheckedChangeListener { _, checkedId ->
                selectedPaymentMethod = when(checkedId) {
                    R.id.rbCOD -> "COD"
                    R.id.rbOnline -> "UPI" ?: "Net-Banking"
                    else -> "COD"
                }
            }

            btnPlaceOrder.setOnClickListener {
                if (selectedAddressId == null) {
                    Toast.makeText(this@BuyNowActivity, "Please select an address", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    placeOrder()
                }
            }

            tvChangeAddress.setOnClickListener {
                val intent = Intent(this@BuyNowActivity, SelectAddressActivity::class.java)
                selectAddressLauncher.launch(intent)
            }
        }
    }
    private fun placeOrder() {
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

        val items = listOf(Items(menuItemId!!, quantity!!))
        val request = PlaceOrderRequest(restaurantId!!, selectedAddressId!!, items, selectedPaymentMethod)

        apiServiceWithInterceptor.placeOrder(request).enqueue(object : Callback<PlaceOrderResponse> {
            override fun onResponse(
                call: Call<PlaceOrderResponse?>,
                response: Response<PlaceOrderResponse?>
            ) {
                showLoading(false)
                if (response.isSuccessful && response.body() != null) {
                    val orderResponse = response.body()!!.data
                    val backendTotal = orderResponse?.order?.totalPrice ?: 0

                    binding.tvTotalPrice.text = "₹$backendTotal"
                    Toast.makeText(this@BuyNowActivity, "Order Placed successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }else {
                    Toast.makeText(this@BuyNowActivity, "Failed to place order", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PlaceOrderResponse?>, t: Throwable) {
                Log.e("BuyNowActivity", "Error placing order: ${t.localizedMessage}")
                Toast.makeText(this@BuyNowActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun fetchUserAddress() {
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

        apiServiceWithInterceptor.getAddress().enqueue(object : Callback<SavedAddressResponse> {
            override fun onResponse(
                call: Call<SavedAddressResponse?>,
                response: Response<SavedAddressResponse?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val addresses = response.body()!!.data
                    if (addresses.isNotEmpty()) {
                        selectedAddressId = addresses.first()._id
                        val addr = addresses.first()
                        binding.tvDeliveryAddress.text = "${addr.street}, ${addr.city}, ${addr.state} - ${addr.pinCode}"
                    } else {
                        Toast.makeText(this@BuyNowActivity, "No addresses found", Toast.LENGTH_SHORT).show()
                    }
                } else{
                    Toast.makeText(this@BuyNowActivity, "Failed to fetch addresses", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SavedAddressResponse?>, t: Throwable) {
                Log.e("BuyNowActivity", "Error fetching addresses: ${t.localizedMessage}")
                Toast.makeText(this@BuyNowActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun fetchMenuDetails(menuItemId: String) {
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
                if (response.isSuccessful) {
                    val menuItemData = response.body()!!.data.menuItem
                    populateMenuItemDetails(menuItemData.name, menuItemData.restaurant.name, menuItemData.image, menuItemData.price)
                } else {
                    Toast.makeText(this@BuyNowActivity, "Failed to fetch menu item details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetMenuItemsModelResponse?>, t: Throwable) {
                Log.e("BuyNowActivity", "Error fetching menu item details: ${t.localizedMessage}")
                Toast.makeText(this@BuyNowActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun populateMenuItemDetails(name: String, restaurantName: String, imageUrl: String, price: String) {
        binding.apply {
            tvFoodName.text = name
            tvRestaurantName.text = restaurantName
            tvPrice.text = price
            tvQuantity.text = "x${quantity}"

            val numericPrice = price.replace(Regex("[^\\d.]"), "").toDoubleOrNull() ?: 0.0
            val totalPrice = quantity?.let { numericPrice * it }
            binding.tvTotalPrice.text = "₹${String.format("%.2f", totalPrice)}"

            Glide.with(this@BuyNowActivity)
                .load(imageUrl)
                .into(ivFoodImage)
        }
    }
    private fun showLoading(isLoading: Boolean) {}
}
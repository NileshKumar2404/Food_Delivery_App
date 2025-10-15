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
import com.example.fooddeliveryapp.Adapter.CartAdapter
import com.example.fooddeliveryapp.AuthInterceptor.AuthInterceptor
import com.example.fooddeliveryapp.DataModel.CartItems
import com.example.fooddeliveryapp.DataModel.GetCartModelResponse
import com.example.fooddeliveryapp.DataModel.RemoveItemFromCartModelResponse
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.Routes.ApiService
import com.example.fooddeliveryapp.URLs.RetrofitInstance
import com.example.fooddeliveryapp.databinding.ActivityCartBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter
    private val cartList = mutableListOf<CartItems>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        fetchCartItems()

        binding.cartBtnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.cartRefreshLayout.setOnRefreshListener {
            fetchCartItems()
        }
    }
    private fun setupRecyclerView() {
        adapter = CartAdapter(
            cartList,
            onQuantityChanged = { items, newQty ->
//                updateCartItem(items._id, newQty)
            },
            onDeleteItem = { items, position ->
                deleteCartItem(items._id, position)
            }
        )

        binding.rcCartItems.layoutManager = LinearLayoutManager(this)
        binding.rcCartItems.adapter = adapter
    }
    private fun fetchCartItems() {
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

        apiServiceWithInterceptor.getCart().enqueue(object : Callback<GetCartModelResponse> {
            override fun onResponse(
                call: Call<GetCartModelResponse?>,
                response: Response<GetCartModelResponse?>
            ) {
                showLoading(false)
                binding.cartRefreshLayout.isRefreshing = false
                if (response.isSuccessful) {
                    val data = response.body()!!.data

                    cartList.clear()
                    cartList.addAll(data.cartItems)

                    binding.tvcartTotal.text = "₹${data.total}"
                    toggleEmptyState()
                }else {
                    Toast.makeText(this@CartActivity, "Failed to fetch cart", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetCartModelResponse?>, t: Throwable) {
                showLoading(false)
                binding.cartRefreshLayout.isRefreshing = false
                Log.e("CartActivity Error: ", "${t.localizedMessage}")
                Toast.makeText(this@CartActivity, "Failed to fetch cart", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun deleteCartItem(itemId: String, position: Int) {
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
        
        apiServiceWithInterceptor.removeItemFromCart(itemId).enqueue(object : Callback<RemoveItemFromCartModelResponse>{
            override fun onResponse(
                call: Call<RemoveItemFromCartModelResponse?>,
                response: Response<RemoveItemFromCartModelResponse?>
            ) {
                if (response.isSuccessful) {
                    val updatedData = response.body()!!.data
                    binding.tvcartTotal.text = "₹${updatedData.total}"
                    toggleEmptyState()
                    Toast.makeText(this@CartActivity, "Item removed from cart", Toast.LENGTH_SHORT).show()
                    fetchCartItems()
                }else {
                    Toast.makeText(this@CartActivity, "Failed to remove item from cart", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RemoveItemFromCartModelResponse?>, t: Throwable) {
                Log.e("Delete item from cart: ", "${t.localizedMessage}")
                Toast.makeText(this@CartActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun toggleEmptyState() {
        if (cartList.isEmpty()) {
            binding.tvEmptyCart.visibility = View.VISIBLE
            binding.rcCartItems.visibility = View.GONE
        } else {
            binding.tvEmptyCart.visibility = View.GONE
            binding.rcCartItems.visibility = View.VISIBLE
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbCart.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
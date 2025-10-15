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
import com.example.fooddeliveryapp.Adapter.MyOrderAdapter
import com.example.fooddeliveryapp.AuthInterceptor.AuthInterceptor
import com.example.fooddeliveryapp.DataModel.GetMyOrderModelResponse
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.Routes.ApiService
import com.example.fooddeliveryapp.URLs.RetrofitInstance
import com.example.fooddeliveryapp.databinding.ActivityMyOrderBinding
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyOrderBinding
    private lateinit var adapter: MyOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpRecyclerView()
        setUpListeners()
        fetchOrder()
    }
    private fun setUpRecyclerView() {
        adapter = MyOrderAdapter()
        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewOrders.adapter = adapter
    }
    private fun setUpListeners() {
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, ProfileViewActivity::class.java))
            finish()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchOrder()
        }
    }
    private fun fetchOrder() {
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

        apiServiceWithInterceptor.getUserOrder().enqueue(object : Callback<GetMyOrderModelResponse> {
            override fun onResponse(
                call: Call<GetMyOrderModelResponse?>,
                response: Response<GetMyOrderModelResponse?>
            ) {
                showLoading(false)
                binding.swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful && response.body() != null) {
                    val orderResponse = response.body()!!.data.orders
                    if (orderResponse.isNotEmpty()) {
                        binding.recyclerViewOrders.visibility = View.VISIBLE
                        binding.layoutEmptyOrders.visibility = View.GONE
                        adapter.submitList(orderResponse)
                    } else {
                        binding.recyclerViewOrders.visibility = View.GONE
                        binding.layoutEmptyOrders.visibility = View.VISIBLE
                    }
                }else{
                    Toast.makeText(this@MyOrderActivity, "Failed to fetch orders", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GetMyOrderModelResponse?>, t: Throwable) {
                showLoading(false)
                binding.swipeRefreshLayout.isRefreshing = false
                Log.e("MyOrderActivity", "Error fetching orders: ${t.localizedMessage}")
                Toast.makeText(this@MyOrderActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.recyclerViewOrders.visibility = View.GONE
            binding.layoutEmptyOrders.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}
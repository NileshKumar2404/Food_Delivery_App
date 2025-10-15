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
import com.example.fooddeliveryapp.Adapter.SavedAddressAdapter
import com.example.fooddeliveryapp.AuthInterceptor.AuthInterceptor
import com.example.fooddeliveryapp.DataModel.DeleteSavedAddressResponse
import com.example.fooddeliveryapp.DataModel.SavedAddress
import com.example.fooddeliveryapp.DataModel.SavedAddressResponse
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.Routes.ApiService
import com.example.fooddeliveryapp.URLs.RetrofitInstance
import com.example.fooddeliveryapp.databinding.ActivitySavedAddressBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SavedAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedAddressBinding
    private val savedAddresses = mutableListOf<SavedAddress>()
    private lateinit var adapter: SavedAddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySavedAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        setupListeners()
        setUpRecyclerview()
        fetchSavedAddresses()
    }
    private fun setupListeners() {
        binding.btnBackSavedAddress.setOnClickListener { 
            startActivity(Intent(this, ProfileViewActivity::class.java))
            finish()
        }

        binding.btnAddNew.setOnClickListener {
            startActivity(Intent(this, AddressActivity::class.java))
            finish()
        }

        binding.savedAddressRefresh.setOnRefreshListener {
            fetchSavedAddresses()
        }
    }
    private fun setUpRecyclerview() {
        adapter = SavedAddressAdapter(savedAddresses,
            {address, i -> 
            deleteAddress(address._id, i)
        })
        binding.rvSavedAddress.layoutManager = LinearLayoutManager(this)
        binding.rvSavedAddress.adapter = adapter
    }
    private fun fetchSavedAddresses() {
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
        
        apiServiceWithInterceptor.getAddress().enqueue(object : Callback<SavedAddressResponse> {
            override fun onResponse(
                call: Call<SavedAddressResponse?>,
                response: Response<SavedAddressResponse?>
            ) {
                showLoading(false)
                binding.savedAddressRefresh.isRefreshing = false
                if (response.isSuccessful) {
                    binding.rvSavedAddress.visibility = View.VISIBLE
                    
                    val fetchedAddress = response.body()!!.data
                    savedAddresses.clear()
                    savedAddresses.addAll(fetchedAddress)
                    adapter.notifyDataSetChanged()
                    
                    if (savedAddresses.isEmpty()) {
                        binding.tvSavedAddress.visibility = View.VISIBLE
                        binding.rvSavedAddress.visibility = View.GONE
                    } else {
                        binding.tvSavedAddress.visibility = View.GONE
                        binding.rvSavedAddress.visibility = View.VISIBLE
                    }
                }else {
                    Toast.makeText(this@SavedAddressActivity, "Failed to load addresses", Toast.LENGTH_SHORT).show()
                    showEmptyState()
                }
            }
            override fun onFailure(call: Call<SavedAddressResponse?>, t: Throwable) {
                binding.savedAddressRefresh.isRefreshing = false
                showLoading(false)
                Log.e("fetch address error: ", "${t.localizedMessage}")
                Toast.makeText(
                    this@SavedAddressActivity,
                    "Error: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
                showEmptyState()
            }
        })
    }
    private fun deleteAddress(_id: String, i: Int) {
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

        apiServiceWithInterceptor.deleteAddress(_id).enqueue(object : Callback<DeleteSavedAddressResponse> {
            override fun onResponse(
                call: Call<DeleteSavedAddressResponse?>,
                response: Response<DeleteSavedAddressResponse?>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    adapter.removeAt(i)
                    fetchSavedAddresses()
                    Toast.makeText(this@SavedAddressActivity, "Address deleted", Toast.LENGTH_SHORT).show()
                    if (savedAddresses.isEmpty()) {
                        showEmptyState()
                    }
                }else {
                    Toast.makeText(this@SavedAddressActivity, "Failed to delete address", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<DeleteSavedAddressResponse?>, t: Throwable) {
                showLoading(false)
                Toast.makeText(
                    this@SavedAddressActivity,
                    "Error: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbAddress.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showEmptyState() {
        binding.tvSavedAddress.visibility = View.VISIBLE
        binding.rvSavedAddress.visibility = View.GONE
    }
}
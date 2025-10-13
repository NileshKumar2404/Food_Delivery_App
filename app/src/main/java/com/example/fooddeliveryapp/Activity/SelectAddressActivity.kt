package com.example.fooddeliveryapp.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fooddeliveryapp.Adapter.SelectedAddressAdapter
import com.example.fooddeliveryapp.AuthInterceptor.AuthInterceptor
import com.example.fooddeliveryapp.DataModel.SavedAddress
import com.example.fooddeliveryapp.DataModel.SavedAddressResponse
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.Routes.ApiService
import com.example.fooddeliveryapp.URLs.RetrofitInstance
import com.example.fooddeliveryapp.databinding.ActivitySelectAddressBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SelectAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectAddressBinding
    private lateinit var adapter : SelectedAddressAdapter
    private var selectedAddressId: String? = null
    private var selectedAddressText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelectAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpRecyclerView()
        setUpListeners()
        fetchAddresses()
    }
    private fun setUpRecyclerView() {
        adapter = SelectedAddressAdapter(onSelect = {addressId ->
            selectedAddressId = addressId
        })
        binding.rvAddresses.layoutManager = LinearLayoutManager(this)
        binding.rvAddresses.adapter = adapter
    }
    private fun setUpListeners() {
        binding.btnConfirmAddress.setOnClickListener {
            if (selectedAddressId != null && selectedAddressText != null) {
                val resultIntent = Intent().apply {
                    putExtra("selectedAddressId", selectedAddressId)
                    putExtra("selectedAddressText", selectedAddressText)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else{
                Toast.makeText(this, "Please select an address", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun fetchAddresses() {
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
                    val addresssList = response.body()!!.data
                    if (addresssList.isNotEmpty()) {
                        adapter.submitList(addresssList)
                    }

                    adapter = SelectedAddressAdapter( onSelect = { id ->
                        val address = addresssList.find { it._id == id }
                        if (address != null) {
                            selectedAddressId = address._id
                            selectedAddressText = "${address.street}, ${address.city}, ${address.state} - ${address.pinCode}"
                        }
                    })
                    binding.rvAddresses.adapter = adapter
                    adapter.submitList(addresssList)
                } else {
                    Toast.makeText(this@SelectAddressActivity, "Failed to fetch addresses", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SavedAddressResponse?>, t: Throwable) {
                Log.e("SelectAddressActivity", "Error fetching addresses: ${t.localizedMessage}")
                Toast.makeText(this@SelectAddressActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
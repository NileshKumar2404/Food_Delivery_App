package com.example.fooddeliveryapp.Activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.databinding.ActivityAddressBinding
import com.example.fooddeliveryapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.R.attr.name
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.fooddeliveryapp.AuthInterceptor.AuthInterceptor
import com.example.fooddeliveryapp.DataModel.AddAddressRequest
import com.example.fooddeliveryapp.DataModel.AddAddressResponse
import com.example.fooddeliveryapp.DataModel.Coordinates
import com.example.fooddeliveryapp.DataModel.UpdateAddressModelResponse
import com.example.fooddeliveryapp.DataModel.UpdateAddressRequest
import com.example.fooddeliveryapp.Routes.ApiService
import com.example.fooddeliveryapp.URLs.RetrofitInstance
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale


class AddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var addressId: String ?= null
    private var currentLat: Double ? = null
    private var currentLong: Double ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val isEditMode = intent.getBooleanExtra("IS_EDIT_MODE", false)
        if (isEditMode) {
            addressId = intent.getStringExtra("ADDRESS_ID")
            prefillFieldsFromIntent()
            binding.btnAddAddress.text = getString(R.string.update_address)
        }

        binding.btnAddAddress.setOnClickListener {
            if (isEditMode) {
                updatedAddress()
            } else{
                saveAddress()
            }
        }
        
        setupListeners()
    }
    private fun updatedAddress() {
        val name = binding.etName.text.toString()
        val street = binding.etStreet.text.toString()
        val city = binding.etCity.text.toString()
        val state = binding.etState.text.toString()
        val label = binding.etLabel.text.toString()
        val pincode = binding.etPincode.text.toString()
        val phone = binding.etPhone.text.toString()

        if (name.isEmpty() || street.isEmpty() || city.isEmpty() || state.isEmpty() || label.isEmpty() || pincode.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedAddress = UpdateAddressRequest(name, phone, label, street, city, state, pincode.toInt())

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

        apiServiceWithInterceptor.updateAddress(updatedAddress, addressId!!).enqueue(object : Callback<UpdateAddressModelResponse> {
            override fun onResponse(
                call: Call<UpdateAddressModelResponse?>,
                response: Response<UpdateAddressModelResponse?>
            ) {
                if (response.isSuccessful) {

                    Toast.makeText(this@AddressActivity, "Address updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddressActivity, "Failed to update address", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateAddressModelResponse?>, t: Throwable) {
                Log.e("Update address: ", "${t.localizedMessage}")
                Toast.makeText(this@AddressActivity, "Error updating address: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun prefillFieldsFromIntent() {
        binding.etName.setText(intent.getStringExtra("NAME"))
        binding.etPhone.setText(intent.getStringExtra("PHONE"))
        binding.etStreet.setText(intent.getStringExtra("STREET"))
        binding.etCity.setText(intent.getStringExtra("CITY"))
        binding.etState.setText(intent.getStringExtra("STATE"))
        binding.etPincode.setText(intent.getStringExtra("PINCODE"))
        binding.etLabel.setText(intent.getStringExtra("LABEL"))
    }
    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnLocationAddress.setOnClickListener {
            requestLocationPermission()
        }
    }
    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            } else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
    private val locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        granted ->
        if (granted) getCurrentLocation()
        else Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
    }
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location !== null) {
                currentLat = location.latitude
                currentLong = location.longitude
                reverseGeocode(location.latitude, location.longitude)
            }else {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error getting location: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun reverseGeocode(lat: Double, long: Double) {
        val geocode = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocode.getFromLocation(lat, long, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                binding.etCity.setText(addr.locality ?: "")
                binding.etState.setText(addr.adminArea ?: "")
                binding.etPincode.setText(addr.postalCode ?: "")
                binding.etStreet.setText(addr.thoroughfare ?: "")
                Toast.makeText(this, "Location detected", Toast.LENGTH_SHORT).show()
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun saveAddress() {
        val name = binding.etName.text.toString()
        val street = binding.etStreet.text.toString()
        val city = binding.etCity.text.toString()
        val state = binding.etState.text.toString()
        val label = binding.etLabel.text.toString()
        val pincode = binding.etPincode.text.toString()
        val phone = binding.etPhone.text.toString()
        val coords = if (currentLat != null && currentLong != null) {
            Coordinates(lat = currentLat!!, long = currentLong!!)
        } else null

        if (name.isEmpty() || street.isEmpty() || city.isEmpty() || state.isEmpty() || label.isEmpty() || pincode.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val address = AddAddressRequest(name, phone, label, street, city, state, pincode.toInt(), coordinates = coords)

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

        apiServiceWithInterceptor.addAddress(address).enqueue(object : Callback<AddAddressResponse> {
            override fun onResponse(
                call: Call<AddAddressResponse?>,
                response: Response<AddAddressResponse?>
            ) {
                if (response.isSuccessful) {
                    val addAddressResponse = response.body()
                    Toast.makeText(this@AddressActivity, "Address added successfully", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this@AddressActivity, "Failed to add address", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AddAddressResponse?>, t: Throwable) {
                Log.e("AddressActivity", t.message.toString())
                Toast.makeText(this@AddressActivity, "Error adding address", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
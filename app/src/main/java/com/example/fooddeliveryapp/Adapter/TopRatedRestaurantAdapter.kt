package com.example.fooddeliveryapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddeliveryapp.DataModel.listtopRatedRestaurants
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.databinding.ViewFoodSmallCardBinding

class TopRatedRestaurantAdapter (
    private var restaurant: List<listtopRatedRestaurants>,
): RecyclerView.Adapter<TopRatedRestaurantAdapter.TopRatedRestaurantViewHolder> (){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopRatedRestaurantViewHolder {
        val binding = ViewFoodSmallCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopRatedRestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TopRatedRestaurantViewHolder,
        position: Int
    ) {
        val restaurant = restaurant[position]

        val addressText = "${restaurant.address.street}, ${restaurant.address.city}"
        holder.binding.apply {
            resName.text = restaurant.name
            restaurantAddress.text = addressText
            ratings.text = restaurant.ratings.toString()

            Glide.with(ivFood)
                .load(restaurant.image)
                .into(ivFood)
        }
    }

    override fun getItemCount(): Int = restaurant.size

    inner class TopRatedRestaurantViewHolder(val binding: ViewFoodSmallCardBinding)
        : RecyclerView.ViewHolder(binding.root)

    fun updateList(newList: List<listtopRatedRestaurants>) {
        restaurant = newList
        notifyDataSetChanged()
    }

    fun getRestaurants() = restaurant

}
package com.example.fooddeliveryapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddeliveryapp.DataModel.FavouriteRestaurant
import com.example.fooddeliveryapp.databinding.ItemViewRestaurantBinding

class FavouriteRestaurantAdapter(
    private var favouriteRestaurant: MutableList<FavouriteRestaurant>,
    private val onRemoveClicked: (FavouriteRestaurant, Int) -> Unit
): RecyclerView.Adapter<FavouriteRestaurantAdapter.FavouriteRestaurantViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouriteRestaurantViewHolder {
        val binding = ItemViewRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouriteRestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FavouriteRestaurantViewHolder,
        position: Int
    ) {
        val restaurant = favouriteRestaurant[position]

        val restaurantAddress = "${restaurant.address.street}, ${restaurant.address.city}"

        holder.binding.apply {
            tvRestaurantName.text = restaurant.name
            tvRestaurantAddress.text = restaurantAddress
            tvRestaurantRating.text = "${restaurant.ratings}"

            Glide.with(ivRestaurantImage)
                .load(restaurant.image)
                .into(ivRestaurantImage)

            btnRemoveRestaurant.setOnClickListener {
                onRemoveClicked(restaurant, position)
            }
        }
    }

    override fun getItemCount(): Int = favouriteRestaurant.size

    inner class FavouriteRestaurantViewHolder(val binding: ItemViewRestaurantBinding) : RecyclerView.ViewHolder(binding.root)

    fun removeAt(position: Int) {
        favouriteRestaurant.removeAt(position)
        notifyItemRemoved(position)
    }
}
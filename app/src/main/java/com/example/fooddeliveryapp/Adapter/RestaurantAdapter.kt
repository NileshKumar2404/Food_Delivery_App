package com.example.fooddeliveryapp.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddeliveryapp.DataModel.CreatedRestaurants
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.databinding.ItemAllRestaurantBinding

class RestaurantAdapter(
    private var items: MutableList<CreatedRestaurants>,
    private val onAddClick: (CreatedRestaurants) -> Unit
): RecyclerView.Adapter<RestaurantAdapter.FavoriteRestaurantViewHolder>() {
    inner class FavoriteRestaurantViewHolder(val binding: ItemAllRestaurantBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteRestaurantViewHolder {
        val binding = ItemAllRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteRestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteRestaurantViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        Log.d("RestaurantAdapter", "Binding item: ${item.name}")

        holder.binding.apply {
            tvRestaurantName.text = item.name
            tvRestaurantDescription.text = item.description
            tvIsOpen.text = "isOpen: ${item.isOpen}"

            Glide.with(restaurantImage)
                .load(item.image)
                .into(restaurantImage)

            btnAddRestaurant.setOnClickListener {
                val isFavourite = btnAddRestaurant.tag == true
                if (isFavourite) {
                    btnAddRestaurant.setImageResource(R.drawable.heart)
                    btnAddRestaurant.tag = false
                } else {
                    btnAddRestaurant.setImageResource(R.drawable.heart_filled)
                    btnAddRestaurant.tag = true
                }
                onAddClick(item)
            }

        }
    }
    override fun getItemCount() = items.size

    fun updateList(newList: List<CreatedRestaurants>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}
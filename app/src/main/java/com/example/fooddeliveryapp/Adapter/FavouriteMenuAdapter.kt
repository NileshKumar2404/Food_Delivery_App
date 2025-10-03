package com.example.fooddeliveryapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddeliveryapp.DataModel.FavouriteMenuItem
import com.example.fooddeliveryapp.databinding.ItemViewMenuitemBinding

class FavouriteMenuAdapter(
    private var items: MutableList<FavouriteMenuItem>,
    private val onRemoveClicked: (FavouriteMenuItem, Int) -> Unit
): RecyclerView.Adapter<FavouriteMenuAdapter.MenuViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuViewHolder {
        val binding = ItemViewMenuitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MenuViewHolder,
        position: Int
    ) {
        val menu = items[position]

        holder.binding.apply {
            tvMenuItemName.text = menu.name
            tvMenuItemPrice.text = menu.price
            tvMenuItemRestaurantName.text = menu.restaurant.name

            Glide.with(ivMenuItemImage)
                .load(menu.image)
                .into(ivMenuItemImage)

            btnRemoveMenuItem.setOnClickListener {
                onRemoveClicked(menu, position)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class MenuViewHolder(val binding: ItemViewMenuitemBinding) : RecyclerView.ViewHolder(binding.root)

    fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}